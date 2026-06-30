package com.virtixstudio.viyubeta;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class PseudoActivity extends AppCompatActivity {

    private EditText etPseudo;
    private TextView tvStatus;
    private Button btnValider;
    private ProgressBar progressBar;
    private boolean isPseudoValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pseudo);

        etPseudo = findViewById(R.id.et_pseudo);
        tvStatus = findViewById(R.id.tv_pseudo_status);
        btnValider = findViewById(R.id.btn_valider_pseudo);
        progressBar = findViewById(R.id.pb_pseudo_loading);

        // Écoute en temps réel des changements
        etPseudo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pseudo = s.toString().trim();
                if (pseudo.startsWith("@") && pseudo.length() >= 3) {
                    tvStatus.setText("✔️");
                    isPseudoValid = true;
                } else {
                    tvStatus.setText("❌");
                    isPseudoValid = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnValider.setOnClickListener(v -> {
            String pseudo = etPseudo.getText().toString().trim();

            if (!isPseudoValid) {
                Toast.makeText(this, "Format du pseudo invalide (ex: @pseudo)", Toast.LENGTH_SHORT).show();
                return;
            }

            btnValider.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

            HashMap<String, Object> map = new HashMap<>();
            map.put("uid", uid);
            map.put("username", pseudo);
            map.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());

            userRef.setValue(map).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    startActivity(new Intent(PseudoActivity.this, MainActivity.class));
                    finish();
                } else {
                    btnValider.setVisibility(View.VISIBLE);
                    Toast.class.getSimpleName();
                    Toast.makeText(PseudoActivity.this, "Erreur Firebase : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
