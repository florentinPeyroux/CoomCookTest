package com.eservices.floamnx.coomcook.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.eservices.floamnx.coomcook.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

import java.util.ArrayList;


public class FriendListFragment extends Fragment{

    private DynamicListView mDynamicListView;
    private DatabaseReference databaseFriendList;
    private FirebaseAuth firebaseAuth;
    private ArrayList<FriendDetails> friendDetails;

    public FriendListFragment() {
        //ne fait rien
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rView = inflater.inflate(R.layout.activity_users, container, false);

        mDynamicListView = (DynamicListView) rView.findViewById(R.id.dynamic_listview);
        mDynamicListView.setDividerHeight(0);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseFriendList = FirebaseDatabase.getInstance().getReference("friendList").child(firebaseAuth.getCurrentUser().getUid());

        friendDetails = new ArrayList<>();

        laodMyFriendList();

        return rView;
    }

    public void laodMyFriendList() {
        databaseFriendList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                for (com.google.firebase.database.DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    FriendDetails userData = postSnapshot.getValue(FriendDetails.class);
                    friendDetails.add(userData);
                }

                callFriendListAdapter(friendDetails);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ne fait rien
            }
        });

    }

    private void callFriendListAdapter(ArrayList<FriendDetails> friendDetails) {
        final FriendListAdapter adapter = new FriendListAdapter(
                getContext(), friendDetails,getActivity());
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
