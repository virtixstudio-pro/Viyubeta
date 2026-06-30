package com.virtixstudio.viyubeta;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class PseudoActivity extends AppCompatActivity {

    private EditText etPseudo;
    private Button btnSave;
    private DatabaseReference userRef;
    private String uid, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // On force le layout immédiatement pour éviter l'écran gris pendant le chargement
        setContentView(R.layout.activity_pseudo);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        uid = auth.getCurrentUser().getUid();
        email = auth.getCurrentUser().getEmail();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        etPseudo = findViewById(R.id.et_pseudo);
        btnSave = findViewById(R.id.btn_save_pseudo);

        // Vérification du pseudo existant
        userRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && !TextUtils.isEmpty(snapshot.getValue(String.class))) {
                    allerA_Main();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(PseudoActivity.this, "Erreur réseau Firebase", Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(v -> {
            String pseudo = etPseudo.getText().toString().trim();
            if (TextUtils.isEmpty(pseudo)) {
                Toast.makeText(this, "Mets un pseudo valide !", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> userData = new HashMap<>();
            userData.put("uid", uid);
            userData.put("email", email);
            userData.put("username", pseudo);
            userData.put("status", "online");

            userRef.setValue(userData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    allerA_Main();
                } else {
                    Toast.makeText(this, "Erreur de sauvegarde", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void allerA_Main() {
        startActivity(new Intent(PseudoActivity.this, MainActivity.class));
        finish();
    }
}
