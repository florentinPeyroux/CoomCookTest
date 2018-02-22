package com.eservices.floamnx.coomcook.event;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.eservices.floamnx.coomcook.authentification.User;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class FragmentCreateEvent extends Fragment implements View.OnClickListener {

    private EditText editTextEventName;
    private EditText editTextEventCity;
    private EditText editTextEventFood;
    private EditText editTextEventInterest;
    private EditText editTextEventDescription;

    private ImageView eventPicture;
    private Button eventStartTimeBtn;
    private Button eventEndTimeBtn;
    private Button eventDateBtn;

    private TextView textViewEventDate;
    private TextView textViewEventStartTime;
    private TextView textViewEventEndTime;
    private TextView selectEventPicture;
    private TextView uploadEventPicture;
    private TextView saveEvent;

    private Spinner spinnerEventNbrPersonnes;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseEvents;
    private DatabaseReference databaseUsers;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri downloadUrl;

    private int mHour;
    private int mMinute;

    private ArrayList<String> ownerData;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rView = inflater.inflate(R.layout.fragment_create_event, container, false);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));

        editTextEventName = (EditText) rView.findViewById(R.id.eventName);
        editTextEventCity = (EditText) rView.findViewById(R.id.eventCity);
        editTextEventFood = (EditText) rView.findViewById(R.id.eventFood);
        editTextEventInterest = (EditText) rView.findViewById(R.id.eventInterest);
        editTextEventDescription = (EditText) rView.findViewById(R.id.eventDescription);

        textViewEventDate = (TextView) rView.findViewById(R.id.eventDate);
        textViewEventStartTime = (TextView) rView.findViewById(R.id.eventStartTime);
        textViewEventEndTime = (TextView) rView.findViewById(R.id.eventEndTime);
        saveEvent = (TextView) rView.findViewById(R.id.saveEvent);

        eventPicture = (ImageView) rView.findViewById(R.id.eventPicture);

        eventStartTimeBtn = (Button) rView.findViewById(R.id.buttonStartTime);
        eventEndTimeBtn = (Button) rView.findViewById(R.id.buttonEndTime);
        eventDateBtn = (Button) rView.findViewById(R.id.buttonDate);

        selectEventPicture = (TextView) rView.findViewById(R.id.selectPicture);
        uploadEventPicture = (TextView) rView.findViewById(R.id.uploadPicture);

        spinnerEventNbrPersonnes = (Spinner) rView.findViewById(R.id.eventParticipantsNbr);
        Integer[] items = new Integer[]{1, 2, 3, 4};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_spinner_item, items);
        spinnerEventNbrPersonnes.setAdapter(adapter);

        ownerData = new ArrayList<>();


        firebaseAuth = FirebaseAuth.getInstance();

        databaseEvents = FirebaseDatabase.getInstance().getReference("upcomingEvents");
        databaseUsers = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid());


        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        eventStartTimeBtn.setOnClickListener(this);
        eventEndTimeBtn.setOnClickListener(this);
        eventDateBtn.setOnClickListener(this);
        selectEventPicture.setOnClickListener(this);
        uploadEventPicture.setOnClickListener(this);
        saveEvent.setOnClickListener(this);

        loadOwnerDetails();
        return rView;
    }


    @Override
    public void onClick(View v) {
        if (v == eventDateBtn) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            textViewEventDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();

        }

        if (v == eventStartTimeBtn) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            textViewEventStartTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();
        }

        if (v == eventEndTimeBtn) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            textViewEventEndTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();
        }


        if (v == saveEvent) {
            addEvent();
            Toast.makeText(getContext(), "Evenement ajouté", Toast.LENGTH_SHORT).show();
            FragmentListEvent nextFrag = new FragmentListEvent();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, nextFrag)
                    .addToBackStack(null)
                    .commit();

        }
        if (v == selectEventPicture) {
            chooseImage();
        }

        if (v == uploadEventPicture) {
            uploadImage();
        }
    }


    public void addEvent() {
        String eventId = databaseEvents.push().getKey();
        String eventName = editTextEventName.getText().toString().trim();
        String eventDate = textViewEventDate.getText().toString().trim();
        String eventStartTime = textViewEventStartTime.getText().toString().trim();
        String eventEndTime = textViewEventEndTime.getText().toString().trim();
        String eventCity = editTextEventCity.getText().toString().trim();
        String eventFood = editTextEventFood.getText().toString().trim();
        String eventInterest = editTextEventInterest.getText().toString().trim();
        int eventNbrPersons = Integer.parseInt(spinnerEventNbrPersonnes.getSelectedItem().toString());

        String eventDescription = editTextEventDescription.getText().toString().trim();
        String uriImage = downloadUrl.toString();


        if (!TextUtils.isEmpty(eventDate) && !TextUtils.isEmpty(eventName)) {

            Event event = new Event(eventId, firebaseAuth.getCurrentUser().getUid(), ownerData.get(0), ownerData.get(1),
                    eventName, eventDate, eventStartTime, eventEndTime, eventCity, eventFood, eventInterest, eventNbrPersons, eventDescription
                    , uriImage);
                databaseEvents.child(eventId).setValue(event);
        } else {
            Toast.makeText(getContext(), "Vous devez entrez les champs obligatoires", Toast.LENGTH_LONG).show();
        }

    }

   public void creationBaseEvents() {
        String eventId1 = databaseEvents.push().getKey();
        String eventId2 = databaseEvents.push().getKey();
        String eventId3 = databaseEvents.push().getKey();
        String eventId4 = databaseEvents.push().getKey();
        String eventId5 = databaseEvents.push().getKey();
        String eventId6 = databaseEvents.push().getKey();
        String eventId7 = databaseEvents.push().getKey();

        Event event1 = new Event(eventId1, "sNcxF2OtVlamk3jLRkrDm3OKe962", "Sam", "https://i.imgur.com/ePgVjzE.jpg",
                "Soirée débat philosophique", "09/02/2018", "21:00", "23:59", "Lille",
                "Raclette", "Philosophie", 4, "Il y aura raclette pour tout le monde ! Venez rejoindre le débat philosphique :)"
                , "https://www.odelices.com/images/articles/Fotolia_94204024_M.jpg");

        Event event2 = new Event(eventId2, "sNcxF2OtVlamk3jLRkrDm3OKe962", "Sam", "https://i.imgur.com/ePgVjzE.jpg",
                "PSG vs Real Madrid", "14/02/2018", "21:00", "23:59", "Lille",
                "Pizza", "Football", 4, "Regarder le match de football ! N'oubliez pas vos bières :)"
                , "http://real-france.fr/wp-content/uploads/2015/10/Snapshot_2015-10-16_204722.png");

        Event event3 = new Event(eventId3, "sNcxF2OtVlamk3jLRkrDm3OKe962", "Sam", "https://i.imgur.com/ePgVjzE.jpg",
                "Soirée culture espagnol", "25/02/2018", "21:00", "23:59", "Lille",
                "Paella", "Culture", 4, "Nourriture espagnole, décoration mais aussi la langue ! Prepárese amigos !!"
                , "http://cache.magicmaman.com/data/photo/w800_c18/3u/recettes_espagnoles.jpg");


        Event event4 = new Event(eventId4, "sNcxF2OtVlamk3jLRkrDm3OKe962", "Sam", "https://i.imgur.com/ePgVjzE.jpg",
                "Soirée discussion politique", "14/02/2018", "21:00", "23:59", "Lille",
                "Tartiflette", "Politique", 4, "Tartiflette, bière et discussion politique. Evénement réservé au +25 ans"
                , "http://cuisinemoiunmouton.com/wp-content/uploads/2016/05/Gnocchis-tartiflette-2.jpg");


        Event event5 = new Event(eventId5, "sNcxF2OtVlamk3jLRkrDm3OKe962", "Sam", "https://i.imgur.com/ePgVjzE.jpg",
                "Soirée cinéphile", "25/02/2018", "21:00", "23:59", "Lille",
                "Gratin de courgette", "Cinéma", 4, "Gratin de courgette au menu suivie d'une projection et une petit débat cinématique juste après"
                , "https://s3.eu-central-1.amazonaws.com/media.quitoque.fr/recipe_w1536_h1024/recipes/images/2017/29/gratin_courgettes.jpg");


       Event event6 = new Event(eventId6, "sNcxF2OtVlamk3jLRkrDm3OKe962", "Sam", "https://i.imgur.com/ePgVjzE.jpg",
               "Ratatouille et projection de film", "25/02/2018", "21:00", "23:59", "Paris",
               "Gratin de courgette", "Cinéma", 4, "Ratatouille au menu suivie d'une projection d'un film culturelle"
               , "https://www.france-hotel-guide.com/fr/blog/wp-content/uploads/2014/11/ratatouille-modif.jpg");


       Event event7 = new Event(eventId7, "sNcxF2OtVlamk3jLRkrDm3OKe962", "Sam", "https://i.imgur.com/ePgVjzE.jpg",
               "Soirée barbecue et jeux de société", "25/02/2018", "21:00", "23:59", "Paris",
               "Steak de boeuf", "Animation", 4, "Barbecue au jardin, discussion, animation et jeux de société "
               , "http://www.egarden.de/imgs/11/2/3/5/2/7/Grillparty-ec8eba00d0f54250.jpg");


       databaseEvents.child(eventId1).setValue(event1);
        databaseEvents.child(eventId2).setValue(event2);
        databaseEvents.child(eventId3).setValue(event3);
        databaseEvents.child(eventId4).setValue(event4);
        databaseEvents.child(eventId5).setValue(event5);
        databaseEvents.child(eventId6).setValue(event6);
        databaseEvents.child(eventId7).setValue(event7);

    }


    public void loadOwnerDetails() {
        databaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User owner = dataSnapshot.getValue(User.class);
                ownerData.add(owner.getSurname());
                ownerData.add(owner.getImageURL());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
                            downloadUrl = taskSnapshot.getDownloadUrl();
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


}
