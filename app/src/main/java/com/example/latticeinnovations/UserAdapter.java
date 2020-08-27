package com.example.latticeinnovations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.latticeinnovations.RoomDB.UserEntity;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Context mContext;
    List<UserEntity> userEntityList;

    public UserAdapter(Context mContext, List<UserEntity> userEntityList) {
        this.mContext = mContext;
        this.userEntityList = userEntityList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserEntity currentUser = userEntityList.get(position);

        holder.username.setText(currentUser.name);
        holder.userEmail.setText(currentUser.email);
        holder.userAddress.setText(currentUser.address);
        holder.userPhone.setText(currentUser.phoneNo);
    }

    @Override
    public int getItemCount() {
        return userEntityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView username, userAddress, userEmail, userPhone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.listUsername);
            userEmail = itemView.findViewById(R.id.listEmail);
            userAddress = itemView.findViewById(R.id.listAddress);
            userPhone = itemView.findViewById(R.id.listPhone);

        }
    }
}
