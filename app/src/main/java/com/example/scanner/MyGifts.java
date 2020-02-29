package com.example.scanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.scanner.databinding.MyGiftsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyGifts extends AppCompatActivity {


    MyGiftsBinding binding;
    FirebaseDatabase database;
    DatabaseReference reference;
    String uid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.my_gifts);


        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        uid = preferences.getString("uid", null);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) return;
                if (user.getGift1() != null) binding.gift11.setText(String.valueOf(user.getGift1()));
                if (user.getGift2() != null) binding.gift22.setText(String.valueOf(user.getGift2()));
                if (user.getGift3() != null) binding.gift33.setText(String.valueOf(user.getGift3()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };

        reference.child("USERS").child(uid).addValueEventListener(valueEventListener);


    }
}
