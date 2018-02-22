package com.eservices.floamnx.coomcook.chat;

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
import com.eservices.floamnx.coomcook.event.util.ImageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class FriendListAdapter extends BaseAdapter {


	private LayoutInflater mInflater;
	private ArrayList<FriendDetails> friendList;
	private FragmentActivity activity;


	public FriendListAdapter(Context context,
                             ArrayList<FriendDetails> myList, FragmentActivity activity) {

		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		friendList = myList;
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
		this.activity = activity;

	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public int getCount() {
		return friendList.size();
	}

	@Override
	public Object getItem(int position) {
		return friendList.get(position);
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}


	public String getFriendId(int position) {
		return friendList.get(position).getUserId();
	}


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.list_item_friend_chat, parent, false);
			holder = new ViewHolder();
			holder.userPicture = (ImageView) convertView
					.findViewById(R.id.foodPicture);
			holder.userName = (TextView) convertView
					.findViewById(R.id.eventName);
			holder.icon = (TextView) convertView
					.findViewById(R.id.icon);
			convertView.setTag(holder);
			holder.layoutItem = convertView
					.findViewById(R.id.layoutItem);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		FriendDetails user = friendList.get(position);

		ImageUtil.displayRoundImage(holder.userPicture, user.getUserPicture(), null);
		holder.userName.setText(user.getUserName());

		holder.layoutItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
			goToChatWithFriend(position);

			}
		});
		return convertView;
	}




	private static class ViewHolder {
		private View layoutItem;
		private ImageView userPicture;
		private TextView userName;
		private TextView icon;
	}

	private void goToChatWithFriend(int position) {
		Bundle bundle = new Bundle();
		bundle.putString("userId", getFriendId(position));
		ChatFragment nextFrag = new ChatFragment();
		nextFrag.setArguments(bundle);
		activity.getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, nextFrag)
				.addToBackStack(null)
				.commit();
	}
}
