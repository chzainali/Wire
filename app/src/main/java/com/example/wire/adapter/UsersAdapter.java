package com.example.wire.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wire.R;
import com.example.wire.databinding.ItemUsersBinding;
import com.example.wire.model.OnItemClick;
import com.example.wire.model.UserModel;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.Vh> {
    List<UserModel> list;
    Context context;
    OnItemClick onClick;

    public UsersAdapter(List<UserModel> list, Context context, OnItemClick onClick) {
        this.list = list;
        this.context = context;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_users, parent, false);
        return new Vh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, @SuppressLint("RecyclerView") int position) {
        UserModel model = list.get(position);
        holder.binding.userUsername.setText(model.getName());
        if (!model.getImage().isEmpty()) {
            Glide.with(context).load(model.getImage()).into(holder.binding.userImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Vh extends RecyclerView.ViewHolder {
        ItemUsersBinding binding;

        public Vh(@NonNull View itemView) {
            super(itemView);
            binding = ItemUsersBinding.bind(itemView);
        }
    }
}
