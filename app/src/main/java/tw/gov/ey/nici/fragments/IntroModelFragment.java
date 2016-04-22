package tw.gov.ey.nici.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import tw.gov.ey.nici.events.IntroDataErrorEvent;
import tw.gov.ey.nici.events.IntroDataReadyEvent;
import tw.gov.ey.nici.events.IntroDataRequestEvent;
import tw.gov.ey.nici.models.NiciIntro;
import tw.gov.ey.nici.network.NiciClient;

public class IntroModelFragment extends Fragment {
    public static final String FIRST_REQUEST_ID = "first_intro_request";

    private NiciClient client = null;

    private NiciIntro model = null;

    private boolean isStarting = false;
    private String currentRequestId = FIRST_REQUEST_ID;

    public static IntroModelFragment newInstance(NiciClient client) {
        check(client);
        return new IntroModelFragment().setClient(client);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (!isStarting) {
            new LoadIntroDataThread().start();
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

    public NiciIntro getModel() {
        return model;
    }

    @Subscribe
    public void onEvent(IntroDataRequestEvent event) {
        if (isStarting || event == null) {
            return;
        }

        isStarting = true;
        currentRequestId = event.getId() == null ? "" : event.getId();
        new LoadIntroDataThread().start();
    }

    class LoadIntroDataThread extends Thread {
        @Override
        public void run() {
            if (client == null) {
                clearLoadDataFlags();
                return;
            }

            try {
                model = client.getNiciIntro();
                // post data ready event
                EventBus.getDefault().post(new IntroDataReadyEvent(model));
            } catch (Exception e) {
                // post data error event
                EventBus.getDefault().post(new IntroDataErrorEvent(currentRequestId));
            } finally {
                clearLoadDataFlags();
            }
        }
    }

    private void clearLoadDataFlags() {
        isStarting = false;
        currentRequestId = null;
    }

    private IntroModelFragment setClient(NiciClient client) {
        this.client = client; return this;
    }

    private static void check(NiciClient client) {
        if (client == null) {
            throw new IllegalArgumentException();
        }
    }
}
