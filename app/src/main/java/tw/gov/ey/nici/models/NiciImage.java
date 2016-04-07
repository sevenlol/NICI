package tw.gov.ey.nici.models;

import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NiciImage extends NiciText {

    private String imageUrl = null;
    private CharSequence imageDescription = null;
    private ImageView imageView = null;

    public NiciImage(String imageUrl, String imageDescription) {
        checkUrl(imageUrl);
        checkDescription(imageDescription);
        this.imageUrl = imageUrl;
        // description is allowed to be null
        this.imageDescription = imageDescription == null ?
                null : Html.fromHtml(imageDescription);
    }

    public String getImageUrl() { return imageUrl; }
    public ImageView getImageView(Context context) {
        check(context);
        if (imageView == null) {
            imageView = getBaseImageView(context);
        }
        return imageView;
    }

    @Override
    public View getView(Context context) {
        check(context);

        // create linear layout
        LinearLayout imageLayout = new LinearLayout(context);
        imageLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageLayout.setLayoutParams(params);

        if (imageView == null) {
            imageView = getBaseImageView(context);
        }
        if (imageView.getParent() != null) {
            ((ViewGroup) imageView.getParent()).removeView(imageView);
        }
        imageLayout.addView(imageView);

        if (imageDescription != null) {
            TextView textView = new TextView(context);
            textView.setText(imageDescription);
            textView.setTextSize(NiciText.DEFAULT_UNIT, getTextSize(setting));
            // center description text
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textParams.gravity = Gravity.CENTER_HORIZONTAL;
            textView.setLayoutParams(textParams);
            imageLayout.addView(textView);
        }

        return imageLayout;
    }

    private ImageView getBaseImageView(Context context) {
        ImageView view = new ImageView(context);
        return view;
    }

    private void checkUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.equals("")) {
            throw new IllegalArgumentException();
        }

        // TODO check if the url is valid
    }

    private void checkDescription(String description) {
        if ("".equals(description)) {
            throw new IllegalArgumentException();
        }
    }
}
