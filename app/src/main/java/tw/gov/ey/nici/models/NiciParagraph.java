package tw.gov.ey.nici.models;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class NiciParagraph extends NiciText {

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

        return textView;
    }

    private void checkParagraph(String paragraph) {
        if (paragraph == null || paragraph.equals("")) {
            throw new IllegalArgumentException();
        }
    }
}
