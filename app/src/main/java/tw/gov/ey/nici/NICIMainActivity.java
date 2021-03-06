package tw.gov.ey.nici;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import tw.gov.ey.nici.fragments.FacebookFragment;
import tw.gov.ey.nici.fragments.InfoFragment;
import tw.gov.ey.nici.fragments.InfoModelFragment;
import tw.gov.ey.nici.fragments.IntroFragment;
import tw.gov.ey.nici.fragments.IntroModelFragment;
import tw.gov.ey.nici.fragments.MeetingFragment;
import tw.gov.ey.nici.fragments.MeetingInfoFragment;
import tw.gov.ey.nici.fragments.MeetingInfoModelFragment;
import tw.gov.ey.nici.fragments.MeetingModelFragment;
import tw.gov.ey.nici.fragments.ProjectFragment;
import tw.gov.ey.nici.fragments.ProjectModelFragment;
import tw.gov.ey.nici.network.NiciClient;
import tw.gov.ey.nici.network.NiciClientFactory;

public class NICIMainActivity extends AppCompatActivity
        implements OnMenuTabClickListener, View.OnClickListener {
    public static final String PAGE_TYPE_KEY = "niciPageTypeKey";
    public static final String INTRO_MODEL_FRAGMENT_TAG = "niciIntroModelFragment";
    public static final String PROJECT_MODEL_FRAGMENT_TAG = "niciProjectModelFragment";
    public static final String MEETING_MODEL_FRAGMENT_TAG = "niciMeetingModelFragment";
    public static final String MEETING_INFO_MODEL_FRAGMENT_TAG = "niciMeetingInfoModelFragment";
    public static final String INFO_MODEL_FRAGMENT_TAG = "niciInfoModelFragment";

    private static final String FACEBOOK_PAGE_URL = "https://www.facebook.com/dcoffice";

    public enum PageType {
        INTRO(0), PROJECT(1), MEETING(2), MEETING_INFO(3),
        INFO(4), FACEBOOK(5);

        public final int position;
        private PageType(int position) {
            this.position = position;
        }
    }

    private BottomBar mBottomBar;
    private NiciClient niciTestClient;
    private NiciClient niciServerClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nici_main);

        // get nici client
        niciTestClient = NiciClientFactory.getClient(NiciClientFactory.ClientType.TESTING);
        niciServerClient = NiciClientFactory.getClient(NiciClientFactory.ClientType.SERVER);

        // initiate action bar
        Toolbar actionBar = (Toolbar) findViewById(R.id.main_action_bar);
        setSupportActionBar(actionBar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.home_icon_white);
            getSupportActionBar().setShowHideAnimationEnabled(true);
        }

        // set close icon on click listener
        if (actionBar != null) {
            actionBar.setNavigationOnClickListener(this);
        }

        // initiate bottom bar
        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.useOnlyStatusBarTopOffset();
        mBottomBar.useFixedMode();
        mBottomBar.useDarkTheme();
        mBottomBar.setActiveTabColor(ContextCompat.getColor(this, R.color.activeBottomBarColor));
        mBottomBar.setItemsFromMenu(R.menu.bottom_bar_menu, this);

        // set tab's background color
        mBottomBar.mapColorForTab(PageType.INTRO.position, ContextCompat.getColor(this, R.color.bottomBarIntro));
        mBottomBar.mapColorForTab(PageType.PROJECT.position, ContextCompat.getColor(this, R.color.bottomBarProject));
        mBottomBar.mapColorForTab(PageType.MEETING.position, ContextCompat.getColor(this, R.color.bottomBarMeeting));
        mBottomBar.mapColorForTab(PageType.MEETING_INFO.position, ContextCompat.getColor(this, R.color.bottomBarInfo));
        mBottomBar.mapColorForTab(PageType.INFO.position, ContextCompat.getColor(this, R.color.bottomBarMeeting));
        mBottomBar.mapColorForTab(PageType.FACEBOOK.position, ContextCompat.getColor(this, R.color.bottomBarInfo));

        // setup the default tab
        // Note: the default position has to be set before setting background colors
        //       for some reason, otherwise the color won't be changed in the first time
        if (getSupportFragmentManager().findFragmentById(R.id.main_fragment) == null) {
            int position = PageType.INTRO.position;
            if (getIntent() != null && getIntent().getExtras() != null &&
                    getIntent().getExtras().getString(PAGE_TYPE_KEY) != null) {
                PageType type = null;
                try {
                    type = PageType.valueOf(getIntent().getExtras().getString(PAGE_TYPE_KEY));
                } catch (Exception e) {}

                switch (type) {
                    case INTRO:
                    case PROJECT:
                    case MEETING:
                    case MEETING_INFO:
                    case INFO:
                    case FACEBOOK:
                        position = type.position;
                        break;
                    default:
                        position = PageType.INTRO.position;
                }
            }
            mBottomBar.setDefaultTabPosition(position);
        }

        // update model for orientation change
        updateModelForCurrentFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mBottomBar.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingIntent = new Intent(this, SettingActivity.class);
                startActivity(settingIntent);
                return true;
            case R.id.action_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void hideBars() {
        if (mBottomBar != null && mBottomBar.isShown()) {
            mBottomBar.hide();
        }
        if (getSupportActionBar() != null && getSupportActionBar().isShowing()) {
            getSupportActionBar().hide();
        }
    }

    public void showBars() {
        if (mBottomBar != null) {
            mBottomBar.show();
        }
        if (getSupportActionBar() != null && !getSupportActionBar().isShowing()) {
            getSupportActionBar().show();
        }
    }

    @Override
    public void onMenuTabSelected(@IdRes int menuItemId) {
        switch(menuItemId) {
            case R.id.bottomBarIntro:
                // set model
                IntroModelFragment introModelFragment = (IntroModelFragment)
                        getFragmentFromTag(INTRO_MODEL_FRAGMENT_TAG);
                if (introModelFragment == null) {
                    introModelFragment = IntroModelFragment.newInstance(niciServerClient);
                    getSupportFragmentManager()
                        .beginTransaction()
                        .add(introModelFragment, INTRO_MODEL_FRAGMENT_TAG)
                        .commit();
                }
                replaceCurrentFragment(IntroFragment.newInstance()
                    .setModel(introModelFragment.getModel()));
                setActionBarTitle(R.string.intro_page_title);
                break;
            case R.id.bottomBarProject:
                // set model
                ProjectModelFragment projectModelFragment = (ProjectModelFragment)
                        getFragmentFromTag(PROJECT_MODEL_FRAGMENT_TAG);
                if (projectModelFragment == null) {
                    projectModelFragment = ProjectModelFragment.newInstance(niciServerClient);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(projectModelFragment, PROJECT_MODEL_FRAGMENT_TAG)
                            .commit();
                }
                replaceCurrentFragment(ProjectFragment.newInstance()
                        .setModel(projectModelFragment.getModel()));
                setActionBarTitle(R.string.project_page_title);
                break;
            case R.id.bottomBarMeeting:
                MeetingModelFragment meetingModelFragment = (MeetingModelFragment)
                        getFragmentFromTag(MEETING_MODEL_FRAGMENT_TAG);
                if (meetingModelFragment == null) {
                    meetingModelFragment = MeetingModelFragment.newInstance(niciServerClient);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(meetingModelFragment, MEETING_MODEL_FRAGMENT_TAG)
                            .commit();
                }
                replaceCurrentFragment(MeetingFragment.newInstance()
                        .setModel(meetingModelFragment.getModel())
                        .setTotal(meetingModelFragment.getTotal())
                        .setCurrentPageCount(meetingModelFragment.getCurrentPageCount()));
                setActionBarTitle(R.string.meeting_page_title);
                break;
            case R.id.bottomBarMeetingInfo:
                MeetingInfoModelFragment meetingInfoModelFragment = (MeetingInfoModelFragment)
                        getFragmentFromTag(MEETING_INFO_MODEL_FRAGMENT_TAG);
                if (meetingInfoModelFragment == null) {
                    meetingInfoModelFragment = MeetingInfoModelFragment.newInstance(niciServerClient);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(meetingInfoModelFragment, MEETING_INFO_MODEL_FRAGMENT_TAG)
                            .commit();
                }
                replaceCurrentFragment(MeetingInfoFragment.newInstance()
                    .setModel(meetingInfoModelFragment.getModel())
                    .setTotal(meetingInfoModelFragment.getTotal())
                    .setCurrentPageCount(meetingInfoModelFragment.getCurrentPageCount()));
                setActionBarTitle(R.string.meeting_info_page_title);
                break;
            case R.id.bottomBarInfo:
                // set model
                InfoModelFragment infoModelFragment = (InfoModelFragment)
                        getFragmentFromTag(INFO_MODEL_FRAGMENT_TAG);
                if (infoModelFragment == null) {
                    infoModelFragment = InfoModelFragment.newInstance(niciServerClient);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(infoModelFragment, INFO_MODEL_FRAGMENT_TAG)
                            .commit();
                }
                replaceCurrentFragment(InfoFragment.newInstance()
                        .setModel(infoModelFragment.getModel())
                        .setTotal(infoModelFragment.getTotal())
                        .setCurrentPageCount(infoModelFragment.getCurrentPageCount()));
                setActionBarTitle(R.string.info_page_title);
                break;
            case R.id.bottomBarFacebook:
                replaceCurrentFragment(FacebookFragment.newInstance(FACEBOOK_PAGE_URL));
                setActionBarTitle(R.string.facebook_page_title);
                break;
            default:
        }
    }

    @Override
    public void onMenuTabReSelected(int menuItemId) {
        // do nothing at the moment
    }

    // remove the current model fragment and create a new one
    public void reloadCurrentModel() {
        switch (mBottomBar.getCurrentTabPosition()) {
            case 0:
                IntroModelFragment introModelFragment = (IntroModelFragment)
                        getFragmentFromTag(INTRO_MODEL_FRAGMENT_TAG);
                if (introModelFragment != null) {
                    getSupportFragmentManager()
                        .beginTransaction()
                        .remove(introModelFragment)
                        .commit();
                }
                introModelFragment = IntroModelFragment.newInstance(niciServerClient);
                getSupportFragmentManager()
                    .beginTransaction()
                    .add(introModelFragment, INTRO_MODEL_FRAGMENT_TAG)
                    .commit();
                break;
            case 1:
                ProjectModelFragment projectModelFragment = (ProjectModelFragment)
                        getFragmentFromTag(PROJECT_MODEL_FRAGMENT_TAG);
                if (projectModelFragment != null) {
                    getSupportFragmentManager()
                        .beginTransaction()
                        .remove(projectModelFragment)
                        .commit();
                }
                projectModelFragment = ProjectModelFragment.newInstance(niciServerClient);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(projectModelFragment, PROJECT_MODEL_FRAGMENT_TAG)
                        .commit();
                break;
            case 2:
                MeetingModelFragment meetingModelFragment = (MeetingModelFragment)
                        getFragmentFromTag(MEETING_MODEL_FRAGMENT_TAG);
                if (meetingModelFragment != null) {
                    getSupportFragmentManager()
                        .beginTransaction()
                        .remove(meetingModelFragment)
                        .commit();
                }
                meetingModelFragment = MeetingModelFragment.newInstance(niciServerClient);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(meetingModelFragment, MEETING_MODEL_FRAGMENT_TAG)
                        .commit();
                break;
            case 3:
                MeetingInfoModelFragment meetingInfoModelFragment = (MeetingInfoModelFragment)
                        getFragmentFromTag(MEETING_INFO_MODEL_FRAGMENT_TAG);
                if (meetingInfoModelFragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .remove(meetingInfoModelFragment)
                            .commit();
                }
                meetingInfoModelFragment = MeetingInfoModelFragment.newInstance(niciServerClient);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(meetingInfoModelFragment, MEETING_INFO_MODEL_FRAGMENT_TAG)
                        .commit();
                break;
            case 4:
                InfoModelFragment infoModelFragment = (InfoModelFragment)
                        getFragmentFromTag(INFO_MODEL_FRAGMENT_TAG);
                if (infoModelFragment != null) {
                    getSupportFragmentManager()
                        .beginTransaction()
                        .remove(infoModelFragment)
                        .commit();
                }
                infoModelFragment = InfoModelFragment.newInstance(niciServerClient);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(infoModelFragment, INFO_MODEL_FRAGMENT_TAG)
                        .commit();
                break;
            case 5:
                break;
            default:
        }
    }

    private void replaceCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, fragment)
                .commit();
    }

    private void setActionBarTitle(int stringId) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(stringId);
        }
    }

    private Fragment getFragmentFromTag(String tag) {
        return getSupportFragmentManager()
                .findFragmentByTag(tag);
    }

    private void updateModelForCurrentFragment() {
        switch (mBottomBar.getCurrentTabPosition()) {
            case 0:
                IntroModelFragment introModelFragment = (IntroModelFragment) getSupportFragmentManager()
                        .findFragmentByTag(INTRO_MODEL_FRAGMENT_TAG);
                IntroFragment introFragment = (IntroFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.main_fragment);
                if (introFragment != null && introModelFragment != null) {
                    introFragment.setModel(introModelFragment.getModel());
                }
                break;
            case 1:
                ProjectModelFragment projectModelFragment = (ProjectModelFragment) getSupportFragmentManager()
                        .findFragmentByTag(PROJECT_MODEL_FRAGMENT_TAG);
                ProjectFragment projectFragment = (ProjectFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.main_fragment);
                if (projectFragment != null && projectModelFragment != null) {
                    projectFragment.setModel(projectModelFragment.getModel());
                }
                break;
            case 2:
                MeetingModelFragment meetingModelFragment = (MeetingModelFragment) getSupportFragmentManager()
                        .findFragmentByTag(MEETING_MODEL_FRAGMENT_TAG);
                MeetingFragment meetingFragment = (MeetingFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.main_fragment);
                if (meetingFragment != null && meetingModelFragment != null) {
                    meetingFragment.setModel(meetingModelFragment.getModel());
                }
                break;
            case 3:
                MeetingInfoModelFragment meetingInfoModelFragment = (MeetingInfoModelFragment) getSupportFragmentManager()
                        .findFragmentByTag(MEETING_INFO_MODEL_FRAGMENT_TAG);
                MeetingInfoFragment meetingInfoFragment = (MeetingInfoFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.main_fragment);
                if (meetingInfoFragment != null && meetingInfoModelFragment != null) {
                    meetingInfoFragment.setModel(meetingInfoModelFragment.getModel());
                }
                break;
            case 4:
                InfoModelFragment infoModelFragment = (InfoModelFragment) getSupportFragmentManager()
                        .findFragmentByTag(INFO_MODEL_FRAGMENT_TAG);
                InfoFragment infoFragment = (InfoFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.main_fragment);
                if (infoModelFragment != null && infoFragment != null) {
                    infoFragment.setModel(infoModelFragment.getModel());
                }
                break;
            case 5:
                break;
            default:
        }
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
