package tw.gov.ey.nici.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import tw.gov.ey.nici.events.MeetingDataErrorEvent;
import tw.gov.ey.nici.events.MeetingDataReadyEvent;
import tw.gov.ey.nici.events.MeetingDataRequestEvent;
import tw.gov.ey.nici.models.NiciEvent;
import tw.gov.ey.nici.network.NiciClient;

public class MeetingModelFragment extends Fragment {
    public static final int DEFAULT_SHOW_MORE_DATA_COUNT = 4;
    public static final int DEFAULT_INIT_LOAD_PAGE_COUNT = 2;
    public static final int DEFAULT_INIT_LOAD_COUNT =
            DEFAULT_INIT_LOAD_PAGE_COUNT * DEFAULT_SHOW_MORE_DATA_COUNT;
    public static final String FIRST_REQUEST_ID = "first_meeting_request";

    private int initLoadCount = DEFAULT_INIT_LOAD_COUNT;
    private NiciClient client = null;
    private Integer total = null;
    private ArrayList<NiciEvent> model = new ArrayList<NiciEvent>();
    private boolean isStarting = false;
    private String currentRequestId = FIRST_REQUEST_ID;

    public static MeetingModelFragment newInstance(NiciClient client) {
        if (client == null) {
            throw new IllegalArgumentException();
        }
        return new MeetingModelFragment().setClient(client);
    }

    public static MeetingModelFragment newInstance(NiciClient client, int initLoadCount) {
        if (client == null || initLoadCount <= 0) {
            throw new IllegalArgumentException();
        }
        return new MeetingModelFragment().setClient(client).setInitLoadCount(initLoadCount);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (!isStarting) {
            new LoadMeetingDataThread(initLoadCount).start();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    public Integer getTotal() { return total; }

    public ArrayList<NiciEvent> getModel() {
        return new ArrayList<NiciEvent>(model);
    }

    @Subscribe
    public void onEvent(MeetingDataRequestEvent event) {
        if (isStarting || event == null ||
            event.getCount() <= 0) {
            return;
        }

        isStarting = true;
        currentRequestId = event.getId() == null ? "" : event.getId();
        new LoadMeetingDataThread(event.getCount()).start();
    }

    class LoadMeetingDataThread extends Thread {
        private int count;

        public LoadMeetingDataThread(int count) {
            if (count <= 0) {
                throw new IllegalArgumentException();
            }
            this.count = count;
        }

        @Override
        public void run() {
            if (client == null) {
                isStarting = false;
                currentRequestId = null;
                return;
            }

            try {
                total = client.getNiciEventCount();
                int skip = model == null ? 0 : model.size();
                List<NiciEvent> result = client.getNiciEvent(skip, count);

                // post data ready event
                EventBus.getDefault().post(new MeetingDataReadyEvent(skip, total, result));

                // add data to current model
                if (result != null) {
                    for (int i = 0; i < result.size(); i++) {
                        model.add(result.get(i));
                    }
                }
            } catch (Exception e) {
                // post error event
                EventBus.getDefault().post(new MeetingDataErrorEvent(currentRequestId));
            } finally {
                isStarting = false;
                currentRequestId = null;
            }
        }
    }

    private MeetingModelFragment setClient(NiciClient client) {
        this.client = client; return this;
    }

    private MeetingModelFragment setInitLoadCount(int initLoadCount) {
        if (initLoadCount > 0) {
            this.initLoadCount = initLoadCount;
        }
        return this;
    }
}
