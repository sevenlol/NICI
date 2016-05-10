package tw.gov.ey.nici.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import tw.gov.ey.nici.NICIMainActivity;
import tw.gov.ey.nici.R;
import tw.gov.ey.nici.events.MeetingInfoDataErrorEvent;
import tw.gov.ey.nici.events.MeetingInfoDataReadyEvent;
import tw.gov.ey.nici.events.MeetingInfoDataRequestEvent;
import tw.gov.ey.nici.models.NiciEventInfo;
import tw.gov.ey.nici.utils.RandomStringGenerator;
import tw.gov.ey.nici.MeetingInfoDetailActivity;
import tw.gov.ey.nici.views.NiciEventInfoAdapter;

public class MeetingInfoFragment extends Fragment implements ListView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, ListView.OnScrollListener, View.OnClickListener {
    public static final int DEFAULT_SHOW_MORE_DATA_COUNT = MeetingInfoModelFragment.DEFAULT_SHOW_MORE_DATA_COUNT;
    public static final int DEFAULT_EVENT_ID_LENGTH = 20;
    public static final int DEFAULT_REQUEST_TIMEOUT = 10000;
    public static final boolean DEFAULT_SCROLL_DETECTION_ENABLED = false;

    private Handler handler = new Handler();

    private SwipeRefreshLayout swipeRefreshLayout = null;
    private RelativeLayout headerLayout = null;
    private TextView showMoreMeetingInfoLabel = null;
    private ProgressBar showMoreMeetingInfoProgress = null;
    private ListView listView = null;
    private FloatingActionButton scrollToTopBtn = null;

    private int currentPageCount = 0;
    private int total = 0;
    private ArrayAdapter<NiciEventInfo> adapter = null;
    private ArrayList<NiciEventInfo> model = null;

    private boolean isSendingRequest = true;
    private String currentRequestId = MeetingInfoModelFragment.FIRST_REQUEST_ID;

    private boolean scrollDetectionEnabled = DEFAULT_SCROLL_DETECTION_ENABLED;
    private int lastFirstVisibleItem = 0;

    public static MeetingInfoFragment newInstance() {
        return new MeetingInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
    public void onResume() {
        super.onResume();

        // get preference
        if (getActivity() != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            scrollDetectionEnabled = prefs.getBoolean(
                    getString(R.string.pref_scroll_detection_key), scrollDetectionEnabled);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.meeting_info_fragment, container, false);

        // set the swipe refresh layout
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_meeting_info);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(this);
        }

        // set the scroll to top button
        scrollToTopBtn = (FloatingActionButton) root.findViewById(R.id.scroll_to_top_btn);
        if (scrollToTopBtn != null) {
            scrollToTopBtn.setOnClickListener(this);
        }

        // inflate header layout
        headerLayout = (RelativeLayout) LayoutInflater.from(getContext())
                .inflate(R.layout.list_header, null);
        ImageView titleImage = (ImageView) headerLayout.findViewById(R.id.list_title);
        if (titleImage != null) {
            titleImage.setImageResource(R.drawable.meeting_info_title);
        }

        // inflate footer layout
        FrameLayout footerLayout = (FrameLayout) LayoutInflater.from(getContext())
                .inflate(R.layout.list_footer, null);
        showMoreMeetingInfoLabel = (TextView) footerLayout.findViewById(R.id.show_more_label);
        showMoreMeetingInfoProgress = (ProgressBar) footerLayout.findViewById(R.id.show_more_progress);
        if (model != null && currentPageCount > 0) {
            // some data is already loaded
            setShowMoreMeetingBtnProgressBar(true, false);
            setListHeader(true);

            if (currentPageCount * DEFAULT_SHOW_MORE_DATA_COUNT >= total) {
                setShowMoreLabelText(getString(R.string.no_more_meeting_info));
            }

            // if some data is already loaded, not sending request
            clearRequestFlags();
        }

        // start request timer in case the first request timeout
        if (model != null && model.size() == 0 && currentPageCount == 0) {
            startRequestTimer();
        }

        // set listview and adapter
        adapter = new NiciEventInfoAdapter(getActivity(), model);
        listView = (ListView) root.findViewById(R.id.meeting_info_list);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);
        listView.setAdapter(adapter);
        listView.addHeaderView(headerLayout);
        listView.addFooterView(footerLayout);

        return root;
    }

    @Override
    public void onRefresh() {
        if (isSendingRequest) {
            // instantly cancel
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
            return;
        }
        reload();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if (listView == null) {
            return;
        }
        if (!scrollDetectionEnabled) {
            return;
        }

        if (scrollState == SCROLL_STATE_IDLE && absListView.getId() == listView.getId()) {
            final int currentFirstVisibleItem = listView.getFirstVisiblePosition();
            if (currentFirstVisibleItem >= lastFirstVisibleItem) {
                Log.d("MeetingInfo", "Scroll Down");
                // prevent the glitch at the top of the list
                if (lastFirstVisibleItem != 0 || currentFirstVisibleItem != 1) {
                    showHideBars(false);
                }
            } else {
                Log.d("MeetingInfo", "Scroll Up");
                showHideBars(true);
            }
            lastFirstVisibleItem = currentFirstVisibleItem;
        }
    }

    // receive new meeting data
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MeetingInfoDataReadyEvent event) {
        if (currentPageCount == 0) {
            currentPageCount += MeetingInfoModelFragment.DEFAULT_INIT_LOAD_PAGE_COUNT;
        } else {
            currentPageCount += 1;
        }

        // update total and model
        // and set show more btn status
        if (event != null) {
            if (event.getTotal() >= 0) {
                total = event.getTotal();
            }

            if (adapter != null && event.getEventInfoList() != null) {
                for (int i = 0; i < event.getEventInfoList().size(); i++) {
                    adapter.add(event.getEventInfoList().get(i));
                }
            }
        }

        // set show more btn availablity
        if (currentPageCount * DEFAULT_SHOW_MORE_DATA_COUNT >= total) {
            setShowMoreLabelText(getString(R.string.no_more_meeting_info));
        }

        // TODO add id verification for data ready event
        // stop request timer and clear flags
        stopRequestTimer();
        clearRequestFlags();
    }

    // meeting data request failed
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MeetingInfoDataErrorEvent event) {
        if (event == null) {
            return;
        }
        if (!isSendingRequest || currentRequestId == null) {
            return;
        }

        // the id is not matched, exit
        if (!currentRequestId.equals(event.getId())) {
            return;
        }

        if (MeetingInfoModelFragment.FIRST_REQUEST_ID.equals(currentRequestId)) {
            // TODO handle the first request failing
            // TODO disable show more meeting action and only allow reload
            // TODO show error text
        }

        // notify user request failed
        makeShortToast(R.string.request_failed);

        // stop request timer and clear flags
        stopRequestTimer();
        clearRequestFlags();
    }

    @Override
    // meeting item clicked
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Meeting Event", "Item Position: " + position);
        if (model == null || model.size() <= position) {
            return;
        }

        NiciEventInfo eventInfo = model.get(position);
        if (eventInfo == null || eventInfo.getId() == null) {
            return;
        }
        try {
            // TODO add other event info field for preview
            Log.d("MeetingInfo", "Start Activity");
            Intent meetingDetailIntent = new Intent(getActivity(), MeetingInfoDetailActivity.class);
            meetingDetailIntent.putExtra(MeetingInfoDetailActivity.NICI_EVENT_INFO_ID_KEY, eventInfo.getId());
            if (eventInfo.getTitle() != null) {
                meetingDetailIntent.putExtra(
                        MeetingInfoDetailActivity.MEETING_INFO_DETAIL_TITLE_KEY,
                        eventInfo.getTitle());
            }
            startActivity(meetingDetailIntent);
        } catch (Exception e) {
            // TODO handle parse error
            Log.d("MeetingInfo", "Exception: " + e.getMessage());
        }
    }

    @Override
    public void onScroll(
            AbsListView absListView,
            int firstVisibleIndex,
            int visibleItemCount,
            int total) {
        int totalItems = firstVisibleIndex + visibleItemCount;
        if (totalItems == total && !isSendingRequest) {
            if (model != null && total < model.size()) {
                return;
            }
            showMoreMeetingInfoData();
        }
    }

    @Override
    public void onClick(View v) {
        if (listView != null) {
            // instant top
//            listView.setSelectionAfterHeaderView();
            // smooth scroll
            listView.smoothScrollToPosition(0);
        }
    }

    public MeetingInfoFragment setModel(ArrayList<NiciEventInfo> model) {
        this.model = model; return this;
    }

    public MeetingInfoFragment setTotal(Integer total) {
        this.total = (total == null ? 0 : total);
        return this;
    }

    public MeetingInfoFragment setCurrentPageCount(int currentPageCount) {
        this.currentPageCount = currentPageCount; return this;
    }

    private void showMoreMeetingInfoData() {
        if (total <= 0) {
            return;
        }
        if (currentPageCount * DEFAULT_SHOW_MORE_DATA_COUNT >= total) {
            return;
        }
        if (isSendingRequest) {
            return;
        }

        // set safety flags and start timer
        // id is only checked when receiving error events at the moment
        startRequestTimer();
        setRequestFlags();
        EventBus.getDefault().post(new MeetingInfoDataRequestEvent(
                currentRequestId, DEFAULT_SHOW_MORE_DATA_COUNT));
    }

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

    private void reload() {
        isSendingRequest = true;
        currentRequestId = MeetingInfoModelFragment.FIRST_REQUEST_ID;
        adapter.clear();
        total = 0;
        currentPageCount = 0;
        // will be using the pull down refresh icon when reloading
        setShowMoreMeetingBtnProgressBar(false, false);
        setListHeader(false);
        startRequestTimer();
        if (getActivity() != null &&
                getActivity() instanceof NICIMainActivity) {
            ((NICIMainActivity) getActivity()).reloadCurrentModel();
        }
    }

    private void setRequestFlags() {
        isSendingRequest = true;
        currentRequestId = RandomStringGenerator.getString(DEFAULT_EVENT_ID_LENGTH);
        setShowMoreMeetingBtnProgressBar(false, true);
    }

    private void clearRequestFlags() {
        isSendingRequest = false;
        currentRequestId = null;
        setShowMoreMeetingBtnProgressBar(true, false);
        setListHeader(true);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setListHeader(final boolean isVisible) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (headerLayout != null) {
                    headerLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                }
            }
        });
    }

    private void setShowMoreMeetingBtnProgressBar(
            final boolean labelVisible, final boolean progressBarVisible) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (showMoreMeetingInfoLabel != null) {
                    showMoreMeetingInfoLabel.setVisibility(labelVisible ? View.VISIBLE : View.GONE);
                }
                if (showMoreMeetingInfoProgress != null) {
                    showMoreMeetingInfoProgress.setVisibility(
                            progressBarVisible ? View.VISIBLE : View.GONE);
                }
            }
        });
    }

    private void setShowMoreLabelText(final String text) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (showMoreMeetingInfoLabel != null) {
                    showMoreMeetingInfoLabel.setText(text);
                }
            }
        });
    }

    private Runnable requestTimer = new Runnable() {
        @Override
        public void run() {
            Log.d("MeetingInfo", "Request Timeout");
            makeShortToast(R.string.request_timeout);
            clearRequestFlags();
        }
    };

    private void makeShortToast(int resourceId) {
        if (getActivity() == null) {
            return;
        }
        Toast.makeText(
                getContext(),
                getString(resourceId),
                Toast.LENGTH_SHORT).show();
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
}
