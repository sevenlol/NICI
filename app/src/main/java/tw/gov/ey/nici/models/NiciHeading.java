package tw.gov.ey.nici.models;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NiciHeading extends NiciText {
    private static final float DEFAULT_SMALL_TEXT_SIZE = 20;
    private static final float DEFAULT_MEDIUM_TEXT_SIZE = 26;
    private static final float DEFAULT_LARGE_TEXT_SIZE = 32;

    private static final int DEFAULT_SMALL_MARGIN = 30;
    private static final int DEFAULT_MEDIUM_MARGIN = 48;
    private static final int DEFAULT_LARGE_MARGIN = 60;

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

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, getMargin(), 0, getMargin());

        textView.setLayoutParams(params);

        return textView;
    }

    @Override
    public String toString() {
        return String.format("Heading: %s", heading == null ? "NULL" : heading);
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

    private void checkHeading(String heading) {
        if (heading == null || heading.equals("")) {
            throw new IllegalArgumentException();
        }
    }
}
