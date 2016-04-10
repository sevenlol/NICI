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
import tw.gov.ey.nici.models.NiciEvent;

public class NiciEventAdapter extends ArrayAdapter<NiciEvent> {
    private static class ViewHolder {
        TextView title;
        TextView date;
        TextView location;
    }

    public NiciEventAdapter(Context context, ArrayList<NiciEvent> eventList) {
        super(context, 0, eventList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NiciEvent event = getItem(position);

        ViewHolder viewHolder;
        // view not being reused, create one
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.meeting_list_item, parent, false);

            viewHolder.title = (TextView) convertView.findViewById(R.id.meeting_title);
            viewHolder.date = (TextView) convertView.findViewById(R.id.meeting_date);
            viewHolder.location = (TextView) convertView.findViewById(R.id.meeting_location);

            // set view holder
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // set required view
        viewHolder.title.setText(event.getTitle() == null ? "" : event.getTitle());
        viewHolder.location.setText(String.format(
                getContext().getString(R.string.meeting_location_str_format),
                event.getLocation() == null ? "" : event.getLocation()));
        String dateStr = "";
        if (event.getDate() != null) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(
                        getContext().getString(R.string.meeting_date_format), Locale.getDefault());
                dateStr = format.format(event.getDate());
            } catch (Exception e) { /* do nothing */ }
        }
        viewHolder.date.setText(String.format(
                getContext().getString(R.string.meeting_date_str_format), dateStr));

        return convertView;
    }
}
