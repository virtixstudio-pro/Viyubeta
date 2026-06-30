package com.virtixstudio.viyubeta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView lvContacts;
    private ArrayList<String> contactNames;
    private ArrayList<String> contactUIDs;
    private ArrayAdapter<String> adapter;
    private DatabaseReference usersRef;
    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        currentUid = auth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        lvContacts = findViewById(R.id.lv_contacts);
        contactNames = new ArrayList<>();
        contactUIDs = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactNames);
        lvContacts.setAdapter(adapter);

        // Charger la liste depuis Firebase
        chargerContacts();

        // Clic sur un contact pour ouvrir les MD (on créera ChatActivity juste après)
        lvContacts.setOnItemClickListener((parent, view, position, id) -> {
            String targetUid = contactUIDs.get(position);
            String targetName = contactNames.get(position);
            
            // Intent temporaire ou direct vers le chat
            Toast.makeText(this, "Ouverture de la discussion avec " + targetName, Toast.LENGTH_SHORT).show();
        });
    }

    private void chargerContacts() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                contactNames.clear();
                contactUIDs.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String uid = data.child("uid").getValue(String.class);
                    String username = data.child("username").getValue(String.class);

                    // On n'affiche pas notre propre compte dans notre liste
                    if (uid != null && !uid.equals(currentUid)) {
                        contactNames.add(username != null ? username : data.child("email").getValue(String.class));
                        contactUIDs.add(uid);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity.this, "Erreur de chargement des contacts", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
