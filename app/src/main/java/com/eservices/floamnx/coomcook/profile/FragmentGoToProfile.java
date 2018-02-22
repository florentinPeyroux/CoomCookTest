package com.eservices.floamnx.coomcook.profile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eservices.floamnx.coomcook.R;
import com.eservices.floamnx.coomcook.authentification.User;
import com.eservices.floamnx.coomcook.chat.FriendDetails;
import com.eservices.floamnx.coomcook.chat.FriendListFragment;
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


public class FragmentGoToProfile extends Fragment implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseUser;
    private DatabaseReference databasePastEvents;
    private DatabaseReference databaseFriendList;
    private DynamicListView mDynamicListView;
    private ImageView profilePicture;
    private TextView userName;
    private TextView surname;
    private TextView userAge;
    private TextView userLocation;
    private TextView userFood;
    private TextView userInterest;
    private TextView eventsTextView;
    private TextView contact;

    private String test;
    private String userId;
    private String userSurname;
    private String userPictureURL;
    private String myUsername;
    private String myPictureURL;
    private ArrayList<Event> participedEvents;
    private int vote;
    private int numVoters;
    private Button btn;
    private RatingBar ratingBar;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rView = inflater.inflate(R.layout.profile_layout_visitor, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        participedEvents = new ArrayList<>();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userId = bundle.getString("userId");
        } else {
            Toast.makeText(getContext(), "Utilisateur introuvable", Toast.LENGTH_LONG).show();
        }

        databaseUser = FirebaseDatabase.getInstance().getReference("users");
        databasePastEvents = FirebaseDatabase.getInstance().getReference("upcomingEvents");
        databaseFriendList = FirebaseDatabase.getInstance().getReference("friendList");

        profilePicture = (ImageView) rView.findViewById(R.id.userPicture);

        userName = (TextView) rView.findViewById(R.id.name);
        surname = (TextView) rView.findViewById(R.id.surname);
        userAge = (TextView) rView.findViewById(R.id.userAge);
        userLocation = (TextView) rView.findViewById(R.id.userLocation);
        userFood = (TextView) rView.findViewById(R.id.userFood);
        userInterest = (TextView) rView.findViewById(R.id.userInterest);
        eventsTextView = (TextView) rView.findViewById(R.id.lastEventsTextView);
        contact = (TextView) rView.findViewById(R.id.contactTextView);



        contact.setOnClickListener(this);


        ratingBar = (RatingBar) rView.findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        loadMyData();
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));
        btn = (Button) rView.findViewById(R.id.vote);
        btn.setOnClickListener(this);



        mDynamicListView = (DynamicListView) rView.findViewById(R.id.dynamic_listview);
        mDynamicListView.setDividerHeight(0);

        checkExistingEvents();
        loadUserData();
        return rView;
    }

    public void loadUserData() {
        databaseUser.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User data = dataSnapshot.getValue(User.class);
                userName.setText(data.getName());
                surname.setText(data.getSurname());
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
                ratingBar.setRating((int) ( (double)data.getNumStar()/ (double)numVoters));
                ImageUtil.displayRoundImage(profilePicture, data.getImageURL(), null);
                userSurname = data.getSurname();
                userPictureURL = data.getImageURL();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ne fait rien
            }
        });
    }


    public void checkExistingEvents() {
        databasePastEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Event event = postSnapshot.getValue(Event.class);
                    if (event.getEventOwnerId().equals(userId)
                            || event.getParticipantID1().equals(userId)
                            || event.getParticipantID2().equals(userId)
                            || event.getParticipantID3().equals(userId)
                            || event.getParticipantID4().equals(userId)) {
                        participedEvents.add(event);
                    }

                }
                if (participedEvents.isEmpty()) {
                    eventsTextView.setText(R.string.noEvent);
                } else
                    callAdapter(participedEvents);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ne fait rien quand on annule
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onClick(View v) {
        if (v == btn) {
            voter();
            Toast.makeText(getContext(),"Votre vote a été pris en compte",Toast.LENGTH_LONG).show();

        }

        if (v == contact) {
            checkIfMyFriend(userId);
        }
    }


    public void voter() {

        vote = (int) this.ratingBar.getRating();

        databaseUser.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User data = dataSnapshot.getValue(User.class);
                numVoters = data.getNumVoters();
                int voter = (data.getNumStar() + vote);
                numVoters++;
                data.setNumStar(voter);
                data.setNumVoters(numVoters);
                dataSnapshot.getRef().setValue(data);
                ratingBar.setRating((int) ((double) voter /(double) numVoters));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ne fait rien quand on annule
            }
        });
    }

    public void callAdapter(ArrayList<Event> events) {
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



    public void loadMyData() {
        databaseUser.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User data = dataSnapshot.getValue(User.class);
                myUsername = data.getSurname();
                myPictureURL = data.getImageURL();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ne fait rien quand on annule
            }
        });
    }


    public void checkIfMyFriend(final String userId) {
        final FriendDetails myDetails = new FriendDetails(firebaseAuth.getCurrentUser().getUid(), myUsername, myPictureURL);
        final FriendDetails friendDetails = new FriendDetails(userId, userSurname, userPictureURL);
        databaseFriendList.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    FriendDetails user = postSnapshot.getValue(FriendDetails.class);
                    if (user.getUserId().equals(userId)) {
                        test=user.getUserId();
                        Log.e("bagla",test);
                        FriendListFragment nextFrag = new FriendListFragment();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_frame, nextFrag)
                                .addToBackStack(null)
                                .commit();
                    }
                }
                databaseFriendList.child(firebaseAuth.getCurrentUser().getUid()).push().setValue(friendDetails);
                databaseFriendList.child(userId).push().setValue(myDetails);
                FriendListFragment nextFrag = new FriendListFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, nextFrag)
                        .addToBackStack(null)
                        .commit();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ne fait rien quand on annule
            }
        });

    }



}