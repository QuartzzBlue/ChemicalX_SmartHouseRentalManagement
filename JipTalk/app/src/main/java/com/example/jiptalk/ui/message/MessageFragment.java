package com.example.jiptalk.ui.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jiptalk.R;

import java.util.ArrayList;

public class MessageFragment extends Fragment {
    ArrayList<MessageVO> msgList = new ArrayList<>();
    ArrayList<NotiVO> notiList = new ArrayList<>();

    MessageItemAdapter messageItemAdapter;
    NotiItemAdapter notiItemAdapter;

    private MessageViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(MessageViewModel.class);
        View root = inflater.inflate(R.layout.fragment_message, container, false);


        getMsgData();
        getNotiData();

        messageItemAdapter = new MessageItemAdapter(msgList);
        notiItemAdapter = new NotiItemAdapter(notiList);

        RecyclerView recyclerViewMsg = root.findViewById(R.id.recyclerViewMsg);
        recyclerViewMsg.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMsg.setAdapter(messageItemAdapter);

        RecyclerView recyclerViewNoti = root.findViewById(R.id.recyclerViewNoti);
        recyclerViewNoti.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewNoti.setAdapter(notiItemAdapter);

        return root;
    }


    class MessageItemAdapter extends RecyclerView.Adapter<MessageItemAdapter.ViewHolder> {

        ArrayList<MessageVO> messageList;

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, title, content, time;

            public ViewHolder(View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.textViewMsgName);
                title = itemView.findViewById(R.id.textViewMsgTitle);
                content = itemView.findViewById(R.id.textViewMsgContent);
                time = itemView.findViewById(R.id.textViewMsgTime);
            }
        }


        public MessageItemAdapter(ArrayList<MessageVO> messageList) {
            this.messageList = messageList;
        }


        @NonNull
        @Override
        public MessageItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_message, parent, false);
            MessageItemAdapter.ViewHolder viewHolder = new MessageItemAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MessageItemAdapter.ViewHolder holder, final int position) {

            holder.name.setText(messageList.get(position).getName() + "");
            holder.title.setText(messageList.get(position).getTitle() + "");
            holder.content.setText(messageList.get(position).getContent() + "");
            holder.time.setText(messageList.get(position).getTime() + "");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getContext(), messageList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), MessageDetailActivity.class);
                    intent.putExtra("name", messageList.get(position).getName()+"");
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }
    }

    class NotiItemAdapter extends RecyclerView.Adapter<NotiItemAdapter.ViewHolder> {

        ArrayList<NotiVO> notiList;

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


        public NotiItemAdapter(ArrayList<NotiVO> notiList) {
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


    public void getMsgData() {
        msgList.add(new MessageVO("최여진", "집수리", "화장실 세면대 수돗꼭지 누수", "00:00pm"));
        msgList.add(new MessageVO("조현민", "층간소음", "화장실 세면대 수돗꼭지 누수", "00:00pm"));
        msgList.add(new MessageVO("이슬", "복도청소", "화장실 세면대 수돗꼭지 누수", "00:00pm"));
        msgList.add(new MessageVO("ㅊㅇㅈ", "집수리", "화장실 세면대 수돗꼭지 누수", "00:00pm"));
        msgList.add(new MessageVO("ㅈㅎㅁ", "집수리", "화장실 세면대 수돗꼭지 누수", "00:00pm"));
        msgList.add(new MessageVO("ㅇㅅ", "집수리", "화장실 세면대 수돗꼭지 누수", "00:00pm"));
        msgList.add(new MessageVO("최여진", "집수리", "화장실 세면대 수돗꼭지 누수", "00:00pm"));
        msgList.add(new MessageVO("최여진", "집수리", "화장실 세면대 수돗꼭지 누수", "00:00pm"));
        msgList.add(new MessageVO("최여진", "집수리", "화장실 세면대 수돗꼭지 누수", "00:00pm"));
    }

    public void getNotiData() {
        notiList.add(new NotiVO("최여진", "집수리", "화장실 세면대 수돗꼭지 누수", "00:00pm"));
        notiList.add(new NotiVO("조현민", "층간소음", "화장실 세면대 수돗꼭지 누수", "00:00pm"));
        notiList.add(new NotiVO("이슬", "복도청소", "화장실 세면대 수돗꼭지 누수", "00:00pm"));
        notiList.add(new NotiVO("ㅊㅇㅈ", "집수리", "화장실 세면대 수돗꼭지 누수", "00:00pm"));
        notiList.add(new NotiVO("ㅈㅎㅁ", "집수리", "화장실 세면대 수돗꼭지 누수", "00:00pm"));
        notiList.add(new NotiVO("ㅇㅅ", "집수리", "화장실 세면대 수돗꼭지 누수", "00:00pm"));
        notiList.add(new NotiVO("최여진", "집수리", "화장실 세면대 수돗꼭지 누수", "00:00pm"));
        notiList.add(new NotiVO("최여진", "집수리", "화장실 세면대 수돗꼭지 누수", "00:00pm"));
        notiList.add(new NotiVO("최여진", "집수리", "화장실 세면대 수돗꼭지 누수", "00:00pm"));
        notiList.add(new NotiVO("최여진", "집수리", "화장실 세면대 수돗꼭지 누수", "00:00pm"));
    }
}