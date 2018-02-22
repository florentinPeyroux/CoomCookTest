package com.eservices.floamnx.coomcook.didacticiel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.eservices.floamnx.coomcook.R;
import com.eservices.floamnx.coomcook.menu.LeftMenuActivity;


public class DidacticielActivity extends AppCompatActivity {

	private MyPagerAdapter adapter;
	private ViewPager pager;
	private TextView previousButton;
	private TextView nextButton;
	private TextView navigator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_didacticiel);


		pager = (ViewPager) findViewById(R.id.activity_wizard_media_pager);
		previousButton = (TextView) findViewById(R.id.activity_wizard_media_previous);
		nextButton = (TextView) findViewById(R.id.activity_wizard_media_next);
		navigator = (TextView) findViewById(R.id.activity_wizard_media_possition);

		previousButton.setVisibility(View.INVISIBLE);

		adapter = new MyPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		pager.setCurrentItem(0);
		setNavigator();

		pager.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				//ne fait rien
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				//ne fait rien
			}

			@Override
			public void onPageScrollStateChanged(int position) {
				if (pager.getCurrentItem() == 0) {
					previousButton.setVisibility(View.INVISIBLE);
				} else {
					previousButton.setVisibility(View.VISIBLE);
				}
				if (pager.getCurrentItem() == (pager.getAdapter().getCount() - 1)) {
					nextButton.setText(R.string.demarrer);
				} else {
					nextButton.setText(R.string.suivant);
				}
				setNavigator();
			}
		});

		previousButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (pager.getCurrentItem() != 0) {
					pager.setCurrentItem(pager.getCurrentItem() - 1);
				}
				setNavigator();
			}
		});

		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (pager.getCurrentItem() != (pager.getAdapter().getCount() - 1)) {
					pager.setCurrentItem(pager.getCurrentItem() + 1);
				} else {
					startActivity(new Intent(DidacticielActivity.this, LeftMenuActivity.class));
				}
				setNavigator();
			}
		});

	}

	public void setNavigator() {
		String navigation = "";
		for (int i = 0; i < adapter.getCount(); i++) {
			if (i == pager.getCurrentItem()) {
				navigation =navigation.concat(getString(R.string.material_icon_point_full));
			} else {
				navigation= navigation.concat(getString(R.string.material_icon_point_empty));
			}
		}
		navigator.setText(navigation);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {

		private MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return null;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Fragment getItem(int position) {
			return DidacticielFragment.newInstance(position);
		}
	}
}