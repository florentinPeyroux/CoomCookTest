package com.eservices.floamnx.coomcook.menu;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.eservices.floamnx.coomcook.authentification.LoginActivity;
import com.eservices.floamnx.coomcook.authentification.User;
import com.eservices.floamnx.coomcook.chat.FriendListFragment;
import com.eservices.floamnx.coomcook.didacticiel.DidacticielActivity;
import com.eservices.floamnx.coomcook.event.FragmentCreateEvent;
import com.eservices.floamnx.coomcook.event.FragmentListEvent;
import com.eservices.floamnx.coomcook.event.FragmentListEvent_nosearch;
import com.eservices.floamnx.coomcook.profile.FragmentMyProfile;
import com.eservices.floamnx.coomcook.R;
import com.eservices.floamnx.coomcook.event.util.ImageUtil;
import com.eservices.floamnx.coomcook.interfaces.FragmentManagerInterface;
import com.eservices.floamnx.coomcook.splashSceen.SplashScreensActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class LeftMenuActivity extends AppCompatActivity implements FragmentManagerInterface {

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private ImageView iv;
    private TextView userName;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth = FirebaseAuth.getInstance();


        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, SplashScreensActivity.class));
        }
        else {
            databaseUser = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid());
            loadUserInformations();

        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_view);


        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault((LeftMenuActivity.this)));

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);

        View headerView = getLayoutInflater().inflate(
                R.layout.header_navigation_drawer, mDrawerList, false);


        iv = (ImageView) headerView
                .findViewById(R.id.header_navigation_drawer_media_image);

        userName = (TextView) headerView.findViewById(R.id.header_navigation_drawer_media_username);



        mDrawerList.addHeaderView(headerView);
        mDrawerList.setAdapter(new DrawerMediaAdapter(this));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerList
                .setBackgroundResource(R.drawable.background_menu);
        mDrawerList.getLayoutParams().width = (int) getResources()
                .getDimension(R.dimen.drawer_width_media);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            mDrawerLayout.openDrawer(mDrawerList);
        }

        showFragment(new FragmentListEvent_nosearch());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showFragment(Fragment sFragment) {
        showFragment(sFragment, null);
    }

    @Override
    public void showFragment(Fragment sFragment, Bundle sBundle) {
        if (sFragment != null) {
            final android.support.v4.app.FragmentManager tFragmentManager = getSupportFragmentManager();
            if (sBundle != null) {
                sFragment.setArguments(sBundle);
            }
            FragmentTransaction tFragmentTransaction;
            tFragmentTransaction = tFragmentManager.beginTransaction();
            tFragmentTransaction.addToBackStack(sFragment.getClass().getName());
            tFragmentTransaction.replace(R.id.content_frame, sFragment, sFragment.toString());
            tFragmentTransaction.commit();
        }
    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if (position == 0) {
                showFragment(new FragmentMyProfile());
            }
            if (position == 1) {
                showFragment(new FragmentListEvent());
            }
            if (position == 2) {
                showFragment(new FragmentCreateEvent());
            }
            if (position == 3) {
                showFragment(new FragmentMyProfile());
            }
            if (position == 4) {
                showFragment(new FriendListFragment());
            }
            if (position == 5) {
                startActivity(new Intent(getApplicationContext(), DidacticielActivity.class));
            }

            if (position == 6) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }

            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    public void loadUserInformations(){
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User data = dataSnapshot.getValue(User.class);
                userName.setText(data.getSurname());
                ImageUtil.displayRoundImage(iv, data.getImageURL(), null);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ne fait rien
            }
        });
    }
}