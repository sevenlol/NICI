package tw.gov.ey.nici.models;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NiciParagraph extends NiciText {
    private static final int DEFAULT_SMALL_MARGIN = 20;
    private static final int DEFAULT_MEDIUM_MARGIN = 30;
    private static final int DEFAULT_LARGE_MARGIN = 40;

    private CharSequence paragraph;

    public NiciParagraph(String paragraph) {
        checkParagraph(paragraph);
        this.paragraph = Html.fromHtml(paragraph);
    }

    @Override
    public View getView(Context context) {
        check(context);

        TextView textView = new TextView(context);
        textView.setText(paragraph);
        textView.setTextSize(NiciText.DEFAULT_UNIT, getTextSize(setting));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, getMargin());

        textView.setLayoutParams(params);

        return textView;
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

    private void checkParagraph(String paragraph) {
        if (paragraph == null || paragraph.equals("")) {
            throw new IllegalArgumentException();
        }
    }
}
