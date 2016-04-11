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
import tw.gov.ey.nici.models.NiciEventInfo;

public class NiciEventInfoAdapter extends ArrayAdapter<NiciEventInfo> {
    private static class ViewHolder {
        TextView title;
        TextView date;
        TextView location;
    }

    public NiciEventInfoAdapter(Context context, ArrayList<NiciEventInfo> eventInfoList) {
        super(context, 0, eventInfoList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NiciEventInfo eventInfo = getItem(position);

        ViewHolder viewHolder;
        // view not being reused, create one
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.meeting_info_list_item, parent, false);

            viewHolder.title = (TextView) convertView.findViewById(R.id.meeting_info_title);
            viewHolder.date = (TextView) convertView.findViewById(R.id.meeting_info_date);
            viewHolder.location = (TextView) convertView.findViewById(R.id.meeting_info_location);

            // set view holder
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // set required view
        viewHolder.title.setText(eventInfo.getTitle() == null ? "" : eventInfo.getTitle());
        viewHolder.location.setText(String.format(
                getContext().getString(R.string.meeting_location_str_format),
                eventInfo.getLocation() == null ? "" : eventInfo.getLocation()));
        String dateStr = "";
        if (eventInfo.getDate() != null) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(
                        getContext().getString(R.string.meeting_date_format), Locale.getDefault());
                dateStr = format.format(eventInfo.getDate());
            } catch (Exception e) { /* do nothing */ }
        }
        viewHolder.date.setText(String.format(
                getContext().getString(R.string.meeting_date_str_format), dateStr));

        return convertView;
    }
}
