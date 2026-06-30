package com.virtixstudio.viyubeta;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        EditText etMsg = findViewById(R.id.et_message);
        Button btnSend = findViewById(R.id.btn_send);

        btnSend.setOnClickListener(v -> {
            String msg = etMsg.getText().toString();
            if (!msg.isEmpty()) {
                String chatId = getIntent().getStringExtra("chatId");
                HashMap<String, Object> map = new HashMap<>();
                map.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                map.put("text", msg);
                FirebaseDatabase.getInstance().getReference("chats").child(chatId).push().setValue(map);
                etMsg.setText("");
            }
        });
    }
}
