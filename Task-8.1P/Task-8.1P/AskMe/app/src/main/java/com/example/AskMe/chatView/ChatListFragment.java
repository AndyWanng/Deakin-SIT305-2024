package com.example.AskMe.chatView;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AskMe.MainActivity;
import com.example.AskMe.R;
import com.example.AskMe.utils.SqlOperate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChatListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatListAdapter adapter;
    private SqlOperate sqlOperate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat_list, container, false);

        sqlOperate = new SqlOperate();
        recyclerView = view.findViewById(R.id.recycler_view_chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatListAdapter(getContext(), sqlOperate.getAllConversations(), sqlOperate);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAddChat = view.findViewById(R.id.fab_add_chat);
        fabAddChat.setOnClickListener(v -> addNewChat());

        return view;
    }

    private void addNewChat() {
        long conversationId = sqlOperate.addConversation();
        if (conversationId != -1) {
            ChatView chatView = new ChatView(conversationId);
            Activity activity = getActivity();
            if (activity instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) activity;
                mainActivity.frameLayout.removeAllViews();
                mainActivity.frameLayout.addView(chatView.Load(mainActivity));
            }
        } else {
            Toast.makeText(getActivity(), "New chat created failed", Toast.LENGTH_SHORT).show();
        }
    }

}
