package com.slt.devops.netfacilitysales.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.slt.devops.netfacilitysales.R;
import com.slt.devops.netfacilitysales.activity.networkfacility.FormActivity;
import com.slt.devops.netfacilitysales.api.ApiClient;
import com.slt.devops.netfacilitysales.models.BaseClass;
import com.slt.devops.netfacilitysales.models.LoginResponse;
import com.slt.devops.netfacilitysales.models.VersionResponse;
import com.slt.devops.netfacilitysales.storage.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseClass {

    ProgressBar splashProgress;
    int SPLASH_TIME = 2000; //This is 2 seconds

    private AlertDialog.Builder builder;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;


    private boolean pressedDontAskAgain,pressedLater;
    private long laterPressedTime;


    private final String[] permissions = {"Camera", "Media & Storage", "Location"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());

        String [] split = currentDateandTime.split(":");
        String aa;
        if( Integer.parseInt(split[0]) <12 &&  Integer.parseInt(split[0]) > 5){

            aa= "Good Morning!";
        }else if( Integer.parseInt(split[0]) <17 ){

            aa= "Good Afternoon!";
        }else{
            aa= "Good Evening!";
        }
        TextView txt1 = (TextView)findViewById(R.id.text1);
        txt1.setText(aa);
        splashProgress = findViewById(R.id.splashProgress);



        //Check Runtime Permissions
        // creating builder object for alert dialoges
        builder = new AlertDialog.Builder(MainActivity.this);

        // calling sharedpreferences and getting sharedpreferences values
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        pressedLater = sharedPref.getBoolean(getResources().getString(R.string.later), false);
        pressedDontAskAgain = sharedPref.getBoolean(getResources().getString(R.string.dont_ask_again), false);
        laterPressedTime = sharedPref.getLong(getResources().getString(R.string.later_pressed_time), 0);

        // Check if all the permissions were been granted
        // If not granted show a dialog requesting permissions from the user.
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {


            playProgress();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do any action here. Now we are moving to next page
                    if(new SharedPrefManager(MainActivity.this).getIsRegistered()) {
                        if(new SharedPrefManager(MainActivity.this).getIsLogged()) {
                            if(haveNetworkConnection()){
                            Call<VersionResponse> call = ApiClient
                                    .getInstance().getApi().checkVersion(SharedPrefManager.getInstance(MainActivity.this).getSid(),getResources().getString(R.string.app_version));

                            call.enqueue(new Callback<VersionResponse>() {
                                @Override
                                public void onResponse(Call<VersionResponse> call, Response<VersionResponse> response) {
                                    VersionResponse versionResponse = response.body();

                                    if (!versionResponse.isError()) {
                                        Intent intent = new Intent(MainActivity.this, FormActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        showOkMessageCh(versionResponse.getMessage(),versionResponse.getData(),getResources().getString(R.string.ok));


                                       /* Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();*/
                                    }
                                }

                                @Override
                                public void onFailure(Call<VersionResponse> call, Throwable t) {
                                    showOkMessage(t.getMessage(), getResources().getString(R.string.ok));
                                }
                            });
                            }else {
                                showOkMessage("Please Check the Internet Connection", getResources().getString(R.string.ok));
                            }



                        }else {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    }else {
                        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                        startActivity(intent);
                        finish();

                    }

                }
            }, SPLASH_TIME);


            // check if pressedLater variable is been true
        }if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            alertDialogeForAskingPermissions();

            // check if pressedLater variable is been true
        }else if (pressedLater) {
            if (laterPressedTime != 0) {

                // check if its been 1 hour since later is been pressed.
                Date dateObj = new Date();
                long timeNow = dateObj.getTime();
                long oneHourLater = laterPressedTime + (3600 * 1000);
                if (oneHourLater <= timeNow) {

                    requestPermission();
                    editor.putBoolean(getResources().getString(R.string.later), false);
                    editor.commit();
                }
            }
            // If pressed don't ask again the app should bot request permissions again.
        } else if (!pressedDontAskAgain) {
            requestPermission();
        }
        //====



    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void playProgress() {
        ObjectAnimator.ofInt(splashProgress, "progress", 100)
                .setDuration(2000)
                .start();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                //few important permissions were not been granted
                // ask the user again.
                alertDialoge();
            }

            //Code to start timer and take action after the timer ends
            playProgress();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do any action here. Now we are moving to next page
                    if(new SharedPrefManager(MainActivity.this).getIsRegistered()) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        //This 'finish()' is for exiting the app when back button pressed from Home page which is ActivityHome
                        finish();
                    }else {
                        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }
            }, SPLASH_TIME);
        }
    }

    private void alertDialoge() {

        //code to Set the message and title from the strings.xml file
        builder.setMessage(R.string.dialoge_desc).setTitle(R.string.we_request_again);

        builder.setCancelable(false)
                .setPositiveButton(R.string.give_permissions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission();
                    }
                })
                .setNegativeButton(R.string.dont_ask_again, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'Don't Ask Again' Button
                        // the sharedpreferences value is true
                        editor.putBoolean(getResources().getString(R.string.dont_ask_again), true);
                        editor.commit();
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void alertDialogeForAskingPermissions() {

        //code to Set the message and title from the strings.xml file
        builder.setMessage(getResources().getString(R.string.app_name) + " needs access to " + permissions[0] + ", "+ permissions[1] + ", " + permissions[2]).setTitle(R.string.permissions_required);

        //Setting message manually and performing action on button click
        //builder.setMessage("Do you want to close this application ?")
        builder.setCancelable(false)
                .setPositiveButton(R.string.later, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // Action for 'Later'
                        //Saving later boolean value as true, also saving time of pressed later
                        Date dateObj = new Date();
                        long timeNow = dateObj.getTime();
                        editor.putLong(getResources().getString(R.string.later_pressed_time), timeNow);
                        editor.putBoolean(getResources().getString(R.string.later), true);
                        editor.commit();
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.give_permissions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       requestPermission();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    //check permissions
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            }
        }
    }
}