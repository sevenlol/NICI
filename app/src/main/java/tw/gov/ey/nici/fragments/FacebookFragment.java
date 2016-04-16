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

public class FacebookFragment extends Fragment implements View.OnClickListener {
    private WebView webView = null;
    private FloatingActionButton scrollToTopBtn = null;

    private String facebookPageUrl = null;

    public static FacebookFragment newInstance() {
        return new FacebookFragment();
    }

    public static FacebookFragment newInstance(String facebookPageUrl) {
        if (facebookPageUrl == null || facebookPageUrl.equals("")) {
            throw new IllegalArgumentException();
        }
        return new FacebookFragment().setFacebookPageUrl(facebookPageUrl);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.facebook_fragment, container, false);

        webView = (WebView) root.findViewById(R.id.facebook_container);
        if (webView != null && facebookPageUrl != null) {
            webView.setWebViewClient(webViewClient);
            webView.loadUrl(facebookPageUrl);
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

    private FacebookFragment setFacebookPageUrl(String facebookPageUrl) {
        this.facebookPageUrl = facebookPageUrl; return this;
    }

    @Override
    public void onClick(View v) {
        if (webView != null) {
            webView.scrollTo(0, 0);
        }
    }
}
