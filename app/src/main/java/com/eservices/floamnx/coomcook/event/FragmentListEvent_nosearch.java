package com.eservices.floamnx.coomcook.event;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.eservices.floamnx.coomcook.R;
import com.eservices.floamnx.coomcook.authentification.LoginActivity;
import com.eservices.floamnx.coomcook.authentification.User;
import com.eservices.floamnx.coomcook.event.adapter.EventAdapter;
import com.eservices.floamnx.coomcook.event.model.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class FragmentListEvent_nosearch extends Fragment implements OnDismissCallback {

    private static final int INITIAL_DELAY_MILLIS = 300;
    private EventAdapter eventListAdapter;
    private ListView listView;
    private String userCity;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseUpcomingEvents;
    private DatabaseReference databaseUser;
    private List<Event> events;



    public FragmentListEvent_nosearch() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rView = inflater.inflate(R.layout.fragment_list_events_nosearch, container, false);

        events = new ArrayList<>();


        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        databaseUpcomingEvents = FirebaseDatabase.getInstance().getReference("upcomingEvents");
        databaseUser = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid());

        listView = (ListView) rView.findViewById(R.id.list_view);

        loadEntries();
        return rView;
    }

    private void loadEntries() {
        getUserCity();
        events.clear();
        databaseUpcomingEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Event event = postSnapshot.getValue(Event.class);
                    if (event.getEventCity().toLowerCase().contains(userCity)) {
                        events.add(event);
                        callAdapter(listView, events);
                    }

                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Probleme de base de données", Toast.LENGTH_LONG).show();

            }
        });


    }


    public void callAdapter(ListView listView, List<Event> events) {
        eventListAdapter = new EventAdapter(getContext(), events, getActivity());
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
                new SwipeDismissAdapter(eventListAdapter, this));
        swingBottomInAnimationAdapter.setAbsListView(listView);
        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(
                INITIAL_DELAY_MILLIS);
        listView.setClipToPadding(false);
        listView.setDivider(null);
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                8, r.getDisplayMetrics());
        listView.setDividerHeight(px);
        listView.setFadingEdgeLength(0);
        listView.setFitsSystemWindows(true);
        px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                r.getDisplayMetrics());
        listView.setPadding(px, px, px, px);
        listView.setScrollBarStyle(ListView.SCROLLBARS_OUTSIDE_OVERLAY);
        listView.setAdapter(swingBottomInAnimationAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDismiss(@NonNull final ViewGroup listView,
                          @NonNull final int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            eventListAdapter.remove(eventListAdapter.getItem(position));
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));

    }

    public void moveEventDatabase(DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Event event = postSnapshot.getValue(Event.class);
                    if (testDate(event.getEventDate(), event.getEventEndTime())) {
                        toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.e("KO ", databaseError.getMessage());
                                } else {
                                    Log.e("OK ", "now removing old record !");
                                    dataSnapshot.getRef().removeValue();
                                }

                            }

                        });

                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ne fait rien
            }


        });
    }


    public Boolean testDate(String eventDate, String eventTime) {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("d/M/yyyy");
        SimpleDateFormat tf = new SimpleDateFormat("H:m");

        String formattedDate = df.format(c.getTime());
        String formattedTime = tf.format(c.getTime());

        String[] rd = formattedDate.split("/");
        String[] rt = formattedTime.split(":");

        String[] ed = eventDate.split("/");
        String[] et = eventTime.split(":");

        if (Integer.parseInt(rd[2]) > Integer.parseInt(ed[2])) {
            return true;
        } else if (Integer.parseInt(rd[2]) == Integer.parseInt(ed[2]) && Integer.parseInt(rd[1]) > Integer.parseInt(ed[1])) {
            return true;
        } else if (Integer.parseInt(rd[2]) == Integer.parseInt(ed[2]) && Integer.parseInt(rd[1]) == Integer.parseInt(ed[1]) && Integer.parseInt(rd[0]) > Integer.parseInt(ed[0])) {
            return true;
        } else if (Integer.parseInt(rd[2]) == Integer.parseInt(ed[2]) && Integer.parseInt(rd[1]) == Integer.parseInt(ed[1]) && Integer.parseInt(rd[0]) == Integer.parseInt(ed[0]) &&
                Integer.parseInt(rt[1]) > Integer.parseInt(et[1])) {
            return true;
        } else
            return (Integer.parseInt(rd[2]) == Integer.parseInt(ed[2]) && Integer.parseInt(rd[1]) == Integer.parseInt(ed[1])
                && Integer.parseInt(rd[0]) == Integer.parseInt(ed[0]) &&
                Integer.parseInt(rt[1]) == Integer.parseInt(et[1]) && Integer.parseInt(rt[0]) > Integer.parseInt(et[0])) ;
    }

    public void getUserCity() {
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                User data = dataSnapshot.getValue(User.class);
                userCity = data.getCity().toLowerCase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ne fait rien
            }
        });


    }

}
