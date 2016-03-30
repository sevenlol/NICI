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

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import tw.gov.ey.nici.fragments.HomeFragment;
import tw.gov.ey.nici.fragments.InfoFragment;
import tw.gov.ey.nici.fragments.IntroFragment;
import tw.gov.ey.nici.fragments.MeetingFragment;
import tw.gov.ey.nici.fragments.TeamFragment;

public class NICIMainActivity extends AppCompatActivity implements OnMenuTabClickListener {

    private BottomBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nici_main);

        // initiate action bar
        Toolbar actionBar = (Toolbar) findViewById(R.id.main_action_bar);
        setSupportActionBar(actionBar);

        // initiate bottom bar
        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.setItemsFromMenu(R.menu.bottom_bar_menu, this);
        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.bottomBarHome));
        mBottomBar.mapColorForTab(1, ContextCompat.getColor(this, R.color.bottomBarIntro));
        mBottomBar.mapColorForTab(2, ContextCompat.getColor(this, R.color.bottomBarTeam));
        mBottomBar.mapColorForTab(3, ContextCompat.getColor(this, R.color.bottomBarInfo));
        mBottomBar.mapColorForTab(4, ContextCompat.getColor(this, R.color.bottomBarMeeting));

        // setup home fragment
        if (getSupportFragmentManager().findFragmentById(R.id.main_fragment) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_fragment, HomeFragment.newInstance())
                    .commit();
        }
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
            case R.id.bottomBarHome:
                replaceCurrentFragment(HomeFragment.newInstance());
                break;
            case R.id.bottomBarIntro:
                replaceCurrentFragment(IntroFragment.newInstance());
                break;
            case R.id.bottomBarTeam:
                replaceCurrentFragment(TeamFragment.newInstance());
                break;
            case R.id.bottomBarInfo:
                replaceCurrentFragment(InfoFragment.newInstance());
                break;
            case R.id.bottomBarMeeting:
                replaceCurrentFragment(MeetingFragment.newInstance());
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
}
