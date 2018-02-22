package com.eservices.floamnx.coomcook.didacticiel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eservices.floamnx.coomcook.R;

public class DidacticielFragment extends Fragment {

	private static final String ARG_POSITION = "position";

	private int position;
	private ImageView mockImg;
	private TextView title;
	private TextView text;

	public static DidacticielFragment newInstance(int position) {
		DidacticielFragment f = new DidacticielFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_didacticiel,
				container, false);
		mockImg = (ImageView) rootView.findViewById(R.id.fragment_wizard_media_icon);
		title = (TextView) rootView.findViewById(R.id.fragment_wizard_media_title);
		text = (TextView) rootView.findViewById(R.id.fragment_wizard_media_text);
		
		if (position == 0) {
			mockImg.setImageResource(R.drawable.menu_mock);
			title.setText("Naviguer facilement grâce au menu glissant");
			text.setText("Accéder rapidement à toutes les rubriques de l'application");
		} else if (position == 1) {
			mockImg.setImageResource(R.drawable.search_mock);
			title.setText("Chercher un événement selon vos préférences");
			text.setText("Utiliser les filtres afin de trouver l'événement dont vous avez vraiment besoin !");
		} else {
			mockImg.setImageResource(R.drawable.profile_mock);
			title.setText("Un profil utilisateur complet");
			text.setText("Accéder aux préférences des autres utilisateurs sur leur profils et n'oublier pas de les noter après chaque événement ;)");
		}

		ViewCompat.setElevation(rootView, 50);
		return rootView;
	}

}