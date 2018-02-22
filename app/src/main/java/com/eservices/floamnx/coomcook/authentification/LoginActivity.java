package com.eservices.floamnx.coomcook.authentification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eservices.floamnx.coomcook.R;
import com.eservices.floamnx.coomcook.menu.LeftMenuActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textViewLogin;
    private TextView textViewsignUp;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference bd;

    private static final int RC_SIGN_IN = 234;
    private GoogleSignInClient mGoogleSignInClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        firebaseAuth = FirebaseAuth.getInstance();
        bd = FirebaseDatabase.getInstance().getReference("users");

        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, LeftMenuActivity.class));
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


        editTextEmail = (EditText) findViewById(R.id.userEmail);
        editTextPassword = (EditText) findViewById(R.id.userPassword);

        textViewLogin = (TextView) findViewById(R.id.login);
        textViewsignUp = (TextView) findViewById(R.id.register);

        progressDialog = new ProgressDialog(this);

        textViewLogin.setOnClickListener(this);
        textViewsignUp.setOnClickListener(this);


    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String pw = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            //pas d'email
            Toast.makeText(this, "Veuillez entrez un email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(pw)) {
            //pas de mdp
            Toast.makeText(this, "Veuillez entrer un mot de passe", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Authentification en cours ...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), LeftMenuActivity.class));

                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v == textViewLogin) {
            userLogin();
        }

        if (v == textViewsignUp) {
            finish();
            startActivity(new Intent(this, AuthSignUpActivity.class));
        }

            }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("CoomCook", "firebaseAuthWithGoogle:" + acct.getId());

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("CoomCook", "signInWithCredential:success");
                            Toast.makeText(getApplicationContext(), "Utilisateur connecté", Toast.LENGTH_SHORT).show();
                            checkUserDatabase();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("CoomCook", "problème lors de la connexion", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication échoué.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }


    //this method is called on click
    private void signIn() {
        //getting the google signin intent
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        //starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void checkUserDatabase() {
        bd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(firebaseAuth.getCurrentUser().getUid())) {
                    startActivity(new Intent(LoginActivity.this,LeftMenuActivity.class));
                }
                else{
                    startActivity(new Intent(LoginActivity.this,CompleteSignUpGoogle.class));

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ne rien faire
            }
        });
    }
}
