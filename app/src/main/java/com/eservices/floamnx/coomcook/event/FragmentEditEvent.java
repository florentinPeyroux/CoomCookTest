package com.eservices.floamnx.coomcook.event;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.eservices.floamnx.coomcook.R;
import com.eservices.floamnx.coomcook.authentification.LoginActivity;
import com.eservices.floamnx.coomcook.event.model.Event;
import com.eservices.floamnx.coomcook.event.util.ImageUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.Calendar;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class FragmentEditEvent extends Fragment implements View.OnClickListener {

    private TextView eventDate ;
    private TextView eventStartTime;
    private TextView eventEndTime;
    private TextView selectPicture;
    private TextView uploadPicture;
    private EditText eventName;
    private EditText eventPlate;
    private EditText eventInterest;
    private EditText eventCity;
    private EditText eventDescription;

    private ImageView eventPicture;

    private String eventId;
    private String downloadUrl;
    private String oldEventPicture;
    private String ownerPicture;
    private String ownerSurname;
    private String participant1;
    private String participant2;
    private String participant3;
    private String participant4;
    private String participant1pic;
    private String participant2pic;
    private String participant3pic;
    private String participant4pic;
    private String participant1Id;
    private String participant2Id;
    private String participant3Id;
    private String participant4Id;

    private Spinner eventParticipantsNbr;

    private Button saveButton;
    private Button cancelButton;
    private Button eventDateButton;
    private Button eventStartTimeButton;
    private Button eventEndTimeButton;
    private Button delete;

    private Uri filePath;
    private static final int PICK_IMAGE_REQUEST = 71;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseEvent;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private int nbrParticipants;
    private int maxParticipants;


    public FragmentEditEvent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rView = inflater.inflate(R.layout.fragment_edit_event, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
        }

        eventDate = (TextView) rView.findViewById(R.id.eventDate);
        eventStartTime = (TextView) rView.findViewById(R.id.eventStartTime);
        eventEndTime = (TextView) rView.findViewById(R.id.eventEndTime);
        selectPicture = (TextView) rView.findViewById(R.id.selectPicture);
        uploadPicture = (TextView) rView.findViewById(R.id.uploadPicture);

        eventParticipantsNbr = (Spinner) rView.findViewById(R.id.eventParticipantsNbr);

        eventName = (EditText) rView.findViewById(R.id.eventName);
        eventPlate = (EditText) rView.findViewById(R.id.eventFood);
        eventInterest = (EditText) rView.findViewById(R.id.eventInterest);
        eventCity = (EditText) rView.findViewById(R.id.eventCity);
        eventDescription = (EditText) rView.findViewById(R.id.eventDescription);


        delete = (Button) rView.findViewById(R.id.deleteEvent);
        delete.setOnClickListener(this);

        eventPicture = (ImageView) rView.findViewById(R.id.eventPicture);

        Integer[] items = new Integer[]{1, 2, 3, 4};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_spinner_item, items);
        eventParticipantsNbr.setAdapter(adapter);




        eventDateButton = (Button) rView.findViewById(R.id.buttonDate);
        eventDateButton.setOnClickListener(this);

        eventStartTimeButton = (Button) rView.findViewById(R.id.buttonStartTime);
        eventStartTimeButton.setOnClickListener(this);

        eventEndTimeButton = (Button) rView.findViewById(R.id.buttonEndTime);
        eventEndTimeButton.setOnClickListener(this);

        saveButton = (Button) rView.findViewById(R.id.saveEvent);
        saveButton.setOnClickListener(this);

        cancelButton = (Button) rView.findViewById(R.id.cancelEdit);
        cancelButton.setOnClickListener(this);


        selectPicture.setOnClickListener(this);
        uploadPicture.setOnClickListener(this);


        firebaseAuth = FirebaseAuth.getInstance();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            eventId = bundle.getString("eventId");
            Log.e("eventId", eventId);
        } else {
            Toast.makeText(getContext(), "Evénement introuvable", Toast.LENGTH_LONG).show();
        }

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));

        databaseEvent = FirebaseDatabase.getInstance().getReference("upcomingEvents").child(eventId);


        loadEventDetails();


        return rView;
    }

    private void loadEventDetails() {
        databaseEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event data = dataSnapshot.getValue(Event.class);
                eventName.setText(data.getEventName());
                eventInterest.setText(data.getEventInterest());
                eventPlate.setText(data.getEventFood());
                eventCity.setText(data.getEventCity());
                eventDescription.setText(data.getEventDescription());
                eventDate.setText(data.getEventDate());
                eventStartTime.setText(data.getEventStartTime());
                eventEndTime.setText(data.getEventEndTime());
                ImageUtil.displayImage(eventPicture, data.getEventFoodUrl(), null);

                oldEventPicture = data.getEventFoodUrl();
                nbrParticipants = data.getNbrParticipants();
                nbrParticipants = data.getMaxNbrPersons();
                ownerPicture = data.getEventOwnerPicture();
                ownerSurname = data.getEventOwnerSurname();
                participant1 = data.getParticipantName1();
                participant2 = data.getParticipantName2();
                participant3 = data.getParticipantName3();
                participant4 = data.getParticipantName4();
                participant1Id = data.getParticipantID1();
                participant2Id = data.getParticipantID2();
                participant3Id = data.getParticipantID3();
                participant4Id = data.getParticipantID4();
                participant1pic= data.getParticipantPicture1();
                participant2pic= data.getParticipantPicture2();
                participant3pic= data.getParticipantPicture3();
                participant4pic= data.getParticipantPicture4();


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
        if (view == eventDateButton) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            eventDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (view == eventStartTimeButton) {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            eventStartTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();
        }

        if (view == eventEndTimeButton) {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            eventEndTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();

        }

        if (view == saveButton) {
            editEvent();
        }
        if (view == selectPicture) {
            chooseImage();

        }

        if (view == uploadPicture) {
            uploadImage();

        }

        if (view == cancelButton) {
            cancelEdit();
        }


        if(view==delete){
            AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
            alert.setMessage("Souhaitez-vous vraiment supprimer cet événement ?");
            alert.setNegativeButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteEvent();
                }
            });
            alert.setPositiveButton("Non", null);
            alert.show();
        }
    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selectionnez une image"), PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
        }
    }

    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Transmission en cours ...");
            progressDialog.show();

            StorageReference ref = storageRef.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Image transférée", Toast.LENGTH_SHORT).show();
                            downloadUrl = taskSnapshot.getDownloadUrl().toString();
                            ImageUtil.displayImage(eventPicture, filePath.toString(), null);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Echec " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage((int) progress + "%");

                        }
                    });
        }
    }

    public void editEvent(){
        String title = eventName.getText().toString().trim();
        String food = eventPlate.getText().toString().trim();
        String interest = eventInterest.getText().toString().trim();
        String description = eventDescription.getText().toString().trim();
        String city = eventCity.getText().toString().trim();
        String date = eventDate.getText().toString();
        String starTime = eventStartTime.getText().toString();
        String endTime = eventEndTime.getText().toString();



        if(downloadUrl!=null){
            Event event = new Event(eventId,firebaseAuth.getCurrentUser().getUid(),
                    title,date,starTime,endTime,city,food,interest,maxParticipants,description,downloadUrl,ownerSurname, ownerPicture,
                    nbrParticipants, participant1, participant2,participant3, participant4, participant1Id, participant2Id, participant3Id, participant4Id,
                    participant1pic, participant2pic, participant3pic, participant4pic);
            databaseEvent.setValue(event);
            Bundle bundle = new Bundle();
            bundle.putString("eventId", eventId);
            FragmentEventDetails nextFrag = new FragmentEventDetails();
            nextFrag.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, nextFrag)
                    .addToBackStack(null)
                    .commit();
        }
        else{
            Event event = new Event(eventId,firebaseAuth.getCurrentUser().getUid(),
                    title,date,starTime,endTime,city,food,interest,nbrParticipants,description, oldEventPicture, ownerSurname, ownerPicture,
                    nbrParticipants, participant1, participant2,participant3, participant4, participant1Id, participant2Id, participant3Id, participant4Id,
                    participant1pic, participant2pic, participant3pic, participant4pic);
            databaseEvent.setValue(event);
            Bundle bundle = new Bundle();
            bundle.putString("eventId", eventId);
            FragmentEventDetails nextFrag = new FragmentEventDetails();
            nextFrag.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, nextFrag)
                    .addToBackStack(null)
                    .commit();

        }

    }

    public void cancelEdit(){
        Bundle bundle = new Bundle();
        bundle.putString("eventId", eventId);
        FragmentEventDetails nextFrag = new FragmentEventDetails();
        nextFrag.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, nextFrag)
                .addToBackStack(null)
                .commit();
    }

    public void deleteEvent() {
        databaseEvent.removeValue();
        FragmentListEvent_nosearch nextFrag = new FragmentListEvent_nosearch();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, nextFrag)
                .addToBackStack(null)
                .commit();
    }

}
