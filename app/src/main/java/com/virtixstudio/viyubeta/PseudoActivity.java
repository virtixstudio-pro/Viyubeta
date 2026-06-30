package com.virtixstudio.viyubeta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class PseudoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pseudo);

        EditText etPseudo = findViewById(R.id.et_pseudo);
        Button btnValider = findViewById(R.id.btn_valider_pseudo);

        btnValider.setOnClickListener(v -> {
            String pseudo = etPseudo.getText().toString().trim();
            
            // Exigence stricte de l'arobase au début
            if (pseudo.isEmpty() || !pseudo.startsWith("@")) {
                Toast.makeText(this, "Le pseudo doit commencer par @", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

            HashMap<String, Object> map = new HashMap<>();
            map.put("uid", uid);
            map.put("username", pseudo);
            map.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());

            userRef.setValue(map).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    startActivity(new Intent(PseudoActivity.this, MainActivity.class));
                    finish();
                }
            });
        });
    }
}
