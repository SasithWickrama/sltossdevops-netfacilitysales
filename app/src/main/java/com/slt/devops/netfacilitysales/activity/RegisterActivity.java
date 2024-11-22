package com.slt.devops.netfacilitysales.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.slt.devops.netfacilitysales.R;
import com.slt.devops.netfacilitysales.activity.networkfacility.FormActivity;
import com.slt.devops.netfacilitysales.api.ApiClient;
import com.slt.devops.netfacilitysales.models.BaseClass;
import com.slt.devops.netfacilitysales.models.LoginResponse;
import com.slt.devops.netfacilitysales.storage.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseClass {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        textViewVersion = findViewById(R.id.versionId);
        appVersion = getResources().getString(R.string.app_version);
        // textViewVersion.setText("App by OSS-DevOps. SV"+ appVersion);

        String app1 = "App by ";
        String app2 = "<font color='#EE0000'>DevOps</font>";
        String app3 = ". DV";
        textViewVersion.setText(Html.fromHtml(app1 + app2+app3+getResources().getString(R.string.app_version)));
    }


    public void addCode(View view){
        findViewById(R.id.buttonPass).setVisibility(View.GONE);
        findViewById(R.id.buttonReg).setVisibility(View.GONE);
        findViewById(R.id.buttonVry).setVisibility(View.VISIBLE);
        findViewById(R.id.inputLayoutCode).setVisibility(View.VISIBLE);

    }

    public void getMobNo(View view){
        findViewById(R.id.buttonPass).setVisibility(View.GONE);
        findViewById(R.id.buttonReg).setVisibility(View.GONE);
        findViewById(R.id.buttonVry).setVisibility(View.GONE);
        findViewById(R.id.buttonGet).setVisibility(View.VISIBLE);
        findViewById(R.id.inputLayoutCode).setVisibility(View.GONE);
        findViewById(R.id.inputLayoutMobile).setVisibility(View.VISIBLE);

    }

    public void getCode(View view){

        ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        editTextMobile = findViewById(R.id.editTextMobile);
        String mobile = editTextMobile.getText().toString().trim();

        Call<LoginResponse> call = ApiClient
                .getInstance().getApi().getCode(mobile,appVersion);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();

                if (!loginResponse.isError()) {
                    progressDialog.dismiss();
                    if(loginResponse.getMessage().equals("SUCCESS")){

                        findViewById(R.id.buttonPass).setVisibility(View.GONE);
                        findViewById(R.id.buttonReg).setVisibility(View.GONE);
                        findViewById(R.id.buttonGet).setVisibility(View.GONE);
                        findViewById(R.id.buttonVry).setVisibility(View.VISIBLE);
                        findViewById(R.id.inputLayoutMobile).setVisibility(View.GONE);
                        findViewById(R.id.inputLayoutCode).setVisibility(View.VISIBLE);

                        new SharedPrefManager(RegisterActivity.this).saveMobile(mobile);


                    }else{
                        progressDialog.dismiss();
                        showOkMessage(loginResponse.getMessage(),getResources().getString(R.string.ok));
                    }

                } else {
                    progressDialog.dismiss();
                    showOkMessage(loginResponse.getMessage(),getResources().getString(R.string.ok));
                    }
                }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });


    }

    public void checkCode(View view){

        ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        editTextCode = findViewById(R.id.editTextCode);
        String passcode = editTextCode.getText().toString().trim();
        String mobile = SharedPrefManager.getInstance(this).getMobile();


        Call<LoginResponse> call = ApiClient
                .getInstance().getApi().checkCode(mobile,passcode,appVersion);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();

                if (!loginResponse.isError()) {
                    progressDialog.dismiss();
                    if(loginResponse.getMessage().equals("SUCCESS")){

                        showOkMessage("Registration Completed Successfully",getResources().getString(R.string.ok));
                        new SharedPrefManager(RegisterActivity.this).saveIsRegistered(true);

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                    }else{
                        progressDialog.dismiss();
                        showOkMessage(loginResponse.getMessage(),getResources().getString(R.string.ok));
                    }

                } else {
                    progressDialog.dismiss();
                    showOkMessage(loginResponse.getMessage(),getResources().getString(R.string.ok));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });


    }
}