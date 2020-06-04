package com.example.jiptalk.ui.message;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class TenantMessageActivity extends AppCompatActivity {

    private String TAG = "=== jiptalk.ui.message.LandLordMessageActivity";


    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private ChatDataAdapter chatDataAdapter;


    private DatabaseReference databaseReference;
    private String currentUserUID;
    private String chatUserName;
    private String chatUserUID;
    private String category;
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
        category = Constant.category;
//        databaseReference.child("user").child(currentUserUID).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                Constant.category = user.getCategory();
//                Log.d(TAG, "category : " + Constant.category);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        Constant.category = "임대인";
        Log.d(TAG, "currentUserUID : " + currentUserUID);
        /* These data should be saved in Constant class upon successful login.

        currentUserUID = Constant.userUID;
        category = Constant.category;
        */
        // Set ActionBar Title to name of the Tenant

        chatUserUID = getIntent().getStringExtra("clientUID").toString();

        chatUserName = getIntent().getStringExtra("clientName").toString();
        token = getIntent().getStringExtra("clientToken").toString();
        Log.d(TAG, "chatUserName : " + chatUserName);
        Log.d(TAG, "token : " + token);
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


//        imageViewSendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imageViewSendButton.setImageResource(R.drawable.ic_chat_send_hold);
//                sendMessage();
//                editTextMessage.setText("");
//                imageViewSendButton.setImageResource(R.drawable.ic_chat_send);
//            }
//        });

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

//        DatabaseReference messageRef = databaseReference.child("messages").child(currentUserUID).child(chatUserName);
//
//        Query messageQuery = messageRef.limitToLast(currentPage * TOTAL_ITEMS_TO_LOAD);

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
        public void onBindViewHolder(@NonNull ChatDataAdapter.ChatDataViewHolder holder, int position) {

            ChatDataDTO chatData = chatDataList.get(position);
            String from = chatData.getFrom();
            String subject = chatData.getSubject();

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
                holder.cardView.setCardBackgroundColor(Color.BLACK);
                holder.textViewSenderName.setVisibility(View.VISIBLE);
                holder.textViewSenderName.setText(chatUserID + "");
                holder.textViewMsgRecieved.setVisibility(View.VISIBLE);
                holder.textViewMsgRecieved.setText(chatData.getMessage());
                holder.textViewMsgSent.setVisibility(View.INVISIBLE);
                holder.textViewMsgSentTime.setVisibility(View.INVISIBLE);
                holder.textViewMsgRecievedTime.setVisibility(View.VISIBLE);
                holder.textViewMsgRecievedTime.setText(getDate(chatData.getTime()));
                holder.buttonPositive.setVisibility(View.VISIBLE);
                holder.buttonNegative.setVisibility(View.VISIBLE);



            }
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

        final LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View frameLayoutMsgInputFormTenant = inflater.inflate(R.layout.layout_message_detail_msg_input_form_tenant, frameLayoutMsgDetail, false);
        frameLayoutMsgDetail.addView(frameLayoutMsgInputFormTenant);
        initializeView();

    }


    public void initializeView() {
        frameLayoutDark = frameLayoutMsgDetail.findViewById(R.id.frameLayoutDark2);
        frameLayoutSendMsgLayout = frameLayoutMsgDetail.findViewById(R.id.frameLayoutSendMsgLayout2);

        buttonTitleAppointment = frameLayoutMsgDetail.findViewById(R.id.buttonTitleAppointment2);
        buttonTitleFee = frameLayoutMsgDetail.findViewById(R.id.buttonTitleFee2);
        buttonTitleMonthlyPayment = frameLayoutMsgDetail.findViewById(R.id.buttonTitleMonthlyPayment2);
        buttonTitleBldgConstruction = frameLayoutMsgDetail.findViewById(R.id.buttonTitleBldgContruction2);
        buttonTitleNoise = frameLayoutMsgDetail.findViewById(R.id.buttonTitleNoise2);
        buttonTitleRecycle = frameLayoutMsgDetail.findViewById(R.id.buttonTitleRecycle2);

        textViewMsgPreview = frameLayoutMsgDetail.findViewById(R.id.textViewMsgPreview2);

        frameLayoutTitleChoices = frameLayoutMsgDetail.findViewById(R.id.frameLayoutTitleChoices2);
//        frameLayoutCalendar = frameLayoutMsgDetail.findViewById(R.id.frameLayoutCalendar);


//        datePickerRequestDate = frameLayoutMsgDetail.findViewById(R.id.datePickerRequestDate);
        buttonInsertDateStart = frameLayoutMsgDetail.findViewById(R.id.buttonInsertDate_start2);
        buttonInsertDateEnd = frameLayoutMsgDetail.findViewById(R.id.buttonInsertDate_end2);
        buttonInsertTime = frameLayoutMsgDetail.findViewById(R.id.buttonInsertTime2);

        buttonSendMsg = frameLayoutMsgDetail.findViewById(R.id.buttonSendMsg2);


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
                Toast.makeText(TenantMessageActivity.this, "hello?", Toast.LENGTH_SHORT).show();
                frameLayoutMsgDetail.removeAllViews();
            }
        });


        buttonTitleAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewMsgPreview.setText("안녕하세요 세입자 입니다. \n0000년 00월 00일 00시 00분에 방문 가능한지 여쭤봅니다.");
                buttonInsertDateStart.setVisibility(View.VISIBLE);
                buttonInsertDateEnd.setVisibility(View.VISIBLE);
                buttonInsertTime.setVisibility(View.VISIBLE);
                titleClicked = "appointment";
                subject = "약속잡기";
            }
        });
//        buttonTitleFee.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                textViewMsgPreview.setText("안녕하세요 집주인 입니다. \n관리비가 아직 입금이 안되었습니다. 확인 후 입금 바랍니다.");
//                buttonInsertDateStart.setVisibility(View.INVISIBLE);
//                buttonInsertDateEnd.setVisibility(View.INVISIBLE);
//                buttonInsertTime.setVisibility(View.INVISIBLE);
//                subject = "관리비 청구 요청";
//            }
//        });
//
//        buttonTitleMonthlyPayment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                textViewMsgPreview.setText("안녕하세요 집주인 입니다. \n월세가 아직 입금이 안되었습니다. 확인 후 입금 바랍니다.");
//                buttonInsertDateStart.setVisibility(View.INVISIBLE);
//                buttonInsertDateEnd.setVisibility(View.INVISIBLE);
//                buttonInsertTime.setVisibility(View.INVISIBLE);
//                subject = "월세 청구 요청";
//            }
//        });

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
                textViewMsgPreview.setText("안녕하세요 해피하우스 101호 입니다. \n밤 중 층간소음이 너무 심해 항의 드립니다.\n.");
                buttonInsertDateStart.setVisibility(View.INVISIBLE);
                buttonInsertDateEnd.setVisibility(View.INVISIBLE);
                buttonInsertTime.setVisibility(View.INVISIBLE);
                subject = "층간소음 주의";
            }
        });
        buttonTitleRecycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewMsgPreview.setText("안녕하세요 해피하우스 101호 입니다. \n분리수거에 신경써 주시길 바랍니다.");
                buttonInsertDateStart.setVisibility(View.INVISIBLE);
                buttonInsertDateEnd.setVisibility(View.INVISIBLE);
                buttonInsertTime.setVisibility(View.INVISIBLE);
                subject = "분리수거";
            }
        });
    }

    public void initializeDatePickerListenr() {
        callbackMethodDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                if (dateStartEnd.equals("start")) {
                    textViewMsgPreview.setText("안녕하세요 집주인 입니다. \n" + year + "년 " + month + "월 " + dayOfMonth + "일 ~ 0000년 00월 00일 공사가 있습니다.\n소음에 양해부탁드립니다.");
                } else if (dateStartEnd.equals("end")) {
                    textViewMsgPreview.setText("안녕하세요 집주인 입니다. \n" + year + "년 " + month + "월 " + dayOfMonth + "일 ~ 0000년 00월 00일 공사가 있습니다.\n소음에 양해부탁드립니다.");
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
                Log.d(TAG, "buttonSendMsg clicked");
                sendMessage();
            }
        });

    }

}

