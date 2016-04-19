package tw.gov.ey.nici.fragments;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import tw.gov.ey.nici.DocViewerActivity;
import tw.gov.ey.nici.NICIMainActivity;
import tw.gov.ey.nici.R;
import tw.gov.ey.nici.events.ProjectDataErrorEvent;
import tw.gov.ey.nici.events.ProjectDataReadyEvent;
import tw.gov.ey.nici.models.NiciContent;
import tw.gov.ey.nici.models.NiciDocViewerLink;
import tw.gov.ey.nici.models.NiciFileUtilBar;
import tw.gov.ey.nici.models.NiciImage;
import tw.gov.ey.nici.models.NiciProject;
import tw.gov.ey.nici.utils.NiciContentUtils;
import tw.gov.ey.nici.utils.RandomStringGenerator;

public class ProjectFragment extends Fragment
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        ViewTreeObserver.OnScrollChangedListener {
    public static final int DEFAULT_EVENT_ID_LENGTH = 20;
    public static final int DEFAULT_DOWNLOAD_TIMEOUT = 30000;
    public static final int DEFAULT_REQUEST_TIMEOUT = 5000;
    public static final int DEFAULT_SCROLL_WINDOW_SIZE = 15;
    public static final int DEFAULT_SCROLL_TIMEOUT = 5000;
    public static final boolean DEFAULT_SCROLL_DETECTION_ENABLED = false;
    public static final NiciContent.Setting DEFAULT_DISPLAY_CHOICE =
            NiciContent.Setting.MEDIUM;

    private DownloadManager downloadManager = null;

    private Handler handler = new Handler();

    private ScrollView scrollView = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;
    private ProgressBar loadingProgress = null;
    private RelativeLayout projectTitleLogoContainer = null;
    private LinearLayout projectContainer = null;
    private List<NiciImage> imageList = new ArrayList<>();
    private FloatingActionButton scrollToTopBtn = null;

    private NiciProject model = null;

    private List<NiciFileUtilBar> fileUtilBars = new ArrayList<>();
    private List<NiciDocViewerLink> docViewerLinks = new ArrayList<>();

    private boolean isSendingRequest = true;
    private String currentRequestId = ProjectModelFragment.FIRST_REQUEST_ID;

    private Long currentDownloadId = null;

    private boolean scrollDetectionEnabled = DEFAULT_SCROLL_DETECTION_ENABLED;
    private NiciContent.Setting displayChoice = DEFAULT_DISPLAY_CHOICE;
    private Queue<Integer> scrollYQueue = new LinkedList<>();
    private long lastScrollUpdateTime = SystemClock.currentThreadTimeMillis();

    public static ProjectFragment newInstance() {
        return new ProjectFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        getActivity().registerReceiver(downloadEventReceiver, filter);
        // make sure the download button is enabled
        // does not start a new downloadTimer at the moment
        resetDownloadFlags();

        // get preference
        if (getActivity() != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            scrollDetectionEnabled = prefs.getBoolean(
                    getString(R.string.pref_scroll_detection_key), scrollDetectionEnabled);
            NiciContent.Setting oldDisplayChoice = displayChoice;
            displayChoice = NiciContentUtils.getSetting(
                    prefs.getString(
                            getString(R.string.pref_display_choice_key),
                            displayChoice.name()));
            if (oldDisplayChoice != displayChoice) {
                updateContainer();
            }
        }
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(downloadEventReceiver);
        // stop download timer when exiting
        stopDownloadTimer();

        super.onPause();
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
        // get project manager
        downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

        // setup views
        View view = inflater.inflate(R.layout.project_fragment, container, false);
        scrollView = (ScrollView) view.findViewById(R.id.project_anchor);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_project);
        projectTitleLogoContainer = (RelativeLayout) view.findViewById(R.id.project_title_logo_container);
        projectContainer = (LinearLayout) view.findViewById(R.id.project_container);
        loadingProgress = (ProgressBar) view.findViewById(R.id.project_loading_progress);
        scrollToTopBtn = (FloatingActionButton) view.findViewById(R.id.scroll_to_top_btn);

        // set the scroll listener
        if (scrollView != null) {
            scrollView.getViewTreeObserver().addOnScrollChangedListener(this);
        }

        // set swipe refresh layout
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(this);
        }

        // set download btn listener
        if (scrollToTopBtn != null) {
            scrollToTopBtn.setOnClickListener(this);
        }

        // hide title logo container
        setProjectTitleLogoContainer(false);

        // already has model, stop loading
        if (model != null) {
            clearRequestFlags();
            // update view with model
            updateContainer();
        } else {
            // start request timer
            startRequestTimer();
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

        // enable download
        if (model.getProjectFileUrl() != null &&
            !model.getProjectFileUrl().equals("") &&
            scrollToTopBtn != null) {
            scrollToTopBtn.setEnabled(true);
        }

        // update view
        updateContainer();

        // TODO add id verification for data ready event
        // stop request timer and clear flags
        stopRequestTimer();
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

        // stop request timer and clear flags
        stopRequestTimer();
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

        // show title logo container
        setProjectTitleLogoContainer(true);

        for (NiciContent content : model.getContentList()) {
            if (content == null) {
                continue;
            }
            View view = content.getView(getContext(), displayChoice);
            if (view == null) {
                continue;
            }
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
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

            // file util bars
            if (content instanceof NiciFileUtilBar) {
                NiciFileUtilBar bar = (NiciFileUtilBar) content;
                fileUtilBars.add(bar);
                if (bar.getDownloadButton(getContext()) != null) {
                    bar.getDownloadButton(getContext()).setOnClickListener(this);
                }
                if (bar.getViewButton(getContext()) != null) {
                    bar.getViewButton(getContext()).setOnClickListener(this);
                }
            }

            // doc viewer links
            if (content instanceof NiciDocViewerLink) {
                NiciDocViewerLink link = (NiciDocViewerLink) content;
                docViewerLinks.add(link);
                if (link.getLinkButton(getContext()) != null) {
                    link.getLinkButton(getContext()).setOnClickListener(this);
                }
            }
        }

        // TODO change image setting and check url
        for (NiciImage image : imageList) {
            Picasso.with(getActivity())
                    .load(image.getImageUrl())
                    .into(image.getImageView(getActivity()));
        }
    }

    @Override
    public void onRefresh() {
        reload();
    }

    private void reload() {
        Log.d("Project", "Reloading");
        isSendingRequest = true;
        currentRequestId = ProjectModelFragment.FIRST_REQUEST_ID;
        model = null;
        if (projectContainer != null) {
            projectContainer.removeAllViews();
        }
        // hide title logo container
        setProjectTitleLogoContainer(false);
        // will be using the pull down refresh icon when reloading
        setLoadingProgressBar(false);
        startRequestTimer();
        if (getActivity() != null &&
            getActivity() instanceof NICIMainActivity) {
            ((NICIMainActivity) getActivity()).reloadCurrentModel();
        }
    }

    private void setRequestFlags() {
        isSendingRequest = true;
        currentRequestId = RandomStringGenerator.getString(DEFAULT_EVENT_ID_LENGTH);
        setLoadingProgressBar(true);
    }

    private void clearRequestFlags() {
        isSendingRequest = false;
        currentRequestId = null;
        setLoadingProgressBar(false);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setProjectTitleLogoContainer(final boolean isVisible) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (projectTitleLogoContainer == null) {
                    return;
                }
                projectTitleLogoContainer.setVisibility(isVisible ?
                    View.VISIBLE : View.GONE);
            }
        });
    }

    private void setLoadingProgressBar(final boolean isVisible) {
        if (getActivity() == null) {
            return;
        }
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

    @Override
    public void onClick(View v) {
        // TODO implement download and online view document functions
        if (v == scrollToTopBtn) {
            scrollToTop();
            return;
        }

        // file util bars
        for (NiciFileUtilBar bar : fileUtilBars) {
            if (bar == null || bar.getFileUrl() == null) {
                continue;
            }

            if (v == bar.getDownloadButton(getContext())) {
                downloadProjectFile(bar.getFileUrl());
                return;
            }
            if (v == bar.getViewButton(getContext())) {
                viewDoc(bar.getFileUrl(), bar.getFileTitle());
                return;
            }
        }

        // doc viewer link
        for (NiciDocViewerLink link : docViewerLinks) {
            if (link == null || link.getFileUrl() == null) {
                continue;
            }

            if (v == link.getLinkButton(getContext())) {
                viewDoc(link.getFileUrl(), link.getFileTitle());
                return;
            }
        }
    }

    @Override
    public void onScrollChanged() {
        if (scrollView == null) {
            return;
        }
        if (!scrollDetectionEnabled) {
            return;
        }
        if (SystemClock.currentThreadTimeMillis() - lastScrollUpdateTime > DEFAULT_SCROLL_TIMEOUT) {
            scrollYQueue.clear();
        }
        if (scrollYQueue.size() >= DEFAULT_SCROLL_WINDOW_SIZE) {
            scrollYQueue.poll();
        }
        scrollYQueue.add(scrollView.getScrollY());
        lastScrollUpdateTime = SystemClock.currentThreadTimeMillis();

        // check scroll up or down
        if (scrollYQueue.size() == DEFAULT_SCROLL_WINDOW_SIZE) {
            boolean isScrollingUp = true;
            boolean isScrollingDown = true;
            Integer lastY = null;
            for (Integer y : scrollYQueue) {
                if (y == null) {
                    isScrollingUp = false;
                    isScrollingDown = false;
                    break;
                }

                if (lastY != null) {
                    isScrollingDown &= (y - lastY >= 0);
                    isScrollingUp &= (y - lastY < 0);
                }
                lastY = y;
            }

            if (isScrollingUp) {
                Log.d("Project", "Scrolling Up");
                showHideBars(true);
            } else if (isScrollingDown) {
                Log.d("Project", "Scrolling Down");
                showHideBars(false);
            }

            scrollYQueue.clear();
        }
    }

    private void scrollToTop() {
        if (scrollView != null) {
            scrollView.scrollTo(0, 0);
        }
    }

    private void viewDoc(String fileUrl, String fileTitle) {
        if (fileUrl == null || fileUrl.equals("")) {
            return;
        }

        Intent intent = new Intent(getContext(), DocViewerActivity.class);
        intent.putExtra(DocViewerActivity.DOC_URL_KEY, fileUrl);
        if (fileTitle != null && !fileTitle.equals("")) {
            intent.putExtra(DocViewerActivity.DOC_TITLE_KEY, fileTitle);
        }
        if (getActivity() != null) {
            getActivity().startActivity(intent);
        }
    }

    private void downloadProjectFile(String fileUrl) {
        if (fileUrl == null || fileUrl.equals("")) {
            makeShortToast(R.string.no_project_file);
            return;
        }

        if (downloadManager == null) {
            makeShortToast(R.string.download_failed);
            return;
        }

        // is already downloading, return
        if (currentDownloadId != null) {
            makeShortToast(R.string.already_downloading);
            return;
        }

        // TODO download project file
        Uri uri = null;
        try {
            uri = Uri.parse(fileUrl);
        } catch (Exception e) {
            makeShortToast(R.string.no_project_file);
            return;
        }

        DownloadManager.Request req = new DownloadManager.Request(uri);

        // download settings
        req.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_MOBILE |
                        DownloadManager.Request.NETWORK_WIFI)
                .setAllowedOverRoaming(false)
                .setTitle(getString(R.string.project_title))
                .setDescription(getString(R.string.project_desc))
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, getName(uri))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        currentDownloadId = downloadManager.enqueue(req);
        // start download timer
        startDownloadTimer();
    }

    // TODO handle download notification clicked event
    private BroadcastReceiver downloadEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                if (currentDownloadId == null) {
                    return;
                }
                Bundle extras = intent.getExtras();
                Long id = extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID);
                if (currentDownloadId.equals(id)) {
                    // download complete
                    Log.d("Project", "Download Complete");
                    stopDownloadTimer();
                    resetDownloadFlags();
                    makeShortToast(R.string.download_complete);
                }
            }
        }
    };

    private Runnable requestTimer = new Runnable() {
        @Override
        public void run() {
            Log.d("Project", "Request Timeout");
            clearRequestFlags();
        }
    };

    private Runnable downloadTimer = new Runnable() {
        @Override
        public void run() {
            Log.d("Project", "Download Timeout");
            resetDownloadFlags();
        }
    };

    private void showHideBars(boolean showBars) {
        if (getActivity() == null || !(getActivity() instanceof NICIMainActivity)) {
            return;
        }

        if (showBars) {
            ((NICIMainActivity) getActivity()).showBars();
        } else {
            ((NICIMainActivity) getActivity()).hideBars();
        }
    }

    private void resetDownloadFlags() {
        currentDownloadId = null;
    }

    private void startRequestTimer() {
        if (handler != null) {
            handler.postDelayed(requestTimer, DEFAULT_REQUEST_TIMEOUT);
        }
    }

    private void stopRequestTimer() {
        if (handler != null) {
            handler.removeCallbacks(requestTimer);
        }
    }

    private void startDownloadTimer() {
        if (handler != null) {
            handler.postDelayed(downloadTimer, DEFAULT_DOWNLOAD_TIMEOUT);
        }
    }

    private void stopDownloadTimer() {
        if (handler != null) {
            handler.removeCallbacks(downloadTimer);
        }
    }

    private void makeShortToast(int resourceId) {
        Toast.makeText(
                getContext(),
                getString(resourceId),
                Toast.LENGTH_SHORT).show();
    }

    private String getName(Uri uri) {
        File file = new File(uri.toString());
        return file.getName();
    }
}
