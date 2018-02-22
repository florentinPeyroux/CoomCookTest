package com.eservices.floamnx.coomcook.profile;

import java.util.ArrayList;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eservices.floamnx.coomcook.R;
import com.eservices.floamnx.coomcook.event.FragmentEventDetails;
import com.eservices.floamnx.coomcook.event.model.Event;
import com.eservices.floamnx.coomcook.event.util.ImageUtil;

public class ProfileListViewAdapter extends BaseAdapter implements View.OnClickListener {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<Event> eventList;
	private FragmentActivity activity;

	public ProfileListViewAdapter(Context context,
								  ArrayList<Event> myList,FragmentActivity activity) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		eventList = myList;
		this.activity = activity;
	}


	@Override
	public int getCount() {
		return  eventList.size();
	}

	@Override
	public Object getItem(int position) {
		return eventList.get(position);
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	public String getEventId(int position) {
		return eventList.get(position).getEventId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.list_item_event_profile, parent, false);
			holder = new ViewHolder();
			holder.eventPicture = (ImageView) convertView
					.findViewById(R.id.foodPicture);
			holder.eventName = (TextView) convertView
					.findViewById(R.id.eventName);
			holder.eventInterest = (TextView) convertView
					.findViewById(R.id.eventInterest);
			holder.icon = (TextView) convertView
					.findViewById(R.id.icon);
			convertView.setTag(holder);
			holder.layotItem = convertView
					.findViewById(R.id.layoutItem);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Event event = eventList.get(position);
		
		ImageUtil.displayImage(holder.eventPicture, event.getEventFoodUrl(), null);
		holder.eventName.setText(event.getEventName());
		holder.eventInterest.setText(event.getEventInterest());

		holder.layotItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				goToEvent(position);
			}
		});
		return convertView;
	}

	//ne rien faire quand appuis
	@Override
	public void onClick(View view) {
	}

	private static class ViewHolder {
		private View layotItem;
		private ImageView eventPicture;
		private TextView eventName;
		private TextView icon;
		private TextView eventInterest;
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
