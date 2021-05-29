package com.example.armeasure;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class Settings extends AppCompatActivity {
    private static final String TAG = "SETTINGS";
    private Button backBtn, inchesBtn, metersBtn;
    private Switch tutorialSwitch;
    private TextView viewListTv;
    private ConstraintLayout inchesCl, metersCl;
    private boolean showTutorial = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backBtn = findViewById(R.id.backBtn2);
        inchesBtn = findViewById(R.id.inchesBtn);
        metersBtn = findViewById(R.id.metersBtn);
        tutorialSwitch = findViewById(R.id.tutorialSwitch);
        viewListTv = findViewById(R.id.viewListTv);
        inchesCl = findViewById(R.id.inchesCl);
        metersCl = findViewById(R.id.metersCl);

        inchesBtn.setVisibility(View.GONE);

        viewListTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Settings.this, MeasurementList.class);
                startActivity(i);

            }
        });

        metersCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inchesBtn.setVisibility(View.GONE);
                metersBtn.setVisibility(View.VISIBLE);
            }
        });
        inchesCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inchesBtn.setVisibility(View.VISIBLE);
                metersBtn.setVisibility(View.GONE);
            }
        });
        tutorialSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tutorialSwitch.isChecked())
                {
                    showTutorial = true;
                    Log.d(TAG, "show tutorial true");
                }
                else{
                    showTutorial = false;
                    Log.d(TAG, "show tutorial false");
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Settings.this, MainActivity.class);

                startActivity(i);
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean("showTutorial", showTutorial);
        editor.apply();
        showTutorial = false;
    }
}