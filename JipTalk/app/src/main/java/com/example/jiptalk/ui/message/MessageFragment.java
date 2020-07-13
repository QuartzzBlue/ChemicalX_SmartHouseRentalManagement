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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jiptalk.AppData;
import com.example.jiptalk.R;
import com.example.jiptalk.vo.Building;
import com.example.jiptalk.vo.MessageVO;
import com.example.jiptalk.vo.Noti;
import com.example.jiptalk.vo.Unit;
import com.example.jiptalk.vo.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import static java.nio.file.Paths.get;

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
    private ArrayAdapter<String> arrayAdapterTenant, arrayAdapterNoti;
    private Button buttonAddMsg, buttonAddNoti;
    private String clientSelected;
    private ImageView imageViewDeleteMessages, imageViewDeleteMessage;
    private LinearLayout linearLayoutDeleteMessage;


    /////

    private RecyclerView recyclerViewMsg, recyclerViewNoti;
    private View mView;
    private String currentUserUID;
    private String category;
    private DatabaseReference rootRef, chatData, userData, myData, buildingData, notiData;
    private FirebaseRecyclerOptions<MessageVO> messageOptions;
    public FirebaseRecyclerAdapter<MessageVO, MessageViewHolder> messageAdapter;

    private FirebaseRecyclerOptions<Noti> notiOptions;
    public FirebaseRecyclerAdapter<Noti, NotiViewHolder> notiAdapter;
    private DatabaseReference mChatFriendData;


    ////

    public static final int VIEWTYPE_NORMAL = 0;
    public static final int VIEWTYPE_EDIT = 1;

    private ArrayList<String> clientList2 = new ArrayList<>();
    private ArrayList<String> buildingNameList = new ArrayList<>();
    private ArrayList<Building> buildingList = new ArrayList<>();


//    private MessageViewModel notificationsViewModel;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        Log.d(TAG, "Entered MessageFragment onCreateView");


        // remove Actionbar
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();


//        notificationsViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        root = inflater.inflate(R.layout.fragment_message, container, false);


        // initialize delete messages button etc.
        imageViewDeleteMessages = root.findViewById(R.id.imageViewDeleteMessages);
//        imageViewDeleteMessages.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

//        messageItemAdapter = new MessageItemAdapter(msgList);


//        RecyclerView recyclerViewMsg = root.findViewById(R.id.recyclerViewMsg);
//        recyclerViewMsg.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerViewMsg.setAdapter(messageItemAdapter);


        recyclerViewMsg = root.findViewById(R.id.recyclerViewMsg);
        recyclerViewMsg.setHasFixedSize(false);
        recyclerViewNoti = root.findViewById(R.id.recyclerViewNoti);
        recyclerViewNoti.setHasFixedSize(false);

        currentUserUID = FirebaseAuth.getInstance().getUid();
        Log.d(TAG, "currentUserID : " + currentUserUID);


        rootRef = FirebaseDatabase.getInstance().getReference();
        initiateClientList();
        myData = rootRef.child("user").child(currentUserUID);
        myData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "category : " + dataSnapshot.child("category").getValue().toString());
                category = dataSnapshot.child("category").getValue().toString();
                if (category.equals("세입자")) {
                    ImageView imageViewDeleteNotice = root.findViewById(R.id.imageViewDeleteNotice);
                    imageViewDeleteNotice.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        chatData = rootRef.child("chat").child(currentUserUID);

        notiData = rootRef.child("noti").child(currentUserUID);
        notiOptions = new FirebaseRecyclerOptions.Builder<Noti>().setQuery(notiData, Noti.class).build();
        notiAdapter = new FirebaseRecyclerAdapter<Noti, NotiViewHolder>(notiOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final NotiViewHolder holder, int i, @NonNull final Noti noti) {

                DatabaseReference notiRef = rootRef.child("noti").child(currentUserUID);
                notiRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Noti notiDataSnapshot = dataSnapshot.getValue(Noti.class);
                        Log.d(TAG, "notiDataSnapshot : " + notiDataSnapshot.toString());

                        holder.setBuildingName(notiDataSnapshot.getBuildingName());
                        holder.setTitle(notiDataSnapshot.getTitle());
                        holder.setContent(notiDataSnapshot.getContent());
                        holder.setTime(getDate(notiDataSnapshot.getTime()));
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
                notiRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            return;
                        }

//                        Noti notiDataSnapshot = dataSnapshot.getValue(Noti.class);
//                        Log.d(TAG, "notiDataSnapshot : " + notiDataSnapshot.toString());
//
//                        String key = dataSnapshot.getKey();
//                        Log.d(TAG, "noti key : " + key);
//                        Log.d(TAG, "noti value : " + dataSnapshot.getValue());
//                        Map<String, Object> notiMap = (Map<String, Object>) dataSnapshot.getValue();
//                        Iterator<String> notiKeyIterator = notiMap.keySet().iterator();
//                        while (notiKeyIterator.hasNext()) {
//                            String notiKey = notiKeyIterator.next();
//                            Log.d(TAG, "notiKey : " + notiKey);
//                            Object obj = notiMap.get(notiKey);
//                            Map notiObjMap = (Map) obj;
//                            Log.d(TAG, "noti VO buildingName : " + notiObjMap.get("buildingName").toString());
//
//
//                            final String buildingName = notiObjMap.get("buildingName").toString();
//                            final String title = notiObjMap.get("title").toString();
//                            final String content = notiObjMap.get("content").toString();
//                            final Long time = (Long) notiObjMap.get("time");
//
//                            Log.d(TAG, "noti added : " + title);
//
//
//                            holder.setBuildingName(buildingName);
//                            holder.setTitle(title);
//                            holder.setContent(content);
//                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("mOursMessage: ", databaseError.getMessage());
                    }
                });
            }

            @NonNull
            @Override
            public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_noti, parent, false);

                return new NotiViewHolder(view);
            }
        };

        messageOptions = new FirebaseRecyclerOptions.Builder<MessageVO>().setQuery(chatData, MessageVO.class).build();
        messageAdapter = new FirebaseRecyclerAdapter<MessageVO, MessageViewHolder>(messageOptions) {

            int viewType = VIEWTYPE_NORMAL;

            public void setViewType(int viewType) {
                this.viewType = viewType;
            }


            @Override
            protected void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position, @NonNull final MessageVO model) {
                final String chatUserUID = getRef(position).getKey();
                String chatUserToken = "";

                for (User u : clientList) {
                    if (u.getUID().equals(chatUserUID)) {
                        chatUserToken = u.getToken();
                    }
                }
                Log.d(TAG, "chatUserUID : " + chatUserUID);
                DatabaseReference mRootChat = rootRef.child("chat");

                final String finalChatUserToken = chatUserToken;
                mRootChat.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(currentUserUID).child(chatUserUID).child("lastMessageId").getValue() != null) {
                            String lastMessageId = dataSnapshot.child(currentUserUID).child(chatUserUID).child("lastMessageId").getValue().toString();
                            holder.chatLayout.setVisibility(View.VISIBLE);
                            holder.chatLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = null;
                                    if (category.equals("집주인") | category.equals("임대인")) {
                                        intent = new Intent(getContext(), LandLordMessageActivity.class);
                                    } else if (category.equals("세입자")) {
                                        intent = new Intent(getContext(), TenantMessageActivity.class);
                                    }
                                    intent.putExtra("clientName", getChatUserName(chatUserUID));
                                    intent.putExtra("clientUID", chatUserUID);
                                    intent.putExtra("clientToken", finalChatUserToken);
                                    startActivity(intent);
                                }
                            });

                            holder.chatLayout.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    Toast.makeText(getContext(), position + " Long Clicked", Toast.LENGTH_SHORT).show();


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

                                    Log.d("===", "chatUserUID: " + chatUserUID);
                                    holder.setName(getChatUserName(chatUserUID));
                                    holder.setContent(last_message);
                                    holder.setTime(getDate(message_time));
//

//                                    View.OnClickListener deleteButtonListener = new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            holder.linearLayoutDeleteMessage.setVisibility(View.VISIBLE);
//                                        }
//                                    };
//                                    imageViewDeleteMessages.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            holder.linearLayoutDeleteMessage.setVisibility(View.VISIBLE);
//                                        }
//                                    });
                                    //imageViewDeleteMessages.setOnClickListener(deleteButtonListener);

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
                View view;
                if (viewType == VIEWTYPE_NORMAL) {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
                } else {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_delete, parent, false);
                }
                return new MessageViewHolder(view);
            }

            public void setDeleteButtonVisibility(LinearLayout button) {
                if (imageViewDeleteMessages.callOnClick()) {
                    button.setVisibility(View.VISIBLE);
                }
            }


        };

        //notiItemAdapter = new NotiItemAdapter(notiList);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        recyclerViewMsg.setLayoutManager(layoutManager);
//        messageAdapter.startListening();
//        recyclerViewMsg.setAdapter(messageAdapter);


//        RecyclerView recyclerViewNoti = root.findViewById(R.id.recyclerViewNoti);
//        recyclerViewNoti.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerViewNoti.setAdapter(notiItemAdapter);


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
        spinnerBuildingList = root.findViewById(R.id.spinnerBuildingList);
        buttonAddMsg = root.findViewById(R.id.buttonAddMsg);
        buttonAddNoti = root.findViewById(R.id.buttonAddNoti);


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
                arrayAdapterNoti = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, buildingNameList);
                spinnerBuildingList.setAdapter(arrayAdapterNoti);
                spinnerBuildingList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                        buttonAddNoti.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (category != null && (category.equals("집주인") || category.equals("임대인"))) {
                                    Intent intent = new Intent(getContext(), AddNotiActivity.class);
                                    intent.putExtra("buildingName", buildingList.get(position).getName());
                                    intent.putExtra("buildingKey", buildingList.get(position).getId());
                                    startActivity(intent);
                                }
                            }
                        });

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


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
                                    if (category.equals("집주인") | category.equals("임대인")) {
                                        intent = new Intent(getContext(), LandLordMessageActivity.class);
                                    } else if (category.equals("세입자")) {
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
                if (category != null && category.equals("세입자")) {
                    fabAddNoti.setVisibility(View.GONE);
                }
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


        LinearLayoutManager layoutManagerMsg = new LinearLayoutManager(getContext());
        recyclerViewMsg.setLayoutManager(layoutManagerMsg);
        messageAdapter.startListening();
        recyclerViewMsg.setAdapter(messageAdapter);

        LinearLayoutManager layoutManagerNoti = new LinearLayoutManager(getContext());
        recyclerViewNoti.setLayoutManager(layoutManagerNoti);
        notiAdapter.startListening();
        recyclerViewNoti.setAdapter(notiAdapter);


        imageViewDeleteMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return root;
    }


    public void initiateClientList() {

        buildingData = rootRef.child("buildings").child(currentUserUID);
        buildingData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Building buildingItem = postSnapshot.getValue(Building.class);
                    buildingItem.setId(postSnapshot.getKey());
                    buildingList.add(buildingItem);
                    buildingNameList.add(buildingItem.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.d(TAG, "BuildingList : " + buildingList.toString());
        Iterator<String> keys = AppData.buildings.keySet().iterator();
        while (keys.hasNext()) {


            Map<String, Unit> unitHashMap = AppData.buildings.get(keys.next()).getUnits();

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


    // Message ViewHolder for Firebase RecyclerView Adapter
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout chatLayout;
        TextView textViewName, textViewTitle, textViewContent, textViewTime;
        LinearLayout linearLayoutDeleteMessage;

        public MessageViewHolder(final View itemView) {
            super(itemView);
            chatLayout = itemView.findViewById(R.id.linearLayoutMessageItem);
            textViewName = itemView.findViewById(R.id.textViewMsgName);
            textViewTitle = itemView.findViewById(R.id.textViewMsgTitle);
            textViewContent = itemView.findViewById(R.id.textViewMsgContent);
            textViewTime = itemView.findViewById(R.id.textViewMsgTime);
            linearLayoutDeleteMessage = itemView.findViewById(R.id.linearLayoutDeleteMessage);
        }

        public void setName(String name) {
            textViewName.setText(name);
        }

        public void setTitle(String title) {
            textViewTitle.setText(title);
        }

        public void setContent(String content) {
            textViewContent.setText(content);
        }


        public void setTime(String time) {
            textViewTime.setText(time);
        }

        public void setDeleteButtonVisible() {
            linearLayoutDeleteMessage.setVisibility(View.VISIBLE);
        }

        public void setDeleteButtonGone() {
            linearLayoutDeleteMessage.setVisibility(View.GONE);
        }

    }

    // Noti ViewHolder for Firebase RecyclerView Adapter
    public static class NotiViewHolder extends RecyclerView.ViewHolder {

        LinearLayout noticeLayout;
        TextView textViewTitle, textViewConent, textViewTime, textViewBuildingName;

        public NotiViewHolder(final View itemView) {
            super(itemView);
            noticeLayout = itemView.findViewById(R.id.linearLayoutNoticeItem);
            textViewBuildingName = itemView.findViewById(R.id.textViewNotiBuildingName);
            textViewTitle = itemView.findViewById(R.id.textViewNotiTitle);
            textViewConent = itemView.findViewById(R.id.textViewNotiContent);
            textViewTime = itemView.findViewById(R.id.textViewNotiTime);
        }

        public void setBuildingName(String buildingName) {
            buildingName = buildingName.substring(0, 2);
            textViewBuildingName.setText(buildingName);
        }

        public void setTitle(String title) {
            textViewTitle.setText(title);
        }

        public void setContent(String content) {
            textViewConent.setText(content);
        }


        public void setTime(String time) {
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


    //


    class NotiItemAdapter extends RecyclerView.Adapter<NotiItemAdapter.ViewHolder> {

        ArrayList<Noti> notiList;

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, title, content, time;

            public ViewHolder(View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.textViewNotiBuildingName);
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

            holder.name.setText(notiList.get(position).getBuildingName() + "");
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