package com.virtixstudio.viyubeta;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FloatingActionButton fabAddContact;
    private ImageView ivMenuMore;
    private EditText etGlobalSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        fabAddContact = findViewById(R.id.fab_add_contact);
        ivMenuMore = findViewById(R.id.iv_menu_more);
        etGlobalSearch = findViewById(R.id.et_global_search);
        NavigationView navView = findViewById(R.id.nav_view);

        // Ouvre le menu depuis la DROITE
        ivMenuMore.setOnClickListener(v -> {
            drawerLayout.openDrawer(Gravity.RIGHT);
        });

        fabAddContact.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddContactActivity.class));
        });

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }
}
