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
    public static final String VIEW_LABEL = "線上瀏覽";
    public static final String DOWNLOAD_LABEL = "下載檔案";

    public static final int DEFAULT_VIEW_ICON = R.drawable.ic_home_black_24dp;
    public static final int DEFAULT_DOWNLOAD_ICON = R.drawable.ic_home_black_24dp;

    public static final int DEFAULT_DRAWABLE_PADDING = 20;

    private boolean showViewBtn = false;
    private boolean showDownladBtn = false;
    private String fileUrl = null;

    private Button viewBtn = null;
    private Button downloadBtn = null;

    public NiciFileUtilBar(boolean showViewBtn, boolean showDownladBtn, String fileUrl) {
        // cannot be both false
        if (!showViewBtn && !showDownladBtn) {
            throw new IllegalArgumentException();
        }
        if (fileUrl == null || fileUrl.equals("")) {
            throw new IllegalArgumentException();
        }
        this.showViewBtn = showViewBtn;
        this.showDownladBtn = showDownladBtn;
        this.fileUrl = fileUrl;
    }

    @Override
    public View getView(Context context) {
        check(context);

        LinearLayout barLayout = new LinearLayout(context);
        barLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        barLayout.setLayoutParams(params);

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
        downloadBtn.setText(DOWNLOAD_LABEL);
        downloadBtn.setTextSize(NiciText.DEFAULT_UNIT, getTextSize(setting));

        int viewIconId = getViewIcon();
        viewBtn.setCompoundDrawablesWithIntrinsicBounds(viewIconId, 0, 0, 0);
        viewBtn.setCompoundDrawablePadding(getDrawablePadding());

        int downloadIconId = getDownloadIcon();
        downloadBtn.setCompoundDrawablesWithIntrinsicBounds(downloadIconId, 0, 0, 0);
        downloadBtn.setCompoundDrawablePadding(getDrawablePadding());

        barLayout.addView(viewBtn);
        barLayout.addView(downloadBtn);

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
