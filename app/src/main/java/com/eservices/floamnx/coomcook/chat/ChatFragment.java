package com.eservices.floamnx.coomcook.chat;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.eservices.floamnx.coomcook.R;
import com.eservices.floamnx.coomcook.authentification.User;
import com.eservices.floamnx.coomcook.event.util.ImageUtil;
import com.eservices.floamnx.coomcook.font.MaterialDesignIconsTextView;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class ChatFragment extends Fragment implements View.OnClickListener {


    private LinearLayout layout;
    private MaterialDesignIconsTextView sendButton;
    private EditText messageArea;
    private ScrollView scrollView;
    private Firebase reference1;
    private Firebase reference2;
    private ImageView partnerPicture;
    private TextView partnerSurname;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference db;
    private String partnerId = "";

    public ChatFragment() {
        //ne fait rien
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rView = inflater.inflate(R.layout.activity_chat, container, false);

        layout = (LinearLayout) rView.findViewById(R.id.layout1);
        sendButton = (MaterialDesignIconsTextView) rView.findViewById(R.id.sendButton);
        messageArea = (EditText) rView.findViewById(R.id.messageEditText);
        scrollView = (ScrollView) rView.findViewById(R.id.scrollView);

        partnerPicture = (ImageView) rView.findViewById(R.id.partnerPicture);
        partnerSurname = (TextView) rView.findViewById(R.id.partnerName);


        sendButton.setOnClickListener(this);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            partnerId = bundle.getString("userId");
        } else {
            Toast.makeText(getContext(), "Partenaire introuvable", Toast.LENGTH_LONG).show();
        }


        firebaseAuth = FirebaseAuth.getInstance();
        Firebase.setAndroidContext(getContext());
        reference1 = new Firebase("https://coomcook-85c8d.firebaseio.com/messages/" + firebaseAuth.getCurrentUser().getUid() + "_" + partnerId);
        reference2 = new Firebase("https://coomcook-85c8d.firebaseio.com/messages/" + partnerId + "_" + firebaseAuth.getCurrentUser().getUid());

        db = FirebaseDatabase.getInstance().getReference("users").child(partnerId);


        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if (userName.equals(firebaseAuth.getCurrentUser().getUid())) {
                    addMessageBox(message, 1);
                } else {
                    addMessageBox(message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //ne fait rien
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //ne fait rien
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //ne fait rien
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //ne fait rien
            }
        });

        getPartnerInfos();

        return rView;
    }


    @Override
    public void onClick(View view) {
        if (view == sendButton) {
            sendMessage();
        }
    }

    private void sendMessage() {
        String messageText = messageArea.getText().toString();
        if (!messageText.equals("")) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("message", messageText);
            map.put("user", firebaseAuth.getCurrentUser().getUid());
            reference1.push().setValue(map);
            reference2.push().setValue(map);
            messageArea.setText("");
        }
    }

    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(getActivity());
        textView.setPadding(14, 9, 14, 9);
        textView.setTextColor(Color.WHITE);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;
        lp2.setMargins(0, 2, 0, 2);


        if (type == 1) {
            lp2.gravity = Gravity.START;
            textView.setBackgroundResource(R.drawable.bg_msg_to);
        } else {
            lp2.gravity = Gravity.END;
            textView.setBackgroundResource(R.drawable.bg_msg_from);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public void getPartnerInfos() {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                User data = dataSnapshot.getValue(User.class);
                partnerSurname.setText(data.getSurname());
                ImageUtil.displayRoundImage(partnerPicture, data.getImageURL(), null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ne fait rien
            }
        });


    }


}
