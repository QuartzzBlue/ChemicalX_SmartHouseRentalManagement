package com.example.jiptalk.ui.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jiptalk.Constant;
import com.example.jiptalk.R;
import com.example.jiptalk.vo.Building;
import com.example.jiptalk.vo.MessageVO;
import com.example.jiptalk.vo.Noti;
import com.example.jiptalk.vo.Unit;
import com.example.jiptalk.vo.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class MessageFragment extends Fragment {

    private String TAG = "=== jiptalk.ui.message.MessageFragment";

    ArrayList<MessageVO> msgList = new ArrayList<>();
    ArrayList<Noti> notiList = new ArrayList<>();

    NotiItemAdapter notiItemAdapter;

    View root;


    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fabAddBtn, fabAddMsg, fabAddNoti;
    private FrameLayout frameLayoutMessage, frameLayoutAddMsg, frameLayoutAddNoti;
    private TextView textViewAddMsg, textViewAddNoti;
    private Spinner spinnerTenantList, spinnerBuildingList;
    private ArrayList<String> clientNameList = new ArrayList<>();
    private ArrayList<User> clientList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapterTenant;
    private Button buttonAddMsg, buttonAddNoti;
    private String clientSelected;
    private ImageView imageViewDeleteMessages, imageViewDeleteMessage;
    private LinearLayout linearLayoutDeleteMessage;


    /////

    private RecyclerView recyclerViewMsg;
    private View mView;
    private String currentUserUID;
    private String category;
    private DatabaseReference rootRef, chatData, userData;
    private FirebaseRecyclerOptions<MessageVO> messageOptions;
    private FirebaseRecyclerAdapter<MessageVO, MessageViewHolder> messageAdapter;
    private DatabaseReference mChatFriendData;


    ////

    private ArrayList<String> clientList2 = new ArrayList<>();


//    private MessageViewModel notificationsViewModel;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



        Log.d(TAG, "Entered MessageFragment onCreateView");
        initiateClientList();


        // remove Actionbar
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();


//        notificationsViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        root = inflater.inflate(R.layout.fragment_message, container, false);

//        messageItemAdapter = new MessageItemAdapter(msgList);
        notiItemAdapter = new NotiItemAdapter(notiList);

//        RecyclerView recyclerViewMsg = root.findViewById(R.id.recyclerViewMsg);
//        recyclerViewMsg.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerViewMsg.setAdapter(messageItemAdapter);


        recyclerViewMsg = root.findViewById(R.id.recyclerViewMsg);
        recyclerViewMsg.setHasFixedSize(false);
        currentUserUID = FirebaseAuth.getInstance().getUid();
//         currentUserUID = "집주인";
//        currentUserUID = "조현민";
//        category = "임대인";
//        currentUserUID = "이슬";
//        category = "임차인";
        rootRef = FirebaseDatabase.getInstance().getReference();
        chatData = rootRef.child("chat").child(currentUserUID);

        messageOptions = new FirebaseRecyclerOptions.Builder<MessageVO>().setQuery(chatData, MessageVO.class).build();


        messageAdapter = new FirebaseRecyclerAdapter<MessageVO, MessageViewHolder>(messageOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final MessageViewHolder holder, int position, @NonNull final MessageVO model) {
                final String chatUserUID = getRef(position).getKey();
                Log.d(TAG, "chatUserUID : " + chatUserUID);
                DatabaseReference mRootChat = rootRef.child("chat");
                mRootChat.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(currentUserUID).child(chatUserUID).child("lastMessageId").getValue() != null) {
                            String lastMessageId = dataSnapshot.child(currentUserUID).child(chatUserUID).child("lastMessageId").getValue().toString();
                            holder.chatLayout.setVisibility(View.VISIBLE);
                            holder.chatLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = null;
                                    if (Constant.category.equals("집주인")) {
                                        intent = new Intent(getContext(), LandLordMessageActivity.class);
                                    } else if (Constant.category.equals("세입자")) {
                                        intent = new Intent(getContext(), TenantMessageActivity.class);
                                    }


                                    intent.putExtra("clientName", getChatUserName(chatUserUID));
                                    intent.putExtra("clientUID", chatUserUID);
                                    intent.putExtra("clientToken", model.getToken());
                                    startActivity(intent);
                                }
                            });

                            holder.chatLayout.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    Toast.makeText(getContext(), "Long Clicked", Toast.LENGTH_SHORT).show();


                                    return false;
                                }
                            });
                            DatabaseReference mOursMessage = rootRef.child("messages").child(currentUserUID).child(chatUserUID).child(lastMessageId);
                            mOursMessage.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() == 0) {
                                        return;
                                    }
                                    final String fromID = dataSnapshot.child("from").getValue(String.class);
                                    final String last_message = dataSnapshot.child("message").getValue(String.class);
                                    final Long message_time = dataSnapshot.child("time").getValue(Long.class);


                                    holder.setName(getChatUserName(chatUserUID));
                                    holder.setContent(last_message);
                                    holder.setTime(getDate(message_time));

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.d("mOursMessage: ", databaseError.getMessage());
                                }
                            });
                        } else {
                            holder.chatLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("mRootChat: ", databaseError.getMessage());
                    }
                });


            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
                return new MessageViewHolder(view);
            }
        };


//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        recyclerViewMsg.setLayoutManager(layoutManager);
//        messageAdapter.startListening();
//        recyclerViewMsg.setAdapter(messageAdapter);


        RecyclerView recyclerViewNoti = root.findViewById(R.id.recyclerViewNoti);
        recyclerViewNoti.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewNoti.setAdapter(notiItemAdapter);


        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);

        fabAddBtn = (FloatingActionButton) root.findViewById(R.id.fabAddBtn);
        fabAddMsg = (FloatingActionButton) root.findViewById(R.id.fabAddMsg);
        fabAddNoti = (FloatingActionButton) root.findViewById(R.id.fabAddNoti);

        frameLayoutMessage = (FrameLayout) root.findViewById(R.id.frameLayoutMessage);
        frameLayoutAddMsg = (FrameLayout) root.findViewById(R.id.frameLayoutAddMsg);
        frameLayoutAddNoti = (FrameLayout) root.findViewById(R.id.frameLayoutAddNoti);

        textViewAddMsg = root.findViewById(R.id.textViewAddMsg);
        textViewAddNoti = root.findViewById(R.id.textViewAddNoti);

        spinnerTenantList = root.findViewById(R.id.spinnerTenantList);
        buttonAddMsg = root.findViewById(R.id.buttonAddMsg);


        /**
         * @author: JHM9191
         * @detail: set name of clients in spinner when add new message button clicked to add new message
         */
//        userData = FirebaseDatabase.getInstance().getReference().child("client"); // data should be fetched from client collection
        userData = FirebaseDatabase.getInstance().getReference().child("user"); // let's just get data from user from now.

//        userData.addListenerForSingleValueEvent(new ValueEventListener() {
        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "Entered MessageFragment addListenerForSingleValueEvent");

                    User client = userSnapshot.getValue(User.class);
                    client.setUID(userSnapshot.getKey());
                    Log.d(TAG, client.toString());
                    Log.d(TAG, client.getUID() + "");
                    if (client != null) {
                        clientList.add(client);
                        clientNameList.add(client.getName());
                    }
                }

                arrayAdapterTenant = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, clientNameList);
                spinnerTenantList.setAdapter(arrayAdapterTenant);
                spinnerTenantList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                        clientSelected = clientNameList.get(position);
                        buttonAddMsg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (clientSelected != null) {
                                    Intent intent = null;
                                    if (Constant.category.equals("집주인")) {
                                        intent = new Intent(getContext(), LandLordMessageActivity.class);
                                    } else if (Constant.category.equals("세입자")) {
                                        intent = new Intent(getContext(), TenantMessageActivity.class);
                                    }
                                    Log.d(TAG, "client UID : " + clientList.get(position).getUID());
                                    intent.putExtra("clientUID", clientList.get(position).getUID() + "");
                                    intent.putExtra("clientName", clientSelected + "");
                                    intent.putExtra("clientToken", clientList.get(position).getToken() + "");
                                    startActivity(intent);
                                }
                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        fabAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayoutMessage.setAlpha(0.7f);
                frameLayoutMessage.setClickable(true);
                anim();

            }
        });


        fabAddMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
                frameLayoutMessage.setVisibility(View.VISIBLE);
                frameLayoutMessage.setAlpha(0.7f);
                frameLayoutAddMsg.setVisibility(View.VISIBLE);

            }
        });

        fabAddNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
                frameLayoutMessage.setVisibility(View.VISIBLE);
                frameLayoutMessage.setAlpha(0.7f);
                frameLayoutAddNoti.setVisibility(View.VISIBLE);
            }
        });

        frameLayoutMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayoutAddMsg.setVisibility(View.GONE);
                frameLayoutAddNoti.setVisibility(View.GONE);
//                frameLayoutMessage.setClickable(false);
                frameLayoutMessage.setVisibility(View.GONE);

                if (isFabOpen) {
                    fabAddMsg.startAnimation(fab_close);
                    fabAddNoti.startAnimation(fab_close);
                }
                fabAddMsg.setClickable(false);
                fabAddNoti.setClickable(false);
                textViewAddMsg.setVisibility(View.INVISIBLE);
                textViewAddNoti.setVisibility(View.INVISIBLE);
                isFabOpen = false;
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewMsg.setLayoutManager(layoutManager);
        messageAdapter.startListening();
        recyclerViewMsg.setAdapter(messageAdapter);



        // initialize delete messages button etc.
        imageViewDeleteMessages = root.findViewById(R.id.imageViewDeleteMessages);
        imageViewDeleteMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });




        return root;
    }


    public void initiateClientList() {

        Iterator<String> keys = Constant.buildings.keySet().iterator();
        while (keys.hasNext()) {
            Map<String, Unit> unitHashMap = Constant.buildings.get(keys.next()).getUnits();

            Iterator<String> unitKeys = unitHashMap.keySet().iterator();
            while (unitKeys.hasNext()) {
                clientList2.add(unitHashMap.get(unitKeys.next()).getTenantName());
            }
        }
        Log.d(TAG, "testtest : " + clientList2.toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Entered MessageFragment onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Entered MessageFragment on Destroy");
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.d(TAG, "Entered MessageFragment on onAttachFragment");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "Entered MessageFragment on onDetach");

    }

    public String getChatUserName(String chatUserUID) {
        Log.d(TAG, "Entered MessageFragment getChatUserName");
        Log.d(TAG, "clientList : " + clientList);
        for (User user : clientList) {
            if (chatUserUID.equals(user.getUID())) {
                return user.getName();
            }
        }
        return "NoName";
    }

    public void anim() {

        if (isFabOpen) {
//            frameLayoutMessage.setAlpha(0);
            frameLayoutMessage.setVisibility(View.GONE);
            fabAddMsg.startAnimation(fab_close);
            fabAddNoti.startAnimation(fab_close);
            fabAddMsg.setClickable(false);
            fabAddNoti.setClickable(false);
            textViewAddMsg.setVisibility(View.INVISIBLE);
            textViewAddNoti.setVisibility(View.INVISIBLE);
            isFabOpen = false;
        } else {
            frameLayoutMessage.setVisibility(View.VISIBLE);
            frameLayoutMessage.setAlpha(0.7f);
            frameLayoutMessage.setClickable(true);
            fabAddMsg.startAnimation(fab_open);
            fabAddNoti.startAnimation(fab_open);
            fabAddMsg.setClickable(true);
            fabAddNoti.setClickable(true);
            textViewAddMsg.setVisibility(View.VISIBLE);
            textViewAddNoti.setVisibility(View.VISIBLE);
            isFabOpen = true;
        }
    }


    // MessageVO
    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        LinearLayout chatLayout;

        public MessageViewHolder(final View itemView) {
            super(itemView);
            chatLayout = itemView.findViewById(R.id.linearLayoutMessageItem);
        }

        public void setName(String name) {
            TextView textViewName = itemView.findViewById(R.id.textViewMsgName);
            textViewName.setText(name);
        }

        public void setTitle(String title) {
            TextView textViewTitle = itemView.findViewById(R.id.textViewMsgTitle);
            textViewTitle.setText(title);
        }

        public void setContent(String content) {
            TextView textViewConent = itemView.findViewById(R.id.textViewMsgContent);
            textViewConent.setText(content);
        }


        public void setTime(String time) {
            TextView textViewTime = itemView.findViewById(R.id.textViewMsgTime);
            textViewTime.setText(time);
        }

    }

    public String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = null;
//        Log.d(TAG, "yesterday : " + DateFormat.format("MMdd", cal.get(cal.DATE) - 1));
        long today = System.currentTimeMillis();
//        Log.d(TAG, "today : " + today);
//        Log.d(TAG, "time : " + time);
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


    class NotiItemAdapter extends RecyclerView.Adapter<NotiItemAdapter.ViewHolder> {

        ArrayList<Noti> notiList;

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, title, content, time;

            public ViewHolder(View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.textViewNotiName);
                title = itemView.findViewById(R.id.textViewNotiTitle);
                content = itemView.findViewById(R.id.textViewNotiContent);
                time = itemView.findViewById(R.id.textViewNotiTime);
            }
        }


        public NotiItemAdapter(ArrayList<Noti> notiList) {
            this.notiList = notiList;
        }


        @NonNull
        @Override
        public NotiItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_noti, parent, false);
            NotiItemAdapter.ViewHolder viewHolder = new NotiItemAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull NotiItemAdapter.ViewHolder holder, final int position) {

            holder.name.setText(notiList.get(position).getName() + "");
            holder.title.setText(notiList.get(position).getTitle() + "");
            holder.content.setText(notiList.get(position).getContent() + "");
            holder.time.setText(notiList.get(position).getTime() + "");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getContext(), notiList.get(position).getTitle(), Toast.LENGTH_SHORT).show();

                }
            });
        }

        @Override
        public int getItemCount() {
            return notiList.size();
        }
    }


}