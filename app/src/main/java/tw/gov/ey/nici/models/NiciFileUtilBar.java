package tw.gov.ey.nici.models;

import android.content.Context;
import android.view.View;
import android.widget.Button;

public class NiciFileUtilBar extends NiciText {
    private boolean showViewBtn = false;
    private boolean showDownladBtn = false;
    private String fileUrl = null;

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
        return null;
    }

    public Button getViewButton(Context context) {
        return null;
    }

    public Button getDownloadButton(Context context) {
        return null;
    }

    public String getFileUrl() { return fileUrl; }
}
