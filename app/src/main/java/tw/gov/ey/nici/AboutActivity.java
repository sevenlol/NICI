package tw.gov.ey.nici;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        // initiate action bar
        Toolbar actionBar = (Toolbar) findViewById(R.id.about_action_bar);
        setSupportActionBar(actionBar);

        // set close icon and title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.about_title));
        }

        // set close icon on click listener
        if (actionBar != null) {
            actionBar.setNavigationOnClickListener(this);
        }

        // set about container
        RelativeLayout aboutContainer = (RelativeLayout) findViewById(R.id.about_container);
        if (aboutContainer != null) {
            AboutPage about = new AboutPage(this)
                    .isRTL(false)
                    .setImage(R.drawable.nici_logo_full)
                    .setDescription(getString(R.string.about_description))
                    .addItem(getVersionElement())
                    .addGroup(getString(R.string.about_website_group));

            String[] websiteTitleArr = getResources().getStringArray(R.array.about_website_group_title);
            String[] websiteUrlArr = getResources().getStringArray(R.array.about_website_group_url);
            if (websiteTitleArr != null && websiteUrlArr != null &&
                websiteTitleArr.length == websiteUrlArr.length) {
                for (int i = 0; i < websiteTitleArr.length; i++) {
                    if (websiteTitleArr[i] == null || websiteUrlArr[i] == null) {
                        continue;
                    }

                    about.addItem(getWebsiteElement(websiteTitleArr[i], websiteUrlArr[i]));
                }
            }

            aboutContainer.addView(about.create());
        }
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    private Element getWebsiteElement(String title, String url) {
        Element element = new Element();
        element.setTitle(title);
        element.setIcon(R.drawable.about_icon_link);
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            element.setIntent(intent);
        } catch (Exception e) {
            Log.d("About", "Parse Uri Failed: " + url);
        }
        return element;
    }

    private Element getVersionElement() {
        String version = "";
        if (getPackageManager() != null && getPackageName() != null) {
            try {
                PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
                if (info.versionName != null) {
                    version = info.versionName;
                }
            } catch (Exception e) {
                Log.d("About", "Get Package Info Failed");
            }
        }
        Element element = new Element();
        element.setTitle(String.format(getString(R.string.about_version_format), version));
        return element;
    }
}
