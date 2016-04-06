package tw.gov.ey.nici.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import tw.gov.ey.nici.R;
import tw.gov.ey.nici.events.ProjectDataErrorEvent;
import tw.gov.ey.nici.events.ProjectDataReadyEvent;
import tw.gov.ey.nici.models.NiciContent;
import tw.gov.ey.nici.models.NiciImage;
import tw.gov.ey.nici.models.NiciProject;
import tw.gov.ey.nici.utils.RandomStringGenerator;

public class ProjectFragment extends Fragment {
    public static final int DEFAULT_EVENT_ID_LENGTH = 20;

    // TODO implement reload mechanism

    private ProgressBar loadingProgress = null;
    private LinearLayout projectContainer = null;
    private List<NiciImage> imageList = new ArrayList<>();

    private NiciProject model = null;

    private boolean isSendingRequest = true;
    private String currentRequestId = ProjectModelFragment.FIRST_REQUEST_ID;

    public static ProjectFragment newInstance() {
        return new ProjectFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.project_fragment, container, false);
        projectContainer = (LinearLayout) view.findViewById(R.id.project_container);
        loadingProgress = (ProgressBar) view.findViewById(R.id.project_loading_progress);

        // already has model, stop loading
        if (model != null) {
            clearRequestFlags();
            // update view with model
            updateContainer();
        }

        return view;
    }

    public ProjectFragment setModel(NiciProject model) {
        this.model = model; return this;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ProjectDataReadyEvent event) {
        if (event == null || event.getProject() == null) {
            return;
        }
        NiciProject project = event.getProject();
        if (project.getContentList() == null ||
            project.getProjectFileUrl() == null) {
            return;
        }
        model = project;

        // update view
        updateContainer();

        // TODO add id verification for data ready event
        clearRequestFlags();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ProjectDataErrorEvent event) {
        if (event == null) {
            return;
        }

        // the id is not matched, exit
        if (currentRequestId != null && !currentRequestId.equals(event.getId())) {
            return;
        }

        // the first request failed
        if (ProjectModelFragment.FIRST_REQUEST_ID.equals(currentRequestId)) {
            // TODO show error text to let user know how to reload
        }

        clearRequestFlags();
    }

    private void updateContainer() {
        if (model == null || projectContainer == null) {
            return;
        }
        if (model.getContentList() == null ||
            model.getContentList().size() == 0) {
            return;
        }

        // clear all child views
        projectContainer.removeAllViews();
        // clear image list
        imageList.clear();

        for (NiciContent content : model.getContentList()) {
            if (content == null) {
                continue;
            }
            View view = content.getView(getContext());
            if (view == null) {
                continue;
            }
            projectContainer.addView(view);

            // save NiciImage to a list
            if (content instanceof NiciImage) {
                NiciImage image = (NiciImage) content;
                if (image.getImageUrl() != null &&
                    image.getImageView(getContext()) != null) {
                    imageList.add(image);
                }
            }
        }

        // TODO set project file url for download

        // TODO load images
        for (NiciImage image : imageList) {

        }
    }

    // TODO  add a timer thread for request timeout
    private void setRequestFlags() {
        isSendingRequest = true;
        currentRequestId = RandomStringGenerator.getString(DEFAULT_EVENT_ID_LENGTH);
        setLoadingProgressBar(true);
    }

    // TODO cancel the timer thread
    private void clearRequestFlags() {
        isSendingRequest = false;
        currentRequestId = null;
        setLoadingProgressBar(false);
    }

    private void setLoadingProgressBar(final boolean isVisible) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadingProgress == null) {
                    return;
                }

                loadingProgress.setVisibility(isVisible ?
                    View.VISIBLE : View.GONE);
            }
        });
    }
}
