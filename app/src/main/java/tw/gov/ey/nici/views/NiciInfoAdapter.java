package tw.gov.ey.nici.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import tw.gov.ey.nici.R;
import tw.gov.ey.nici.models.NiciInfo;

public class NiciInfoAdapter extends ArrayAdapter<NiciInfo> {
    private static class ViewHolder {
        TextView title;
        TextView location;
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
            viewHolder.location = (TextView) convertView.findViewById(R.id.info_location);

            // set view holder
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // set required view
        viewHolder.title.setText(info.getTitle() == null ? "" : info.getTitle());
        viewHolder.location.setText(info.getLocation() == null ? "" : info.getLocation());

        return convertView;
    }
}
