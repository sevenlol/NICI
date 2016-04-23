package tw.gov.ey.nici.models;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;

public abstract class NiciText implements NiciContent {
    protected static final Setting DEFAULT_SETTING = Setting.MEDIUM;
    // not allowing overriding unit for now
    protected static final int DEFAULT_UNIT = TypedValue.COMPLEX_UNIT_SP;
    protected static final float DEFAULT_SMALL_TEXT_SIZE = 14;
    protected static final float DEFAULT_MEDIUM_TEXT_SIZE = 18;
    protected static final float DEFAULT_LARGE_TEXT_SIZE = 22;

    protected Setting setting = DEFAULT_SETTING;
    protected float smallTextSize = DEFAULT_SMALL_TEXT_SIZE;
    protected float mediumTextSize = DEFAULT_MEDIUM_TEXT_SIZE;
    protected float largeTextSize = DEFAULT_LARGE_TEXT_SIZE;

    @Override
    public View getView(Context context, Setting setting) {
        check(context);
        check(setting);
        this.setting = setting;
        return getView(context);
    }

    public void setTextSize(Setting setting, float textSize) {
        check(setting);
        checkTextSize(textSize);
        switch (setting) {
            case SMALL:
                smallTextSize = textSize;
                break;
            case MEDIUM:
                mediumTextSize = textSize;
                break;
            case LARGE:
                largeTextSize = textSize;
                break;
            default:
        }
    }

    protected float getTextSize(Setting setting) {
        switch (setting) {
            case SMALL:
                return smallTextSize;
            case MEDIUM:
                return mediumTextSize;
            case LARGE:
                return largeTextSize;
            default:
                return DEFAULT_SETTING == null ?
                        mediumTextSize :
                        getTextSize(DEFAULT_SETTING);
        }
    }

    protected void check(Context context) {
        if (context == null) {
            throw new IllegalArgumentException();
        }
    }

    protected void check(Setting setting) {
        if (setting == null) {
            throw new IllegalArgumentException();
        }
    }

    protected void checkTextSize(float textSize) {
        if (textSize <= 0) {
            throw new IllegalArgumentException();
        }
    }
}
