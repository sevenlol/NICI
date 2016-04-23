package tw.gov.ey.nici.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import tw.gov.ey.nici.R;

public class NiciFileUtilBar extends NiciText {
    private static final int DEFAULT_SMALL_MARGIN = 20;
    private static final int DEFAULT_MEDIUM_MARGIN = 30;
    private static final int DEFAULT_LARGE_MARGIN = 40;
    public static final String VIEW_LABEL = "線上瀏覽";
    public static final String DOWNLOAD_LABEL = "下載檔案";

    public static final int DEFAULT_VIEW_ICON = R.drawable.ic_pageview_black_24dp;
    public static final int DEFAULT_DOWNLOAD_ICON = R.drawable.ic_file_download_black_24dp;

    public static final int DEFAULT_DRAWABLE_PADDING = 20;

    private boolean showViewBtn = false;
    private boolean showDownladBtn = false;
    private String fileUrl = null;
    private String fileTitle = null;

    private Button viewBtn = null;
    private Button downloadBtn = null;

    public NiciFileUtilBar(boolean showViewBtn, boolean showDownladBtn, String fileUrl, String fileTitle) {
        // cannot be both false
        if (!showViewBtn && !showDownladBtn) {
            throw new IllegalArgumentException();
        }
        if (fileUrl == null || fileUrl.equals("") ||
            fileTitle == null || fileTitle.equals("")) {
            throw new IllegalArgumentException();
        }
        this.showViewBtn = showViewBtn;
        this.showDownladBtn = showDownladBtn;
        this.fileUrl = fileUrl;
        this.fileTitle = fileTitle;
    }

    @Override
    public View getView(Context context) {
        check(context);

        LinearLayout barLayout = new LinearLayout(context);
        barLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, getMargin());
        barLayout.setLayoutParams(params);
        barLayout.setPadding(0, 0, 0, 0);

        if (viewBtn == null) {
            viewBtn = getViewButton(context);
        }
        if (downloadBtn == null) {
            downloadBtn = getDownloadButton(context);
        }

        if (viewBtn.getParent() != null) {
            ((ViewGroup) viewBtn.getParent()).removeView(viewBtn);
        }
        if (downloadBtn.getParent() != null) {
            ((ViewGroup) downloadBtn.getParent()).removeView(downloadBtn);
        }

        viewBtn.setText(VIEW_LABEL);
        viewBtn.setTextSize(NiciText.DEFAULT_UNIT, getTextSize(setting));
        viewBtn.setPadding(0, 0, 0, 0);
        viewBtn.setMinHeight(40);
        viewBtn.setMinimumHeight(40);
        viewBtn.setLayoutParams(getViewBtnParams());
        downloadBtn.setText(DOWNLOAD_LABEL);
        downloadBtn.setTextSize(NiciText.DEFAULT_UNIT, getTextSize(setting));
        downloadBtn.setPadding(0, 0, 0, 0);
        downloadBtn.setMinHeight(40);
        downloadBtn.setMinimumHeight(40);
        downloadBtn.setLayoutParams(getDownloadBtnParams());

        int viewIconId = getViewIcon();
        viewBtn.setCompoundDrawablesWithIntrinsicBounds(viewIconId, 0, 0, 0);
        viewBtn.setCompoundDrawablePadding(getDrawablePadding());

        int downloadIconId = getDownloadIcon();
        downloadBtn.setCompoundDrawablesWithIntrinsicBounds(downloadIconId, 0, 0, 0);
        downloadBtn.setCompoundDrawablePadding(getDrawablePadding());

        // FIXME probably not create the button if the flag is false
        if (showViewBtn) {
            barLayout.addView(viewBtn);
        }
        if (showDownladBtn) {
            barLayout.addView(downloadBtn);
        }

        return barLayout;
    }

    public Button getViewButton(Context context) {
        if (viewBtn == null) {
            viewBtn = getBaseButton(context);
        }
        return viewBtn;
    }

    public Button getDownloadButton(Context context) {
        if (downloadBtn == null) {
            downloadBtn = getBaseButton(context);
        }
        return downloadBtn;
    }

    public String getFileUrl() { return fileUrl; }
    public String getFileTitle() { return fileTitle; }

    private Button getBaseButton(Context context) {
        return new Button(context, null,
                android.support.v7.appcompat.R.attr.borderlessButtonStyle);
    }

    private int getViewIcon() {
        switch (setting) {
            case SMALL:
            case MEDIUM:
            case LARGE:
                return DEFAULT_VIEW_ICON;
            default:
                return DEFAULT_VIEW_ICON;
        }
    }

    private int getDownloadIcon() {
        switch (setting) {
            case SMALL:
            case MEDIUM:
            case LARGE:
                return DEFAULT_DOWNLOAD_ICON;
            default:
                return DEFAULT_DOWNLOAD_ICON;
        }
    }

    private int getMargin() {
        switch (setting) {
            case SMALL:
                return DEFAULT_SMALL_MARGIN;
            case MEDIUM:
                return DEFAULT_MEDIUM_MARGIN;
            case LARGE:
                return DEFAULT_LARGE_MARGIN;
            default:
                return DEFAULT_MEDIUM_MARGIN;
        }
    }

    private LinearLayout.LayoutParams getViewBtnParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 50, 0);
        return params;
    }

    private LinearLayout.LayoutParams getDownloadBtnParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        return params;
    }

    private int getDrawablePadding() {
        switch (setting) {
            case SMALL:
            case MEDIUM:
            case LARGE:
                return DEFAULT_DRAWABLE_PADDING;
            default:
                return DEFAULT_DRAWABLE_PADDING;
        }
    }
}
