package tw.gov.ey.nici.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import tw.gov.ey.nici.events.ProjectDataErrorEvent;
import tw.gov.ey.nici.events.ProjectDataReadyEvent;
import tw.gov.ey.nici.events.ProjectDataRequestEvent;
import tw.gov.ey.nici.models.NiciContent;
import tw.gov.ey.nici.models.NiciProject;
import tw.gov.ey.nici.network.NiciClient;

public class ProjectModelFragment extends Fragment {
    public static final String FIRST_REQUEST_ID = "first_project_request";

    private NiciClient client = null;

    private NiciProject model = null;

    private boolean isStarting = false;
    private String currentRequestId = FIRST_REQUEST_ID;

    public static ProjectModelFragment newInstance(NiciClient client) {
        check(client);
        return new ProjectModelFragment().setClient(client);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (!isStarting) {
            new LoadProjectDataThread().start();
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

    public NiciProject getModel() {
        if (model == null) {
            return null;
        }
        return new NiciProject()
                .setProjectFileUrl(model == null ? null : model.getProjectFileUrl())
                .setContentList(model == null ? null :
                        new ArrayList<NiciContent>(model.getContentList()));
    }

    @Subscribe
    public void onEvent(ProjectDataRequestEvent event) {
        if (isStarting || event == null) {
            return;
        }

        isStarting = true;
        currentRequestId = event.getId() == null ? "" : event.getId();
        new LoadProjectDataThread().start();
    }

    class LoadProjectDataThread extends Thread {
        @Override
        public void run() {
            if (client == null) {
                clearLoadDataFlags();
                return;
            }

            try {
                model = client.getNiciProject();
                // post data ready event
                EventBus.getDefault().post(new ProjectDataReadyEvent(model));
            } catch (Exception e) {
                // post data error event
                EventBus.getDefault().post(new ProjectDataErrorEvent(currentRequestId));
            } finally {
                clearLoadDataFlags();
            }
        }
    }

    private void clearLoadDataFlags() {
        isStarting = false;
        currentRequestId = null;
    }

    private ProjectModelFragment setClient(NiciClient client) {
        this.client = client; return this;
    }

    private static void check(NiciClient client) {
        if (client == null) {
            throw new IllegalArgumentException();
        }
    }
}
