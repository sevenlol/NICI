package tw.gov.ey.nici.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import tw.gov.ey.nici.R;

public class FacebookFragment extends Fragment {

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

        WebView webView = (WebView) root.findViewById(R.id.facebook_container);
        if (webView != null && facebookPageUrl != null) {
            webView.setWebViewClient(webViewClient);
            webView.loadUrl(facebookPageUrl);
        }

        return root;
    }

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            webView.loadUrl(url);
            return true;
        }
    };

    private FacebookFragment setFacebookPageUrl(String facebookPageUrl) {
        this.facebookPageUrl = facebookPageUrl; return this;
    }
}
