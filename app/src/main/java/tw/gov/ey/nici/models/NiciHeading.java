package tw.gov.ey.nici.models;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

public class NiciHeading extends NiciText {
    private static final float DEFAULT_SMALL_TEXT_SIZE = 20;
    private static final float DEFAULT_MEDIUM_TEXT_SIZE = 24;
    private static final float DEFAULT_LARGE_TEXT_SIZE = 28;

    private String heading = null;

    public NiciHeading(String heading) {
        checkHeading(heading);
        // overriding default text size
        setTextSize(Setting.SMALL, DEFAULT_SMALL_TEXT_SIZE);
        setTextSize(Setting.MEDIUM, DEFAULT_MEDIUM_TEXT_SIZE);
        setTextSize(Setting.LARGE, DEFAULT_LARGE_TEXT_SIZE);
        this.heading = heading;
    }

    @Override
    public View getView(Context context) {
        check(context);

        TextView textView = new TextView(context);
        textView.setText(heading);
        textView.setTextSize(NiciText.DEFAULT_UNIT, getTextSize(setting));
        textView.setTypeface(null, Typeface.BOLD);

        return textView;
    }

    private void checkHeading(String heading) {
        if (heading == null || heading.equals("")) {
            throw new IllegalArgumentException();
        }
    }
}
