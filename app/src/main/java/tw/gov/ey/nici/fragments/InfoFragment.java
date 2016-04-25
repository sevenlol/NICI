package tw.gov.ey.nici.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import tw.gov.ey.nici.NICIMainActivity;
import tw.gov.ey.nici.R;
import tw.gov.ey.nici.events.InfoDataErrorEvent;
import tw.gov.ey.nici.events.InfoDataReadyEvent;
import tw.gov.ey.nici.events.InfoDataRequestEvent;
import tw.gov.ey.nici.models.NiciInfo;
import tw.gov.ey.nici.utils.RandomStringGenerator;
import tw.gov.ey.nici.views.NiciInfoAdapter;

public class InfoFragment extends Fragment implements ListView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, ListView.OnScrollListener, View.OnClickListener {
    public static final int DEFAULT_SHOW_MORE_DATA_COUNT = 3;
    public static final int DEFAULT_EVENT_ID_LENGTH = 20;
    public static final int DEFAULT_REQUEST_TIMEOUT = 5000;
    public static final boolean DEFAULT_SCROLL_DETECTION_ENABLED = false;

    private Handler handler = new Handler();

    private SwipeRefreshLayout swipeRefreshLayout = null;
    private TextView showMoreInfoLabel = null;
    private ProgressBar showMoreInfoProgress = null;
    private ListView listView = null;
    private FloatingActionButton scrollToTopBtn = null;

    private int total = 0;
    private ArrayAdapter<NiciInfo> adapter = null;
    private ArrayList<NiciInfo> model = null;

    private boolean isSendingRequest = true;
    private String currentRequestId = InfoModelFragment.FIRST_REQUEST_ID;

    private boolean scrollDetectionEnabled = DEFAULT_SCROLL_DETECTION_ENABLED;
    private int lastFirstVisibleItem = 0;

    public static InfoFragment newInstance() {
        return new InfoFragment();
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
        View root = inflater.inflate(R.layout.info_fragment, container, false);

        // set the swipe refresh layout
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_info);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(this);
        }

        // set the scroll to top button
        scrollToTopBtn = (FloatingActionButton) root.findViewById(R.id.scroll_to_top_btn);
        if (scrollToTopBtn != null) {
            scrollToTopBtn.setOnClickListener(this);
        }

        // inflate footer layout
        FrameLayout footerLayout = (FrameLayout) LayoutInflater.from(getContext())
                .inflate(R.layout.list_footer, null);
        showMoreInfoLabel = (TextView) footerLayout.findViewById(R.id.show_more_label);
        showMoreInfoProgress = (ProgressBar) footerLayout.findViewById(R.id.show_more_progress);
        if (model != null && model.size() > 0) {
            // some data is already loaded
            setShowMoreInfoBtnProgressBar(true, false);

            if (model.size() >= total) {
                setShowMoreLabelText(getString(R.string.no_more_info));
            }

            // if some data is already loaded, not sending request
            clearRequestFlags();
        }

        // start request timer in case the first request timeout
        if (model != null && model.size() == 0) {
            startRequestTimer();
        }

        // set listview and adapter
        adapter = new NiciInfoAdapter(getActivity(), model);
        listView = (ListView) root.findViewById(R.id.info_list);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);
        listView.setAdapter(adapter);
        listView.addFooterView(footerLayout);

        return root;
    }

    // receive new info data
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(InfoDataReadyEvent event) {
        // update total and model
        // and set show more btn status
        if (event != null) {
            if (event.getTotal() >= 0) {
                total = event.getTotal();
            }

            if (adapter != null && event.getInfoList() != null) {
                for (int i = 0; i < event.getInfoList().size(); i++) {
                    adapter.add(event.getInfoList().get(i));
                }

                // set show more btn availablity
                if (model.size() >= total) {
                    setShowMoreLabelText(getString(R.string.no_more_info));
                }
            }
        }

        // TODO add id verification for data ready event
        // stop request timer and clear flags
        stopRequestTimer();
        clearRequestFlags();
    }

    // info data request failed
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(InfoDataErrorEvent event) {
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

        if (InfoModelFragment.FIRST_REQUEST_ID.equals(currentRequestId)) {
            // TODO handle the first request failing
            // TODO disable show more info action and only allow reload
            // TODO show error text
        }

        // notify user request failed
        makeShortToast(R.string.request_failed);

        // stop request timer and clear flags
        stopRequestTimer();
        clearRequestFlags();
    }

    @Override
    // info item clicked
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Info Event", "Item Position: " + position);
        if (model == null || model.size() <= position) {
            return;
        }

        NiciInfo info = model.get(position);
        if (info == null || info.getLinkUrl() == null) {
            return;
        }
        try {
            String linkUrl = info.getLinkUrl();
            if (!linkUrl.startsWith("http") && !linkUrl.startsWith("https")) {
                linkUrl = "http://" + linkUrl;
            }
            Uri uri = Uri.parse(linkUrl);
            Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            viewIntent.setData(uri);
            startActivity(viewIntent);
        } catch (Exception e) {
            Toast.makeText(
                    getContext(),
                    getString(R.string.view_link_failed),
                    Toast.LENGTH_SHORT).show();
        }
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
                Log.d("Info", "Scroll Down");
                // prevent the glitch at the top of the list
                if (lastFirstVisibleItem != 0 || currentFirstVisibleItem != 1) {
                    showHideBars(false);
                }
            } else {
                Log.d("Info", "Scroll Up");
                showHideBars(true);
            }
            lastFirstVisibleItem = currentFirstVisibleItem;
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
            showMoreInfoData();
        }
    }

    @Override
    public void onClick(View v) {
        if (listView != null) {
            // instant top
            listView.setSelectionAfterHeaderView();
            // smooth scroll
//            listView.smoothScrollToPosition(0);
        }
    }

    public InfoFragment setModel(ArrayList<NiciInfo> model) {
        Log.d("Info Event", "Set Model: " + (model == null ? 0 : model.size()));
        this.model = model; return this;
    }

    public InfoFragment setTotal(Integer total) {
        this.total = (total == null ? 0 : total);
        return this;
    }

    private void showMoreInfoData() {
        Log.d("Info Event", "Total: " + total);
        Log.d("Info Event", "IsSendingReq: " + isSendingRequest);
        Log.d("Info Event", "Show More Info Data: " + (model == null ? 0 : model.size()));
        if (total <= 0) {
            return;
        }
        if (model != null && model.size() >= total) {
            return;
        }
        if (isSendingRequest) {
            return;
        }

        // set safety flags and start timer
        // id is only checked when receiving error events at the moment
        startRequestTimer();
        setRequestFlags();
        EventBus.getDefault().post(new InfoDataRequestEvent(
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
        currentRequestId = InfoModelFragment.FIRST_REQUEST_ID;
        adapter.clear();
        total = 0;
        // will be using the pull down refresh icon when reloading
        setShowMoreInfoBtnProgressBar(false, false);
        startRequestTimer();
        if (getActivity() != null &&
                getActivity() instanceof NICIMainActivity) {
            ((NICIMainActivity) getActivity()).reloadCurrentModel();
        }
    }

    private void setRequestFlags() {
        isSendingRequest = true;
        currentRequestId = RandomStringGenerator.getString(DEFAULT_EVENT_ID_LENGTH);
        setShowMoreInfoBtnProgressBar(false, true);
    }

    private void clearRequestFlags() {
        isSendingRequest = false;
        currentRequestId = null;
        setShowMoreInfoBtnProgressBar(true, false);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setShowMoreInfoBtnProgressBar(
            final boolean labelVisible, final boolean progressBarVisible) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (showMoreInfoLabel != null) {
                    showMoreInfoLabel.setVisibility(labelVisible ? View.VISIBLE : View.GONE);
                }
                if (showMoreInfoProgress != null) {
                    showMoreInfoProgress.setVisibility(
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
                if (showMoreInfoLabel != null) {
                    showMoreInfoLabel.setText(text);
                }
            }
        });
    }

    private Runnable requestTimer = new Runnable() {
        @Override
        public void run() {
            Log.d("Info", "Request Timeout");
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
