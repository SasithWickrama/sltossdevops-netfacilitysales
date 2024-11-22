package com.slt.devops.netfacilitysales.activity.networkfacility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.LocationServices;
import com.slt.devops.netfacilitysales.R;
import com.slt.devops.netfacilitysales.activity.LoginActivity;
import com.slt.devops.netfacilitysales.activity.SettingsActivity;
import com.slt.devops.netfacilitysales.api.ApiClient;
import com.slt.devops.netfacilitysales.models.BaseClass;
import com.slt.devops.netfacilitysales.models.UpdateResponse;
import com.slt.devops.netfacilitysales.models.networkfacility.LandLine;
import com.slt.devops.netfacilitysales.models.networkfacility.LandlineResponce;
import com.slt.devops.netfacilitysales.storage.SharedPrefManager;
import com.tiper.MaterialSpinner;
import com.williamww.silkysignature.views.SignaturePad;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormActivity extends BaseClass implements View.OnClickListener {
    String value, sid, dp, index, svType, rtom, lea, narea, nstart, nend, location, distance, cuslon, cuslat, imgRetVal,
            imgNIC1 = "", imgNIC2 = "", imgSIG = "";
    MaterialSpinner serviceSelect, rtomSelect, numberSelect, hnoSelect, cushon, reservedno, bbpkgSelect, iptvpkgSelect;
    EditText noArea, noStart, noEnd, cusname, cusnic, cusemail, cuscontact, address , cus_sig;

    public List<LandLine> nolist;
    private ImageView imageButton;
    private PopupMenu dropDownMenu;
    public Menu menu;
    SignaturePad mSignaturePad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.netf_activity_form);

        // Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        imageButton = findViewById(R.id.textMenu);
        dropDownMenu = new PopupMenu(FormActivity.this, imageButton);
        menu = dropDownMenu.getMenu();
        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        dropDownMenu.getMenuInflater().inflate(R.menu.setting_menu, menu);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropDownMenu.show();
            }
        });

        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()) {
                    case R.id.action_settings:
                        Intent intents = new Intent(FormActivity.this, SettingsActivity.class);
                        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intents);
                        break;
                    case R.id.action_logout:
                        new SharedPrefManager(FormActivity.this).saveIsLogged(false);
                        Intent intent = new Intent(FormActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                }

                return false;
            }
        });

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {

            }

            @Override
            public void onClear() {

            }
        });

        SharedPrefManager PrefManager = new SharedPrefManager(this);
        value = PrefManager.getRtom();
        sid = PrefManager.getSid();
        String[] rtomval = value.split("/");


        ArrayAdapter<String> serviceAdapter =
                new ArrayAdapter<>(
                        getApplicationContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.service_type));

        ArrayAdapter<String> rtomAdapter =
                new ArrayAdapter<>(
                        getApplicationContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        rtomval);

        ArrayAdapter<String> hnoAdapter =
                new ArrayAdapter<>(
                        getApplicationContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.honorifics));

        ArrayAdapter<String> megalinebb =
                new ArrayAdapter<>(
                        getApplicationContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.megalinebb));

        ArrayAdapter<String> megalineiptv =
                new ArrayAdapter<>(
                        getApplicationContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.megalineiptv));


        ArrayAdapter<String> ftthbb =
                new ArrayAdapter<>(
                        getApplicationContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.ftthbb));

        ArrayAdapter<String> ftthiptv =
                new ArrayAdapter<>(
                        getApplicationContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.ftthiptv));

        pgsBar = (ProgressBar) findViewById(R.id.loading_indicator);
        serviceSelect = findViewById(R.id.service);
        serviceSelect.setAdapter(serviceAdapter);
        rtomSelect = findViewById(R.id.rtom);
        rtomSelect.setAdapter(rtomAdapter);
        hnoSelect = findViewById(R.id.hnorif);
        hnoSelect.setAdapter(hnoAdapter);

        bbpkgSelect = findViewById(R.id.bbpkg);
        iptvpkgSelect = findViewById(R.id.iptvpkg);


        numberSelect = findViewById(R.id.number);


        noArea = findViewById(R.id.areacode);
        noStart = findViewById(R.id.starno);
        cusname = findViewById(R.id.CusName);
        cusnic = findViewById(R.id.nic);
        cushon = findViewById(R.id.hnorif);
        cusemail = findViewById(R.id.email);
        cuscontact = findViewById(R.id.contact_no);
        reservedno = findViewById(R.id.number);
        address = findViewById(R.id.cus_address);
        cus_sig = findViewById(R.id.cus_sig);

        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);

        SharedPreferences sharedPref = getSharedPreferences("myKey", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        cus_sig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        serviceSelect.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner materialSpinner, View view, int i, long l) {
                svType = ((serviceSelect.getSelectedItem() == null) ? null : serviceSelect.getSelectedItem().toString());
                if (svType.equalsIgnoreCase("Megaline Double Play-IPTV")) {
                    iptvpkgSelect.setAdapter(megalineiptv);
                    iptvpkgSelect.setVisibility(View.VISIBLE);
                    bbpkgSelect.setVisibility(View.GONE);
                }
                if (svType.equalsIgnoreCase("FTTH Double Play-IPTV")) {
                    iptvpkgSelect.setAdapter(ftthiptv);
                    iptvpkgSelect.setVisibility(View.VISIBLE);
                    bbpkgSelect.setVisibility(View.GONE);
                }
                if (svType.equalsIgnoreCase("Megaline Triple Play")) {
                    iptvpkgSelect.setAdapter(megalineiptv);
                    iptvpkgSelect.setVisibility(View.VISIBLE);
                    bbpkgSelect.setAdapter(megalinebb);
                    bbpkgSelect.setVisibility(View.VISIBLE);
                }
                if (svType.equalsIgnoreCase("FTTH Triple Play")) {
                    iptvpkgSelect.setAdapter(ftthiptv);
                    iptvpkgSelect.setVisibility(View.VISIBLE);
                    bbpkgSelect.setAdapter(ftthbb);
                    bbpkgSelect.setVisibility(View.VISIBLE);
                }
                if (svType.equalsIgnoreCase("Megaline Double Play-BB")) {
                    bbpkgSelect.setAdapter(megalinebb);
                    bbpkgSelect.setVisibility(View.VISIBLE);
                    iptvpkgSelect.setVisibility(View.GONE);
                }
                if (svType.equalsIgnoreCase("FTTH Double Play-BB")) {
                    bbpkgSelect.setAdapter(ftthbb);
                    bbpkgSelect.setVisibility(View.VISIBLE);
                    iptvpkgSelect.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(MaterialSpinner materialSpinner) {

            }
        });

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putString("imgNo", "1");
                editor.apply();
                dispatchTakePictureIntent();
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putString("imgNo", "2");
                editor.apply();
                dispatchTakePictureIntent();
            }
        });

       /* imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putString("imgNo", "3");
                editor.apply();
                dispatchTakePictureIntent();
            }
        });*/

    }


    public void goto_Map_Click(View view) {
        svType = ((serviceSelect.getSelectedItem() == null) ? null : serviceSelect.getSelectedItem().toString());
        rtom = ((rtomSelect.getSelectedItem() == null) ? null : rtomSelect.getSelectedItem().toString());

        if (!svType.equalsIgnoreCase("") && !rtom.equalsIgnoreCase("")) {

            if (isLocationEnabled(this.getApplicationContext())) {
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

                Intent intent = new Intent(FormActivity.this, NFMapActivity.class);
                Bundle extras = new Bundle();
                extras.putString("sid", sid);
                extras.putString("rtom", rtom);

                if (svType.contains("FTTH")) {
                    extras.putString("dptype", "FDP");
                } else {
                    extras.putString("dptype", "DP");
                }
                intent.putExtras(extras);
                startActivityForResult(intent, 100);

            } else {
                showOkMessage(getResources().getString(R.string.err_nolocation), getResources().getString(R.string.ok));
            }

        } else {
            showOkMessage(getResources().getString(R.string.err_nortom), getResources().getString(R.string.ok));
        }
    }

    public void get_SLT_No_List(View v) {
        pgsBar.setVisibility(View.VISIBLE);
        findViewById(R.id.nosearch).setVisibility(View.INVISIBLE);
        nstart = noStart.getText().toString();
        svType = ((serviceSelect.getSelectedItem() == null) ? null : serviceSelect.getSelectedItem().toString());
        rtom = ((rtomSelect.getSelectedItem() == null) ? null : rtomSelect.getSelectedItem().toString());
        if (!rtom.equalsIgnoreCase("")) {

            if (nstart.length() != 10) {
               // showOkMessage(getResources().getString(R.string.err_invalid_starting_no), getResources().getString(R.string.ok));
               // pgsBar.setVisibility(View.GONE);
                //return;
                AlertDialog alertDialog = new AlertDialog.Builder(FormActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setMessage(getResources().getString(R.string.err_invalid_starting_no));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        nstart = "";
                        List<String> nolistarray = new ArrayList<String>();
                        Call<LandlineResponce> call = ApiClient
                                .getInstance().getApi().getNOlist(sid, rtom, svType, "STANDARD", "", nstart, "", lea);

                        call.enqueue(new Callback<LandlineResponce>() {

                            @Override
                            public void onResponse(Call<LandlineResponce> call, Response<LandlineResponce> response) {
                                LandlineResponce landline = response.body();

                                if (!landline.isError()) {
                                    nolist = landline.getNoList();
                                    pgsBar.setVisibility(View.GONE);
                                    findViewById(R.id.nosearch).setVisibility(View.VISIBLE);

                                    for (int i = 0; i < nolist.size(); i++) {
                                        nolistarray.add(nolist.get(i).getNUMB());
                                    }

                                } else {
                                    pgsBar.setVisibility(View.GONE);
                                    findViewById(R.id.nosearch).setVisibility(View.VISIBLE);
                                    showOkMessage(landline.getMessage(), getResources().getString(R.string.ok));
                                }
                            }

                            @Override
                            public void onFailure(Call<LandlineResponce> call, Throwable t) {
                                pgsBar.setVisibility(View.GONE);
                                showOkMessage(t.getMessage(), getResources().getString(R.string.ok));
                                Intent intent = new Intent(FormActivity.this, FormActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });

                        ArrayAdapter<String> nolistadapter = new ArrayAdapter<String>(FormActivity.this, android.R.layout.simple_spinner_item, nolistarray);
                        nolistadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        numberSelect.setAdapter(nolistadapter);
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pgsBar.setVisibility(View.GONE);
                        findViewById(R.id.nosearch).setVisibility(View.VISIBLE);
                        return;
                    }
                });
                alertDialog.show();

            }else {


                List<String> nolistarray = new ArrayList<String>();
                Call<LandlineResponce> call = ApiClient
                        .getInstance().getApi().getNOlist(sid, rtom, svType, "STANDARD", "", nstart, "", lea);

                call.enqueue(new Callback<LandlineResponce>() {

                    @Override
                    public void onResponse(Call<LandlineResponce> call, Response<LandlineResponce> response) {
                        LandlineResponce landline = response.body();

                        if (!landline.isError()) {
                            nolist = landline.getNoList();
                            pgsBar.setVisibility(View.GONE);
                            findViewById(R.id.nosearch).setVisibility(View.VISIBLE);

                            for (int i = 0; i < nolist.size(); i++) {
                                nolistarray.add(nolist.get(i).getNUMB());
                            }

                        } else {
                            pgsBar.setVisibility(View.GONE);
                            findViewById(R.id.nosearch).setVisibility(View.VISIBLE);
                            showOkMessage(landline.getMessage(), getResources().getString(R.string.ok));
                        }
                    }

                    @Override
                    public void onFailure(Call<LandlineResponce> call, Throwable t) {
                        showOkMessage(t.getMessage(), getResources().getString(R.string.ok));
                    }
                });

                ArrayAdapter<String> nolistadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nolistarray);
                nolistadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                numberSelect.setAdapter(nolistadapter);
            }
        } else {
            showOkMessage(getResources().getString(R.string.err_nortom), getResources().getString(R.string.ok));
            pgsBar.setVisibility(View.GONE);
            findViewById(R.id.nosearch).setVisibility(View.VISIBLE);
        }
    }


    public void upload_data(View v) {
        rtom = ((rtomSelect.getSelectedItem() == null) ? null : rtomSelect.getSelectedItem().toString());
        svType = ((serviceSelect.getSelectedItem() == null) ? null : serviceSelect.getSelectedItem().toString());
        String scusname = cusname.getText().toString();
        String scuscon = cuscontact.getText().toString();
        String scusemail = cusemail.getText().toString();
        String scusnic = cusnic.getText().toString();
        String scushon = ((cushon.getSelectedItem() == null) ? null : cushon.getSelectedItem().toString());
        String resnumber = ((reservedno.getSelectedItem() == null) ? null : reservedno.getSelectedItem().toString());
        String iptvpkg = ((iptvpkgSelect.getSelectedItem() == null) ? null : iptvpkgSelect.getSelectedItem().toString());
        String bbpkg = ((bbpkgSelect.getSelectedItem() == null) ? null : bbpkgSelect.getSelectedItem().toString());
        String add = address.getText().toString();

        if (currentPhotoPath1 != null || currentPhotoPath2 != null) {
            if (!mSignaturePad.isEmpty()) {
                if (!rtom.equalsIgnoreCase("")) {

                    if (!scusname.equalsIgnoreCase("") && !scuscon.equalsIgnoreCase("") && !scusnic.equalsIgnoreCase("") && !add.equalsIgnoreCase("")) {

                        if (!resnumber.equalsIgnoreCase("")) {
                            pgsBar.setVisibility(View.VISIBLE);

                            Call<UpdateResponse> call = ApiClient
                                    .getInstance().getApi().uploadNetFacility(sid, rtom, svType, location, dp, index, distance, resnumber, scushon, scusname, scusemail, scuscon, scusnic, add, cuslon, cuslat, bbpkg, iptvpkg);

                            call.enqueue(new Callback<UpdateResponse>() {
                                @Override
                                public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                                    UpdateResponse resp = response.body();

                                    if (!resp.isError()) {
                                        //showOkMessage(getResources().getString(R.string.upload_success), getResources().getString(R.string.ok));


                                        if ((currentPhotoPath1 != null && !currentPhotoPath1.trim().isEmpty()) || (currentPhotoPath2 != null && !currentPhotoPath2.trim().isEmpty()) ) {

                                            location = resp.getMessage();
                                            if ((currentPhotoPath1 != null && !currentPhotoPath1.trim().isEmpty())) {

                                                imageFileName1 = resp.getMessage() + "_NIC1";

                                                imgNIC1 = uploadImage(currentPhotoPath1, imageFileName1, location, CAMERA_REQUEST_CODE);
                                            }
                                            if ((currentPhotoPath2 != null && !currentPhotoPath2.trim().isEmpty())) {
                                                imageFileName2 = resp.getMessage() + "_NIC2";
                                                imgNIC2 = uploadImage(currentPhotoPath2, imageFileName2, location, CAMERA_REQUEST_CODE);
                                            }
                                            if (!mSignaturePad.isEmpty()) {
                                                imgSIG = uploadSig(mSignaturePad.getSignatureBitmap(), "Signature", location);
                                            }
                                        }
                                        pgsBar.setVisibility(View.GONE);

                                        //showOkMessage("Data Updated Success", getResources().getString(R.string.ok));
                                        Toast.makeText(getApplicationContext(), "Data Updated Successfully...", Toast.LENGTH_SHORT).show();
                                        //  setResult(Activity.RESULT_OK);
                                        // finish();
                                        //  return;
                                        Intent i = new Intent(FormActivity.this, FormActivity.class);
                                        finish();
                                        overridePendingTransition(0, 0);
                                        startActivity(i);
                                        overridePendingTransition(0, 0);

                                    } else {
                                        showOkMessage(resp.getMessage(), getResources().getString(R.string.ok));
                                    }
                                }

                                @Override
                                public void onFailure(Call<UpdateResponse> call, Throwable t) {

                                }
                            });
                        } else {
                            showOkMessage(getResources().getString(R.string.err_invalid_res_no), getResources().getString(R.string.ok));
                        }
                    } else {
                        showOkMessage(getResources().getString(R.string.err_no_contact_name), getResources().getString(R.string.ok));
                    }
                } else {
                    showOkMessage(getResources().getString(R.string.err_nortom), getResources().getString(R.string.ok));
                }
            } else {
                showOkMessage(getResources().getString(R.string.err_nosig), getResources().getString(R.string.ok));
            }
        } else {
            showOkMessage(getResources().getString(R.string.err_noimages), getResources().getString(R.string.ok));
        }
    }


    public void upload_data2(View v) {
        rtom = ((rtomSelect.getSelectedItem() == null) ? null : rtomSelect.getSelectedItem().toString());
        svType = ((serviceSelect.getSelectedItem() == null) ? null : serviceSelect.getSelectedItem().toString());
        String scusname = cusname.getText().toString();
        String scuscon = cuscontact.getText().toString();
        String scusemail = cusemail.getText().toString();
        String scusnic = cusnic.getText().toString();
        String scushon = ((cushon.getSelectedItem() == null) ? null : cushon.getSelectedItem().toString());
        String resnumber = ((reservedno.getSelectedItem() == null) ? null : reservedno.getSelectedItem().toString());
        String add = address.getText().toString();

        if (!rtom.equalsIgnoreCase("")) {

            if (!scusname.equalsIgnoreCase("") && !scuscon.equalsIgnoreCase("")) {

                if (!narea.equalsIgnoreCase("") && !nstart.equalsIgnoreCase("")) {



                  /*  File file = new File(currentPhotoPath1);
                    RequestBody requestFile =  RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    MultipartBody.Part body =   MultipartBody.Part.createFormData("image", file.getName(), requestFile);*/

                    MultipartBody.Part[] imagearray = new MultipartBody.Part[3];


                    //Log.d("Upload request", "requestUploadSurvey: survey image " + index + "  " + imageModelArrayList.get(index).path);
                    if (!currentPhotoPath1.equals("")) {
                        File file = new File(currentPhotoPath1);
                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        imagearray[0] = MultipartBody.Part.createFormData("nic1", file.getName(), requestFile);
                    }
                    if (!currentPhotoPath2.equals("")) {
                        File file = new File(currentPhotoPath2);
                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        imagearray[1] = MultipartBody.Part.createFormData("nic2", file.getName(), requestFile);
                    }
                    if (!currentPhotoPath3.equals("")) {
                        File file = new File(currentPhotoPath3);
                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        imagearray[2] = MultipartBody.Part.createFormData("sig", file.getName(), requestFile);
                    }


                    Call<UpdateResponse> call = ApiClient
                            .getInstance().getApi().uploadNetFacilitywithImage(sid, rtom, svType, location, dp, index, distance, resnumber, scushon, scusname, scusemail, scuscon, scusnic, add, cuslon, cuslat, imagearray);

                    call.enqueue(new Callback<UpdateResponse>() {
                        @Override
                        public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                            UpdateResponse resp = response.body();

                            if (!resp.isError()) {
                                showOkMessage(getResources().getString(R.string.upload_success), getResources().getString(R.string.ok));

                            } else {
                                showOkMessage(resp.getMessage(), getResources().getString(R.string.ok));
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdateResponse> call, Throwable t) {

                        }
                    });
                } else {
                    showOkMessage(getResources().getString(R.string.err_no_number_select), getResources().getString(R.string.ok));
                }
            } else {
                showOkMessage(getResources().getString(R.string.err_no_contact_name), getResources().getString(R.string.ok));
            }
        } else {
            showOkMessage(getResources().getString(R.string.err_nortom), getResources().getString(R.string.ok));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            dp = data.getStringExtra("dp");
            index = data.getStringExtra("index");
            location = data.getStringExtra("location");
            distance = data.getStringExtra("distance");
            cuslon = data.getStringExtra("cuslon");
            cuslat = data.getStringExtra("cuslat");
            lea = data.getStringExtra("lea");

            EditText dplable = findViewById(R.id.dp);
            dplable.setText(dp);
            EditText dpindex = findViewById(R.id.dpindex);
            dpindex.setText(index);

        }

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
            String num1 = sharedPreferences.getString("imgNo", "");

            File f = new File(currentPhotoPath);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            if (num1.equals("1")) {

                imageView1.setImageURI(Uri.fromFile(f));
                imgCount = 1;
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                contentUri1 = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri1);
                this.sendBroadcast(mediaScanIntent);
                currentPhotoPath1 = currentPhotoPath;
                // imageFileName1 = "JPEG_" + timeStamp + "." + getFileExt(contentUri1);
                Log.d("tag", "ABsolute Url of Image is " + currentPhotoPath1);
            }
            if (num1.equals("2")) {

                imageView2.setImageURI(Uri.fromFile(f));
                imgCount = 2;
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                contentUri2 = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri2);
                this.sendBroadcast(mediaScanIntent);
                currentPhotoPath2 = currentPhotoPath;
                // imageFileName2 = "JPEG_" + timeStamp + "." + getFileExt(contentUri2);
                Log.d("tag", "ABsolute Url of Image is " + currentPhotoPath2);
            }
            if (num1.equals("3")) {

                imageView3.setImageURI(Uri.fromFile(f));
                imgCount = 3;
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                contentUri3 = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri3);
                this.sendBroadcast(mediaScanIntent);
                currentPhotoPath3 = currentPhotoPath;
                //imageFileName3 = "JPEG_" + timeStamp + "." + getFileExt(contentUri3);
                Log.d("tag", "ABsolute Url of Image is " + currentPhotoPath3);
            }

            // uploadFile(contentUri, imageFileName, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onClick(View v) {

    }


}
