package com.eservices.floamnx.coomcook.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eservices.floamnx.coomcook.R;
import com.eservices.floamnx.coomcook.authentification.User;
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

import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class FragmentEditProfile extends Fragment implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseUser;
    private TextView uploadButton;
    private TextView selectButton;
    private ImageView profilePicture;
    private EditText surname;
    private EditText lastName;
    private EditText userAge;
    private EditText userLocation;
    private Spinner userFood;
    private Spinner userInterest;

    private Button buttonSave;
    private Button cancelButton;

    private Uri filePath;
    private static final int PICK_IMAGE_REQUEST = 71;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String downloadUrl;
    private String userBirthday;
    private String userPicture;
    private int userNumStars;
    private int userNumVoters;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rView = inflater.inflate(R.layout.profile_edit, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseUser = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid());

        profilePicture = (ImageView) rView.findViewById(R.id.userPicture);

        selectButton = (TextView) rView.findViewById(R.id.selectPhoto);
        uploadButton = (TextView) rView.findViewById(R.id.uploadPhoto);


        surname = (EditText) rView.findViewById(R.id.surname);
        lastName = (EditText) rView.findViewById(R.id.lastName);
        userAge = (EditText) rView.findViewById(R.id.userAge);
        userLocation = (EditText) rView.findViewById(R.id.userLocation);

        userFood = (Spinner) rView.findViewById(R.id.userFood);
        userInterest = (Spinner) rView.findViewById(R.id.userInterest);

        buttonSave = (Button) rView.findViewById(R.id.save);
        cancelButton = (Button) rView.findViewById(R.id.cancel);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));

        buttonSave.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        selectButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);


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
                userLocation.setText(data.getCity());

                userBirthday = data.getBirthDate();
                userNumStars = data.getNumStar();
                userNumVoters = data.getNumVoters();
                userPicture = data.getImageURL();


                ImageUtil.displayRoundImage(profilePicture, data.getImageURL(), null);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ne fait rien
            }
        });
    }

    public void editUserData(){
        String userSurname = surname.getText().toString().trim();
        String userName = lastName.getText().toString().trim();
        String age = userAge.getText().toString().trim();
        String location = userLocation.getText().toString().trim();
        String food = userFood.getSelectedItem().toString();
        String interest = userInterest.getSelectedItem().toString();

        if(downloadUrl!=null){
            User user = new User(firebaseAuth.getCurrentUser().getUid(),
                    userSurname,userName,userBirthday,age,food,interest,location,downloadUrl,userNumStars,userNumVoters);
            databaseUser.setValue(user);
            Bundle bundle = new Bundle();
            FragmentMyProfile nextFrag = new FragmentMyProfile();
            nextFrag.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, nextFrag)
                    .addToBackStack(null)
                    .commit();
        }
        else{
            User user = new User(firebaseAuth.getCurrentUser().getUid(),
                    userSurname,userName,userBirthday,age,food,interest,location,userPicture,userNumStars,userNumVoters);
            databaseUser.setValue(user);
            Bundle bundle = new Bundle();
            FragmentMyProfile nextFrag = new FragmentMyProfile();
            nextFrag.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, nextFrag)
                    .addToBackStack(null)
                    .commit();

        }

    }

    @Override
    public void onClick(View v) {
        if(v==buttonSave){
            editUserData();

        }
        if(v==selectButton){
            chooseImage();

        }
        if(v==uploadButton){
            uploadImage();

        }
        if(v==cancelButton){
            Bundle bundle = new Bundle();
            FragmentMyProfile nextFrag = new FragmentMyProfile();
            nextFrag.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, nextFrag)
                    .addToBackStack(null)
                    .commit();

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
                            ImageUtil.displayRoundImage(profilePicture,filePath.toString(), null);
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
