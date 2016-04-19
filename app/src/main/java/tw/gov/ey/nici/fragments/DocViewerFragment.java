package tw.gov.ey.nici.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import tw.gov.ey.nici.R;

public class DocViewerFragment extends Fragment implements View.OnClickListener {
    public static final String DOC_VIEWER_URL_FORMAT = "http://drive.google.com/" +
            "viewerng/viewer?embedded=true&url=%s";
    private WebView webView = null;
    private FloatingActionButton scrollToTopBtn = null;

    private String docUrl = null;

    public static DocViewerFragment newInstance() { return new DocViewerFragment(); }

    public static DocViewerFragment newInstance(String docUrl) {
        if (docUrl == null || docUrl.equals("")) {
            throw new IllegalArgumentException();
        }
        return new DocViewerFragment().setDocUrl(docUrl);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.doc_viewer_fragment, container, false);

        webView = (WebView) root.findViewById(R.id.doc_viewer_container);
        if (webView != null && docUrl != null) {
            webView.setWebViewClient(webViewClient);
            if (webView.getSettings() != null) {
                webView.getSettings().setJavaScriptEnabled(true);
            }
            webView.loadUrl(String.format(DOC_VIEWER_URL_FORMAT, docUrl));
        }

        scrollToTopBtn = (FloatingActionButton) root.findViewById(R.id.scroll_to_top_btn);
        if (scrollToTopBtn != null) {
            scrollToTopBtn.setOnClickListener(this);
        }

        return root;
    }

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            webView.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                flushCookies();
            } else {
                CookieSyncManager.getInstance().sync();
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private void flushCookies() {
            CookieManager.getInstance().flush();
        }
    };

    private DocViewerFragment setDocUrl(String docUrl) {
        this.docUrl = docUrl; return this;
    }

    @Override
    public void onClick(View v) {
        if (webView != null) {
            // note: scroll does not work with embedded google drive viewer
            //       has to reload instead
            // FIXME find a better solution
            webView.scrollTo(0, 0);
            webView.reload();
        }
    }
}
