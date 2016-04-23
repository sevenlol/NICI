package tw.gov.ey.nici.models;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NiciList extends NiciText {
    public static enum ListType {
        NUMBER, BULLET
    }
    private static final int DEFAULT_SMALL_MARGIN = 20;
    private static final int DEFAULT_MEDIUM_MARGIN = 30;
    private static final int DEFAULT_LARGE_MARGIN = 40;
    private static final ListType DEFAULT_LIST_TYPE = ListType.BULLET;
    private static final String BULLET_PREFIX = "\u2022 ";
    // only support up to 1000 list items
    private static final String NUMBER_PREFIX = "%3d. ";

    private ListType listType = DEFAULT_LIST_TYPE;
    private List<CharSequence> list;

    public NiciList(List<String> list) {
        checkList(list);

        this.list = new ArrayList<CharSequence>();
        for (String item : list) {
            this.list.add(Html.fromHtml(item));
        }
    }

    public NiciList(List<String> list, ListType type) {
        this(list);
        check(type);
        this.listType = type;
    }

    @Override
    public View getView(Context context) {
        check(context);

        // create linear layout
        LinearLayout listLayout = new LinearLayout(context);
        listLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, getMargin());
        listLayout.setLayoutParams(params);

        for (int i = 0 ; i < list.size(); i ++) {
            CharSequence seq = list.get(i);
            TextView textView = new TextView(context);
            textView.setText(TextUtils.concat(getPrefix(listType, i), seq));
            textView.setTextSize(NiciText.DEFAULT_UNIT, getTextSize(setting));
            listLayout.addView(textView);
        }

        return listLayout;
    }

    private String getPrefix(ListType type, int index) {
        switch (type) {
            case BULLET:
                return BULLET_PREFIX;
            case NUMBER:
                return String.format(NUMBER_PREFIX, index + 1);
            default:
                return BULLET_PREFIX;
        }
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

    private void checkList(List<String> list) {
        if (list == null || list.size() == 0) {
            throw new IllegalArgumentException();
        }
        for (String item : list) {
            checkListItem(item);
        }
    }

    private void check(ListType type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }
    }

    private void checkListItem(String item) {
        if (item == null || item.equals("")) {
            throw new IllegalArgumentException();
        }
    }
}
