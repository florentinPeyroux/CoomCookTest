package com.eservices.floamnx.coomcook.menu;

public class MenuItemModel {
	
	private long mId;
	private String mText;
	private int mIconRes;

	public MenuItemModel() {
	}

	public MenuItemModel(long id, String text, int iconRes) {
		mId = id;
		mText = text;
		mIconRes = iconRes;
	}

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		mText = text;
	}

	public int getIconRes() {
		return mIconRes;
	}


	@Override
	public String toString() {
		return mText;
	}

}
