package com.example.armeasure;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;

import java.util.ArrayList;

public class MeasurementList extends AppCompatActivity {

    private RecyclerView rv;

    DBHelper DB;
    ArrayList<Measurement> arrayMeasure = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DB = new DBHelper(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_list2);

        Cursor res = DB.getData();
        while (res.moveToNext()){
            Measurement item = new Measurement();

            item.setObjectName(res.getString(0));
            item.setDimension(res.getString(1));
            item.setMeasurement(Float.parseFloat(res.getString(2)));
            item.setUnit(res.getString(3));
            arrayMeasure.add(item);

        }
        System.out.println("BOBO" + arrayMeasure.size());
        this.rv = findViewById(R.id.rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MeasurementList.this);
        this.rv.setLayoutManager(linearLayoutManager);
        MyAdapter myAdapter = new MyAdapter(this, arrayMeasure);
        this.rv.setAdapter(myAdapter);
    }
}