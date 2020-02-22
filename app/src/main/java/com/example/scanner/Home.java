package com.example.scanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.scanner.databinding.ActivityHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class Home extends AppCompatActivity {

    String uid;
    FirebaseDatabase database;
    DatabaseReference reference;
    ActivityHomeBinding binding;
    private RecycleView recycleView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        database = FirebaseDatabase.getInstance();
         reference = database.getReference();
         recycleView = new RecycleView();
        binding.rec.setLayoutManager(new LinearLayoutManager(Home.this));
        binding.rec.setHasFixedSize(true);
        binding.rec.setAdapter(recycleView);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        uid = preferences.getString("uid", "");

        updateUi();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
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
                if (result.getContents() == null) {
                    Log.e("Scan*******", "Cancelled scan");

                } else {
                    Log.e("Scan", "Scanned");

                     reference.child("USERS").child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user == null) return;
                            String qrCodes = user.getQrCode();
                            int score = user.getScore();

                            String[] codes = qrCodes.split(",");
                            List<String> list = Arrays.asList(codes);
                            if (list.contains(result.getContents())){
                                // Code Existe
                                Toast.makeText(Home.this, "Le code existe deja", Toast.LENGTH_LONG).show();
                            }else {
                                qrCodes += result.getContents() + ",";
                                score += 1;
                                reference.child("USERS").child(uid).child("Score").setValue(score);
                                reference.child("USERS").child(uid).child("qrCode").setValue(qrCodes);
                                updateUi();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            } else {
                // This is important, otherwise the result will not be passed to the fragment
                super.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            super.onActivityResult(requestCode, resultCode, data);
            e.printStackTrace();
        }
    }

    public int maxScore = 50;

    public void updateUi(){
        reference.child("USERS").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) return;
                int sco = user.getScore();
                int p = (maxScore / 100) * sco;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, p / 100);
                binding.pGreen.setLayoutParams(params);
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, (100 - p) / 100);
                binding.pGrey.setLayoutParams(params2);
                binding.score.setText(String.valueOf(sco / 2));
                binding.name.setText(user.getName());
                Glide.with(Home.this).load(user.getPhotoUrl()).into(binding.photo);
                recycleView.swapAdapter(user.getQrCode().split(","));
                binding.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.l.setVisibility(View.VISIBLE);
                        binding.log.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(Home.this, SplachScreen.class));
                            }
                        });
                        binding.cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                binding.l.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

}
