package com.example.scanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.scanner.databinding.ActivityHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class Home extends AppCompatActivity {

    String uid;
    FirebaseDatabase database;
    DatabaseReference reference;
    ActivityHomeBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);



        database = FirebaseDatabase.getInstance();
         reference = database.getReference();
         /*
        binding.rec.setLayoutManager(new LinearLayoutManager(Home.this));
        binding.rec.setHasFixedSize(true);
        binding.rec.setAdapter(recycleView);

          */

        binding.myscore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, MyScore.class));
            }
        });

        binding.mygifts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, MyGifts.class));
            }
        });


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        uid = preferences.getString("uid", null);
        if (uid == null){
            Toast.makeText(Home.this, "User ne pas existe :(", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Home.this, SplachScreen.class));
        }

        updateUi();


        binding.getscore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goScanCode();
            }
        });
    }


    public void goScanCode(){

        IntentIntegrator integrator = new IntentIntegrator(Home.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
            try {
            final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) return;
                reference.child("QrCodes").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.hasChild(result.getContents())) {
                                reference.child("QrCodes").child(result.getContents()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Integer i = dataSnapshot.getValue(Integer.class);
                                        if (i == null) return;
                                        if (i == 0){

                                            reference.child("QrCodes").child(result.getContents()).setValue(1);
                                            reference.child("USERS").child(uid).child("qrCode").push().setValue(result.getContents());

                                            reference.child("USERS").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    User user = dataSnapshot.getValue(User.class);
                                                    if (user == null) return;
                                                    upDateData(user, result.getContents());
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) { }
                                            });

                                            Toast.makeText(Home.this, "Félicitations, vous avez une autre feuille",
                                                    Toast.LENGTH_LONG).show();
                                        }else if(i == 1){
                                            Toast.makeText(Home.this, "Le papier a déjà été utilisé",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }else {
                                Toast.makeText(Home.this, "Le papier n'est pas disponible pour nous !", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            } else {
                super.onActivityResult(requestCode, resultCode, data);
            } } catch (Exception e) { super.onActivityResult(requestCode, resultCode, data);e.printStackTrace(); }

    }


    public void updateUi(){

        reference.child("USERS").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) return;

                if (user.getPhotoUrl() != null){
                    try {
                        Glide.with(Home.this).load(user.getPhotoUrl()).into(binding.photo);
                    }catch (Exception e){ }


                }else{
                    binding.photo.setImageResource(R.drawable.ic_account);
                }
                binding.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       startActivity(new Intent(Home.this, ActivityProfile.class));
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

    public void upDateData(User user, String s){


        Integer score = user.getScore();
        score = score + 1;
        Log.e("Home", "Up score : " + score);
        reference.child("USERS").child(uid).child("score").setValue(score);
        Integer p1 = user.getProd1();
        Integer p2 = user.getProd2();
        Integer p3 = user.getProd3();
        Integer p4 = user.getProd4();
        Integer p5 = user.getProd5();
        switch (s.substring(0, 3)){
            case "111" :
                p1 = p1 + 1;
                reference.child("USERS").child(uid).child("prod1").setValue(p1);
                break;
            case "222" :
                p2 = p2 + 1;
                reference.child("USERS").child(uid).child("prod2").setValue(p2);
                break;
            case "333" :
                p3 = p3 + 1;
                reference.child("USERS").child(uid).child("prod3").setValue(p3);
                break;
            case "444" :
                p4 = p4 + 1;
                reference.child("USERS").child(uid).child("prod4").setValue(p4);
                break;
            case "555" :
                p5 = p5 + 1;
                reference.child("USERS").child(uid).child("prod5").setValue(p5);
                break;

        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

}
