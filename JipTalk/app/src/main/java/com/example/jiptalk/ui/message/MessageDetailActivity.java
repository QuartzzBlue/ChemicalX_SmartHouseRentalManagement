package com.example.jiptalk.ui.message;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.jiptalk.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageDetailActivity extends AppCompatActivity {

    private String TAG = "=== jiptalk.ui.message.MessageDetailActivity";


    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private ChatDataAdapter chatDataAdapter;


    private DatabaseReference databaseReference;
    private String currentUserID;
    private String chatUserID;
    private int currentPage = 1;
    private int itemPos = 0;
    private String lastKey = "";
    private String prevKey = "";
    private Boolean once = false;
    private static final int TOTAL_ITEMS_TO_LOAD = 20;
    private final List<ChatDataDTO> chatDataList = new ArrayList<>();


    private ImageView imageViewSendButton;
    private EditText editTextMessage;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUserID = "조현민";
        Log.d(TAG, "currentUserID : " + currentUserID);
//        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Set ActionBar Title to name of the Tenant
        chatUserID = getIntent().getStringExtra("name").toString();
        Log.d(TAG, "chatUserID : " + chatUserID);
        getSupportActionBar().setTitle(chatUserID);


        recyclerView = findViewById(R.id.recyclerViewMsgDetail);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        chatDataAdapter = new ChatDataAdapter(chatDataList, chatUserID);
        recyclerView.setAdapter(chatDataAdapter);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutMsgDetail);

        loadMessages();


        imageViewSendButton = findViewById(R.id.imageViewSendButton);
        editTextMessage = findViewById(R.id.editTextMessage);
        imageViewSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewSendButton.setImageResource(R.drawable.ic_chat_send_hold);
                sendMessage();
                editTextMessage.setText("");
                imageViewSendButton.setImageResource(R.drawable.ic_chat_send);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;
                itemPos = 0;
//                loadMoreMessages();
            }
        });

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

            }
        });


        loadMessages();
        getChatFriendSent();
    }


    public void sendMessage() {
        String message = editTextMessage.getText().toString();

        if (!TextUtils.isEmpty(message)) {
            final MediaPlayer sendSound = MediaPlayer.create(this, R.raw.light);
            sendSound.start();


            String currentUserRef = "messages/" + currentUserID + "/" + chatUserID;
            String chatUserRef = "messages/" + chatUserID + "/" + currentUserID;


            DatabaseReference userMessagePush = databaseReference.child("messages").child(currentUserID).child(chatUserID).push();

            String pushID = userMessagePush.getKey();

            Map sendTime = ServerValue.TIMESTAMP;

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("time", sendTime);
            messageMap.put("from", currentUserID);

            Map messageUserMap = new HashMap();
            messageUserMap.put(currentUserRef + "/" + pushID, messageMap);
            messageUserMap.put(chatUserRef + "/" + pushID, messageMap);


            databaseReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d(TAG, databaseError.getMessage().toString());
                    }
                }
            });


            Map chatAddMap = new HashMap();
            chatAddMap.put("seen", false);
            chatAddMap.put("timestamp", sendTime);
            chatAddMap.put("lastMessageId", pushID);

            Map chatUserMap = new HashMap();
            chatUserMap.put("chat/" + currentUserID + "/" + chatUserID, chatAddMap);
            chatUserMap.put("chat/" + chatUserID + "/" + currentUserID, chatAddMap);


            databaseReference.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d(TAG, databaseError.getMessage().toString());
                    }
                }
            });


        }

    }


    public void loadMessages() {

        DatabaseReference messageRef = databaseReference.child("messages").child(currentUserID).child(chatUserID);

        Query messageQuery = messageRef.limitToLast(currentPage * TOTAL_ITEMS_TO_LOAD);

        databaseReference.child("messages").child(currentUserID).child(chatUserID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ChatDataDTO chatData = dataSnapshot.getValue(ChatDataDTO.class);

                itemPos++;

                if (itemPos == 1) {
                    String messageKey = dataSnapshot.getKey();
                    lastKey = messageKey;
                    prevKey = messageKey;
                }

                Log.d(TAG, chatData.toString());
                chatDataList.add(chatData);
                chatDataAdapter.notifyDataSetChanged();

                recyclerView.scrollToPosition(chatDataList.size() - 1);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getChatFriendSent() {
        databaseReference.child("chat").child(currentUserID).child(chatUserID).child("lastMessageId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String lastID = dataSnapshot.getValue().toString();
                    databaseReference.child("messages").child(currentUserID).child(chatUserID).child(lastID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("from").getValue() != null) {
                                if (!dataSnapshot.child("from").getValue().toString().equals(currentUserID)) {
                                    if (once) {
                                        final MediaPlayer sendSound = MediaPlayer.create(getApplicationContext(), R.raw.plucky);
                                        sendSound.start();
                                    }
                                    once = true;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadMoreMessages() {

        DatabaseReference messageRef = databaseReference.child("messages").child(currentUserID).child(chatUserID);
        Query messageQuery = messageRef.orderByKey().endAt(lastKey).limitToLast(20);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatDataDTO chatData = dataSnapshot.getValue(ChatDataDTO.class);
                String chatKey = dataSnapshot.getKey();

                chatDataList.add(itemPos++, chatData);
                if (!prevKey.equals(chatKey)) {
                    chatDataList.add(itemPos++, chatData);
                } else {
                    prevKey = lastKey;
                }

                if (itemPos == 1) {
                    lastKey = chatKey;
                }

                chatDataAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                linearLayoutManager.scrollToPositionWithOffset(20, 0);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    class ChatDataAdapter extends RecyclerView.Adapter<ChatDataAdapter.ChatDataViewHolder> {

        private List<ChatDataDTO> chatDataList;
        private String chatUserID;


        public ChatDataAdapter(List<ChatDataDTO> chatDataList, String chatUserID) {
            this.chatDataList = chatDataList;
            this.chatUserID = chatUserID;
        }

        @NonNull
        @Override
        public ChatDataAdapter.ChatDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_tab, parent, false);

            return new ChatDataViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatDataAdapter.ChatDataViewHolder holder, int position) {

            ChatDataDTO chatData = chatDataList.get(position);
            String from = chatData.getFrom();

            if (from.equals(currentUserID)) { // Msg sent by me
                holder.cardView.setVisibility(View.INVISIBLE);
                holder.textViewSenderName.setVisibility(View.INVISIBLE);
                holder.textViewMsgRecieved.setVisibility(View.INVISIBLE);
                holder.textViewMsgSent.setVisibility(View.VISIBLE);
                holder.textViewMsgSent.setText(chatData.getMessage());
            } else { // Msg received
                holder.cardView.setVisibility(View.VISIBLE);
                holder.textViewSenderName.setVisibility(View.VISIBLE);
                holder.textViewSenderName.setText(chatUserID+"");
                holder.textViewMsgRecieved.setVisibility(View.VISIBLE);
                holder.textViewMsgRecieved.setText(chatData.getMessage());
                holder.textViewMsgSent.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return chatDataList.size();
        }


        public class ChatDataViewHolder extends RecyclerView.ViewHolder {
            public TextView textViewMsgRecieved;
            public TextView textViewMsgSent;
            public TextView textViewSenderName;
            public CardView cardView;


            public ChatDataViewHolder(View itemView) {
                super(itemView);
                textViewMsgRecieved = itemView.findViewById(R.id.textViewMsgReceived);
                textViewMsgSent = itemView.findViewById(R.id.textViewMsgSent);
                textViewSenderName = itemView.findViewById(R.id.textViewSenderName);
                cardView = itemView.findViewById(R.id.cardViewChat);

            }
        }
    }
}
