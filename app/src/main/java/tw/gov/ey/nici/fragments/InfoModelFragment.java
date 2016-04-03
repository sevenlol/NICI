package tw.gov.ey.nici.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import tw.gov.ey.nici.events.InfoDataErrorEvent;
import tw.gov.ey.nici.events.InfoDataReadyEvent;
import tw.gov.ey.nici.events.InfoDataRequestEvent;
import tw.gov.ey.nici.models.NiciInfo;
import tw.gov.ey.nici.network.NiciClient;

public class InfoModelFragment extends Fragment {
    public static final int DEFAULT_INIT_LOAD_COUNT = 5;

    private int initLoadCount = DEFAULT_INIT_LOAD_COUNT;
    private NiciClient client = null;
    private Integer total = null;
    private ArrayList<NiciInfo> model = new ArrayList<NiciInfo>();
    private boolean isStarting = false;
    private String currentRequestId = null;

    public static InfoModelFragment newInstance(NiciClient client) {
        if (client == null) {
            throw new IllegalArgumentException();
        }
        return new InfoModelFragment().setClient(client);
    }

    public static InfoModelFragment newInstance(NiciClient client, int initLoadCount) {
        if (client == null || initLoadCount <= 0) {
            throw new IllegalArgumentException();
        }
        return new InfoModelFragment().setClient(client).setInitLoadCount(initLoadCount);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (!isStarting) {
            new LoadInfoDataThread(initLoadCount).start();
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

    public ArrayList<NiciInfo> getModel() {
        return new ArrayList<NiciInfo>(model);
    }

    @Subscribe
    public void onEvent(InfoDataRequestEvent event) {
        if (isStarting || event == null ||
            event.getCount() <= 0) {
            return;
        }

        isStarting = true;
        currentRequestId = event.getId() == null ? "" : event.getId();
        new LoadInfoDataThread(event.getCount()).start();
    }

    class LoadInfoDataThread extends Thread {
        private int count;

        public LoadInfoDataThread(int count) {
            if (count <= 0) {
                throw new IllegalArgumentException();
            }
            this.count = count;
        }

        @Override
        public void run() {
            if (client == null) {
                isStarting = false;
                return;
            }

            try {
                total = client.getNiciInfoCount();
                int skip = model == null ? 0 : model.size();
                List<NiciInfo> result = client.getNiciInfo(skip, count);

                // post data ready event
                EventBus.getDefault().post(new InfoDataReadyEvent(skip, total, result));

                // add data to current model
                if (result != null) {
                    for (int i = 0; i < result.size(); i++) {
                        model.add(result.get(i));
                    }
                }
            } catch (Exception e) {
                // post error event
                EventBus.getDefault().post(new InfoDataErrorEvent(currentRequestId));
            } finally {
                isStarting = false;
                currentRequestId = null;
            }
        }
    }

    private InfoModelFragment setClient(NiciClient client) {
        this.client = client; return this;
    }

    private InfoModelFragment setInitLoadCount(int initLoadCount) {
        if (initLoadCount > 0) {
            this.initLoadCount = initLoadCount;
        }
        return this;
    }
}
