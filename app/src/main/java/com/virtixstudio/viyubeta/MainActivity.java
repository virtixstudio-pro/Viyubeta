package com.virtixstudio.viyubeta;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ListView lvContacts;
    private ArrayList<String> contactNames;
    private ArrayList<String> contactUids;
    private ArrayAdapter<String> adapter;

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
        lvContacts = findViewById(R.id.lv_contacts);
        FloatingActionButton fabAddContact = findViewById(R.id.fab_add_contact);
        ImageView ivMenuMore = findViewById(R.id.iv_menu_more);
        NavigationView navView = findViewById(R.id.nav_view);

        contactNames = new ArrayList<>();
        contactUids = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactNames);
        lvContacts.setAdapter(adapter);

        ivMenuMore.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.RIGHT));
        
        fabAddContact.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddContactActivity.class));
        });

        // CLIC SUR UN CONTACT -> Ouvre le salon privé
        lvContacts.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra("targetUid", contactUids.get(position));
            intent.putExtra("targetName", contactNames.get(position));
            startActivity(intent);
        });

        chargerContacts();
    }

    private void chargerContacts() {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference myContactsRef = FirebaseDatabase.getInstance().getReference("users").child(currentUid).child("contacts");

        myContactsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                contactNames.clear();
                contactUids.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String uid = data.getKey();
                    String name = data.getValue(String.class);
                    if (uid != null && name != null) {
                        contactUids.add(uid);
                        contactNames.add(name);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}
