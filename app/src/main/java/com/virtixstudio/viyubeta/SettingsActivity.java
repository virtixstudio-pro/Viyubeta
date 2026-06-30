package com.virtixstudio.viyubeta;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvUsername, tvEmail, tvStatus, tvBio, tvJoinedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tvUsername = findViewById(R.id.tv_settings_username);
        tvEmail = findViewById(R.id.tv_settings_email);
        tvStatus = findViewById(R.id.tv_settings_status);
        tvBio = findViewById(R.id.tv_settings_bio);
        tvJoinedDate = findViewById(R.id.tv_settings_joined);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            tvEmail.setText(user.getEmail());
            
            // Extraction et formatage de la date de création du compte
            long timestamp = user.getMetadata().getCreationTimestamp();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            tvJoinedDate.setText("Rejoint le : " + sdf.format(new Date(timestamp)));

            // Lecture des données personnalisées via l'URL en dur
            DatabaseReference userRef = FirebaseDatabase.getInstance("https://viyu-message-default-rtdb.firebaseio.com")
                    .getReference("users").child(user.getUid());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String username = snapshot.child("username").getValue(String.class);
                        String status = snapshot.child("status").getValue(String.class);
                        String bio = snapshot.child("bio").getValue(String.class);

                        if (username != null) tvUsername.setText(username);
                        tvStatus.setText("Statut : " + (status != null ? status : "Disponible"));
                        tvBio.setText("Bio : " + (bio != null ? bio : "Aucune description."));
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {}
            });
        }
    }
}
