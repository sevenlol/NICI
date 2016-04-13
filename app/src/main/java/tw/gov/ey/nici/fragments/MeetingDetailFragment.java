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

import java.util.LinkedList;
import java.util.Queue;

import tw.gov.ey.nici.R;
import tw.gov.ey.nici.models.NiciContent;
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

    private Handler handler = new Handler();

    private ScrollView scrollView = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;
    private ProgressBar loadingProgress = null;
    private LinearLayout meetingDetailContainer = null;

    private boolean scrollDetectionEnabled = DEFAULT_SCROLL_DETECTION_ENABLED;
    private NiciContent.Setting displayChoice = DEFAULT_DISPLAY_CHOICE;
    private Queue<Integer> scrollYQueue = new LinkedList<>();
    private long lastScrollUpdateTime = SystemClock.currentThreadTimeMillis();

    public static MeetingDetailFragment newInstance(String eventId) {
        if (eventId == null || eventId.equals("")) {
            throw new IllegalArgumentException();
        }
        return new MeetingDetailFragment().setEventId(eventId);
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

        // FIXME remove this
        if (loadingProgress != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingProgress.setVisibility(View.GONE);
                }
            });
        }

        return root;
    }

    @Override
    public void onRefresh() {
        // FIXME remove this
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
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
                // TODO show toolbar
            } else if (isScrollingDown) {
                // TODO hide toolbar
            }

            scrollYQueue.clear();
        }
    }

    private MeetingDetailFragment setEventId(String eventId) {
        this.eventId = eventId; return this;
    }
}
