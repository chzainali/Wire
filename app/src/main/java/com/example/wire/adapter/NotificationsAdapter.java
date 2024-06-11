package com.example.wire.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wire.R;
import com.example.wire.model.MachineModel;
import com.example.wire.model.NotificationModel;
import com.example.wire.model.OnItemClick;

import java.util.List;
import java.util.Objects;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.Vh> {
    Context context;
    List<NotificationModel> list;
    OnItemClick onItemClick;

    public NotificationsAdapter(Context context, List<NotificationModel> list, OnItemClick click) {
        this.context = context;
        this.list = list;
        this.onItemClick = click;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new Vh(view);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull Vh holder, @SuppressLint("RecyclerView") int position) {
        NotificationModel model = list.get(position);
        holder.tvTitle.setText(model.getMessage());
        holder.tvDate.setText(model.getTime());
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Vh extends RecyclerView.ViewHolder {
        ImageView ivDelete;
        TextView tvTitle, tvDate;

        public Vh(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
