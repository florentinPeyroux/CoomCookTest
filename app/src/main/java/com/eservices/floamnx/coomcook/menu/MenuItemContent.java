package com.eservices.floamnx.coomcook.menu;

import com.eservices.floamnx.coomcook.R;

import java.util.ArrayList;


public class MenuItemContent {

	public static ArrayList<MenuItemModel> getDrawerMediaDummyList() {
		ArrayList<MenuItemModel> list = new ArrayList<>();
		list.add(new MenuItemModel(0, "Chercher", R.string.icon_chercher));
		list.add(new MenuItemModel(1,  "Proposer", R.string.icon_proposer));
		list.add(new MenuItemModel(2, "Notifications", R.string.icon_notification));
		list.add(new MenuItemModel(3, "Messagerie", R.string.icon_messagerie));
		list.add(new MenuItemModel(4, "Didacticiel", R.string.material_icon_help));
		list.add(new MenuItemModel(5, "Se déconnecter", R.string.icon_logout));
		return list;
	}
}
