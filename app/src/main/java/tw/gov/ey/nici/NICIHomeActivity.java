package tw.gov.ey.nici;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.redbooth.WelcomeCoordinatorLayout;
import com.redbooth.WelcomePageLayout;

public class NICIHomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String FACEBOOK_PAGE_URL = "https://www.facebook.com/dcoffice";

    private RelativeLayout homeContainer = null;
    private WelcomeCoordinatorLayout welcomePageContainer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nici_home);

        // get containers
        homeContainer = (RelativeLayout) findViewById(R.id.home_content);
        welcomePageContainer = (WelcomeCoordinatorLayout) findViewById(R.id.welcome_page_container);

        // initiate action bar
        Toolbar actionBar = (Toolbar) findViewById(R.id.home_action_bar);
        setSupportActionBar(actionBar);

        // set title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }

        FloatingActionButton showIntroBtn = (FloatingActionButton) findViewById(R.id.show_intro_btn);
        FloatingActionButton showProjectBtn = (FloatingActionButton) findViewById(R.id.show_project_btn);
        FloatingActionButton showMeetingBtn = (FloatingActionButton) findViewById(R.id.show_meeting_btn);
        FloatingActionButton showMeetingInfoBtn = (FloatingActionButton) findViewById(R.id.show_meeting_info_btn);
        FloatingActionButton showInfoBtn = (FloatingActionButton) findViewById(R.id.show_info_btn);
        FloatingActionButton showFacebookBtn = (FloatingActionButton) findViewById(R.id.show_facebook_btn);
        if (showIntroBtn != null) {
            showIntroBtn.setOnClickListener(this);
        }
        if (showProjectBtn != null) {
            showProjectBtn.setOnClickListener(this);
        }
        if (showMeetingBtn != null) {
            showMeetingBtn.setOnClickListener(this);
        }
        if (showMeetingInfoBtn != null) {
            showMeetingInfoBtn.setOnClickListener(this);
        }
        if (showInfoBtn != null) {
            showInfoBtn.setOnClickListener(this);
        }
        if (showFacebookBtn != null) {
            showFacebookBtn.setOnClickListener(this);
        }

        // init welcome page
        initWelcomePage();
    }

    @Override
    public void onClick(View v) {
        Intent showMainIntent = new Intent(this, NICIMainActivity.class);
        String pageType;
        switch(v.getId()) {
            case R.id.welcome_page:
                // welcome page clicked, show home icons and action bar
                Log.d("Home", "Welcome Page Clicked");
                showHomeContent();
                return;
            case R.id.show_intro_btn:
                pageType = NICIMainActivity.PageType.INTRO.name();
                break;
            case R.id.show_project_btn:
                pageType = NICIMainActivity.PageType.PROJECT.name();
                break;
            case R.id.show_meeting_btn:
                pageType = NICIMainActivity.PageType.MEETING.name();
                break;
            case R.id.show_meeting_info_btn:
                pageType = NICIMainActivity.PageType.MEETING_INFO.name();
                break;
            case R.id.show_info_btn:
                pageType = NICIMainActivity.PageType.INFO.name();
                break;
            case R.id.show_facebook_btn:
                pageType = NICIMainActivity.PageType.FACEBOOK.name();
                break;
            default:
                pageType = NICIMainActivity.PageType.INTRO.name();
        }
        showMainIntent.putExtra(NICIMainActivity.PAGE_TYPE_KEY, pageType);
        startActivity(showMainIntent);
    }

    public void showFacebookPage(View v) {
        // show facebook page
        Intent facebookPageIntent = new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse(FACEBOOK_PAGE_URL));
        startActivity(facebookPageIntent);
    }

    private void initWelcomePage() {
        // hide action bar and home page
        if (homeContainer != null) {
            homeContainer.setVisibility(View.GONE);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // init welcome page
        if (welcomePageContainer != null) {
            welcomePageContainer.addPage(R.layout.welcome_page);
            welcomePageContainer.setScrollingEnabled(false);
            welcomePageContainer.showIndicators(false);
        }
        WelcomePageLayout welcomePage = (WelcomePageLayout) findViewById(R.id.welcome_page);
        if (welcomePage != null) {
            welcomePage.setOnClickListener(this);
        }
    }

    private void showHomeContent() {
        if (homeContainer != null) {
            homeContainer.setVisibility(View.VISIBLE);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
        if (welcomePageContainer != null) {
            welcomePageContainer.setVisibility(View.GONE);
        }
    }
}
