package com.slt.devops.netfacilitysales.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.text.Html;
import android.view.View;

import com.slt.devops.netfacilitysales.R;
import com.slt.devops.netfacilitysales.activity.networkfacility.FormActivity;
import com.slt.devops.netfacilitysales.api.ApiClient;
import com.slt.devops.netfacilitysales.models.LoginResponse;
import com.slt.devops.netfacilitysales.models.BaseClass;
import com.slt.devops.netfacilitysales.storage.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.lang.reflect.Array;

public class LoginActivity extends BaseClass{

    public String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        editTextSid = findViewById(R.id.editTextSid);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewVersion = findViewById(R.id.versionId);
        editTextMobile = findViewById(R.id.editTextMobile);
        appVersion = getResources().getString(R.string.app_version);

        // textViewVersion.setText("App by OSS-DevOps. SV"+ appVersion);

        String app1 = "App by ";
        String app2 = "<font color='#EE0000'>DevOps</font>";
        String app3 = ". DV";
        textViewVersion.setText(Html.fromHtml(app1 + app2+app3+appVersion));

    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }



    public void userLogin(View view) {

        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        String sid = editTextSid.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (sid.isEmpty()) {
            editTextSid.setError("Service ID is required");
            editTextSid.requestFocus();
            return;
        }


        if (password.isEmpty()) {
            editTextPassword.setError("Password required");
            editTextPassword.requestFocus();
            return;
        }

        String mobile = SharedPrefManager.getInstance(this).getMobile();

        Call<LoginResponse> call = ApiClient
                .getInstance().getApi().userLogin(sid, password,mobile,appVersion);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();

                if (!loginResponse.isError()) {
                    progressDialog.dismiss();
                    userList= loginResponse.getUserArray();
                    new SharedPrefManager(LoginActivity.this).saveLoginDetails(userList[0].getId(),
                            userList[0].getName(),userList[0].getContact(),userList[0].getRegdate(),userList[0].getRtom());


                    new SharedPrefManager(LoginActivity.this).saveIsLogged(true);

                    Intent intent = new Intent(LoginActivity.this, FormActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {
                    progressDialog.dismiss();


                    if(loginResponse.getMessage().contains("netfacility_v")){

                        String[] parts = loginResponse.getMessage().split("\\#");
                        showOkMessageCh(parts[1],parts[0],getResources().getString(R.string.ok));

                    }else{
                        showOkMessage(loginResponse.getMessage(),getResources().getString(R.string.ok));
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });

    }



}