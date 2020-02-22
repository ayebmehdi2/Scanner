package com.example.scanner;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SplachScreen extends AppCompatActivity {


    private FirebaseAuth auth;

    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splach_activity);

        auth = FirebaseAuth.getInstance();

        Button b = findViewById(R.id.login);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(auth.getCurrentUser() != null){
                    startActivity(new Intent(SplachScreen.this, Home.class));
                }else {
                    b.animate().alpha(1.0f).setDuration(400);

                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build();

                    mGoogleSignInClient = GoogleSignIn.getClient(SplachScreen.this, gso);


                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            signIn();
                        }
                    });
                }
            }
        }, 2000);

    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(SplachScreen.this, "Login failed try again", Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }

                    }
                });
    }

    public void updateUI(FirebaseUser user){
        if (user == null) {
            startActivity(new Intent(SplachScreen.this, SplachScreen.class));
            return;
        }

        User user1;
        if (user.getPhotoUrl() != null){
            user1 = new User(user.getUid(), user.getDisplayName(), user.getPhotoUrl().toString(), 0, "");

        }else {
            user1 = new User(user.getUid(), user.getDisplayName(), "", 0, "");

        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();

        reference.child("USERS").child(user.getUid()).setValue(user1);

        SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(this).edit();
        preferences.putString("uid", user.getUid());
        preferences.apply();

        Intent i = new Intent(SplachScreen.this, Home.class);
        startActivity(i);
    }


    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}