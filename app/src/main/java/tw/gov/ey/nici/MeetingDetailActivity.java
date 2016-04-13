package tw.gov.ey.nici;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import tw.gov.ey.nici.fragments.MeetingDetailFragment;

public class MeetingDetailActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String NICI_EVENT_ID_KEY = "meeting_detail_nici_event_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_detail_activity);

        // initiate action bar
        Toolbar actionBar = (Toolbar) findViewById(R.id.meeting_action_bar);
        setSupportActionBar(actionBar);

        // set close icon and title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.meeting_detail_title));
        }

        // set close icon on click listener
        if (actionBar != null) {
            actionBar.setNavigationOnClickListener(this);
        }

        String eventId = getIntent() == null ?
                null : getIntent().getExtras() == null ?
                null : getIntent().getExtras().getString(NICI_EVENT_ID_KEY);
        if (getSupportFragmentManager().findFragmentById(R.id.meeting_detail_fragment) == null &&
            eventId != null) {
            Log.d("MeetingDetailActivity", "Event ID: " + eventId);
            getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.meeting_detail_fragment, MeetingDetailFragment.newInstance(eventId))
                .commit();
        }
    }

    @Override
    public void onClick(View view) {
        finish();
    }
}
