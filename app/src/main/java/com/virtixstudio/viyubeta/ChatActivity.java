package com.virtixstudio.viyubeta;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ListView messagesList;
    private EditText messageInput;
    private Button sendButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ArrayList<String> messages;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("messages");

        messagesList = findViewById(R.id.messagesList);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        messages = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        messagesList.setAdapter(adapter);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot msgSnapshot : snapshot.getChildren()) {
                    String text = msgSnapshot.child("text").getValue(String.class);
                    String user = msgSnapshot.child("user").getValue(String.class);
                    if (text != null && user != null) {
                        messages.add(user + " : " + text);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Erreur de chargement", Toast.LENGTH_SHORT).show();
            }
        });

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        String userId = mAuth.getCurrentUser().getUid();
        String userEmail = mAuth.getCurrentUser().getEmail();

        Map<String, Object> message = new HashMap<>();
        message.put("text", text);
        message.put("user", userEmail != null ? userEmail : userId);
        message.put("timestamp", System.currentTimeMillis());

        mDatabase.push().setValue(message)
            .addOnSuccessListener(aVoid -> messageInput.setText(""))
            .addOnFailureListener(e -> Toast.makeText(ChatActivity.this, "Erreur d'envoi", Toast.LENGTH_SHORT).show());
    }
}
