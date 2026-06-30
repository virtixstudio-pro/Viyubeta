package com.virtixstudio.viyubeta;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private CheckBox cbShowPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            prochainEcran();
            return;
        }

        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        cbShowPassword = findViewById(R.id.cb_show_password_login);
        btnLogin = findViewById(R.id.btn_login_submit);
        tvRegister = findViewById(R.id.tv_go_to_register);

        // Afficher / masquer le mot de passe
        cbShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        // Connexion
        btnLogin.setOnClickListener(v -> executeLogin());

        // Basculer vers l'interface d'inscription
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private void executeLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Champs incomplets !", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Connexion...", Toast.LENGTH_SHORT).show();
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(authResult -> prochainEcran())
            .addOnFailureListener(e -> Toast.makeText(this, "Erreur de connexion. Vérifie tes accès.", Toast.LENGTH_LONG).show());
    }

    private void prochainEcran() {
        startActivity(new Intent(LoginActivity.this, PseudoActivity.class));
        finish();
    }
}
