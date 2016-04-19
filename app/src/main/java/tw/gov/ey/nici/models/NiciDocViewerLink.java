package tw.gov.ey.nici.models;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class NiciDocViewerLink extends NiciText {
    private String fileUrl = null;
    private String fileTitle = null;
    private String linkLabel = null;

    private Button linkBtn = null;

    public NiciDocViewerLink(String fileUrl, String fileTitle, String linkLabel) {
        if (fileUrl == null || fileUrl.equals("") ||
            fileTitle == null || fileTitle.equals("") ||
            linkLabel == null || linkLabel.equals("")) {
            throw new IllegalArgumentException();
        }
        this.fileUrl = fileUrl;
        this.fileTitle = fileTitle;
        this.linkLabel = linkLabel;
    }
    @Override
    public View getView(Context context) {
        check(context);

        if (linkBtn == null) {
            linkBtn = getLinkButton(context);
        }

        linkBtn.setText(linkLabel);
        linkBtn.setTextSize(NiciText.DEFAULT_UNIT, getTextSize(setting));
        linkBtn.setPaintFlags(linkBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linkBtn.setLayoutParams(params);

        return linkBtn;
    }

    public Button getLinkButton(Context context) {
        if (linkBtn == null) {
            linkBtn = getBaseButton(context);
        }
        return linkBtn;
    }

    public String getFileUrl() { return fileUrl; }
    public String getFileTitle() { return fileTitle; }

    private Button getBaseButton(Context context) {
        check(context);
        return new Button(context, null,
                android.support.v7.appcompat.R.attr.borderlessButtonStyle);
    }
}
