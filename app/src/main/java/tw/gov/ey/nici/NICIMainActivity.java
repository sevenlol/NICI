package tw.gov.ey.nici;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import tw.gov.ey.nici.fragments.InfoFragment;
import tw.gov.ey.nici.fragments.InfoModelFragment;
import tw.gov.ey.nici.fragments.IntroFragment;
import tw.gov.ey.nici.fragments.MeetingFragment;
import tw.gov.ey.nici.fragments.ProjectFragment;
import tw.gov.ey.nici.network.NiciClientFactory;

public class NICIMainActivity extends AppCompatActivity
        implements OnMenuTabClickListener, View.OnClickListener {
    public static final String PAGE_TYPE_KEY = "niciPageTypeKey";
    public static final String INFO_MODEL_FRAGMENT_TAG = "niciInfoModelFragment";
    public enum PageType {
        INTRO(0), PROJECT(1), MEETING(2), INFO(3);

        public final int position;
        private PageType(int position) {
            this.position = position;
        }
    }

    private BottomBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nici_main);

        // initiate action bar
        Toolbar actionBar = (Toolbar) findViewById(R.id.main_action_bar);
        setSupportActionBar(actionBar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);
        }

        // set close icon on click listener
        if (actionBar != null) {
            actionBar.setNavigationOnClickListener(this);
        }

        // initiate bottom bar
        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.useOnlyStatusBarTopOffset();
        mBottomBar.setItemsFromMenu(R.menu.bottom_bar_menu, this);

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
                    case INFO:
                        position = type.position;
                        break;
                    default:
                        position = PageType.INTRO.position;
                }
            }
            mBottomBar.setDefaultTabPosition(position);
        }

        // set tab's background color
        mBottomBar.mapColorForTab(PageType.INTRO.position, ContextCompat.getColor(this, R.color.bottomBarIntro));
        mBottomBar.mapColorForTab(PageType.PROJECT.position, ContextCompat.getColor(this, R.color.bottomBarProject));
        mBottomBar.mapColorForTab(PageType.MEETING.position, ContextCompat.getColor(this, R.color.bottomBarMeeting));
        mBottomBar.mapColorForTab(PageType.INFO.position, ContextCompat.getColor(this, R.color.bottomBarInfo));
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

    @Override
    public void onMenuTabSelected(@IdRes int menuItemId) {
        switch(menuItemId) {
            case R.id.bottomBarIntro:
                replaceCurrentFragment(IntroFragment.newInstance());
                break;
            case R.id.bottomBarProject:
                replaceCurrentFragment(ProjectFragment.newInstance());
                break;
            case R.id.bottomBarMeeting:
                replaceCurrentFragment(MeetingFragment.newInstance());
                break;
            case R.id.bottomBarInfo:

                // set model
                InfoModelFragment infoModelFragment = (InfoModelFragment)
                        getFragmentFromTag(INFO_MODEL_FRAGMENT_TAG);
                if (infoModelFragment == null) {
                    infoModelFragment = InfoModelFragment.newInstance(
                            NiciClientFactory.getClient(NiciClientFactory.ClientType.TESTING));
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(infoModelFragment, INFO_MODEL_FRAGMENT_TAG)
                            .commit();
                }
                replaceCurrentFragment(InfoFragment.newInstance()
                        .setModel(infoModelFragment.getModel())
                        .setTotal(infoModelFragment.getTotal()));
                break;
            default:
        }
    }

    @Override
    public void onMenuTabReSelected(int menuItemId) {
        // do nothing at the moment
    }

    private void replaceCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, fragment)
                .commit();
    }

    private Fragment getFragmentFromTag(String tag) {
        return getSupportFragmentManager()
                .findFragmentByTag(tag);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
