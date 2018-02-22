package com.eservices.floamnx.coomcook.authentification;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eservices.floamnx.coomcook.R;
import com.eservices.floamnx.coomcook.didacticiel.DidacticielActivity;
import com.eservices.floamnx.coomcook.event.util.ImageUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class CompleteSignUpActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseUsers;

    private TextView registerProfile;
    private TextView selectPhoto;
    private TextView uploadPhoto;
    private EditText editTextSurname;
    private EditText editTextName;
    private EditText editTextCity;
    private Spinner spinnerInterests;
    private Spinner spinnerPlates;
    private Button selectDate;

    private Calendar calendar;

    // photo de profil
    private StorageReference storageRef;

    private ImageView imageView;
    private Uri downloadUrl;
    private String imageUrl;
    private String birthYear;
    private String age;
    private Uri filePath;
    private static final int PICK_IMAGE_REQUEST = 71;

    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_sign_up);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));


        firebaseAuth = FirebaseAuth.getInstance();

        registerProfile = (TextView) findViewById(R.id.registerUser);
        selectPhoto = (TextView) findViewById(R.id.selectPhoto);
        uploadPhoto = (TextView) findViewById(R.id.uploadPhoto);

        editTextSurname = (EditText) findViewById(R.id.prenom);
        editTextName = (EditText) findViewById(R.id.nom);
        editTextCity = (EditText) findViewById(R.id.ville);

        spinnerInterests = (Spinner) findViewById(R.id.spinnerInterets);
        spinnerPlates = (Spinner) findViewById(R.id.spinnerPlats);

        selectDate = (Button) findViewById(R.id.buttonDate);

        calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, CompleteSignUpActivity.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Instantiation de la base de données firebase //
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        registerProfile.setOnClickListener(this);
        selectPhoto.setOnClickListener(this);
        uploadPhoto.setOnClickListener(this);
        selectDate.setOnClickListener(this);

        // photo de profil
        imageView = (ImageView) findViewById(R.id.imageProfile);

        storageRef = FirebaseStorage.getInstance().getReference();


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
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Transmission en cours ...");
            progressDialog.show();

            StorageReference ref = storageRef.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Image transférée", Toast.LENGTH_SHORT).show();
                            downloadUrl = taskSnapshot.getDownloadUrl();
                            ImageUtil.displayRoundImage(imageView, filePath.toString(), null);
                            imageUrl = downloadUrl.toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Echec " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        if (v == registerProfile) {
            addUser();

        }
        if (v == selectPhoto) {
            chooseImage();

        }

        if (v == uploadPhoto) {
            uploadImage();

        }

        if (v == selectDate) {
            datePickerDialog.show();

        }

    }

    private void addUser() {
        String name = editTextName.getText().toString().trim();
        String surname = editTextSurname.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();


        int actualYear = Calendar.getInstance().get(Calendar.YEAR);
        if(birthYear!=null) {
            age = String.valueOf(actualYear - Integer.parseInt(birthYear));
        }

        String interest = spinnerInterests.getSelectedItem().toString();
        String plate = spinnerPlates.getSelectedItem().toString();

        //vérifier au moins que ces trois champs sont fournis
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(surname) && !TextUtils.isEmpty(city) && age!=null) {
            User user = new User(firebaseAuth.getCurrentUser().getUid(),
                    surname, name, birthYear, age, plate, interest, city, imageUrl);
            databaseUsers.child(firebaseAuth.getCurrentUser().getUid()).setValue(user);
            startActivity(new Intent(CompleteSignUpActivity.this, DidacticielActivity.class));


        } else {
            Toast.makeText(this, "Vous devez entrez les champs obligatoires", Toast.LENGTH_LONG).show();
        }



    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String birthdate = sdf.format(calendar.getTime());
        birthYear = birthdate.substring(6);
        Log.d("date ",birthYear);

    }
}
