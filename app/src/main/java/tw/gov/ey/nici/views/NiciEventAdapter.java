package tw.gov.ey.nici.views;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import tw.gov.ey.nici.R;
import tw.gov.ey.nici.models.NiciEvent;

public class NiciEventAdapter extends ArrayAdapter<NiciEvent> {
    public static final int DEFAULT_PREFIEW_IMAGE_SIZE = 72;
    private static class ViewHolder {
        TextView title;
        TextView date;
        TextView location;
        TextView minutesTaker;
        ImageView coverImagePreview;
    }

    private int previewImageSize = DEFAULT_PREFIEW_IMAGE_SIZE *
            (int) Resources.getSystem().getDisplayMetrics().density;

    public NiciEventAdapter(Context context, ArrayList<NiciEvent> eventList, int previewImageSize) {
        super(context, 0, eventList);
        if (previewImageSize > 0) {
            this.previewImageSize = previewImageSize;
        }
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
            viewHolder.minutesTaker = (TextView) convertView.findViewById(R.id.meeting_minutes_taker);
            viewHolder.coverImagePreview = (ImageView) convertView
                    .findViewById(R.id.meeting_cover_image_preview);

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
        viewHolder.minutesTaker.setText(String.format(
                getContext().getString(R.string.meeting_minutes_taker_str_format),
                event.getMinutesTaker() == null ? "" : event.getMinutesTaker()));
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

        // load image
        if (event.getCoverImageUrl() != null && !event.getCoverImageUrl().equals("")) {
            Picasso.with(getContext())
                .load(event.getCoverImageUrl())
                .resize(previewImageSize, previewImageSize)
                .centerCrop()
                .placeholder(R.drawable.image_placeholder)
                .into(viewHolder.coverImagePreview);
            viewHolder.coverImagePreview.setVisibility(View.VISIBLE);
        } else {
            // hide the image
            viewHolder.coverImagePreview.setVisibility(View.GONE);
        }

        return convertView;
    }
}
