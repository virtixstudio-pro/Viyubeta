package com.virtixstudio.viyubeta;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle Bundle) {
        super.onCreate(Bundle);
        setContentView(R.layout.activity_settings);

        Button btnTheme = findViewById(R.id.btn_theme_toggle);
        Button btnFont = findViewById(R.id.btn_font_size);
        Button btnBubble = findViewById(R.id.btn_bubble_color);

        btnTheme.setOnClickListener(v -> Toast.makeText(this, "Thème mis à jour", Toast.LENGTH_SHORT).show());
        btnFont.setOnClickListener(v -> Toast.makeText(this, "Modification de la taille appliquée", Toast.LENGTH_SHORT).show());
        btnBubble.setOnClickListener(v -> Toast.makeText(this, "Palette de couleur modifiée", Toast.LENGTH_SHORT).show());
    }
}
