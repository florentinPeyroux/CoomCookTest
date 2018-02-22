package com.eservices.floamnx.coomcook.authentification;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eservices.floamnx.coomcook.R;
import com.eservices.floamnx.coomcook.menu.LeftMenuActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthSignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail;
    private EditText editTextPw;
    private EditText editTextConfirmEmail;
    private EditText editTextConfirmPassword;
    private ProgressDialog progressDialog;
    private LinearLayout layoutTextErreur;
    private TextView textViewRegister;
    private TextView textViewErreur;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), LeftMenuActivity.class));

        }

        progressDialog = new ProgressDialog(this);

        textViewRegister = (TextView) findViewById(R.id.register);
        textViewErreur = (TextView) findViewById(R.id.textErreur);

        layoutTextErreur = (LinearLayout) findViewById(R.id.layoutTextErreur);

        editTextEmail = (EditText) findViewById(R.id.userEmail);
        editTextPw = (EditText) findViewById(R.id.userPassword);
        editTextConfirmEmail = (EditText) findViewById(R.id.userConfirmEmail);
        editTextConfirmPassword = (EditText) findViewById(R.id.userConfirmPassword);

        textViewRegister.setOnClickListener(this);


    }

    //Enregistrer l'utilisateur dans l'authentification Firebase et lui générer un ID
    // l'ID généré sera utilisé par la suite comme noeud dans la base de données.
    private void registerUser() {
        textViewErreur.setText("");
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPw.getText().toString().trim();
        String emailConfirm = editTextConfirmEmail.getText().toString().trim();
        String passwordConfirm = editTextConfirmPassword.getText().toString().trim();
        String nonConforme = "";
        if(!email.equals(emailConfirm)){
            nonConforme += " Les emails saisis ne sont pas identiques \n ";
        }
        if(!password.equals(passwordConfirm)){
            nonConforme += " Les mots de passes saisis ne sont pas identiques \n ";
        }

        if (TextUtils.isEmpty(email)) {
            nonConforme += "Veuillez entrez un email \n ";
        }

        if (TextUtils.isEmpty(password)) {
            nonConforme += "Veuillez entrer un mot de passe ";
        }

        if(!TextUtils.isEmpty(nonConforme)){
            layoutTextErreur.setVisibility(View.VISIBLE);
            textViewErreur.setText(nonConforme);
            return;
        }

        progressDialog.setMessage("Enregistrement en cours ...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //l'utilisateur s'est enregistré avec succès on va à son profil (page d'accueil par la suite)
                            finish();
                            startActivity(new Intent(getApplicationContext(), CompleteSignUpActivity.class));
                        } else {
                            Toast.makeText(AuthSignUpActivity.this, "Enregistrement échoué", Toast.LENGTH_SHORT).show();

                        }
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    public void onClick(View v) {
        if (v == textViewRegister) {
            registerUser();
            if(TextUtils.isEmpty(textViewErreur.getText()))
                startActivity(new Intent(this, CompleteSignUpActivity.class));
        }
    }
}
