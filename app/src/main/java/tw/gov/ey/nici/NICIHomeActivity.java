package tw.gov.ey.nici;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class NICIHomeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nici_home);

        // initiate action bar
        Toolbar actionBar = (Toolbar) findViewById(R.id.home_action_bar);
        setSupportActionBar(actionBar);

        // set title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }

        Button showIntroBtn = (Button) findViewById(R.id.show_intro_btn);
        Button showProjectBtn = (Button) findViewById(R.id.show_project_btn);
        Button showMeetingBtn = (Button) findViewById(R.id.show_meeting_btn);
        Button showInfoBtn = (Button) findViewById(R.id.show_info_btn);
        if (showIntroBtn != null) {
            showIntroBtn.setOnClickListener(this);
        }
        if (showProjectBtn != null) {
            showProjectBtn.setOnClickListener(this);
        }
        if (showMeetingBtn != null) {
            showMeetingBtn.setOnClickListener(this);
        }
        if (showInfoBtn != null) {
            showInfoBtn.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        Intent showMainIntent = new Intent(this, NICIMainActivity.class);
        String pageType;
        switch(v.getId()) {
            case R.id.show_intro_btn:
                pageType = NICIMainActivity.PageType.INTRO.name();
                break;
            case R.id.show_project_btn:
                pageType = NICIMainActivity.PageType.PROJECT.name();
                break;
            case R.id.show_meeting_btn:
                pageType = NICIMainActivity.PageType.MEETING.name();
                break;
            case R.id.show_info_btn:
                pageType = NICIMainActivity.PageType.INFO.name();
                break;
            default:
                pageType = NICIMainActivity.PageType.INTRO.name();
        }
        showMainIntent.putExtra(NICIMainActivity.PAGE_TYPE_KEY, pageType);
        startActivity(showMainIntent);
    }
}
