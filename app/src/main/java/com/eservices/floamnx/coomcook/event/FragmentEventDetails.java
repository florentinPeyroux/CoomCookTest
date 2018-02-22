package com.eservices.floamnx.coomcook.event;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.eservices.floamnx.coomcook.R;
import com.eservices.floamnx.coomcook.authentification.LoginActivity;
import com.eservices.floamnx.coomcook.authentification.User;
import com.eservices.floamnx.coomcook.event.model.Event;
import com.eservices.floamnx.coomcook.event.util.ImageUtil;
import com.eservices.floamnx.coomcook.profile.FragmentGoToProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class FragmentEventDetails extends Fragment implements View.OnClickListener {

    private String eventId;
    private String ownerID;
    private String participant1ID;
    private String participant2ID;
    private String participant3ID;
    private String participant4ID;

    private TextView eventOwner;
    private TextView eventTitle;
    private TextView eventInterest;
    private TextView eventDescription;
    private TextView eventFood;
    private TextView eventAllowedParticipantsNbr;
    private TextView eventParticipant1;
    private TextView eventParticipant2;
    private TextView eventParticipant3;
    private TextView eventParticipant4;
    private TextView date;
    private TextView startTime;
    private TextView endTime;
    private TextView eventCity;

    private ImageView foodPicture;
    private ImageView profilePic1;
    private ImageView profilePic2;
    private ImageView profilePic3;
    private ImageView profilePic4;
    private ImageView ownerPic;

    private String eventOwnerId;
    private String picURL;

    private Button eventJoinBtn;
    private Button eventEditBtn;
    private Button eventCancelBtn;



    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseEvent;
    private DatabaseReference databaseUser;

    private ArrayList<String> data = new ArrayList<>();
    private String ownerPicURL;

    public FragmentEventDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rView = inflater.inflate(R.layout.fragment_event_details, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
        }

        eventOwner = (TextView) rView.findViewById(R.id.eventOwnerName);
        eventTitle = (TextView) rView.findViewById(R.id.eventTitle);
        eventFood = (TextView) rView.findViewById(R.id.eventFood);
        eventInterest = (TextView) rView.findViewById(R.id.eventInterest);
        eventDescription = (TextView) rView.findViewById(R.id.eventDescription);
        eventCity = (TextView) rView.findViewById(R.id.eventCity);
        eventAllowedParticipantsNbr = (TextView) rView.findViewById(R.id.eventAllowedPlaces);
        eventParticipant1 = (TextView) rView.findViewById(R.id.eventParticipant1);
        eventParticipant2 = (TextView) rView.findViewById(R.id.eventParticipant2);
        eventParticipant3 = (TextView) rView.findViewById(R.id.eventParticipant3);
        eventParticipant4 = (TextView) rView.findViewById(R.id.eventParticipant4);

        date = (TextView) rView.findViewById(R.id.date);
        startTime = (TextView) rView.findViewById(R.id.startTime);
        endTime = (TextView) rView.findViewById(R.id.endTime);

        ownerPic = (ImageView) rView.findViewById(R.id.eventOwnerPic);
        profilePic1 = (ImageView) rView.findViewById(R.id.profile1);
        profilePic2 = (ImageView) rView.findViewById(R.id.profile2);
        profilePic3 = (ImageView) rView.findViewById(R.id.profile3);
        profilePic4 = (ImageView) rView.findViewById(R.id.profile4);
        foodPicture = (ImageView) rView.findViewById(R.id.foodPicture);

        eventJoinBtn = (Button) rView.findViewById(R.id.eventJoin);
        eventJoinBtn.setOnClickListener(this);

        eventEditBtn = (Button) rView.findViewById(R.id.eventEdit);
        eventEditBtn.setOnClickListener(this);

        eventCancelBtn = (Button) rView.findViewById(R.id.eventCancel);
        eventCancelBtn.setOnClickListener(this);



        ownerPic.setOnClickListener(this);
        profilePic1.setOnClickListener(this);
        profilePic2.setOnClickListener(this);
        profilePic3.setOnClickListener(this);
        profilePic4.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            eventId = bundle.getString("eventId");
            Log.e("eventId", eventId);
        } else {
            Toast.makeText(getContext(), "Evénement introuvable", Toast.LENGTH_LONG).show();
        }

        databaseEvent = FirebaseDatabase.getInstance().getReference("upcomingEvents").child(eventId);
        databaseUser = FirebaseDatabase.getInstance().getReference("users");


        loadEventDetails();
        checkUser();
        loadUserDetails(firebaseAuth.getCurrentUser().getUid());
        setButtonVisibility();


        return rView;
    }

    private void loadEventDetails() {
        databaseEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event dataLocal = dataSnapshot.getValue(Event.class);
                picURL = dataLocal.getEventOwnerPicture();
                eventOwner.setText(dataLocal.getEventOwnerSurname());
                eventTitle.setText(dataLocal.getEventName());
                date.setText(dataLocal.getEventDate());
                startTime.setText(dataLocal.getEventStartTime());
                endTime.setText(dataLocal.getEventEndTime());
                eventInterest.setText(dataLocal.getEventInterest());
                eventDescription.setText(dataLocal.getEventDescription());
                eventFood.setText(dataLocal.getEventFood());
                eventCity.setText(dataLocal.getEventCity().concat(", France"));

                ownerID = dataLocal.getEventOwnerId();

                participant1ID = dataLocal.getParticipantID1();
                participant2ID = dataLocal.getParticipantID2();
                participant3ID = dataLocal.getParticipantID3();
                participant4ID = dataLocal.getParticipantID4();
                ImageUtil.displayRoundImage(ownerPic, picURL, null);

                ImageUtil.displayImage(foodPicture, dataLocal.getEventFoodUrl(), null);
                eventAllowedParticipantsNbr.setText(String.valueOf(dataLocal.getMaxNbrPersons()));


                //Chargement de la photo de l'organisateur
                ImageUtil.displayRoundImage(ownerPic,
                        ownerPicURL, null);

                if (dataLocal.getMaxNbrPersons() == 1) {
                    ImageUtil.displayRoundImage(profilePic1, dataLocal.getParticipantPicture1(), null);
                    eventParticipant1.setText(dataLocal.getParticipantName1());
                    eventParticipant2.setVisibility(View.GONE);
                    eventParticipant3.setVisibility(View.GONE);
                    eventParticipant4.setVisibility(View.GONE);
                    profilePic2.setVisibility(View.GONE);
                    profilePic3.setVisibility(View.GONE);
                    profilePic4.setVisibility(View.GONE);

                }
                if (dataLocal.getMaxNbrPersons() == 2) {
                    ImageUtil.displayRoundImage(profilePic1, dataLocal.getParticipantPicture1(), null);
                    ImageUtil.displayRoundImage(profilePic2, dataLocal.getParticipantPicture2(), null);
                    eventParticipant1.setText(dataLocal.getParticipantName1());
                    eventParticipant2.setText(dataLocal.getParticipantName2());
                    eventParticipant3.setVisibility(View.GONE);
                    eventParticipant4.setVisibility(View.GONE);
                    profilePic3.setVisibility(View.GONE);
                    profilePic4.setVisibility(View.GONE);

                }
                if (dataLocal.getMaxNbrPersons() == 3) {
                    ImageUtil.displayRoundImage(profilePic1, dataLocal.getParticipantPicture1(), null);
                    ImageUtil.displayRoundImage(profilePic2, dataLocal.getParticipantPicture2(), null);
                    ImageUtil.displayRoundImage(profilePic3, dataLocal.getParticipantPicture3(), null);
                    eventParticipant1.setText(dataLocal.getParticipantName1());
                    eventParticipant2.setText(dataLocal.getParticipantName2());
                    eventParticipant3.setText(dataLocal.getParticipantName3());
                    eventParticipant4.setVisibility(View.GONE);
                    profilePic4.setVisibility(View.GONE);


                } else {

                    ImageUtil.displayRoundImage(profilePic1, dataLocal.getParticipantPicture1(), null);
                    ImageUtil.displayRoundImage(profilePic2, dataLocal.getParticipantPicture2(), null);
                    ImageUtil.displayRoundImage(profilePic3, dataLocal.getParticipantPicture3(), null);
                    ImageUtil.displayRoundImage(profilePic4, dataLocal.getParticipantPicture4(), null);
                    eventParticipant1.setText(dataLocal.getParticipantName1());
                    eventParticipant2.setText(dataLocal.getParticipantName2());
                    eventParticipant3.setText(dataLocal.getParticipantName3());
                    eventParticipant4.setText(dataLocal.getParticipantName4());

                }
                if (!eventParticipant1.getText().equals(firebaseAuth.getCurrentUser().getUid())
                        && !eventOwner.getText().equals(firebaseAuth.getCurrentUser().getUid())
                        && !eventParticipant2.getText().equals(firebaseAuth.getCurrentUser().getUid())
                        && !eventParticipant3.getText().equals(firebaseAuth.getCurrentUser().getUid())
                        && !eventParticipant4.getText().equals(firebaseAuth.getCurrentUser().getUid())) {
                    eventJoinBtn.setVisibility(View.VISIBLE);
                    eventCancelBtn.setVisibility(View.GONE);
                } else if (eventOwner.getText().equals(firebaseAuth.getCurrentUser().getUid())) {
                    eventJoinBtn.setVisibility(View.VISIBLE);
                } else {
                    eventJoinBtn.setVisibility(View.GONE);
                    eventCancelBtn.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ne fait rien
            }

        });
    }

    public void checkUser() {
        databaseEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                eventOwnerId = event.getEventOwnerId();
                if (eventOwnerId.equals(firebaseAuth.getCurrentUser().getUid())) {
                    eventJoinBtn.setVisibility(View.INVISIBLE);
                } else
                    eventEditBtn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ne fait rien
            }
        });
    }


    public void joinEvent() {
        databaseEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                int availablePlaces = event.getMaxNbrPersons();
                int participantsNbr = event.getNbrParticipants();
                if (participantsNbr == availablePlaces) {
                    Toast.makeText(getContext(), "Evénement complet", Toast.LENGTH_LONG).show();
                } else {
                    if (event.getParticipantName1().equals("vide")
                            && !event.getParticipantName2().equals( firebaseAuth.getCurrentUser().getUid())
                            && !event.getParticipantName3().equals(firebaseAuth.getCurrentUser().getUid())
                            && !event.getParticipantName4().equals( firebaseAuth.getCurrentUser().getUid())) {
                        event.setParticipantID1(firebaseAuth.getCurrentUser().getUid());
                        event.setParticipantName1(data.get(0));
                        event.setParticipantPicture1(data.get(1));
                        event.setNbrParticipants(participantsNbr + 1);
                        dataSnapshot.getRef().setValue(event);
                        eventParticipant1.setText(data.get(0));
                        ImageUtil.displayRoundImage(profilePic1, data.get(1), null);
                    } else if (event.getParticipantName2().equals("vide")
                            && !event.getParticipantName1().equals(firebaseAuth.getCurrentUser().getUid())
                            && !event.getParticipantName3().equals(firebaseAuth.getCurrentUser().getUid())
                            && !event.getParticipantName4().equals(firebaseAuth.getCurrentUser().getUid())) {
                        event.setParticipantID2(firebaseAuth.getCurrentUser().getUid());
                        event.setParticipantName2(data.get(0));
                        event.setParticipantPicture2(data.get(1));
                        event.setNbrParticipants(participantsNbr + 1);
                        dataSnapshot.getRef().setValue(event);
                        eventParticipant2.setText(data.get(0));
                        ImageUtil.displayRoundImage(profilePic2, data.get(1), null);
                    } else if (event.getParticipantName3().equals("vide") && event.getParticipantName2() != firebaseAuth.getCurrentUser().getUid()
                            && !event.getParticipantName1().equals(firebaseAuth.getCurrentUser().getUid())
                            && !event.getParticipantName4().equals(firebaseAuth.getCurrentUser().getUid())) {
                        event.setParticipantID3(firebaseAuth.getCurrentUser().getUid());
                        event.setParticipantName3(data.get(0));
                        event.setParticipantPicture3(data.get(1));
                        event.setNbrParticipants(participantsNbr + 1);
                        dataSnapshot.getRef().setValue(event);
                        eventParticipant3.setText(data.get(0));
                        ImageUtil.displayRoundImage(profilePic3, data.get(1), null);
                    } else {
                        event.setParticipantID4(firebaseAuth.getCurrentUser().getUid());
                        event.setParticipantName4(data.get(0));
                        event.setParticipantPicture4(data.get(1));
                        event.setNbrParticipants(participantsNbr + 1);
                        dataSnapshot.getRef().setValue(event);
                        eventParticipant4.setText(data.get(0));
                        ImageUtil.displayRoundImage(profilePic4, data.get(1), null);
                    }
                    setButtonVisibility();
                    eventJoinBtn.setVisibility(View.GONE);

                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ne fait rien
            }
        });


    }

    public void cancelEvent() {
        databaseEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                if (event.getParticipantID1().equals(firebaseAuth.getCurrentUser().getUid())) {
                    event.setParticipantName1("vide");
                    event.setParticipantID1("vide");
                    eventParticipant1.setText("vide");
                    ImageUtil.displayRoundImage(profilePic1,
                            "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973461_960_720.png", null);
                    event.setParticipantPicture1("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973461_960_720.png");
                    event.setNbrParticipants(event.getNbrParticipants() - 1);
                    dataSnapshot.getRef().setValue(event);

                }
                if (event.getParticipantID2().equals(firebaseAuth.getCurrentUser().getUid())) {
                    event.setParticipantID2("vide");
                    event.setParticipantName2("vide");
                    eventParticipant2.setText(R.string.vide);
                    ImageUtil.displayRoundImage(profilePic2,
                            "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973461_960_720.png", null);
                    event.setParticipantPicture2("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973461_960_720.png");
                    event.setNbrParticipants(event.getNbrParticipants() - 1);
                    dataSnapshot.getRef().setValue(event);

                }

                if (event.getParticipantID3().equals(firebaseAuth.getCurrentUser().getUid())) {
                    event.setParticipantID3("vide");
                    event.setParticipantName3("vide");
                    eventParticipant3.setText(R.string.vide);
                    ImageUtil.displayRoundImage(profilePic3,
                            "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973461_960_720.png", null);
                    event.setParticipantPicture3("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973461_960_720.png");
                    event.setNbrParticipants(event.getNbrParticipants() - 1);
                    dataSnapshot.getRef().setValue(event);

                }

                if (event.getParticipantID4().equals(firebaseAuth.getCurrentUser().getUid())) {
                    event.setParticipantID4("vide");
                    event.setParticipantName4("vide");
                    eventParticipant4.setText(R.string.vide);
                    ImageUtil.displayRoundImage(profilePic4,
                            "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973461_960_720.png", null);
                    event.setParticipantPicture4("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973461_960_720.png");
                    event.setNbrParticipants(event.getNbrParticipants() - 1);
                    dataSnapshot.getRef().setValue(event);

                }

                eventCancelBtn.setVisibility(View.GONE);
                eventJoinBtn.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ne fait rien
            }
        });
    }


    public void setButtonVisibility() {
        databaseEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                String id1 = event.getParticipantID1();
                String id2 = event.getParticipantID2();
                String id3 = event.getParticipantID3();
                String id4 = event.getParticipantID4();


                if (id1.equals(firebaseAuth.getCurrentUser().getUid())
                        || id2.equals(firebaseAuth.getCurrentUser().getUid())
                        || id3.equals(firebaseAuth.getCurrentUser().getUid())
                        || id4.equals(firebaseAuth.getCurrentUser().getUid())) {
                    eventCancelBtn.setVisibility(View.VISIBLE);
                    eventJoinBtn.setVisibility(View.GONE);
                } 
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ne fait rien
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));


    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
        if (view == eventJoinBtn) {
            alert.setMessage(getResources().getString(R.string.participation));
            alert.setNegativeButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    joinEvent();
                }
            });
            alert.setPositiveButton("Non", null);
            alert.show();
        } else if (view == eventCancelBtn) {
            alert.setMessage(getResources().getString(R.string.annuler_participation));
            alert.setNegativeButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelEvent();
                }
            });
            alert.setPositiveButton("Non", null);
            alert.show();
        } else if (view == ownerPic) {
            if (!firebaseAuth.getCurrentUser().getUid().equals(ownerID)) {
                Bundle bundle = new Bundle();
                bundle.putString("userId", eventOwnerId);
                FragmentGoToProfile nextFrag = new FragmentGoToProfile();
                nextFrag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, nextFrag)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(getContext(), "Votre profil", Toast.LENGTH_LONG).show();
            }
        } else if (view == profilePic1) {
            if (!eventParticipant1.getText().equals("vide")) {
                if (!firebaseAuth.getCurrentUser().getUid().equals(participant1ID)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", participant1ID);
                    FragmentGoToProfile nextFrag = new FragmentGoToProfile();
                    nextFrag.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, nextFrag)
                            .addToBackStack(null)
                            .commit();
                }
            } else {
                Toast.makeText(getContext(), "Votre profil", Toast.LENGTH_LONG).show();
            }

        } else if (view == profilePic2) {
            if (!eventParticipant2.getText().equals("vide")) {
                if (!firebaseAuth.getCurrentUser().getUid().equals(participant2ID)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", participant2ID);
                    FragmentGoToProfile nextFrag = new FragmentGoToProfile();
                    nextFrag.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, nextFrag)
                            .addToBackStack(null)
                            .commit();
                }
            } else {
                Toast.makeText(getContext(), "Votre profil", Toast.LENGTH_LONG).show();
            }

        } else if (view == profilePic3) {
            if (!eventParticipant3.getText().equals("vide")) {
                if (!firebaseAuth.getCurrentUser().getUid().equals(participant3ID)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", participant3ID);
                    FragmentGoToProfile nextFrag = new FragmentGoToProfile();
                    nextFrag.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, nextFrag)
                            .addToBackStack(null)
                            .commit();
                }
            } else {
                Toast.makeText(getContext(), "Votre profil", Toast.LENGTH_LONG).show();
            }

        } else if (view == profilePic4) {
            if (!eventParticipant4.getText().equals("vide")) {
                if (!firebaseAuth.getCurrentUser().getUid().equals(participant4ID)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", participant4ID);
                    FragmentGoToProfile nextFrag = new FragmentGoToProfile();
                    nextFrag.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, nextFrag)
                            .addToBackStack(null)
                            .commit();
                }
            } else {
                Toast.makeText(getContext(), "Votre profil", Toast.LENGTH_LONG).show();
            }

        }

        if (view == eventEditBtn) {
            Bundle bundle = new Bundle();
            bundle.putString("eventId", eventId);
            FragmentEditEvent nextFrag = new FragmentEditEvent();
            nextFrag.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, nextFrag)
                    .addToBackStack(null)
                    .commit();
        }


    }

    public void loadUserDetails(String userID) {
        DatabaseReference db = databaseUser.child(userID);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                String userPicture = user.getImageURL();
                String userSurname = user.getSurname();
                data.add(userSurname);
                data.add(userPicture);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ne fait rien
            }
        });


    }

}