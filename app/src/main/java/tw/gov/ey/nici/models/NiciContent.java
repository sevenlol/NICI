package tw.gov.ey.nici.models;

import android.content.Context;
import android.view.View;

public interface NiciContent {
    enum  Setting {
        SMALL, MEDIUM, LARGE
    }
    View getView(Context context);
    View getView(Context context, Setting setting);
}
