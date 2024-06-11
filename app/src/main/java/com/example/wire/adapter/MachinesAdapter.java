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
import com.example.wire.model.OnItemClick;

import java.util.List;
import java.util.Objects;

public class MachinesAdapter extends RecyclerView.Adapter<MachinesAdapter.Vh> {
    Context context;
    List<MachineModel> list;
    OnItemClick onItemClick;

    public MachinesAdapter(Context context, List<MachineModel> list, OnItemClick click) {
        this.context = context;
        this.list = list;
        this.onItemClick = click;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_machines, parent, false);
        return new Vh(view);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull Vh holder, @SuppressLint("RecyclerView") int position) {
        MachineModel model = list.get(position);
        if (Objects.equals(model.getStatus(), "Available")){
            holder.tvStatus.setTextColor(context.getColor(R.color.green));
        }else{
            holder.tvStatus.setTextColor(context.getColor(R.color.red));
        }
        holder.tvMachineName.setText(model.getMachineName());
        holder.tvBrandName.setText("Brand: "+model.getBrandName());
        holder.tvColor.setText("Color: "+model.getColor());
        holder.tvPrice.setText(model.getPrice()+"£");
        holder.tvStatus.setText(model.getStatus());
        Glide.with(holder.ivMachine).load(model.getImage()).into(holder.ivMachine);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
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
        ImageView ivMachine;
        TextView tvMachineName, tvBrandName, tvColor, tvPrice, tvStatus;

        public Vh(@NonNull View itemView) {
            super(itemView);
            ivMachine = itemView.findViewById(R.id.ivMachine);
            tvMachineName = itemView.findViewById(R.id.tvMachineName);
            tvBrandName = itemView.findViewById(R.id.tvBrandName);
            tvColor = itemView.findViewById(R.id.tvColor);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
