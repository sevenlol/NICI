package tw.gov.ey.nici.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import tw.gov.ey.nici.MeetingDetailActivity;
import tw.gov.ey.nici.R;
import tw.gov.ey.nici.models.NiciContent;
import tw.gov.ey.nici.models.NiciEvent;
import tw.gov.ey.nici.models.NiciImage;
import tw.gov.ey.nici.network.NiciClient;
import tw.gov.ey.nici.utils.NiciContentUtils;

public class MeetingDetailFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, ViewTreeObserver.OnScrollChangedListener {
    public static final int DEFAULT_REQUEST_TIMEOUT = 5000;
    public static final int DEFAULT_SCROLL_WINDOW_SIZE = 15;
    public static final int DEFAULT_SCROLL_TIMEOUT = 5000;
    public static final boolean DEFAULT_SCROLL_DETECTION_ENABLED = false;
    public static final NiciContent.Setting DEFAULT_DISPLAY_CHOICE =
            NiciContent.Setting.MEDIUM;

    private String eventId;
    private NiciEvent model;

    private NiciClient client = null;
    private Handler handler = new Handler();

    private ScrollView scrollView = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;
    private ProgressBar loadingProgress = null;
    private LinearLayout meetingDetailContainer = null;

    private boolean isSendingRequest = false;

    private boolean scrollDetectionEnabled = DEFAULT_SCROLL_DETECTION_ENABLED;
    private NiciContent.Setting displayChoice = DEFAULT_DISPLAY_CHOICE;
    private Queue<Integer> scrollYQueue = new LinkedList<>();
    private long lastScrollUpdateTime = SystemClock.currentThreadTimeMillis();

    public static MeetingDetailFragment newInstance(NiciClient client, String eventId) {
        if (eventId == null || eventId.equals("") || client == null) {
            throw new IllegalArgumentException();
        }
        return new MeetingDetailFragment().setEventId(eventId).setClient(client);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // just retain the instance because no tab switch here
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
                // TODO update container
            }
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.meeting_detail_fragment, container, false);

        scrollView = (ScrollView) root.findViewById(R.id.meeting_detail_anchor);
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_meeting_detail);
        meetingDetailContainer = (LinearLayout) root.findViewById(R.id.meeting_detail_container);
        loadingProgress = (ProgressBar) root.findViewById(R.id.meeting_detail_loading_progress);

        // set the scroll listener
        if (scrollView != null) {
            scrollView.getViewTreeObserver().addOnScrollChangedListener(this);
        }

        // set swipe refresh layout
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(this);
        }

        if (model == null) {
            // load data
            new LoadMeetingDetailDataThread().start();
        } else {
            clearRequestFlags();
            updateContainer();
        }

        return root;
    }

    @Override
    public void onRefresh() {
        reload();
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
                // show toolbar
                showHideBar(true);
            } else if (isScrollingDown) {
                // hide toolbar
                showHideBar(false);
            }

            scrollYQueue.clear();
        }
    }

    private void reload() {
        if (isSendingRequest) {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
            return;
        }

        if (meetingDetailContainer != null) {
            meetingDetailContainer.removeAllViews();
        }
        new LoadMeetingDetailDataThread().start();
    }

    private void updateContainer() {
        if (model == null || meetingDetailContainer == null) {
            return;
        }
        if (model.getEventContentList() == null ||
                model.getEventContentList().size() == 0) {
            return;
        }

        // clear all child views
        meetingDetailContainer.removeAllViews();

        List<NiciImage> imageList = new ArrayList<>();
        for (NiciContent content : model.getEventContentList()) {
            if (content == null) {
                continue;
            }
            View view = content.getView(getContext(), displayChoice);
            if (view == null) {
                continue;
            }
            meetingDetailContainer.addView(view);

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
    }

    private Runnable requestTimer = new Runnable() {
        @Override
        public void run() {
            Log.d("MeetingDetail", "Request Timeout");
            clearRequestFlags();
        }
    };

    private void showHideBar(boolean isShow) {
        if (getActivity() != null && getActivity() instanceof MeetingDetailActivity) {
            if (isShow) {
                ((MeetingDetailActivity) getActivity()).showBar();
            } else {
                ((MeetingDetailActivity) getActivity()).hideBar();
            }
        }
    }

    private void clearRequestFlags() {
        isSendingRequest = false;
        setLoadingProgressBar(false);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
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

    class LoadMeetingDetailDataThread extends Thread {
        @Override
        public void run() {
            if (isSendingRequest || eventId == null || eventId.equals("")) {
                return;
            }

            isSendingRequest = true;
            startRequestTimer();

            boolean requestSucceeded = true;
            try {
                NiciEvent result = client.getNiciEventById(eventId);
                if (validateData(result)) {
                    // valid response
                    model = result;
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateContainer();
                            }
                        });
                    }
                } else {
                    requestSucceeded = false;
                }
            } catch (Exception e) {
                Log.d("MeetingDetail", "Exception: " + e.getMessage());
                requestSucceeded = false;
            } finally {
                if (!requestSucceeded) {
                    // TODO load data error, display error message
                }
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clearRequestFlags();
                        }
                    });
                }
                // stop timer
                stopRequestTimer();
            }
        }
    }

    // TODO change the implementation
    private boolean validateData(NiciEvent event) {
        return event != null && event.getEventContentList() != null;
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

    private MeetingDetailFragment setEventId(String eventId) {
        this.eventId = eventId; return this;
    }

    private MeetingDetailFragment setClient(NiciClient client) {
        this.client = client; return this;
    }
}
