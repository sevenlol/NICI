package tw.gov.ey.nici;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import tw.gov.ey.nici.fragments.DocViewerFragment;

public class DocViewerActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String DOC_TITLE_KEY = "nici_doc_viewer_title";
    public static final String DOC_URL_KEY = "nici_doc_viewer_url";

    private static final String DEFAULT_DOC_URL = "";

    private String docTitle = null;
    private String docUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doc_viewer_activity);

        // get doc title and url
        if (getIntent() != null) {
            Bundle extras = getIntent().getExtras();
            docTitle = extras.getString(DOC_TITLE_KEY, getString(R.string.doc_viewer_title));
            docUrl = extras.getString(DOC_URL_KEY, DEFAULT_DOC_URL);
        }

        // initiate action bar
        Toolbar actionBar = (Toolbar) findViewById(R.id.doc_viewer_action_bar);
        setSupportActionBar(actionBar);

        // set close icon and title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(docTitle);
        }

        // set close icon on click listener
        if (actionBar != null) {
            actionBar.setNavigationOnClickListener(this);
        }

        // set preference fragment
        if (getSupportFragmentManager().findFragmentById(R.id.doc_viewer_main_fragment) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.doc_viewer_main_fragment, DocViewerFragment.newInstance(docUrl))
                    .commit();
        }
    }

    @Override
    public void onClick(View view) {
        // TODO save settings
        finish();
    }
}
