package com.virtixstudio.viyubeta;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
    private LinearLayout llResultCard;
    private TextView tvResUsername, tvResEmail;
    private Button btnAdd;
    
    private DatabaseReference usersRef;
    private ArrayList<String> allUsernames;
    private ArrayList<String> allEmails;
    private ArrayList<String> allUids;
    private ArrayList<String> filteredSuggestions;
    private ArrayAdapter<String> suggestionsAdapter;

    private String selectedUid = null;
    private String selectedName = null;
    private String selectedEmail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        etSearch = findViewById(R.id.et_search_query);
        lvSuggestions = findViewById(R.id.lv_search_suggestions);
        llResultCard = findViewById(R.id.ll_result_card);
        tvResUsername = findViewById(R.id.tv_result_username);
        tvResEmail = findViewById(R.id.tv_result_email);
        btnAdd = findViewById(R.id.btn_add_confirm);
        ImageView ivBack = findViewById(R.id.iv_back);

        allUsernames = new ArrayList<>();
        allEmails = new ArrayList<>();
        allUids = new ArrayList<>();
        filteredSuggestions = new ArrayList<>();

        suggestionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredSuggestions);
        lvSuggestions.setAdapter(suggestionsAdapter);

        ivBack.setOnClickListener(v -> finish());

        telechargerBaseUtilisateurs();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrerSuggestions(s.toString().trim());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        lvSuggestions.setOnItemClickListener((parent, view, position, id) -> {
            String selection = filteredSuggestions.get(position);
            for (int i = 0; i < allUsernames.size(); i++) {
                if (allUsernames.get(i).equals(selection) || allEmails.get(i).equals(selection)) {
                    selectedUid = allUids.get(i);
                    selectedName = allUsernames.get(i);
                    selectedEmail = allEmails.get(i);

                    tvResUsername.setText(selectedName);
                    tvResEmail.setText(selectedEmail);
                    llResultCard.setVisibility(View.VISIBLE);
                    lvSuggestions.setVisibility(View.GONE);
                    break;
                }
            }
        });

        btnAdd.setOnClickListener(v -> {
            if (selectedUid != null) {
                String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUid).child("contacts").child(selectedUid).setValue(selectedName)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Contact ajouté aux discussions !", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
            }
        });
    }

    private void telechargerBaseUtilisateurs() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allUsernames.clear();
                allEmails.clear();
                allUids.clear();
                String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String uid = userSnapshot.child("uid").getValue(String.class);
                    if (uid != null && !uid.equals(currentUid)) {
                        allUids.add(uid);
                        allUsernames.add(userSnapshot.child("username").getValue(String.class));
                        allEmails.add(userSnapshot.child("email").getValue(String.class));
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void filtrerSuggestions(String query) {
        filteredSuggestions.clear();
        if (query.isEmpty()) {
            lvSuggestions.setVisibility(View.GONE);
            return;
        }

        String cleanQuery = query.startsWith("@") ? query : "@" + query;

        for (int i = 0; i < allUsernames.size(); i++) {
            String username = allUsernames.get(i);
            String email = allEmails.get(i);

            if ((username != null && username.toLowerCase().contains(cleanQuery.toLowerCase())) ||
                (email != null && email.toLowerCase().contains(query.toLowerCase()))) {
                if (username != null && !filteredSuggestions.contains(username)) {
                    filteredSuggestions.add(username);
                }
            }
        }

        if (!filteredSuggestions.isEmpty()) {
            lvSuggestions.setVisibility(View.VISIBLE);
            suggestionsAdapter.notifyDataSetChanged();
        } else {
            lvSuggestions.setVisibility(View.GONE);
        }
    }
}
