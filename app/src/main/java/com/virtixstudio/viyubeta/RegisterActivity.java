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

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etPasswordConfirm;
    private CheckBox cbShowPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.et_register_email);
        etPassword = findViewById(R.id.et_register_password);
        etPasswordConfirm = findViewById(R.id.et_register_password_confirm);
        cbShowPassword = findViewById(R.id.cb_show_password_register);
        btnRegister = findViewById(R.id.btn_register_submit);
        tvGoToLogin = findViewById(R.id.tv_go_to_login);

        // Afficher / masquer le mot de passe
        cbShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                etPasswordConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                etPasswordConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            // Recaler le curseur à la fin du texte
            etPassword.setSelection(etPassword.getText().length());
            etPasswordConfirm.setSelection(etPasswordConfirm.getText().length());
        });

        // Soumission de l'inscription
        btnRegister.setOnClickListener(v -> executeRegister());

        // Retour à la connexion
        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void executeRegister() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String confirmPass = etPasswordConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(confirmPass)) {
            Toast.makeText(this, "Tous les champs doivent être remplis !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6) {
            Toast.makeText(this, "Le mot de passe doit faire au moins 6 caractères !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirmPass)) {
            Toast.makeText(this, "Les deux mots de passe ne correspondent pas !", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Création du compte...", Toast.LENGTH_SHORT).show();
        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener(authResult -> {
                Toast.makeText(this, "Succès ! Bienvenue sur Viyu.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, PseudoActivity.class));
                finish();
            })
            .addOnFailureListener(e -> Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
