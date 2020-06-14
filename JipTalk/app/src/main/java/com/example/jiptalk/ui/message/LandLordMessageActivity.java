package com.example.jiptalk.ui.message;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.jiptalk.Constant;
import com.example.jiptalk.R;
import com.example.jiptalk.vo.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class LandLordMessageActivity extends AppCompatActivity {

    private String TAG = "=== jiptalk.ui.message.LandLordMessageActivity";


    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private ChatDataAdapter chatDataAdapter;


    private DatabaseReference databaseReference;
    private String currentUserUID;
    private String chatUserName;
    private String chatUserUID;
    private int currentPage = 1;
    private int itemPos = 0;
    private String lastKey = "";
    private String prevKey = "";
    private Boolean once = false;
    private static final int TOTAL_ITEMS_TO_LOAD = 20;
    private final List<ChatDataDTO> chatDataList = new ArrayList<>();


    private FrameLayout frameLayoutDark, frameLayoutSendMsgLayout, frameLayoutTitleChoices, frameLayoutCalendar;
    private Button buttonTitleAppointment, buttonTitleFee, buttonTitleMonthlyPayment,
            buttonTitleBldgConstruction, buttonTitleNoise, buttonTitleRecycle;

    private TextView textViewMsgPreview;
    private Button buttonInsertDateStart, buttonInsertDateEnd, buttonInsertTime, buttonSendMsg;


    private FrameLayout frameLayoutMsgDetail;

    private DatePickerDialog.OnDateSetListener callbackMethodDatePicker;
    private TimePickerDialog.OnTimeSetListener callbackMethodTimePicker;
    String titleClicked = "";
    String dateStartEnd = "";
    String subject;
    String token;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        databaseReference = FirebaseDatabase.getInstance().getReference();


        currentUserUID = Constant.userUID;

        chatUserUID = getIntent().getStringExtra("clientUID").toString();
        token = getIntent().getStringExtra("clientToken").toString();
        Log.d(TAG, "token : " + token);

        // Set ActionBar Title to name of the Tenant
        chatUserName = getIntent().getStringExtra("clientName").toString();
        Log.d(TAG, "chatUserName : " + chatUserName);
        getSupportActionBar().setTitle(chatUserName);


        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutMsgDetail);
        chatDataAdapter = new ChatDataAdapter(chatDataList, chatUserName);
        recyclerView = findViewById(R.id.recyclerViewMsgDetail);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatDataAdapter);

        loadMessages();

        frameLayoutMsgDetail = findViewById(R.id.frameLayoutMsgDetail);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;
                itemPos = 0;
                loadMoreMessages();
            }
        });

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

            }
        });

//        databaseReference.child("chat").child(chatUserUID).child(Constant.userUID).child("seen").setValue(true).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d("CHAT_ERROR", e.getMessage().toString());
//            }
//        });

        databaseReference.child("chat").child(chatUserUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                databaseReference.child("chat").child(chatUserUID).child(Constant.userUID).child("seen").setValue(true).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("CHAT_ERROR", e.getMessage().toString());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        getChatFriendSent();
    }


    public void sendMessage() {
        String message = textViewMsgPreview.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            final MediaPlayer sendSound = MediaPlayer.create(this, R.raw.light);
            sendSound.start();


            String currentUserRef = "messages/" + currentUserUID + "/" + chatUserUID;
            String chatUserRef = "messages/" + chatUserUID + "/" + currentUserUID;


            DatabaseReference userMessagePush = databaseReference.child("message").child(currentUserUID).child(chatUserUID).push();

            String pushID = userMessagePush.getKey();

            Map sendTime = ServerValue.TIMESTAMP;

            Map messageMap = new HashMap();
            messageMap.put("key", pushID);
            messageMap.put("message", message);
            messageMap.put("time", sendTime);
            messageMap.put("from", currentUserUID);
            messageMap.put("subject", subject);
            messageMap.put("hasResponsed", false);
            messageMap.put("response", "");

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


            // push FCM message START
            PushFCMMessageThread pushFCMMessage = new PushFCMMessageThread(token, subject, message);
            new Thread(pushFCMMessage).start();
            // push FCM message END


            Map chatAddMap = new HashMap();
            chatAddMap.put("seen", false);
            chatAddMap.put("timestamp", sendTime);
            chatAddMap.put("lastMessageId", pushID);
            chatAddMap.put("token", token);

            Map chatUserMap = new HashMap();
            chatUserMap.put("chat/" + currentUserUID + "/" + chatUserUID, chatAddMap);
            chatUserMap.put("chat/" + chatUserUID + "/" + currentUserUID, chatAddMap);


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

        databaseReference.child("messages").child(currentUserUID).child(chatUserUID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ChatDataDTO chatData = dataSnapshot.getValue(ChatDataDTO.class);
                for (DataSnapshot d : dataSnapshot.getChildren()) {

                }

                Log.d(TAG, "dataSnapshot.getChildrenCount() : " + dataSnapshot.getChildrenCount());

                itemPos++;

//                if (itemPos == 1) {
//                    String messageKey = dataSnapshot.getKey();
//                    Log.d(TAG, "messageKey : " + messageKey);
//                    lastKey = messageKey;
//                    prevKey = messageKey;
//                }

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
        databaseReference.child("chat").child(currentUserUID).child(chatUserUID).child("lastMessageId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String lastID = dataSnapshot.getValue().toString();
                    databaseReference.child("messages").child(currentUserUID).child(chatUserUID).child(lastID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("from").getValue() != null) {
                                if (!dataSnapshot.child("from").getValue().toString().equals(currentUserUID)) {
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

        DatabaseReference messageRef = databaseReference.child("messages").child(currentUserUID).child(chatUserUID);
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
        public void onBindViewHolder(@NonNull final ChatDataAdapter.ChatDataViewHolder holder, int position) {

            final ChatDataDTO chatData = chatDataList.get(position);
            String from = chatData.getFrom();

            if (from.equals(currentUserUID)) { // Msg sent by me
                holder.cardView.setVisibility(View.INVISIBLE);
                holder.textViewSenderName.setVisibility(View.INVISIBLE);
                holder.textViewMsgRecieved.setVisibility(View.INVISIBLE);
                holder.textViewMsgRecievedTime.setVisibility(View.INVISIBLE);
                holder.textViewMsgSent.setVisibility(View.VISIBLE);
                holder.textViewMsgSent.setText(chatData.getMessage());
                holder.textViewMsgSentTime.setVisibility(View.VISIBLE);
                holder.textViewMsgSentTime.setText(getDate(chatData.getTime()));
                holder.buttonPositive.setVisibility(View.GONE);
                holder.buttonNegative.setVisibility(View.GONE);
            } else { // Msg received
                holder.cardView.setVisibility(View.VISIBLE);
                holder.textViewSenderName.setVisibility(View.VISIBLE);
                holder.textViewSenderName.setText(chatUserID + "");
                holder.textViewMsgRecieved.setVisibility(View.VISIBLE);
                holder.textViewMsgRecieved.setText(chatData.getMessage());
                holder.textViewMsgSent.setVisibility(View.INVISIBLE);
                holder.textViewMsgSentTime.setVisibility(View.INVISIBLE);
                holder.textViewMsgRecievedTime.setVisibility(View.VISIBLE);
                holder.textViewMsgRecievedTime.setText(getDate(chatData.getTime()));
                holder.buttonPositive.setVisibility(View.GONE);
                holder.buttonNegative.setVisibility(View.GONE);
                Log.d(TAG, "chatData.getResponse() : " + chatData.getResponse());
                if (chatData.getResponse().equals("")) {
                    holder.buttonPositive.setVisibility(View.VISIBLE);
                    holder.buttonNegative.setVisibility(View.VISIBLE);
                }
            }


            holder.buttonPositive.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    v.animate().alpha(0)
                            .setDuration(600)
                            .setInterpolator(new AccelerateInterpolator())
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    v.setVisibility(View.GONE);
                                    holder.buttonNegative.setVisibility(View.GONE);
                                }
                            })
                            .start();

                    String response = "네 알겠습니다^^";
                    chatData.setResponse(response);
                    updateMessage(chatData, response);
                }
            });


            holder.buttonNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    v.animate().alpha(0)
                            .setDuration(600)
                            .setInterpolator(new AccelerateInterpolator())
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    v.setVisibility(View.GONE);
                                    holder.buttonPositive.setVisibility(View.GONE);
                                }
                            })
                            .start();

                    String response = "연락 드리겠습니다^^";
                    chatData.setResponse(response);
                    updateMessage(chatData, response);
                }
            });
        }

        public void updateMessage(ChatDataDTO chatData, String response) {
            // update respond to current message
            String currentUserRef = "messages/" + currentUserUID + "/" + chatUserUID;
            String chatUserRef = "messages/" + chatUserUID + "/" + currentUserUID;
            Map messageMap = new HashMap();
            messageMap.put("key", chatData.getKey());
            messageMap.put("message", chatData.getMessage());
            messageMap.put("time", chatData.getTime());
            messageMap.put("from", chatData.getFrom());
            messageMap.put("subject", chatData.getSubject());
            messageMap.put("hasResponsed", true);
            messageMap.put("response", response);

            Map messageUserMap = new HashMap();
            messageUserMap.put(currentUserRef + "/" + chatData.getKey(), messageMap);
            messageUserMap.put(chatUserRef + "/" + chatData.getKey(), messageMap);

            databaseReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d(TAG, databaseError.getMessage().toString());
                    }
                }
            });

            // send response message
            DatabaseReference userMessagePush = databaseReference.child("message").child(currentUserUID).child(chatUserUID).push();

            String pushID = userMessagePush.getKey();
            Map sendTime = ServerValue.TIMESTAMP;
            Map responseMessageMap = new HashMap();
            responseMessageMap.put("key", pushID);
            responseMessageMap.put("message", response);
            responseMessageMap.put("time", sendTime);
            responseMessageMap.put("from", currentUserUID);
            responseMessageMap.put("subject", chatData.getSubject());
            responseMessageMap.put("hasResponsed", true);
            responseMessageMap.put("response", response);

            Map responseMessageUserMap = new HashMap();
            responseMessageUserMap.put(currentUserRef + "/" + pushID, responseMessageMap);
            responseMessageUserMap.put(chatUserRef + "/" + pushID, responseMessageMap);

            databaseReference.updateChildren(responseMessageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d(TAG, databaseError.getMessage().toString());
                    }
                }
            });


            Log.d(TAG, "send FCM token : " + token);
            // push FCM message START
            PushFCMMessageThread pushFCMMessage = new PushFCMMessageThread(token, chatData.getSubject(), response);
            new Thread(pushFCMMessage).start();
            // push FCM message END
        }

        @Override
        public int getItemCount() {
            return chatDataList.size();
        }

        public String getDate(long time) {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(time);
            String date = null;
//            Log.d(TAG, "yesterday : " + DateFormat.format("MMdd", cal.get(cal.DATE) - 1));
            long today = System.currentTimeMillis();
//            Log.d(TAG, "today : " + today);
//            Log.d(TAG, "time : " + time);
            if (DateFormat.format("MMdd", time).equals(DateFormat.format("MMdd", today))) {
                date = DateFormat.format("HH:mm", cal).toString();
            } else if (!DateFormat.format("MMdd", time).equals(DateFormat.format("MMdd", today))) {
                if ((Integer.parseInt(DateFormat.format("MMdd", today).toString()) - Integer.parseInt(DateFormat.format("MMdd", time).toString())) == 1) {
                    return "어제";
                } else if ((Integer.parseInt(DateFormat.format("MMdd", today).toString()) - Integer.parseInt(DateFormat.format("MMdd", time).toString())) > 1) {
                    return DateFormat.format("MM/dd", time).toString();
                }
            }
            return date;
        }


        public class ChatDataViewHolder extends RecyclerView.ViewHolder {
            public TextView textViewMsgRecieved;
            public TextView textViewMsgSent;
            public TextView textViewSenderName;
            public TextView textViewMsgRecievedTime;
            public TextView textViewMsgSentTime;
            public CardView cardView;
            public Button buttonPositive, buttonNegative;


            public ChatDataViewHolder(View itemView) {
                super(itemView);
                textViewMsgRecieved = itemView.findViewById(R.id.textViewMsgReceived);
                textViewMsgSent = itemView.findViewById(R.id.textViewMsgSent);
                textViewSenderName = itemView.findViewById(R.id.textViewSenderName);
                textViewMsgRecievedTime = itemView.findViewById(R.id.textViewMsgReceivedTime);
                textViewMsgSentTime = itemView.findViewById(R.id.textViewMsgSentTime);
                cardView = itemView.findViewById(R.id.cardViewChat);
                buttonPositive = itemView.findViewById(R.id.buttonPositive);
                buttonNegative = itemView.findViewById(R.id.buttonNegative);

            }
        }
    }

    public void composeMsgBtnClick(View view) {
//        buttonInsertDate.setVisibility(View.INVISIBLE);
//        Toast.makeText(this, "view.getId() : " + view.getId(), Toast.LENGTH_SHORT).show();

        if (Constant.category.equals("집주인")) {
            final LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View frameLayoutMsgInputForm = inflater.inflate(R.layout.layout_message_detail_msg_input_form_landlord, frameLayoutMsgDetail, false);
            frameLayoutMsgDetail.addView(frameLayoutMsgInputForm);
            initializeView();

        }

    }


    public void initializeView() {
        frameLayoutDark = frameLayoutMsgDetail.findViewById(R.id.frameLayoutDark);
        frameLayoutSendMsgLayout = frameLayoutMsgDetail.findViewById(R.id.frameLayoutSendMsgLayout);

        buttonTitleAppointment = frameLayoutMsgDetail.findViewById(R.id.buttonTitleAppointment);
        buttonTitleFee = frameLayoutMsgDetail.findViewById(R.id.buttonTitleFee);
        buttonTitleMonthlyPayment = frameLayoutMsgDetail.findViewById(R.id.buttonTitleMonthlyPayment);
        buttonTitleBldgConstruction = frameLayoutMsgDetail.findViewById(R.id.buttonTitleBldgContruction);
        buttonTitleNoise = frameLayoutMsgDetail.findViewById(R.id.buttonTitleNoise);
        buttonTitleRecycle = frameLayoutMsgDetail.findViewById(R.id.buttonTitleRecycle);

        textViewMsgPreview = frameLayoutMsgDetail.findViewById(R.id.textViewMsgPreview);

        frameLayoutTitleChoices = frameLayoutMsgDetail.findViewById(R.id.frameLayoutTitleChoices);
//        frameLayoutCalendar = frameLayoutMsgDetail.findViewById(R.id.frameLayoutCalendar);


//        datePickerRequestDate = frameLayoutMsgDetail.findViewById(R.id.datePickerRequestDate);
        buttonInsertDateStart = frameLayoutMsgDetail.findViewById(R.id.buttonInsertDate_start);
        buttonInsertDateEnd = frameLayoutMsgDetail.findViewById(R.id.buttonInsertDate_end);
        buttonInsertTime = frameLayoutMsgDetail.findViewById(R.id.buttonInsertTime);

        buttonSendMsg = frameLayoutMsgDetail.findViewById(R.id.buttonSendMsg);


        initializeChoicesListener();
        initializeDatePickerListenr();

        //            FrameLayout frameLayoutDark2;
//            Button buttonInsertDate2;
//            DatePicker datePickerRequestDate;
//
//            @Override
//            public void onClick(View v) {
//                final LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                final View viewCalendar = inflater.inflate(R.layout.layout_message_detail_calendar, frameLayoutMsgDetail, false);
////                frameLayoutMsgDetail.removeAllViews();
//                frameLayoutMsgDetail.addView(viewCalendar);
//                frameLayoutDark2 = frameLayoutMsgDetail.findViewById(R.id.frameLayoutDark2);
//                frameLayoutDark2.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        frameLayoutMsgDetail.removeView(viewCalendar);
//
//
//
//                    }
//                });
//                buttonInsertDate2 = frameLayoutMsgDetail.findViewById(R.id.buttonInsertDate2);
//                datePickerRequestDate = frameLayoutMsgDetail.findViewById(R.id.datePickerRequestDate);
//
//                buttonInsertDate2.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        frameLayoutMsgDetail.removeView(viewCalendar);
//                        datePickerRequestDate.getMinDate();
//                        datePickerRequestDate.getMaxDate();
//
//                    }
//                });
//
//
//
//
//            }
//        });

    }

    public void initializeChoicesListener() {
        frameLayoutDark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked dark area");
                Toast.makeText(LandLordMessageActivity.this, "hello?", Toast.LENGTH_SHORT).show();
                frameLayoutMsgDetail.removeAllViews();
            }
        });


        buttonTitleAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewMsgPreview.setText("안녕하세요 집주인 입니다. \n0000년 00월 00일 00시 00분에 방문 가능한지 여쭤봅니다.");
                buttonInsertDateStart.setVisibility(View.VISIBLE);
                buttonInsertDateEnd.setVisibility(View.VISIBLE);
                buttonInsertTime.setVisibility(View.VISIBLE);
                titleClicked = "appointment";
                subject = "약속잡기";
            }
        });
        buttonTitleFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewMsgPreview.setText("안녕하세요 집주인 입니다. \n관리비가 아직 입금이 안되었습니다. 확인 후 입금 바랍니다.");
                buttonInsertDateStart.setVisibility(View.INVISIBLE);
                buttonInsertDateEnd.setVisibility(View.INVISIBLE);
                buttonInsertTime.setVisibility(View.INVISIBLE);
                subject = "관리비 청구 요청";
            }
        });

        buttonTitleMonthlyPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewMsgPreview.setText("안녕하세요 집주인 입니다. \n월세가 아직 입금이 안되었습니다. 확인 후 입금 바랍니다.");
                buttonInsertDateStart.setVisibility(View.INVISIBLE);
                buttonInsertDateEnd.setVisibility(View.INVISIBLE);
                buttonInsertTime.setVisibility(View.INVISIBLE);
                subject = "월세 청구 요청";
            }
        });

        buttonTitleBldgConstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewMsgPreview.setText("안녕하세요 집주인 입니다. \n0000년 00월 00일 ~ 0000년 00월 00일 공사가 있습니다.\n소음에 양해부탁드립니다.");
                buttonInsertDateStart.setVisibility(View.VISIBLE);
                buttonInsertDateEnd.setVisibility(View.VISIBLE);
                titleClicked = "bldgConstruction";
                buttonInsertTime.setVisibility(View.INVISIBLE);
                subject = "건물공사 공지";
            }
        });
        buttonTitleNoise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewMsgPreview.setText("안녕하세요 집주인 입니다. \n층간소음에 대해 항의 연락을 보고받았습니다.\n늦은 저녁 이웃간 층간 소음에 주의해주시길 바랍니다.");
                buttonInsertDateStart.setVisibility(View.INVISIBLE);
                buttonInsertDateEnd.setVisibility(View.INVISIBLE);
                buttonInsertTime.setVisibility(View.INVISIBLE);
                subject = "층간소음 주의";
            }
        });
        buttonTitleRecycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewMsgPreview.setText("안녕하세요 집주인 입니다. \n분리수거에 신경써 주시길 바랍니다.");
                buttonInsertDateStart.setVisibility(View.INVISIBLE);
                buttonInsertDateEnd.setVisibility(View.INVISIBLE);
                buttonInsertTime.setVisibility(View.INVISIBLE);
                subject = "분리수거";
            }
        });
    }

    int sYear, sMonth, sDay;
    int eYear, eMonth, eDay;

    public void initializeDatePickerListenr() {
        callbackMethodDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                if (dateStartEnd.equals("start")) {
                    sYear = year;
                    sMonth = month;
                    sDay = dayOfMonth;
                    textViewMsgPreview.setText("안녕하세요 집주인 입니다. \n" + year + "년 " + month + "월 " + dayOfMonth + "일 ~ "
                            + eYear + "년 " + eMonth + "월 " + eDay + "일 공사가 있습니다.\n소음에 양해부탁드립니다.");
                } else if (dateStartEnd.equals("end")) {
                    textViewMsgPreview.setText("안녕하세요 집주인 입니다. \n" + sYear + "년 " + sMonth + "월 " + sDay + "일 ~ "
                            + year + "년 " + month + "월 " + dayOfMonth + "일 공사가 있습니다.\n소음에 양해부탁드립니다.");
                }
            }
        };
        buttonInsertDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateStartEnd = "start";
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                DatePickerDialog dialog = new DatePickerDialog(frameLayoutMsgDetail.getContext(), callbackMethodDatePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), Calendar.DAY_OF_MONTH);
                dialog.show();
            }
        });

        buttonInsertDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateStartEnd = "end";
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                DatePickerDialog dialog = new DatePickerDialog(frameLayoutMsgDetail.getContext(), callbackMethodDatePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), Calendar.DAY_OF_MONTH);
                dialog.show();
            }
        });


        callbackMethodTimePicker = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            }
        };


        buttonInsertTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(frameLayoutMsgDetail.getContext(), callbackMethodTimePicker, 0, 0, true);
                dialog.show();
            }
        });

        buttonSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }

}

