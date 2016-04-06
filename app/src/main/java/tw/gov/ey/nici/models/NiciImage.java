package tw.gov.ey.nici.models;

import android.content.Context;
import android.text.Html;
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
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageLayout.setLayoutParams(params);

        if (imageView == null) {
            imageView = getBaseImageView(context);
        }
        imageLayout.addView(imageView);

        if (imageDescription != null) {
            TextView textView = new TextView(context);
            textView.setText(imageDescription);
            textView.setTextSize(NiciText.DEFAULT_UNIT, getTextSize(setting));
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
