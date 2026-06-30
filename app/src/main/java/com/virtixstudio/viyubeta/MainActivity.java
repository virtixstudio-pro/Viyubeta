package com.virtixstudio.viyubeta;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvChats;
    private EditText etMessage;
    private Button btnSend;
    private ChatAdapter adapter;
    private List<ChatModel> chatList;
    private DatabaseReference databaseReference;
    
    // Identifiant de l'utilisateur actuel
    private final String MY_NAME = "Moi"; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvChats = findViewById(R.id.rv_chats);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChats.setLayoutManager(layoutManager);

        chatList = new ArrayList<>();
        adapter = new ChatAdapter(chatList, MY_NAME);
        rvChats.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance("VRAIE_URL_DE_TA_BASE_ICI").getReference("chats");
        
        // Maintient la synchronisation des données en cache
        databaseReference.keepSynced(true);

        btnSend.setOnClickListener(v -> {
            String msgText = etMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(msgText)) {
                String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                ChatModel newMessage = new ChatModel(MY_NAME, msgText, currentTime);
                databaseReference.push().setValue(newMessage);
                etMessage.setText(""); 
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatModel chat = dataSnapshot.getValue(ChatModel.class);
                    if (chat != null) {
                        chatList.add(chat);
                    }
                }
                adapter.notifyDataSetChanged();
                if (chatList.size() > 0) {
                    rvChats.scrollToPosition(chatList.size() - 1);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public static class ChatModel {
        private String name;
        private String message;
        private String time;

        public ChatModel() {}

        public ChatModel(String name, String message, String time) {
            this.name = name;
            this.message = message;
            this.time = time;
        }

        public String getName() { return name; }
        public String getMessage() { return message; }
        public String getTime() { return time; }
    }

    public static class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final List<ChatModel> list;
        private final String myName;
        
        private static final int VIEW_TYPE_SENT = 1;
        private static final int VIEW_TYPE_RECEIVED = 2;

        public ChatAdapter(List<ChatModel> list, String myName) {
            this.list = list;
            this.myName = myName;
        }

        @Override
        public int getItemViewType(int position) {
            if (list.get(position).getName().equals(myName)) {
                return VIEW_TYPE_SENT;
            } else {
                return VIEW_TYPE_RECEIVED;
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_SENT) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_sent, parent, false);
                return new SentViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_received, parent, false);
                return new ReceivedViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ChatModel item = list.get(position);
            if (holder.getItemViewType() == VIEW_TYPE_SENT) {
                ((SentViewHolder) holder).tvMsg.setText(item.getMessage());
                ((SentViewHolder) holder).tvTime.setText(item.getTime());
            } else {
                ((ReceivedViewHolder) holder).tvName.setText(item.getName());
                ((ReceivedViewHolder) holder).tvMsg.setText(item.getMessage());
                ((ReceivedViewHolder) holder).tvTime.setText(item.getTime());
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public static class SentViewHolder extends RecyclerView.ViewHolder {
            TextView tvMsg, tvTime;
            public SentViewHolder(@NonNull View itemView) {
                super(itemView);
                tvMsg = itemView.findViewById(R.id.tv_msg);
                tvTime = itemView.findViewById(R.id.tv_time);
            }
        }

        public static class ReceivedViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvMsg, tvTime;
            public ReceivedViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_name);
                tvMsg = itemView.findViewById(R.id.tv_msg);
                tvTime = itemView.findViewById(R.id.tv_time);
            }
        }
    }
}
