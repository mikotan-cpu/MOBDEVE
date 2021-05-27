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

import java.util.ArrayList;

public class MeasurementList extends AppCompatActivity {

    private RecyclerView rv;
    private Button backBtn, showTutBtn;
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
        showTutBtn = findViewById(R.id.showTutBtn);

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

        //hiding navigation
        //TO DO fix this
        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MeasurementList.this, MainActivity.class);
                startActivity(i);
//                overridePendingTransition(R.anim.slidein_left, R.anim.slideout_right);
            }
        });

        showTutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putBoolean("showTutorial", true);
                editor.apply();
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
    }
}