package com.example.SukatApp;

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
    private View decorView;
    private boolean showTutorial = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();

        //        if (!checkIsSupportedDeviceOrFinish(this)) {
//            return;
//        }
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        backBtn = findViewById(R.id.backBtn2);
        inchesBtn = findViewById(R.id.inchesBtn);
        metersBtn = findViewById(R.id.metersBtn);
        tutorialSwitch = findViewById(R.id.tutorialSwitch);
        viewListTv = findViewById(R.id.viewListTv);
        inchesCl = findViewById(R.id.inchesCl);
        metersCl = findViewById(R.id.metersCl);

        //check if the current settings is to show meters or to show inches
        if(sp.getBoolean("isMeters", true))
            inchesBtn.setVisibility(View.GONE);
        else
            metersBtn.setVisibility(View.GONE);

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
                editor.putBoolean("isMeters", true);
                editor.apply();

            }
        });
        inchesCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inchesBtn.setVisibility(View.VISIBLE);
                metersBtn.setVisibility(View.GONE);
                editor.putBoolean("isMeters", false);
                editor.apply();
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