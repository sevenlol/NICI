package tw.gov.ey.nici.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import tw.gov.ey.nici.R;
import tw.gov.ey.nici.events.InfoDataErrorEvent;
import tw.gov.ey.nici.events.InfoDataReadyEvent;
import tw.gov.ey.nici.events.InfoDataRequestEvent;
import tw.gov.ey.nici.models.NiciInfo;
import tw.gov.ey.nici.utils.RandomStringGenerator;

public class InfoFragment extends Fragment implements View.OnClickListener {
    public static final int DEFAULT_SHOW_MORE_DATA_COUNT = 3;
    public static final int DEFAULT_EVENT_ID_LENGTH = 20;

    private Button showMoreInfoBtn = null;
    private ProgressBar showMoreInfoProgress = null;

    private int total = 0;
    private ArrayList<NiciInfo> model = null;

    private boolean isSendingRequest = false;
    private String currentRequestId = null;

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
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.info_fragment, container, false);
        showMoreInfoBtn = (Button) root.findViewById(R.id.show_more_info_btn);
        if (showMoreInfoBtn != null) {
            showMoreInfoBtn.setOnClickListener(this);
        }
        showMoreInfoProgress = (ProgressBar) root.findViewById(R.id.show_more_info_progress);
        if (model != null && model.size() > 0) {
            // some data is already loaded
            setShowMoreInfoBtnProgressBar(true, false);

            if (model.size() >= total) {
                setShowMoreBtnEnabled(false);
            }
        }
        return root;
    }

    // receive new info data
    @Subscribe
    public void onEventMainThread(InfoDataReadyEvent event) {
        // update total and model
        // and set show more btn status
        if (event != null) {
            if (event.getTotal() >= 0) {
                total = event.getTotal();
            }

            if (model != null && event.getInfoList() != null) {
                for (int i = 0; i < event.getInfoList().size(); i++) {
                    model.add(event.getInfoList().get(i));
                }

                // set show more btn availablity
                if (model.size() >= total) {
                    setShowMoreBtnEnabled(false);
                }
            }
        }

        // TODO add id verification for data ready event
        clearRequestFlags();
    }

    // info data request failed
    @Subscribe
    public void onEventMainThread(InfoDataErrorEvent event) {
        Log.d("Info Event", "Data Request Error");
        if (event == null) {
            return;
        }

        // the request id is not null (not the first request)
        // and the id is not matched, exit
        if (currentRequestId != null && !currentRequestId.equals(event.getId())) {
            return;
        }

        clearRequestFlags();
    }

    @Override
    public void onClick(View v) {
        showMoreInfoData(v);
    }

    public InfoFragment setModel(ArrayList<NiciInfo> model) {
        Log.d("Info Event", "Set Model: " + (model == null ? 0 : model.size()));
        this.model = model; return this;
    }

    public InfoFragment setTotal(Integer total) {
        this.total = (total == null ? 0 : total);
        return this;
    }

    private void showMoreInfoData(View view) {
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

        // set safety flags
        // id is only checked when receiving error events at the moment
        setRequestFlags();
        EventBus.getDefault().post(new InfoDataRequestEvent(
                currentRequestId, DEFAULT_SHOW_MORE_DATA_COUNT));
    }

    // TODO  add a timer thread for request timeout
    private void setRequestFlags() {
        isSendingRequest = true;
        currentRequestId = RandomStringGenerator.getString(DEFAULT_EVENT_ID_LENGTH);
        setShowMoreInfoBtnProgressBar(false, true);
    }

    // TODO cancel the timer thread
    private void clearRequestFlags() {
        isSendingRequest = false;
        currentRequestId = null;
        setShowMoreInfoBtnProgressBar(true, false);
    }

    private void setShowMoreInfoBtnProgressBar(
            final boolean btnVisible, final boolean progressBarVisible) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (showMoreInfoBtn != null) {
                    showMoreInfoBtn.setVisibility(btnVisible ? View.VISIBLE : View.GONE);
                }
                if (showMoreInfoProgress != null) {
                    showMoreInfoProgress.setVisibility(
                            progressBarVisible ? View.VISIBLE : View.GONE);
                }
            }
        });
    }

    private void setShowMoreBtnEnabled(final boolean btnEnabled) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (showMoreInfoBtn != null) {
                    showMoreInfoBtn.setEnabled(btnEnabled);
                    if (!btnEnabled) {
                        showMoreInfoBtn.setText(getString(R.string.no_more_info));
                    } else {
                        showMoreInfoBtn.setText(getString(R.string.show_more_info));
                    }
                }
            }
        });
    }
}
