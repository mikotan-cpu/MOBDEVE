package com.example.SukatApp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

    DBHelper DB;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    private float upDistance = 0f;
    private ArFragment arFragment;
    private ModelRenderable andyRenderable;
    private AnchorNode myanchornode;
    private DecimalFormat form_numbers = new DecimalFormat("#0.00 ");
    private boolean showTutorial = true;
    private Anchor anchor1 = null, anchor2 = null;

    private HitResult myhit;

    private TextView text;
    private SeekBar sk_height_control;
    private Button btn_save, btn_width, btn_height, viewList, refreshBtn;
    private ImageButton settingsBtn;
    private int tutorialCounter = 0;
    List<AnchorNode> anchorNodes = new ArrayList<>();

    private boolean measure_height = false, isMeters = true;
    private String dimension;
    private ArrayList<String> arl_saved = new ArrayList<String>();

    private float fl_measurement = 0.0f;
    private View decorView;

    private String message;



    Dialog myDialog;


    /**
     * This will open up the tutorials tab*/
    public void ShowPopup(View v) {

        TextView instructionsTv, skipTv;
        Button nextBtn;
        GifImageView visualGif;
        Context c = getApplicationContext();
        myDialog.setContentView(R.layout.tutorial_layout);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();

        visualGif = myDialog.findViewById(R.id.visualGif);
        nextBtn = myDialog.findViewById(R.id.nextBtn);
        instructionsTv = myDialog.findViewById(R.id.instructionsTv);
        visualGif.setBackgroundResource(getImageId(c, "pick"));
        skipTv = (TextView) myDialog.findViewById(R.id.skipTv);
        SpannableString content = new SpannableString("SKIP");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        skipTv.setText(content);

        tutorialCounter = 0;
        //when skipTv was click, the tutorial tab will be closed, regardless if its finished or not
        skipTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putBoolean("showTutorial", false);
                showTutorial = false;
                editor.apply();
                myDialog.dismiss();
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutorialCounter++;
                //the elements will be changed depending on the order
                switch(tutorialCounter)
                {
                    case 1:
                        visualGif.setBackgroundResource(getImageId(c, "scan"));
                        instructionsTv.setText("Find a surface with visible flat texture.");
                        break;
                    case 2:
                        visualGif.setBackgroundResource(getImageId(c, "dots"));
                        instructionsTv.setText("Scan the surface with device until dots appear on the screen.");
                        break;
                    case 3:
                        visualGif.setBackgroundResource(getImageId(c, "width"));
                        instructionsTv.setText("When measuring width, click the extreme sides of the object based on the dots.");
                        break;
                    case 4:
                        visualGif.setBackgroundResource(getImageId(c, "height"));
                        instructionsTv.setText("When measuring height, click the base of the object and use the slider to adjust height.");
                        break;
                    case 5:
                        visualGif.setBackgroundResource(getImageId(c, "save"));
                        instructionsTv.setText("Once the measuring is done, click the add button to save it.");
                        break;
                    case 6:
                        //if it's the last step of the tutorial, changes will be made on preferences and close the dialog
                        editor.putBoolean("showTutorial", false);
                        showTutorial = false;
                        editor.apply();
                        myDialog.dismiss();
                        break;
                }

            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        myDialog = new Dialog(this);
        DB = new DBHelper(this);
        ArrayList<Measurement> arrayMeasure = new ArrayList<>();
        initializeElements();

        sk_height_control.setEnabled(false);
        arFragment.setMenuVisibility(false);


        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if(i == 0)
                    decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        //if width was selected
        btn_width.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetLayout();
                measure_height = false;
                text.setText("Click the extremes you want to measure");
            }
        });
        //if height was selected
        btn_height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetLayout();
                sk_height_control.getProgressDrawable().setColorFilter(0xFFF5B001, PorterDuff.Mode.MULTIPLY);
                sk_height_control.getThumb().setColorFilter(0xFFF5B001, PorterDuff.Mode.MULTIPLY);
                measure_height = true;
                text.setText("Click the base of the object you want to measure");
            }
        });

/**
       Opens the dialog box that will be saving the data to the DB
       */    
            btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fl_measurement != 0.0f)
                    saveDialog();
                else
                    Toast.makeText(MainActivity.this, "Make a measurement before saving", Toast.LENGTH_SHORT).show();
            }
        });


  /**
     Will bring the user to the Settings activity
     */         
            settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Settings.class);
                startActivity(i);
            }
        });

/**
    Renders the 3D objects that will be used for measuring
     */
                ModelRenderable.builder()
                .setSource(this, R.raw.cubito3)
                .build()
                .thenAccept(renderable -> andyRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
    }

    //this allows the user to use the application with fullscreen
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


    //recheck the setting sthat needs to be set such as showing tutorials, and meters or inches as the unit for measurement
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        showTutorial = sp.getBoolean("showTutorial", true);
        //if it's saved in the preference to show tutorial, only thenw ill the pop up be opened
        if(showTutorial)
            ShowPopup(this.findViewById(android.R.id.content));

        isMeters = sp.getBoolean("isMeters", true);


        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (andyRenderable == null) {
                        return;
                    }
                    myhit = hitResult;

                    // Initializes anchor object(3D object)
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    if(!measure_height) {
                        if(anchor2 != null){
                            emptyAnchors();
                        }
                        if (anchor1 == null) {
                            anchor1 = anchor;
                        } else {
                            anchor2 = anchor;
                            fl_measurement = getMetersBetweenAnchors(anchor1, anchor2);
                            //checking the unit of measurement
                            if(isMeters)
                                text.setText("Width: " + form_numbers.format(fl_measurement) + "m");
                            else {
                                //the default unit of measurement of the API is meters, thus if the user decided to use inches, the measurement will be converted to inches
                                Log.d(TAG, "measurement: " + fl_measurement);
                                fl_measurement = (fl_measurement * 100f) / 2.54f;
                                text.setText("Width: " + form_numbers.format(fl_measurement) + "in.");
                            }

                        }
                    }
                    else{
                        emptyAnchors();
                        anchor1 = anchor;
                        text.setText("Move the slider till the cube reaches the upper base");
                        sk_height_control.setEnabled(true);
                    }

                    myanchornode = anchorNode;
                    anchorNodes.add(anchorNode);

                    // Create the transformable anchor(3D object).
                    TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
                    andy.setParent(anchorNode);
                    if(measure_height==false)
                        andy.setLocalScale(new Vector3(0.25f,0.25f,0.25f));
                    andy.setRenderable(andyRenderable);
                    andy.select();
                    andy.getScaleController().setEnabled(false);
                });


        sk_height_control.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                upDistance = progress;
//                fl_measurement = progress/200f;
                fl_measurement = progress/100f;
                if(isMeters)
                    text.setText("Height: "+form_numbers.format(fl_measurement) + " m");
                else {
                    Toast.makeText(MainActivity.this,String.valueOf(fl_measurement), Toast.LENGTH_SHORT).show();
                    fl_measurement = (fl_measurement * 100f) / 2.54f;
                    text.setText("Height: "+form_numbers.format(fl_measurement) + " in.");

                }


                myanchornode.setLocalScale(new Vector3(1f, progress/10f, 1f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /**
     * Initialize elements in the ui*/
    void initializeElements()
    {
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        text = (TextView) findViewById(R.id.text);


        sk_height_control = (SeekBar) findViewById(R.id.sk_height_control);

        btn_height = (Button) findViewById(R.id.btn_height);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_width = (Button) findViewById(R.id.btn_width);
        settingsBtn = (ImageButton) findViewById(R.id.settingsBtn);
//        refreshBtn = findViewById(R.id.refreshBtn);


    }


    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }


    /**
     * Function to raise an object perpendicular to the ArPlane a specific distance
     * @param an anchor belonging to the object that should be raised
     * @param up distance in centimeters the object should be raised vertically
     */
    private void ascend(AnchorNode an, float up) {
        Anchor anchor = myhit.getTrackable().createAnchor(
                myhit.getHitPose().compose(Pose.makeTranslation(0, up / 100f, 0)));

        an.setAnchor(anchor);
    }

    /**
     * Function to return the distance in meters between two objects placed in ArPlane
     * @param anchor1 first object's anchor
     * @param anchor2 second object's anchor
     * @return the distance between the two anchors in meters
     */
    private float getMetersBetweenAnchors(Anchor anchor1, Anchor anchor2) {
        float[] distance_vector = anchor1.getPose().inverse()
                .compose(anchor2.getPose()).getTranslation();
        float totalDistanceSquared = 0;
        for (int i = 0; i < 3; ++i)
            totalDistanceSquared += distance_vector[i] * distance_vector[i];
        return (float) Math.sqrt(totalDistanceSquared);
    }


    /**
     * Check whether the device supports the tools required to use the measurement tools
     * @param activity
     * @return boolean determining whether the device is supported or not
     */
    private boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }
    
 /**
     * Opens dialog box wherein user can input the anme of the measured object. User can also save the measurement into the DB through this dialog box.
     */
    private void saveDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_save, null);

        EditText et_measure = (EditText) mView.findViewById(R.id.et_measure);
        mBuilder.setTitle("Measurement title");

        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(et_measure.length() != 0){
                    
                    if(measure_height==true)
                    {
                        dimension = "Height";
                    }
                    else
                    {
                        dimension = "Width";
                    }
                    Boolean checkInsertData;
                    if(isMeters)
                        checkInsertData = DB.insertMeasurement(et_measure.getText().toString(),dimension, Float.parseFloat(form_numbers.format(fl_measurement)),"m");
                    else {

                        checkInsertData = DB.insertMeasurement(et_measure.getText().toString(),dimension, Float.parseFloat(form_numbers.format(fl_measurement)),"in");
                    }
                    if(checkInsertData==true)
                        Toast.makeText(MainActivity.this,"Saved", Toast.LENGTH_SHORT).show();

                    else
                        Toast.makeText(MainActivity.this,"Not Inserted", Toast.LENGTH_SHORT).show();

                    dialogInterface.dismiss();
                }
                else
                    Toast.makeText(MainActivity.this, "Title can't be empty", Toast.LENGTH_SHORT).show();
            }
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();

        dialog.show();
    }

    /**
     * Set layout to its initial state
     */
    private void resetLayout(){
        sk_height_control.setProgress(10);
        sk_height_control.setEnabled(false);
        measure_height = false;
        emptyAnchors();
    }

    /**
    *Removes anchors(3D Objects) from the screen.
    */
    private void emptyAnchors(){
        anchor1 = null;
        anchor2 = null;
        for (AnchorNode n : anchorNodes) {
            arFragment.getArSceneView().getScene().removeChild(n);
            n.getAnchor().detach();
            n.setParent(null);
            n = null;
        }
    }
}
