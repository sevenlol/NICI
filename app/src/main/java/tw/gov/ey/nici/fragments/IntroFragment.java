package tw.gov.ey.nici.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
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

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import tw.gov.ey.nici.NICIMainActivity;
import tw.gov.ey.nici.R;
import tw.gov.ey.nici.events.IntroDataErrorEvent;
import tw.gov.ey.nici.events.IntroDataReadyEvent;
import tw.gov.ey.nici.models.NiciContent;
import tw.gov.ey.nici.models.NiciImage;
import tw.gov.ey.nici.models.NiciIntro;
import tw.gov.ey.nici.utils.NiciContentUtils;

public class IntroFragment extends Fragment
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
            ViewTreeObserver.OnScrollChangedListener {
    public static final String YOUTUBE_API_KEY = "AIzaSyA2e_42-4z4KElVeurPFjsESmjzz0PFVfc";
    public static final int DEFAULT_REQUEST_TIMEOUT = 5000;
    public static final int DEFAULT_SCROLL_WINDOW_SIZE = 15;
    public static final int DEFAULT_SCROLL_TIMEOUT = 5000;
    public static final boolean DEFAULT_SCROLL_DETECTION_ENABLED = false;
    public static final NiciContent.Setting DEFAULT_DISPLAY_CHOICE =
            NiciContent.Setting.MEDIUM;

    private Handler handler = new Handler();

    private ScrollView scrollView = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;
    private ProgressBar loadingProgress = null;
    private LinearLayout upperIntroContainer = null;
    private LinearLayout lowerIntroContainer = null;
    private FloatingActionButton scrollToTopBtn = null;
    private RelativeLayout youtubeLayout = null;

    private NiciIntro model = null;

    private boolean isSendingRequest = true;
    private String currentRequestId = IntroModelFragment.FIRST_REQUEST_ID;

    private boolean scrollDetectionEnabled = DEFAULT_SCROLL_DETECTION_ENABLED;
    private NiciContent.Setting displayChoice = DEFAULT_DISPLAY_CHOICE;
    private Queue<Integer> scrollYQueue = new LinkedList<>();
    private long lastScrollUpdateTime = SystemClock.currentThreadTimeMillis();

    public static IntroFragment newInstance() {
        return new IntroFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();

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
                updateContainers();
            }
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

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // setup views
        View view = inflater.inflate(R.layout.intro_fragment, container, false);
        scrollView = (ScrollView) view.findViewById(R.id.intro_anchor);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_intro);
        upperIntroContainer = (LinearLayout) view.findViewById(R.id.upper_intro_container);
        lowerIntroContainer = (LinearLayout) view.findViewById(R.id.lower_intro_container);
        loadingProgress = (ProgressBar) view.findViewById(R.id.intro_loading_progress);
        scrollToTopBtn = (FloatingActionButton) view.findViewById(R.id.scroll_to_top_btn);
        youtubeLayout = (RelativeLayout) view.findViewById(R.id.youtube_fragment);

        // set the scroll listener
        if (scrollView != null) {
            scrollView.getViewTreeObserver().addOnScrollChangedListener(this);
        }

        // set swipe refresh layout
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(this);
        }

        // set scroll to top btn listener
        if (scrollToTopBtn != null) {
            scrollToTopBtn.setOnClickListener(this);
        }

        // already has model, stop loading
        if (model != null) {
            clearRequestFlags();
            // update view with model
            updateContainers();
        } else {
            // start request timer
            startRequestTimer();
        }

        return view;
    }

    @Override
    public void onRefresh() {
        reload();
    }

    @Override
    public void onClick(View v) {
        scrollToTop();
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
                Log.d("Intro", "Scrolling Up");
                showHideBars(true);
            } else if (isScrollingDown) {
                Log.d("Intro", "Scrolling Down");
                showHideBars(false);
            }

            scrollYQueue.clear();
        }
    }

    public IntroFragment setModel(NiciIntro model) {
        this.model = model; return this;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IntroDataReadyEvent event) {
        if (event == null || event.getIntro() == null) {
            return;
        }
        NiciIntro intro = event.getIntro();
        if (intro.getContentList() == null ||
            intro.getVideoId() == null) {
            return;
        }
        model = intro;

        // update views
        updateContainers();

        // TODO add id verification for data ready event
        // stop request timer and clear flags
        stopRequestTimer();
        clearRequestFlags();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IntroDataErrorEvent event) {
        if (event == null) {
            return;
        }

        // the id is not matched, exit
        if (currentRequestId != null && !currentRequestId.equals(event.getId())) {
            return;
        }

        // the first request failed
        if (IntroModelFragment.FIRST_REQUEST_ID.equals(currentRequestId)) {
            // TODO show error text to let user know how to reload
        }

        // stop request timer and clear flags
        stopRequestTimer();
        clearRequestFlags();
    }

    private void reload() {
        Log.d("Intro", "Reloading");
        isSendingRequest = true;
        currentRequestId = IntroModelFragment.FIRST_REQUEST_ID;
        model = null;
        if (upperIntroContainer != null) {
            upperIntroContainer.removeAllViews();
        }
        if (lowerIntroContainer != null) {
            lowerIntroContainer.removeAllViews();
        }
        // will be using the pull down refresh icon when reloading
        setLoadingProgressBar(false);
        // hide youtube layout
        setYoutubeLayout(false);
        startRequestTimer();
        if (getActivity() != null &&
                getActivity() instanceof NICIMainActivity) {
            ((NICIMainActivity) getActivity()).reloadCurrentModel();
        }
    }

    private void updateContainers() {
        if (model == null || upperIntroContainer == null ||
            lowerIntroContainer == null) {
            return;
        }
        if (model.getContentList() == null || model.getContentList().size() == 0) {
            return;
        }

        // clear all child views
        upperIntroContainer.removeAllViews();
        lowerIntroContainer.removeAllViews();

        // if the location index is not set, the video will be placed last
        final int videoLocationIndex = model.getVideoLocationIndex() == null ?
                model.getContentList().size() : model.getVideoLocationIndex();

        List<NiciImage> imageList = new ArrayList<>();
        for (int i = 0; i < model.getContentList().size(); i++) {
            NiciContent content = model.getContentList().get(i);
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

            if (i >= videoLocationIndex) {
                lowerIntroContainer.addView(view);
            } else {
                upperIntroContainer.addView(view);
            }

            // save NiciImage to a list
            if (content instanceof NiciImage) {
                NiciImage image = (NiciImage) content;
                if (image.getImageUrl() != null &&
                        image.getImageView(getContext()) != null) {
                    imageList.add(image);
                }
            }
        }

        // TODO change image setting and check url
        for (NiciImage image : imageList) {
            Picasso.with(getActivity())
                    .load(image.getImageUrl())
                    .into(image.getImageView(getActivity()));
        }

        // show youtube layout
        setYoutubeLayout(true);

        // init youtube video
        if (getChildFragmentManager().findFragmentById(R.id.youtube_fragment) == null &&
            model.getVideoId() != null) {
            YouTubePlayerSupportFragment fragment = YouTubePlayerSupportFragment.newInstance();
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.youtube_fragment, fragment)
                    .commit();
            fragment.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean restored) {
                    youTubePlayer.setOnFullscreenListener(fullscreenListener);
                    youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
                    youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
                    if (!restored) {
                        // only load the video, does not play
                        youTubePlayer.cueVideo(model.getVideoId());
                    }
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                    makeShortToast(R.string.load_video_fail);
                }
            });
        }
    }

    private void scrollToTop() {
        if (scrollView != null) {
            scrollView.scrollTo(0, 0);
        }
    }

    private YouTubePlayer.OnFullscreenListener fullscreenListener = new YouTubePlayer.OnFullscreenListener() {
        @Override
        public void onFullscreen(boolean isFullscreen) {
            if (!isFullscreen) {
                // prevent youtube player from stuck in landscape after leaving fullscreen
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            }
        }
    };

    private void clearRequestFlags() {
        isSendingRequest = false;
        currentRequestId = null;
        setLoadingProgressBar(false);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setYoutubeLayout(final boolean isVisible) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (youtubeLayout == null) {
                    return;
                }

                youtubeLayout.setVisibility(isVisible ?
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

    private Runnable requestTimer = new Runnable() {
        @Override
        public void run() {
            Log.d("Intro", "Request Timeout");
            clearRequestFlags();
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

    private void makeShortToast(int resourceId) {
        Toast.makeText(
                getContext(),
                getString(resourceId),
                Toast.LENGTH_SHORT).show();
    }
}
