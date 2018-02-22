package com.eservices.floamnx.coomcook.event;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eservices.floamnx.coomcook.R;
import com.eservices.floamnx.coomcook.authentification.LoginActivity;
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


public class FragmentListEvent extends Fragment implements OnDismissCallback, View.OnClickListener {


    private static final int INITIAL_DELAY_MILLIS = 300;
    private EventAdapter eventListAdapter;
    ListView listView;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseUpcomingEvents;
    private List<Event> events;
    private List<Event> searchedEvents;

    //Search
    private EditText mSearchField;
    private TextView mXMark;
    private View mMicrofon;
    private ListView mListView;

    private TextView iconTitle;
    private TextView iconFood;
    private TextView iconInterest;
    private TextView iconCity;


    private TextView mFilters;
    private TextView mArrow;
    private LinearLayout mFiltersLayout;
    private TextView mSearchButton;


    public FragmentListEvent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rView = inflater.inflate(R.layout.fragment_list_events, container, false);

        events = new ArrayList<>();
        searchedEvents = new ArrayList<>();


        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        databaseUpcomingEvents = FirebaseDatabase.getInstance().getReference("upcomingEvents");

        listView = (ListView) rView.findViewById(R.id.list_view);

        loadEntries();
        loadSearchBar(rView);
        return rView;
    }

    private void loadEntries() {
        events.clear();
        databaseUpcomingEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Event event = postSnapshot.getValue(Event.class);
                    events.add(event);
                    callAdapter(listView, events);
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
        }else {
            return super.onOptionsItemSelected(item);
        }
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
        } else if (Integer.parseInt(rd[2]) == Integer.parseInt(ed[2]) && Integer.parseInt(rd[1]) == Integer.parseInt(ed[1])
                && Integer.parseInt(rd[0]) == Integer.parseInt(ed[0]) &&
                Integer.parseInt(rt[1]) == Integer.parseInt(et[1]) && Integer.parseInt(rt[0]) > Integer.parseInt(et[0])) {
            return true;
        } else {
            return false;
        }

    }


    public void loadSearchBar(View view) {
        iconTitle = (TextView) view.findViewById(R.id.iconTitle);
        iconFood = (TextView) view.findViewById(R.id.iconFood);
        iconInterest = (TextView) view.findViewById(R.id.iconInterest);
        iconCity = (TextView) view.findViewById(R.id.iconCity);

        mFilters = (TextView) view.findViewById(R.id.activity_search_bar_shop_filters);
        mArrow = (TextView) view.findViewById(R.id.activity_search_bar_shop_arrow);
        mSearchButton = (TextView) view.findViewById(R.id.search_button);
        mFiltersLayout = (LinearLayout) view.findViewById(R.id.activity_search_bar_shop_filters_layout);

        mFilters.setOnClickListener(this);

        mSearchButton.setOnClickListener(this);
        iconTitle.setOnClickListener(this);
        iconFood.setOnClickListener(this);
        iconInterest.setOnClickListener(this);
        iconCity.setOnClickListener(this);
        mArrow.setOnClickListener(this);

        mSearchField = (EditText) view.findViewById(R.id.search_field);
        mXMark = (TextView) view.findViewById(R.id.search_x);
        mMicrofon = view.findViewById(R.id.search_microfon);
        mListView = (ListView) view.findViewById(R.id.list_view);

        mXMark.setOnClickListener(this);
        mMicrofon.setOnClickListener(this);

        mSearchField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                //ne fait rien
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                //ne fait rien
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString().trim();
                if (iconTitle.getText() == getString(R.string.material_icon_check_full)) {
                    for (Event event : events) {
                        if (event.getEventName().toLowerCase()
                                .contains(searchText.toLowerCase())) {
                            searchedEvents.clear();

                            if(!searchedEvents.contains(event))
                            searchedEvents.add(event);
                        }
                    }
                    if (searchText.isEmpty()) {
                        mListView.setAdapter(null);
                        mXMark.setText(R.string.fontello_x_mark);
                    } else {
                        callAdapter(listView,searchedEvents);
                        mXMark.setText(R.string.fontello_x_mark_masked);
                    }
                }

                else if (iconFood.getText() == getString(R.string.material_icon_check_full)) {
                    for (Event event : events) {
                        if (event.getEventFood().toLowerCase()
                                .contains(searchText.toLowerCase())) {
                            if(!searchedEvents.contains(event))
                                searchedEvents.add(event);
                        }
                    }
                    if (searchText.isEmpty()) {
                        mListView.setAdapter(null);
                        mXMark.setText(R.string.fontello_x_mark);
                    } else {
                        callAdapter(listView,searchedEvents);
                        mXMark.setText(R.string.fontello_x_mark_masked);
                    }
                }

                else if (iconInterest.getText() == getString(R.string.material_icon_check_full)) {
                    for (Event event : events) {
                        if (event.getEventInterest().toLowerCase()
                                .contains(searchText.toLowerCase())) {
                            searchedEvents.clear();

                            if(!searchedEvents.contains(event))
                                searchedEvents.add(event);

                        }
                    }
                    if (searchText.isEmpty()) {
                        mListView.setAdapter(null);
                        mXMark.setText(R.string.fontello_x_mark);
                    } else {
                        callAdapter(listView,searchedEvents);
                        mXMark.setText(R.string.fontello_x_mark_masked);
                    }
                }

                else if (iconCity.getText() == getString(R.string.material_icon_check_full)) {
                    for (Event event : events) {
                        if (event.getEventCity().toLowerCase()
                                .contains(searchText.toLowerCase())) {
                            searchedEvents.clear();
                            if(!searchedEvents.contains(event))
                                searchedEvents.add(event);

                        }
                    }
                    if (searchText.isEmpty()) {
                        mListView.setAdapter(null);
                        mXMark.setText(R.string.fontello_x_mark);
                    } else {
                        callAdapter(listView,searchedEvents);

                        mXMark.setText(R.string.fontello_x_mark_masked);
                    }
                }

                else {
                    for (Event event : events) {
                        if (event.getEventName().toLowerCase()
                                .contains(searchText.toLowerCase())) {
                            searchedEvents.clear();
                            if(!searchedEvents.contains(event))
                                searchedEvents.add(event);

                        }
                    }
                    if (searchText.isEmpty()) {
                        mListView.setAdapter(null);
                        mXMark.setText(R.string.fontello_x_mark);
                    } else {

                        callAdapter(listView,searchedEvents);
                        mXMark.setText(R.string.fontello_x_mark_masked);
                    }
                }

            }
            });
        }

        @Override
        public void onClick (View view){
            switch (view.getId()) {
                case R.id.search_x:
                    mSearchField.setText(null);
                    searchedEvents.clear();
                    loadEntries();

                    break;
                case R.id.search_microfon:
                    Toast.makeText(getContext(), "pas encore implémenté", Toast.LENGTH_LONG).show();
                    break;
                case R.id.iconTitle:
                    if (iconTitle.getText() == getString(R.string.material_icon_check_empty)) {
                        iconTitle.setText(getString(R.string.material_icon_check_full));
                    } else {
                        iconTitle.setText(getString(R.string.material_icon_check_empty));
                    }
                    break;
                case R.id.iconFood:
                    if (iconFood.getText() == getString(R.string.material_icon_check_empty)) {
                        iconFood.setText(getString(R.string.material_icon_check_full));
                    } else {
                        iconFood.setText(getString(R.string.material_icon_check_empty));
                    }
                    break;
                case R.id.iconInterest:
                    if (iconInterest.getText() == getString(R.string.material_icon_check_empty)) {
                        iconInterest.setText(getString(R.string.material_icon_check_full));
                    } else {
                        iconInterest.setText(getString(R.string.material_icon_check_empty));
                    }
                    break;
                case R.id.iconCity:
                    if (iconCity.getText() == getString(R.string.material_icon_check_empty)) {
                        iconCity.setText(getString(R.string.material_icon_check_full));
                    } else {
                        iconCity.setText(getString(R.string.material_icon_check_empty));
                    }
                    break;

                case R.id.activity_search_bar_shop_filters:
                    if (mFiltersLayout.getVisibility() == View.VISIBLE) {
                        mFiltersLayout.setVisibility(View.GONE);
                    } else {
                        mFiltersLayout.setVisibility(View.VISIBLE);
                    }
                    if (mArrow.getText() == getString(R.string.material_icon_chevron_up)) {
                        mArrow.setText(getString(R.string.material_icon_chevron_down));
                    } else {
                        mArrow.setText(getString(R.string.material_icon_chevron_up));
                    }
                    break;
                case R.id.activity_search_bar_shop_arrow:
                    if (mFiltersLayout.getVisibility() == View.VISIBLE) {
                        mFiltersLayout.setVisibility(View.GONE);
                    } else {
                        mFiltersLayout.setVisibility(View.VISIBLE);
                    }
                    if (mArrow.getText() == getString(R.string.material_icon_chevron_up)) {
                        mArrow.setText(getString(R.string.material_icon_chevron_down));
                    } else {
                        mArrow.setText(getString(R.string.material_icon_chevron_up));
                    }
                    break;
                case R.id.search_button:
                    Toast.makeText(getContext(), "Recherche en cours ...", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
