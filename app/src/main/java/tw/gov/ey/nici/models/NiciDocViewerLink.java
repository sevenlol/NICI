package tw.gov.ey.nici.models;

import android.content.Context;
import android.view.View;
import android.widget.Button;

public class NiciDocViewerLink extends NiciText {
    private String fileUrl = null;

    public NiciDocViewerLink(String fileUrl) {
        if (fileUrl == null || fileUrl.equals("")) {
            throw new IllegalArgumentException();
        }
        this.fileUrl = fileUrl;
    }
    @Override
    public View getView(Context context) {
        return null;
    }

    public Button getLink(Context context) {
        return null;
    }

    public String getFileUrl() { return fileUrl; }
}
