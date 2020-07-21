package com.example.jiptalk.ui.message;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.contentcapture.DataRemovalRequest;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jiptalk.AppData;
import com.example.jiptalk.R;
import com.example.jiptalk.vo.Building;
import com.example.jiptalk.vo.MessageVO;
import com.example.jiptalk.vo.Noti;
import com.example.jiptalk.vo.Tenant;
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
import com.zerobranch.layout.SwipeLayout;

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


    /////

    private RecyclerView recyclerViewMsg, recyclerViewNoti;
    private View mView;
    private String currentUserUID;
    private String category;
    private DatabaseReference rootRef, chatData, tenantData, myData, buildingData, notiData;
    private FirebaseRecyclerOptions<MessageVO> messageOptions;
    public FirebaseRecyclerAdapter<MessageVO, RecyclerView.ViewHolder> messageAdapter;

    private FirebaseRecyclerOptions<Noti> notiOptions;
    public FirebaseRecyclerAdapter<Noti, NotiViewHolder> notiAdapter;
    private DatabaseReference mChatFriendData;


    ////

    public static final int VIEWTYPE_NORMAL = 0;
    public static final int VIEWTYPE_EDIT = 1;

    private ArrayList<String> clientList2 = new ArrayList<>();
    private ArrayList<String> buildingNameList = new ArrayList<>();
    private ArrayList<Building> buildingList = new ArrayList<>();
    private ArrayList<String> buildingKeyList = new ArrayList<>();


//    private MessageViewModel notificationsViewModel;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        Log.d(TAG, "Entered MessageFragment onCreateView");


        // remove Actionbar
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();


//        notificationsViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        root = inflater.inflate(R.layout.fragment_message, container, false);


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

                Log.d(TAG, "notinoti : " + noti.toString());
                holder.setBuildingName(noti.getBuildingName());
                holder.setTitle(noti.getTitle());
                holder.setContent(noti.getContent());
                holder.setTime(getDate(noti.getTime()));
                holder.noticeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(noti.getBuildingName()).setMessage("제목: " + noti.getTitle() + "\n\n내용:\n" + noti.getContent());
                        builder.setNegativeButton("닫기", null);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    }
                });
                DatabaseReference refNoti = getRef(i);
                Log.d(TAG, "notiUserUID getRef(i) : " + refNoti.getKey());
                final String key = refNoti.getKey();
                holder.swipeLayoutNoti.setOnActionsListener(new SwipeLayout.SwipeActionsListener() {
                    @Override
                    public void onOpen(int direction, boolean isContinuous) {
                        holder.imageView_noti_delete.setVisibility(View.VISIBLE);
                        holder.imageView_noti_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("공지사항 삭제").setMessage("정말로 삭제하시겠습니까?");
                                builder.setNeutralButton("취소", null);
                                builder.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DatabaseReference refNoti = rootRef.child("noti").child(currentUserUID).child(key);
                                        refNoti.removeValue();

                                        Toast.makeText(getContext(), "공지 삭제", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        holder.swipeLayoutNoti.close();
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onClose() {

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
        messageAdapter = new FirebaseRecyclerAdapter<MessageVO, RecyclerView.ViewHolder>(messageOptions) {

            int viewType = VIEWTYPE_NORMAL;

            public void setViewType(int viewType) {
                this.viewType = viewType;
            }


            @Override
            protected void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position, @NonNull final MessageVO model) {

                if (holder instanceof MessageViewHolder) {


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
                                ((MessageViewHolder) holder).chatLayout.setVisibility(View.VISIBLE);
                                ((MessageViewHolder) holder).chatLayout.setOnClickListener(new View.OnClickListener() {
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

                                ((MessageViewHolder) holder).chatLayout.setOnLongClickListener(new View.OnLongClickListener() {
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
                                        String messageUserUID = rootRef.child("messages").child(currentUserUID).child(chatUserUID).getKey();
                                        Log.d(TAG, "messageUserUID : " + messageUserUID);

                                        Log.d("===", "chatUserUID: " + chatUserUID);
                                        ((MessageViewHolder) holder).setName(getChatUserName(chatUserUID));
                                        ((MessageViewHolder) holder).setContent(last_message);
                                        ((MessageViewHolder) holder).setTime(getDate(message_time));
//

                                        ((MessageViewHolder) holder).swipeLayoutMessage.setOnActionsListener(new SwipeLayout.SwipeActionsListener() {
                                            @Override
                                            public void onOpen(int direction, boolean isContinuous) {
                                                if (direction == SwipeLayout.RIGHT) {
                                                    // was executed swipe to the right

                                                    ((MessageViewHolder) holder).imageView_message_delete.setVisibility(View.VISIBLE);
                                                    ((MessageViewHolder) holder).imageView_message_delete.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                            builder.setTitle("메세지 삭제").setMessage("정말로 삭제하시겠습니까?");
                                                            builder.setNeutralButton("취소", null);
                                                            builder.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    DatabaseReference refMessage = rootRef.child("messages").child(currentUserUID).child(chatUserUID);
                                                                    refMessage.removeValue();
                                                                    DatabaseReference refChat = rootRef.child("chat").child(currentUserUID).child(chatUserUID);
                                                                    refChat.removeValue();
                                                                    Toast.makeText(getContext(), last_message + " 삭제", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                            AlertDialog alertDialog = builder.create();
                                                            alertDialog.show();
                                                            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                                @Override
                                                                public void onDismiss(DialogInterface dialog) {
                                                                    ((MessageViewHolder) holder).swipeLayoutMessage.close();
                                                                }
                                                            });
                                                        }
                                                    });

                                                } else if (direction == SwipeLayout.LEFT) {
                                                    // was executed swipe to the left
                                                    ((MessageViewHolder) holder).swipeLayoutMessage.openRight(false);

                                                }
                                            }

                                            @Override
                                            public void onClose() {

                                            }
                                        });
//                                    View.OnClickListener deleteButtonListener = new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            holder.linearLayoutDeleteMessage.setVisibility(View.VISIBLE);
//                                        }
//                                    };


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.d("mOursMessage: ", databaseError.getMessage());
                                    }
                                });
                            } else {
                                ((MessageViewHolder) holder).chatLayout.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("mRootChat: ", databaseError.getMessage());
                        }
                    });
                }

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
//        tenantData = FirebaseDatabase.getInstance().getReference().child("client"); // data should be fetched from client collection
        tenantData = FirebaseDatabase.getInstance().getReference().child("registeredTenants"); // let's just get data from user from now.

//        userData.addListenerForSingleValueEvent(new ValueEventListener() {
        tenantData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "tenantData dataSnapshot : " + dataSnapshot);

                for (DataSnapshot tenantSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "Entered MessageFragment addValueEventListener");
                    Log.d(TAG, "tenantSnapshot : " + tenantSnapshot);
                    final Tenant tenant = tenantSnapshot.getValue(Tenant.class);
                    Log.d(TAG, "tenant : " + tenant.toString());
                    for (String key : buildingKeyList) {
                        if (key.equals(tenant.getBuildingID())) {

                            clientNameList.add(tenant.getName());
                            Log.d(TAG, "clientNameList : " + clientNameList);
                        }
                    }

                    DatabaseReference userData = rootRef.child("user");
                    userData.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                Log.d(TAG, "userSnapshot.getValue  : " + userSnapshot.getValue(User.class).toString());
                                User user = userSnapshot.getValue(User.class);
                                if (tenant.getUnitID().equals(user.getUnitID())) {
                                    clientList.add(user);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
//                    Log.d(TAG, "" + client.toString());
//                    Log.d(TAG, client.getUID() + "");
//
//                    if (client != null) {
//                        clientList.add(client);
//                        clientNameList.add(client.getName());
//                    }
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

                        String name = null;
                        for (User user : clientList) {
                            if (clientSelected.equals(user.getName())) {
                                name = user.getName();
                            }

                        }

                        final String finalName = name;
                        buttonAddMsg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (clientSelected != null && finalName != null) {
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
                                } else if (finalName == null) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("알림").setMessage("없는 계정입니다.");
                                    builder.setNegativeButton("닫기", null);
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
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
//                if (category != null && category.equals("세입자")) {
//                    Log.d(TAG, "clicked fabAddBtn 세입자");
//                    fabAddNoti.setVisibility(View.GONE);
//                } else if (category != null && (category.equals("집주인") || category.equals("임대인"))) {
//                    Log.d(TAG, "clicked fabAddBtn 집주인");
//                }
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


                    buildingKeyList.add(postSnapshot.getKey());
                }
                Log.d(TAG, "buildingKeyList.toString() : " + buildingKeyList.toString());
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
            fabAddMsg.setClickable(false);
            textViewAddMsg.setVisibility(View.INVISIBLE);
            if (category != null && category.equals("세입자")) {
                Log.d(TAG, "세입자 GONE if");
                fabAddNoti.setVisibility(View.GONE);
                textViewAddNoti.setVisibility(View.GONE);

            } else {
                fabAddNoti.startAnimation(fab_close);
                fabAddNoti.setClickable(false);
                textViewAddNoti.setVisibility(View.INVISIBLE);
            }

            isFabOpen = false;
        } else {
            frameLayoutMessage.setVisibility(View.VISIBLE);
            frameLayoutMessage.setAlpha(0.7f);
            frameLayoutMessage.setClickable(true);
            fabAddMsg.startAnimation(fab_open);
            fabAddMsg.setClickable(true);
            textViewAddMsg.setVisibility(View.VISIBLE);
            if (category != null && category.equals("세입자")) {
                Log.d(TAG, "세입자 GONE else");
                fabAddNoti.setVisibility(View.GONE);
                textViewAddNoti.setVisibility(View.GONE);
            } else {
                fabAddNoti.startAnimation(fab_open);
                fabAddNoti.setClickable(true);
                textViewAddNoti.setVisibility(View.VISIBLE);
            }

            isFabOpen = true;
        }
    }


    // Message ViewHolder for Firebase RecyclerView Adapter
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout chatLayout;
        TextView textViewName, textViewTitle, textViewContent, textViewTime;
        LinearLayout linearLayoutDeleteMessage;
        SwipeLayout swipeLayoutMessage;
        ImageView imageView_message_delete;

        public MessageViewHolder(final View itemView) {
            super(itemView);
            chatLayout = itemView.findViewById(R.id.linearLayoutMessageItem);
            textViewName = itemView.findViewById(R.id.textViewMsgName);
            textViewTitle = itemView.findViewById(R.id.textViewMsgTitle);
            textViewContent = itemView.findViewById(R.id.textViewMsgContent);
            textViewTime = itemView.findViewById(R.id.textViewMsgTime);
            linearLayoutDeleteMessage = itemView.findViewById(R.id.linearLayoutDeleteMessage);
            swipeLayoutMessage = itemView.findViewById(R.id.swipe_layout_message);
            imageView_message_delete = itemView.findViewById(R.id.imageView_message_delete);
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

        public LinearLayout getDeleteButton() {
            return this.linearLayoutDeleteMessage;
        }

        public void setDeleteButtonGone() {
            linearLayoutDeleteMessage.setVisibility(View.GONE);
        }

    }


    // Noti ViewHolder for Firebase RecyclerView Adapter
    public static class NotiViewHolder extends RecyclerView.ViewHolder {

        LinearLayout noticeLayout;
        TextView textViewTitle, textViewConent, textViewTime, textViewBuildingName;
        SwipeLayout swipeLayoutNoti;
        ImageView imageView_noti_delete;

        public NotiViewHolder(final View itemView) {
            super(itemView);
            noticeLayout = itemView.findViewById(R.id.linearLayoutNoticeItem);
            textViewBuildingName = itemView.findViewById(R.id.textViewNotiBuildingName);
            textViewTitle = itemView.findViewById(R.id.textViewNotiTitle);
            textViewConent = itemView.findViewById(R.id.textViewNotiContent);
            textViewTime = itemView.findViewById(R.id.textViewNotiTime);
            swipeLayoutNoti = itemView.findViewById(R.id.swipe_layout_noti);
            imageView_noti_delete = itemView.findViewById(R.id.imageView_noti_delete);
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