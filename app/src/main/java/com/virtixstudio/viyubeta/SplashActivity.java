package com.virtixstudio.viyubeta;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                String uid = auth.getCurrentUser().getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

                userRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue() != null) {
                            // Tu as déjà un pseudo -> Direction l'écran principal direct !
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        } else {
                            // Pas de pseudo -> On va le créer
                            startActivity(new Intent(SplashActivity.this, PseudoActivity.class));
                        }
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                });
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 1500); // 1.5 seconde de splash screen
    }
}
