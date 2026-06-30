package com.virtixstudio.viyubeta;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView logo = findViewById(R.id.tv_splash_logo);

        // Animation de rebond (scale up rapide)
        ScaleAnimation bounce = new ScaleAnimation(
                0.3f, 1.0f, 0.3f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        bounce.setDuration(800);
        bounce.setRepeatCount(0);
        logo.startAnimation(bounce);

        // Attente de 2.5 secondes avant de basculer
        new Handler().postDelayed(() -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                // Déjà connecté -> Direction écran de vérification du pseudo
                startActivity(new Intent(SplashActivity.this, PseudoActivity.class));
            } else {
                // Non connecté -> Écran login
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, 2500);
    }
}
