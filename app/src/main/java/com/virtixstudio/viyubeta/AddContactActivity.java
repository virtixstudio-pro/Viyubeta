package com.virtixstudio.viyubeta;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddContactActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageView ivStatus, ivBack;
    private Button btnAdd;
    private DatabaseReference usersRef;
    private String foundUid = null;
    private String foundUsername = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        etSearch = findViewById(R.id.et_search_query);
        ivStatus = findViewById(R.id.iv_status_indicator);
        ivBack = findViewById(R.id.iv_back);
        btnAdd = findViewById(R.id.btn_add_confirm);

        ivBack.setOnClickListener(v -> finish());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                verifierUtilisateur(s.toString().trim());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnAdd.setOnClickListener(v -> {
            if (foundUid != null && foundUsername != null) {
                String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                // Création d'un lien direct ou salon privé dans Firebase
                DatabaseReference contactListRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(currentUid)
                    .child("contacts");

                contactListRef.child(foundUid).setValue(foundUsername).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, foundUsername + " ajouté à tes discussions !", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Échec de l'ajout", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void verifierUtilisateur(String query) {
        if (query.isEmpty()) {
            ivStatus.setVisibility(View.GONE);
            btnAdd.setEnabled(false);
            return;
        }

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean trouve = false;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String username = userSnapshot.child("username").getValue(String.class);
                    String email = userSnapshot.child("email").getValue(String.class);

                    if ((username != null && username.equalsIgnoreCase(query)) || 
                        (email != null && email.equalsIgnoreCase(query))) {
                        trouve = true;
                        foundUid = userSnapshot.child("uid").getValue(String.class);
                        foundUsername = username != null ? username : email;
                        break;
                    }
                }

                if (trouve) {
                    ivStatus.setImageResource(R.drawable.ic_check);
                    ivStatus.setVisibility(View.VISIBLE);
                    btnAdd.setEnabled(true);
                } else {
                    ivStatus.setImageResource(R.drawable.ic_close);
                    ivStatus.setVisibility(View.VISIBLE);
                    btnAdd.setEnabled(false);
                    foundUid = null;
                    foundUsername = null;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}
