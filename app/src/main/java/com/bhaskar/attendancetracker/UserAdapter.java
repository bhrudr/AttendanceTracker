package com.bhaskar.attendancetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    List<UserHelperClass> userList;
    private UserAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(UserAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public UserAdapter(List<UserHelperClass> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card_design, parent, false);
        UserAdapter.UserViewHolder employeeViewHolder = new UserAdapter.UserViewHolder(view, mListener);
        return employeeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        UserHelperClass userHelperClass = userList.get(position);
        holder.name.setText(userHelperClass.getName());
        holder.username.setText(userHelperClass.getUsername());
        holder.role.setText(userHelperClass.getRole());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView name, username, role;

        public UserViewHolder(@NonNull View itemView, final UserAdapter.OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name);
            username = itemView.findViewById(R.id.user_username);
            role = itemView.findViewById(R.id.user_role);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public void filterUserList(List<UserHelperClass> filteredList) {
        userList = filteredList;
        notifyDataSetChanged();
    }
}
