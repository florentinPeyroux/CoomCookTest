package com.eservices.floamnx.coomcook.event.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eservices.floamnx.coomcook.event.FragmentEventDetails;
import com.eservices.floamnx.coomcook.R;
import com.eservices.floamnx.coomcook.event.model.Event;
import com.eservices.floamnx.coomcook.event.util.ImageUtil;

import java.util.List;


public class EventAdapter extends ArrayAdapter<Event> {

    private LayoutInflater mInflater;
    private FragmentActivity activity;


    public EventAdapter(Context context, List<Event> items, FragmentActivity activity) {
        super(context, 0, items);
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
    }


    private String getEventId(int position) {
        return getItem(position).getEventId();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(
                    R.layout.list_item_event, parent, false);
            holder = new ViewHolder();
            holder.eventImage = (ImageView) convertView
                    .findViewById(R.id.list_item_event_image);
            holder.alarmIcon = (TextView) convertView
                    .findViewById(R.id.list_item_alarm);
            holder.eventTitle = (TextView) convertView
                    .findViewById(R.id.list_item_event_title);
            holder.eventDescription = (TextView) convertView
                    .findViewById(R.id.list_item_event_description);
            holder.btnExplorer = (TextView) convertView
                    .findViewById(R.id.list_item_btn_explorer);
            convertView.setTag(holder);

            holder.layoutItem = convertView
                    .findViewById(R.id.layoutItem);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        Event item = getItem(position);
        holder.btnExplorer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEvent(position);
            }
        });

        holder.layoutItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEvent(position);
            }
        });


        ImageUtil.displayImage(holder.eventImage, item.getEventFoodUrl(), null);
        holder.eventTitle.setText(item.getEventName());
        holder.eventDescription.setText(item.getEventDescription());

        return convertView;
    }

    private static class ViewHolder {
        private View layoutItem;
        private ImageView eventImage;
        private TextView alarmIcon;
        private TextView eventTitle;
        private TextView eventDescription;
        private TextView btnExplorer;
    }

    private void goToEvent(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("eventId", getEventId(position));
        FragmentEventDetails nextFrag = new FragmentEventDetails();
        nextFrag.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, nextFrag)
                .addToBackStack(null)
                .commit();
    }


}