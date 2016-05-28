package tw.gov.ey.nici.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import tw.gov.ey.nici.R;
import tw.gov.ey.nici.models.NiciInfo;

public class NiciInfoAdapter extends ArrayAdapter<NiciInfo> {
    private static class ViewHolder {
        TextView title;
        TextView date;
        TextView publishedBy;
    }

    public NiciInfoAdapter(Context context, ArrayList<NiciInfo> infoList) {
        super(context, 0, infoList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NiciInfo info = getItem(position);

        ViewHolder viewHolder;
        // view not being reused, create one
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.info_list_item, parent, false);

            viewHolder.title = (TextView) convertView.findViewById(R.id.info_title);
            viewHolder.date = (TextView) convertView.findViewById(R.id.info_date);
            viewHolder.publishedBy = (TextView) convertView.findViewById(R.id.info_published_by);

            // set view holder
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // set required view
        viewHolder.title.setText(info.getTitle() == null ? "" : info.getTitle());
        viewHolder.publishedBy.setText(String.format(
                getContext().getString(R.string.info_published_by_str_format),
                info.getPublishedBy() == null ? "" : info.getPublishedBy()));
        // disable publish field
        viewHolder.publishedBy.setVisibility(View.GONE);
        String dateStr = "";
        if (info.getDate() != null) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(
                        getContext().getString(R.string.info_date_format), Locale.getDefault());
                dateStr = format.format(info.getDate());
            } catch (Exception e) { /* do nothing */ }
        }
        viewHolder.date.setText(String.format(
                getContext().getString(R.string.info_date_str_format), dateStr));

        return convertView;
    }
}
