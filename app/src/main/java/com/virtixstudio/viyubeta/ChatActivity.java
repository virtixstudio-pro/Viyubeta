package com.virtixstudio.viyubeta;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private ListView lvMessages;
    private EditText etMessage;
    private Button btnSend;
    private TextView tvTitle;
    private TextView tvStatus;
    
    private DatabaseReference chatRef;
    private String currentUid, targetUid, chatRoomId;
    private ArrayList<Message> messageList;
    private ArrayAdapter<Message> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        targetUid = getIntent().getStringExtra("targetUid");
        String targetName = getIntent().getStringExtra("targetName");

        tvTitle = findViewById(R.id.tv_chat_title);
        tvStatus = findViewById(R.id.tv_chat_status);
        if (targetName != null) tvTitle.setText(targetName);

        if (currentUid.compareTo(targetUid) < 0) {
            chatRoomId = currentUid + "_" + targetUid;
        } else {
            chatRoomId = targetUid + "_" + currentUid;
        }

        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatRoomId);

        lvMessages = findViewById(R.id.lv_messages);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);

        messageList = new ArrayList<>();
        
        adapter = new ArrayAdapter<Message>(this, R.layout.item_message, messageList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_message, parent, false);
                }
                Message msg = getItem(position);
                TextView txt = convertView.findViewById(R.id.tv_message_text);
                TextView time = convertView.findViewById(R.id.tv_message_time);
                LinearLayout bubble = convertView.findViewById(R.id.ll_message_bubble);
                LinearLayout container = convertView.findViewById(R.id.ll_message_container);

                txt.setText(msg.getMessage());
                
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                time.setText(sdf.format(new Date(msg.getTimestamp())));

                if (msg.getSenderId().equals(currentUid)) {
                    container.setGravity(Gravity.END);
                    bubble.setBackgroundResource(R.drawable.bg_button_rounded);
                    txt.setTextColor(Color.parseColor("#0A0A0A"));
                } else {
                    container.setGravity(Gravity.START);
                    bubble.setBackgroundResource(R.drawable.bg_input_rounded);
                    txt.setTextColor(Color.parseColor("#FFFFFF"));
                }
                return convertView;
            }
        };
        lvMessages.setAdapter(adapter);

        btnSend.setOnClickListener(v -> envoyerMessage());
        recupererMessages();
    }

    private void envoyerMessage() {
        String text = etMessage.getText().toString().trim();
        if (!text.isEmpty()) {
            Message msg = new Message(currentUid, targetUid, text, System.currentTimeMillis());
            chatRef.push().setValue(msg);
            etMessage.setText("");
        }
    }

    private void recupererMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Message msg = data.getValue(Message.class);
                    if (msg != null) messageList.add(msg);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}
