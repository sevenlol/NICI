package tw.gov.ey.nici.utils;

import android.net.Uri;

public class URLUtil {

    public static String handleChineseUrl(String url) {
        if (url == null || url.equals("")) {
            return url;
        }

        try {
            Uri uri = Uri.parse(url);
            String oriQueryStr = uri.getQuery();
            String newQueryStr = Uri.encode(oriQueryStr, "-![.:/,%?&=]");
            return url.replace(oriQueryStr, newQueryStr);
        } catch (Exception e) {
            return url;
        }
    }
}
