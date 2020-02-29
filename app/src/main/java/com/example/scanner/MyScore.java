package com.example.scanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.scanner.databinding.MyScoreBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyScore extends AppCompatActivity {

    MyScoreBinding binding;
    FirebaseDatabase database;
    DatabaseReference reference;
    String uid;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.my_score);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        uid = preferences.getString("uid", null);



        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) return;
                binding.score.setText(String.valueOf(user.getScore()));
                binding.gifts.setText(String.valueOf(user.getGifts()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };

        reference.child("USERS").child(uid).addValueEventListener(valueEventListener);

        binding.gift1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reference.child("USERS").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user == null) return;
                        Integer score = user.getScore();
                        Integer gift1 = user.getGift1();
                        Integer gifts = user.getGifts();
                        if (score >= 5){
                            score = score - 5;
                            gift1 = gift1 + 1;
                            gifts = gifts + 1;
                            reference.child("USERS").child(uid).child("score").setValue(score);
                            reference.child("USERS").child(uid).child("gift1").setValue(gift1);
                            reference.child("USERS").child(uid).child("gifts").setValue(gifts);
                            binding.score.setText(String.valueOf(score));
                            binding.gifts.setText(String.valueOf(gifts));
                        }else {
                            Toast.makeText(MyScore.this, "Le score insuffisant !", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

        binding.gift2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child("USERS").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user == null) return;
                        Integer score = user.getScore();
                        Integer gift2 = user.getGift2();
                        Integer gifts = user.getGifts();
                        if (score >= 10){
                            score = score - 10;
                            gift2 = gift2 + 1;
                            gifts = gifts + 1;
                            reference.child("USERS").child(uid).child("score").setValue(score);
                            reference.child("USERS").child(uid).child("gift2").setValue(gift2);
                            reference.child("USERS").child(uid).child("gifts").setValue(gifts);
                            binding.score.setText(String.valueOf(score));
                            binding.gifts.setText(String.valueOf(gifts));
                        }else {
                            Toast.makeText(MyScore.this, "Le score insuffisant !", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        binding.gift3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child("USERS").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user == null) return;
                        Integer score = user.getScore();
                        Integer gift3 = user.getGift3();
                        Integer gifts = user.getGifts();
                        if (score >= 15){
                            score = score - 15;
                            gift3 = gift3 + 1;
                            gifts = gifts + 1;
                            reference.child("USERS").child(uid).child("score").setValue(score);
                            reference.child("USERS").child(uid).child("gift3").setValue(gift3);
                            reference.child("USERS").child(uid).child("gifts").setValue(gifts);
                            binding.score.setText(String.valueOf(score));
                            binding.gifts.setText(String.valueOf(gifts));
                        }else {
                            Toast.makeText(MyScore.this, "Le score insuffisant !", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


    }
}
