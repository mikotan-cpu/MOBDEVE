package com.example.SukatApp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    private TextView ObjectName;
    private TextView measurement;
    private TextView unit;
    private TextView dimension;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        this.ObjectName = itemView.findViewById(R.id.ObjectName);
        this.measurement = itemView.findViewById(R.id.measurement);
        this.unit = itemView.findViewById(R.id.unit);
        this.dimension = itemView.findViewById(R.id.dimension);


    }

    public void setUnitTv(String tv) {
        this.unit.setText(tv);
    }

    public void setMeasurementTv(String tv) {
        this.measurement.setText(tv);
    }
    public void setObjectNameTv(String tv){
        this.ObjectName.setText(tv);
    }

    public void setDimensionTv(String tv) {
        this.dimension.setText(tv);
    }
}
