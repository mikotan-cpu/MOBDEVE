package com.example.armeasure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MeasurementList extends AppCompatActivity {

    private RecyclerView rv;
    private Button backBtn, helpBtn;
    private View decorView;
    DBHelper DB;
    ArrayList<Measurement> arrayMeasure = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DB = new DBHelper(this);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_list2);
        backBtn = findViewById(R.id.backBtn);
        helpBtn = findViewById(R.id.helpBtn);

        Cursor res = DB.getData();
        while (res.moveToNext()){
            Measurement item = new Measurement();

            item.setObjectName(res.getString(0));
            item.setDimension(res.getString(1));
            item.setMeasurement(Float.parseFloat(res.getString(2)));
            item.setUnit(res.getString(3));
            arrayMeasure.add(item);

        }
        System.out.println("Test" + arrayMeasure.size());
        this.rv = findViewById(R.id.rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MeasurementList.this);
        this.rv.setLayoutManager(linearLayoutManager);
        MyAdapter myAdapter = new MyAdapter(this, arrayMeasure);
        this.rv.setAdapter(myAdapter);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MeasurementList.this, Settings.class);
                startActivity(i);

            }
        });
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast toast=Toast.makeText(getApplicationContext(),"Swipe left or right to delete a measurement!",Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        ItemTouchHelper.SimpleCallback itemTouchHelperCallbback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                DB.deleteMeasurement(arrayMeasure.get(viewHolder.getAdapterPosition()).getObjectName());
                System.out.println("Test" +viewHolder.getAdapterPosition());
                arrayMeasure.remove(viewHolder.getAdapterPosition());
                myAdapter.notifyDataSetChanged();
            }
        };
        new ItemTouchHelper(itemTouchHelperCallbback).attachToRecyclerView(rv);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if(i == 0)
                    decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
            decorView.setSystemUiVisibility(hideSystemBars());
    }

    private int hideSystemBars() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                 View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
}