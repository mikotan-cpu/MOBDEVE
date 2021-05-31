package com.example.SukatApp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private ArrayList<Measurement> data;
    Context context;

    public MyAdapter(Context ct, ArrayList<Measurement> arrayMeasure)
    {
        context = ct;
        data = arrayMeasure;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.template,parent,false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setDimensionTv(data.get(position).getDimension());
        holder.setMeasurementTv(""+data.get(position).getMeasurement());
        holder.setObjectNameTv(data.get(position).getObjectName());
        holder.setUnitTv(data.get(position).getUnit());


    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
