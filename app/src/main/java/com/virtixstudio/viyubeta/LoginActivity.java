package com.virtixstudio.viyubeta;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login_submit);
        tvRegister = findViewById(R.id.tv_go_to_register);

        btnLogin.setOnClickListener(v -> handleAuth(false));
        tvRegister.setOnClickListener(v -> handleAuth(true));
    }

    private void handleAuth(boolean forceCreate) {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Champs vides !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (forceCreate) {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOn Sharif -> prochainEcran()
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> prochainEcran())
                .addOnFailureListener(e -> Toast.makeText(this, "Échec. Appuie sur 'S'inscrire' si le compte est nouveau.", Toast.LENGTH_LONG).show());
        }
    }

    private void prochainEcran() {
        startActivity(new Intent(LoginActivity.this, PseudoActivity.class));
        finish();
    }
}
