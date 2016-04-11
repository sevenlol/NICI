package tw.gov.ey.nici.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import tw.gov.ey.nici.events.MeetingInfoDataErrorEvent;
import tw.gov.ey.nici.events.MeetingInfoDataReadyEvent;
import tw.gov.ey.nici.events.MeetingInfoDataRequestEvent;
import tw.gov.ey.nici.models.NiciEventInfo;
import tw.gov.ey.nici.network.NiciClient;

public class MeetingInfoModelFragment extends Fragment {
    public static final int DEFAULT_INIT_LOAD_COUNT = 8;
    public static final String FIRST_REQUEST_ID = "first_meeting_info_request";

    private int initLoadCount = DEFAULT_INIT_LOAD_COUNT;
    private NiciClient client = null;
    private Integer total = null;
    private ArrayList<NiciEventInfo> model = new ArrayList<NiciEventInfo>();
    private boolean isStarting = false;
    private String currentRequestId = FIRST_REQUEST_ID;

    public static MeetingInfoModelFragment newInstance(NiciClient client) {
        if (client == null) {
            throw new IllegalArgumentException();
        }
        return new MeetingInfoModelFragment().setClient(client);
    }

    public static MeetingInfoModelFragment newInstance(NiciClient client, int initLoadCount) {
        if (client == null || initLoadCount <= 0) {
            throw new IllegalArgumentException();
        }
        return new MeetingInfoModelFragment().setClient(client).setInitLoadCount(initLoadCount);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (!isStarting) {
            new LoadMeetingInfoDataThread(initLoadCount).start();
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

    public ArrayList<NiciEventInfo> getModel() {
        return new ArrayList<NiciEventInfo>(model);
    }

    @Subscribe
    public void onEvent(MeetingInfoDataRequestEvent event) {
        if (isStarting || event == null ||
            event.getCount() <= 0) {
            return;
        }

        isStarting = true;
        currentRequestId = event.getId() == null ? "" : event.getId();
        new LoadMeetingInfoDataThread(event.getCount()).start();
    }

    class LoadMeetingInfoDataThread extends Thread {
        private int count;

        public LoadMeetingInfoDataThread(int count) {
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
                total = client.getNiciEventInfoCount();
                int skip = model == null ? 0 : model.size();
                List<NiciEventInfo> result = client.getNiciEventInfo(skip, count);

                // post data ready event
                EventBus.getDefault().post(new MeetingInfoDataReadyEvent(skip, total, result));

                // add data to current model
                if (result != null) {
                    for (int i = 0; i < result.size(); i++) {
                        model.add(result.get(i));
                    }
                }
            } catch (Exception e) {
                // post error event
                EventBus.getDefault().post(new MeetingInfoDataErrorEvent(currentRequestId));
            } finally {
                isStarting = false;
                currentRequestId = null;
            }
        }
    }

    private MeetingInfoModelFragment setClient(NiciClient client) {
        this.client = client; return this;
    }

    private MeetingInfoModelFragment setInitLoadCount(int initLoadCount) {
        if (initLoadCount > 0) {
            this.initLoadCount = initLoadCount;
        }
        return this;
    }
}
