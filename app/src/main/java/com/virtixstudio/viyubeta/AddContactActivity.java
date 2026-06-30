package com.virtixstudio.viyubeta;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

public class AddContactActivity extends AppCompatActivity {

    private EditText etSearch;
    private ListView lvSuggestions;
    private ArrayList<String> allUsernames;
    private ArrayList<String> allUids;
    private ArrayList<String> filteredUsernames;
    private ArrayList<String> filteredUids;
    private ArrayAdapter<String> adapter;
    private DatabaseReference usersRef;
    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        etSearch = findViewById(R.id.et_search_username);
        lvSuggestions = findViewById(R.id.lv_suggestions);

        allUsernames = new ArrayList<>();
        allUids = new ArrayList<>();
        filteredUsernames = new ArrayList<>();
        filteredUids = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredUsernames);
        lvSuggestions.setAdapter(adapter);

        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Sécurité : URL passée en dur explicitement ici aussi
        usersRef = FirebaseDatabase.getInstance("https://viyu-message-default-rtdb.firebaseio.com").getReference("users");

        telechargerUtilisateurs();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrerResultats(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        lvSuggestions.setOnItemClickListener((parent, view, position, id) -> {
            String targetUid = filteredUids.get(position);
            String targetName = filteredUsernames.get(position);
            ajouterContact(targetUid, targetName);
        });
    }

    private void telechargerUtilisateurs() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allUsernames.clear();
                allUids.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String uid = userSnapshot.getKey();
                    String username = userSnapshot.child("username").getValue(String.class);
                    if (username != null && uid != null && !uid.equals(currentUid)) {
                        allUsernames.add(username);
                        allUids.add(uid);
                    }
                }
                filtrerResultats(etSearch.getText().toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void filtrerResultats(String query) {
        filteredUsernames.clear();
        filteredUids.clear();
        if (!query.isEmpty()) {
            for (int i = 0; i < allUsernames.size(); i++) {
                if (allUsernames.get(i).toLowerCase().contains(query.toLowerCase())) {
                    filteredUsernames.add(allUsernames.get(i));
                    filteredUids.add(allUids.get(i));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void ajouterContact(String targetUid, String targetName) {
        DatabaseReference myContactsRef = usersRef.child(currentUid).child("contacts");
        myContactsRef.child(targetUid).setValue(targetName).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AddContactActivity.this, targetName + " ajouté !", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(AddContactActivity.this, "Erreur d'ajout", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
