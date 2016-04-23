package tw.gov.ey.nici;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import tw.gov.ey.nici.fragments.MeetingInfoDetailFragment;
import tw.gov.ey.nici.network.NiciClient;
import tw.gov.ey.nici.network.NiciClientFactory;

public class MeetingInfoDetailActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String NICI_EVENT_INFO_ID_KEY = "meeting_info_detail_nici_event_info_id";
    public static final String MEETING_INFO_DETAIL_TITLE_KEY = "meeting_info_detail_nici_event_title";

    private NiciClient niciClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_info_detail_activity);

        // get client
        niciClient = NiciClientFactory.getClient(NiciClientFactory.ClientType.TESTING);

        // initiate action bar
        Toolbar actionBar = (Toolbar) findViewById(R.id.meeting_info_detail_action_bar);
        setSupportActionBar(actionBar);

        // set close icon and title
        if (getSupportActionBar() != null) {
            String title = getIntent() == null ?
                    null : getIntent().getExtras() == null ?
                    null : getIntent().getExtras().getString(MEETING_INFO_DETAIL_TITLE_KEY);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(
                    title == null ? getString(R.string.meeting_info_detail_title) : title);
        }

        // set close icon on click listener
        if (actionBar != null) {
            actionBar.setNavigationOnClickListener(this);
        }

        String eventInfoId = getIntent() == null ?
                null : getIntent().getExtras() == null ?
                null : getIntent().getExtras().getString(NICI_EVENT_INFO_ID_KEY);
        if (getSupportFragmentManager().findFragmentById(R.id.meeting_info_detail_fragment) == null &&
                eventInfoId != null && niciClient != null) {
            Log.d("MeetingInfoDetail", "Event Info ID: " + eventInfoId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.meeting_info_detail_fragment, MeetingInfoDetailFragment.newInstance(niciClient, eventInfoId))
                    .commit();
        }
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    public void hideBar() {
        if (getSupportActionBar() != null && getSupportActionBar().isShowing()) {
            getSupportActionBar().hide();
        }
    }

    public void showBar() {
        if (getSupportActionBar() != null && !getSupportActionBar().isShowing()) {
            getSupportActionBar().show();
        }
    }
}
