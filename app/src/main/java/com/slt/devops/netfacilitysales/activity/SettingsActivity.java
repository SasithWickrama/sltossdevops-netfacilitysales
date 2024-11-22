package com.slt.devops.netfacilitysales.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.slt.devops.netfacilitysales.R;
import com.slt.devops.netfacilitysales.activity.networkfacility.FormActivity;
import com.slt.devops.netfacilitysales.models.BaseClass;
import com.slt.devops.netfacilitysales.storage.SharedPrefManager;

public class SettingsActivity extends BaseClass {

    private ImageView imageButton;
    private PopupMenu dropDownMenu;
    public Menu menu;
    public TextView mob,regdate,usrid,usr,area,sn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        imageButton= findViewById(R.id.textMenu);
        dropDownMenu = new PopupMenu(SettingsActivity.this, imageButton);
        menu = dropDownMenu.getMenu();

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

                switch(item.getItemId()){
                    case R.id.action_home:
                        Intent intenth = new Intent(SettingsActivity.this, FormActivity.class);
                        intenth.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intenth);
                        break;

                    case R.id.action_settings:
                        Intent intents = new Intent(SettingsActivity.this, SettingsActivity.class);
                        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intents);
                        break;
                    case R.id.action_logout:
                        new SharedPrefManager(SettingsActivity.this).saveIsLogged(false);
                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;


                }

                return false;
            }
        });

       mob= findViewById(R.id.textContact);
       mob.setText(SharedPrefManager.getInstance(this).getMobile());

        regdate= findViewById(R.id.textRgdate);
        regdate.setText(SharedPrefManager.getInstance(this).getRegdate());

        usrid= findViewById(R.id.textUserid);
        usrid.setText(SharedPrefManager.getInstance(this).getSid());

        usr= findViewById(R.id.textUser);
        usr.setText(SharedPrefManager.getInstance(this).getName());

        area= findViewById(R.id.textArea);
        area.setText(SharedPrefManager.getInstance(this).getRtom());

        sn= findViewById(R.id.textSerial);
        sn.setText("XXXXXXXXX");



    }
}