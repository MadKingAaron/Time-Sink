package com.example.projecttimesink;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerView_Config
{
    private Context context;
    private UserAdapter userAdapter;

//    public void setConfig(RecyclerView recyclerView, Context context, List<User> users, List<String> keys)
    public void setConfig(RecyclerView recyclerView, Context context, User[] users, String[] keys)
    {
        this.context = context;
        this.userAdapter = new UserAdapter(users, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(this.userAdapter);
    }

    class UserItemView extends RecyclerView.ViewHolder
    {
        private TextView username_textView;
        private TextView time_textView;

        private String key;

        public UserItemView(ViewGroup parent)
        {
            super(LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false));

            this.username_textView = itemView.findViewById(R.id.username_textView);
            this.time_textView = itemView.findViewById(R.id.time_textView);
        }

        public void bind(User user, String key, int placement)
        {
            this.username_textView.setText(placement + ") " + user.username);
            this.time_textView.setText(TimeText.getTimeString(user.longestTimeWasted, 1));
            this.key = key;
        }
    }

    class UserAdapter extends RecyclerView.Adapter<UserItemView>
    {
//        private List<User> users;
//        private List<String> keys;
        private User[] users;
        private String[] keys;

//        public UserAdapter(List<User> users, List<String> keys)
        public UserAdapter(User[] users, String[] keys)
        {
            this.users = users;
            this.keys = keys;
        }

        @NonNull
        @Override
        public UserItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            return new UserItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull UserItemView holder, int position)
        {
//            holder.bind(this.users.get(position), this.keys.get(position));
            holder.bind(this.users[position], this.keys[position], position+1);
        }

        @Override
        public int getItemCount()
        {
//            return users.size();
            return users.length;
        }
    }
}
