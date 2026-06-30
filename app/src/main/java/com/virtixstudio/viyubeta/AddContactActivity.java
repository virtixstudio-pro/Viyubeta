package com.virtixstudio.viyubeta;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
    private ImageView ivBack;
    private LinearLayout llResultCard;
    private TextView tvResUsername, tvResEmail;
    private Button btnAdd;
    
    private DatabaseReference usersRef;
    private String foundUid = null;
    private String foundName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        etSearch = findViewById(R.id.et_search_query);
        ivBack = findViewById(R.id.iv_back);
        llResultCard = findViewById(R.id.ll_result_card);
        tvResUsername = findViewById(R.id.tv_result_username);
        tvResEmail = findViewById(R.id.tv_result_email);
        btnAdd = findViewById(R.id.btn_add_confirm);

        ivBack.setOnClickListener(v -> finish());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                rechercherAbonne(s.toString().trim());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnAdd.setOnClickListener(v -> {
            if (foundUid != null) {
                String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUid).child("contacts").child(foundUid).setValue(foundName)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Contact synchronisé !", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
            }
        });
    }

    private void rechercherAbonne(String query) {
        if (query.isEmpty()) {
            llResultCard.setVisibility(View.GONE);
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
                        foundName = username != null ? username : email;
                        
                        tvResUsername.setText("@" + foundName);
                        tvResEmail.setText(email != null ? email : "Aucun email fourni");
                        break;
                    }
                }

                if (trouve) {
                    llResultCard.setVisibility(View.VISIBLE);
                } else {
                    llResultCard.setVisibility(View.GONE);
                    foundUid = null;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}
