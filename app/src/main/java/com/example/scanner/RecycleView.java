package com.example.scanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecycleView extends RecyclerView.Adapter<RecycleView.holder> {


    private String[] data;
    public void swapAdapter(String[] s){
        if (data == s) return;
        data = s;
        this.notifyDataSetChanged();
    }

    public class holder extends RecyclerView.ViewHolder{

        TextView t;
        public holder(@NonNull View itemView) {
            super(itemView);
            t = itemView.findViewById(R.id.t);
        }
    }

    @NonNull
    @Override
    public RecycleView.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleView.holder holder, int position) {
            holder.t.setText(data[position]);
    }

    @Override
    public int getItemCount() {
        if (data != null){
            return data.length;
        }
        return 0;
    }
}
