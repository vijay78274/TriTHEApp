package com.example.tritheapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tritheapp.R;
import com.example.tritheapp.databinding.EmergencyItemBinding;
import com.example.tritheapp.models.contacts;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    Context context;
    ArrayList<String> list;
    public RecyclerAdapter(Context context, ArrayList<String> contacts){
        this.context = context;
        this.list= contacts;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.emergency_item,parent,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        String pos = list.get(position);
        holder.binding.textView.setText(pos);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        EmergencyItemBinding binding;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = EmergencyItemBinding.bind(itemView);
        }
    }
}

