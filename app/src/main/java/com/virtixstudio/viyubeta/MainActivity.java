package com.virtixstudio.viyubeta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView lvContacts;
    private FloatingActionButton fabAddContact;
    private ImageView ivMenuMore;
    private ArrayList<String> contactNames;
    private ArrayAdapter<String> adapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            retourConnexion();
            return;
        }

        lvContacts = findViewById(R.id.lv_contacts);
        fabAddContact = findViewById(R.id.fab_add_contact);
        ivMenuMore = findViewById(R.id.iv_menu_more);

        contactNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactNames);
        lvContacts.setAdapter(adapter);

        // Menu trois points
        ivMenuMore.setOnClickListener(v -> afficherMenuOptions());

        // Clic sur le bouton flottant pour ouvrir l'interface d'ajout
        fabAddContact.setOnClickListener(v -> {
            // On créera cette activité juste après pour la recherche/ajout
            startActivity(new Intent(MainActivity.this, AddContactActivity.class));
        });
    }

    private void afficherMenuOptions() {
        PopupMenu popup = new PopupMenu(this, ivMenuMore);
        popup.getMenu().add("Modifier les informations");
        popup.getMenu().add("Créer un nouveau compte");
        popup.getMenu().add("Se déconnecter");

        popup.setOnMenuItemClickListener(item -> {
            String titre = item.getTitle().toString();
            if (titre.equals("Se déconnecter")) {
                mAuth.signOut();
                retourConnexion();
                return true;
            } else if (titre.equals("Modifier les informations")) {
                startActivity(new Intent(MainActivity.this, PseudoActivity.class));
                return true;
            } else if (titre.equals("Créer un nouveau compte")) {
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                finish();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void retourConnexion() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}
