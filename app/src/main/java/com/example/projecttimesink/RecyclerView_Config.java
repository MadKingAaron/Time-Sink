package com.example.projecttimesink;

import android.content.Context;
import android.graphics.Typeface;
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
    private Typeface normalTypeface = Typeface.create("casual", Typeface.NORMAL);

    public void setConfig(RecyclerView recyclerView, Context context, User[] users, String[] keys, String userID)
    {
        this.context = context;
        this.userAdapter = new UserAdapter(users, keys, userID);
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

        public void bind(User user, String key, int placement, String userID)
        {
            String username = user.username;

            if(username.length() > 20)
                username = username.substring(0, 20) + "...";

            this.username_textView.setText(placement + ") " + username);
            this.time_textView.setText(TimeText.getTimeString(user.longestTimeWasted, 0));
            this.key = key;

            if(key.equals(userID))
                userText();
            else
                normalText();
        }

        private void userText()
        {
            this.username_textView.setTypeface(normalTypeface, Typeface.BOLD_ITALIC);
            this.time_textView.setTypeface(normalTypeface, Typeface.BOLD_ITALIC);
        }

        private void normalText()
        {
            this.username_textView.setTypeface(normalTypeface);
            this.time_textView.setTypeface(normalTypeface);
        }
    }

    class UserAdapter extends RecyclerView.Adapter<UserItemView>
    {
        private User[] users;
        private String[] keys;
        private String userID;

        public UserAdapter(User[] users, String[] keys, String userID)
        {
            this.users = users;
            this.keys = keys;
            this.userID = userID;
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
            holder.bind(this.users[position], this.keys[position], position+1, this.userID);
        }

        @Override
        public int getItemCount()
        {
            return users.length;
        }
    }
}
