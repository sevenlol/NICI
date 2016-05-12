package tw.gov.ey.nici.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import tw.gov.ey.nici.models.NiciContent;
import tw.gov.ey.nici.models.NiciDocViewerLink;
import tw.gov.ey.nici.models.NiciFileUtilBar;
import tw.gov.ey.nici.models.NiciHeading;
import tw.gov.ey.nici.models.NiciImage;
import tw.gov.ey.nici.models.NiciParagraph;

// nice naming ... fml
public class NiciContentUtil {
    private static final String UTF8 = "UTF-8";

    private static final String KEY_VIDEO_ID = "v";
    private static final String NICI_CONTENT_LINE_SEPARATOR = "\r\n";

    public static class Parser {
        public static List<NiciContent> parse(String content) {
            if (content == null || content.equals("")) {
                throw new IllegalArgumentException();
            }
            List<NiciContent> contentList = new ArrayList<>();
            String[] paragraphList = content.split(NICI_CONTENT_LINE_SEPARATOR);
            for (String paragraph : paragraphList) {
                if (paragraph == null || paragraph.equals("")) {
                    continue;
                }
                contentList.add(new NiciParagraph(paragraph));
            }
            return contentList;
        }
    }

    public static String getVideoIdFromUrl(String url) {
        if (url == null) {
            return null;
        }

        try {
            HttpUrl parsedUrl = HttpUrl.parse(url);
            if (parsedUrl == null || parsedUrl.querySize() == 0) {
                return null;
            }

            for (int i = 0; i < parsedUrl.querySize(); i++) {
                if (parsedUrl.queryParameterName(i) == null ||
                        parsedUrl.queryParameterValue(i) == null) {
                    continue;
                }
                // ignore case, just in case
                if (parsedUrl.queryParameterName(i).equalsIgnoreCase(KEY_VIDEO_ID)) {
                    return parsedUrl.queryParameterValue(i);
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static void addPhotos(
            List<NiciContent> contentList,
            Map<String, String> photoMap,
            boolean showDescription) {
        if (contentList == null || photoMap == null) {
            return;
        }

        for (String key : photoMap.keySet()) {
            if (key == null || key.equals("")) {
                continue;
            }
            String url = photoMap.get(key);
            if (url == null || url.equals("")) {
                continue;
            }

            contentList.add(new NiciImage(url, showDescription ? key : null));
        }
    }

    public static void addAttachments(
            List<NiciContent> contentList,
            Map<String, String> attachmentMap,
            boolean showHeadingWhenNoAttachment,
            boolean showKeyAsTitle,
            boolean showKeyAsLabel,
            String defaultHeading,
            String defaultTitle,
            String defaultLabel) {
        if (contentList == null || attachmentMap == null) {
            return;
        }
        if (defaultHeading == null) {
            return;
        }

        // set default title, label
        final String DEFAULT_TITLE = (defaultTitle == null) ? "" : defaultTitle;
        final String DEFAULT_LABEL = (defaultLabel == null) ? "" : defaultLabel;

        // add heading
        if (showHeadingWhenNoAttachment || attachmentMap.size() > 0) {
            contentList.add(new NiciHeading(defaultHeading));
        }

        for (String key : attachmentMap.keySet()) {
            if (key == null || key.equals("")) {
                continue;
            }
            String url = attachmentMap.get(key);
            if (url == null || url.equals("")) {
                continue;
            }
            contentList.add(new NiciDocViewerLink(
                    url,
                    showKeyAsTitle ? key : DEFAULT_TITLE,
                    showKeyAsLabel ? key : DEFAULT_LABEL));
        }
    }

    public static void addFileActionBars(
            List<NiciContent> contentList,
            Map<String, String> attachmentMap,
            boolean showHeadingWhenNoAttachment,
            String defaultHeading) {
        if (contentList == null || attachmentMap == null) {
            return;
        }
        if (defaultHeading == null) {
            return;
        }

        // add heading
        if (showHeadingWhenNoAttachment || attachmentMap.size() > 0) {
            contentList.add(new NiciHeading(defaultHeading));
        }

        for (String key : attachmentMap.keySet()) {
            if (key == null || key.equals("")) {
                continue;
            }
            String url = attachmentMap.get(key);
            if (url == null || url.equals("")) {
                continue;
            }
            // file name
            contentList.add(new NiciParagraph(key));
            // file util bar
            contentList.add(new NiciFileUtilBar(
                    true, true,
                    url,
                    key));
        }
    }
}
