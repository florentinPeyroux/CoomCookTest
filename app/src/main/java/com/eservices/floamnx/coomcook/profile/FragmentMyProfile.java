package com.eservices.floamnx.coomcook.profile;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.eservices.floamnx.coomcook.R;
import com.eservices.floamnx.coomcook.authentification.User;
import com.eservices.floamnx.coomcook.event.model.Event;
import com.eservices.floamnx.coomcook.event.util.ImageUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import java.util.ArrayList;


public class FragmentMyProfile extends Fragment implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseUser;
    private DatabaseReference databasePastEvents;
    private DynamicListView mDynamicListView;
    private ImageView profilePicture;
    private TextView surname;
    private TextView lastName;
    private TextView userAge;
    private TextView userLocation;
    private TextView userFood;
    private TextView userInterest;
    private TextView eventsTextView;
    private TextView editProfile;

    private ArrayList<Event> participedEvents;
    private int numVoters;
    private RatingBar ratingBar;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rView = inflater.inflate(R.layout.profile_layout, container, false);


        firebaseAuth = FirebaseAuth.getInstance();

        participedEvents = new ArrayList<>();

        databaseUser = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid());
        databasePastEvents = FirebaseDatabase.getInstance().getReference("upcomingEvents");

        profilePicture = (ImageView) rView.findViewById(R.id.userPicture);

        surname = (TextView) rView.findViewById(R.id.surname);
        lastName = (TextView) rView.findViewById(R.id.lastName);
        userAge = (TextView) rView.findViewById(R.id.userAge);
        userLocation = (TextView) rView.findViewById(R.id.userLocation);
        userFood = (TextView) rView.findViewById(R.id.userFood);
        userInterest = (TextView) rView.findViewById(R.id.userInterest);
        eventsTextView = (TextView) rView.findViewById(R.id.lastEventsTextView);
        editProfile = (TextView) rView.findViewById(R.id.editProfile);
        editProfile.setOnClickListener(this);



        ratingBar = (RatingBar) rView.findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));

        mDynamicListView = (DynamicListView) rView.findViewById(R.id.dynamic_listview);
        mDynamicListView.setDividerHeight(0);


        checkExistingEvents();
        loadUserData();
        return rView;
    }


    public void loadUserData() {
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User data = dataSnapshot.getValue(User.class);
                surname.setText(data.getSurname());
                lastName.setText(data.getName());
                userAge.setText(data.getAge());
                userLocation.setText(data.getCity().concat(", France"));
                userFood.setText(data.getPlate());
                userInterest.setText(data.getInterest());
                if(data.getNumVoters()==0){
                    numVoters = data.getNumVoters() + 1 ;
                }
                else{
                    numVoters = data.getNumVoters();
                }
                ratingBar.setRating((int) ( (double) data.getNumStar()/ (double)numVoters));

                ImageUtil.displayRoundImage(profilePicture, data.getImageURL(), null);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ne fait rien
            }
        });
    }

    public void checkExistingEvents() {
        databasePastEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Event event = postSnapshot.getValue(Event.class);
                    if (event.getEventOwnerId().equals(firebaseAuth.getCurrentUser().getUid())
                            || event.getParticipantID1().equals(firebaseAuth.getCurrentUser().getUid())
                            || event.getParticipantID2().equals(firebaseAuth.getCurrentUser().getUid())
                            || event.getParticipantID3().equals(firebaseAuth.getCurrentUser().getUid())
                            || event.getParticipantID4().equals(firebaseAuth.getCurrentUser().getUid())) {
                        participedEvents.add(event);
                    }

                }
                if(participedEvents.isEmpty()){
                    eventsTextView.setText(R.string.noEvent);
                }
                else
                callAdapter(participedEvents);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ne fait rien

            }
        });
    }

    @Override
    public void onClick(View v) {

        if(v== editProfile){
            Bundle bundle = new Bundle();
            FragmentEditProfile nextFrag = new FragmentEditProfile();
            nextFrag.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, nextFrag)
                    .addToBackStack(null)
                    .commit();

        }
    }



    public void callAdapter (ArrayList<Event> events){
        final ProfileListViewAdapter adapter = new ProfileListViewAdapter(
                getContext(), events, getActivity());
        mDynamicListView.setAdapter(adapter);
        mDynamicListView
                .setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                                                   View view, int position, long id) {
                        mDynamicListView.startDragging(position);
                        return true;
                    }
                });
    }

}
