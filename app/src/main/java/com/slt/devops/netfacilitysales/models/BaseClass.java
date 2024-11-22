package com.slt.devops.netfacilitysales.models;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.location.LocationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.slt.devops.netfacilitysales.R;
import com.slt.devops.netfacilitysales.adapters.EquipmentAdapter;
import com.slt.devops.netfacilitysales.api.ApiImage;
import com.slt.devops.netfacilitysales.storage.SharedPrefManager;
import com.squareup.picasso.Picasso;
import com.tiper.MaterialSpinner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class BaseClass extends AppCompatActivity {

    public GoogleMap mMap;
    public View mapView;
    public Location mLastKnownLocation;

    public static final int DEFAULT_ZOOM = 18;
    public static final LatLng mDefaultLocation = new LatLng(6.934868, 79.846656);
    public static final  int REQUEST_CODE = 200;
    public static final String KEY_CAMERA_POSITION = "camera_position";
    public static final String KEY_LOCATION = "location";
    public static final String TAG = BaseClass.class.getSimpleName();
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;

    public LocationRequest mLocationRequest;
    public GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;
    public Marker mCurrLocationMarker;

    public ImageView imageView1,imageView2,imageView3;
    public int imgCount= 0;

    public Uri contentUri1, contentUri2, contentUri3;

    public FusedLocationProviderClient mFusedLocationProviderClient;


    public String appVersion,currentPhotoPath,imageFileName1,
            imageFileName2,currentPhotoPath1,currentPhotoPath2,currentPhotoPath3,imageFileName3, imgRetVal;

    public ProgressBar pgsBar;

    public EditText editTextSid,editTextPassword,editTextMobile,editTextCode;
    public TextView textViewVersion,textViewContact;


    public User[] userList;



    //Check network Connection
    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        android.net.ConnectivityManager cm = (android.net.ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    //check location service
    public boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }

    public void showOkMessage(String message,String positiveBtnText){
        new android.app.AlertDialog.Builder(this)
                .setCancelable(true)
                .setMessage(message)
                .setPositiveButton(positiveBtnText, null)
                .show();

    }

    public void showOkMessageCh(String message,String appver, String positiveBtnText){
        String $uri="https://serviceportal.slt.lk/MobileApp/apk/FACILITYSALES/"+appver+".apk";
        new android.app.AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent httpIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse($uri));
                        startActivity(httpIntent);
                    }
                })
                .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        finish();

                    }
                })
                .show();

    }


    public String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=walking";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+"&key=" +getResources().getString(R.string.google_maps_key);

        return url;
    }

    public String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.slt.devops.netfacilitysales.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //  File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );


        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    public String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    public String  uploadImage(String fileUri, String desc, String location, int reqType) {
        RequestBody requestFile = null;
        RequestBody descBody= null;
        RequestBody descLoc= null;

        /*if(reqType ==GALLERY_REQUEST_CODE){
            //creating a file
            File file2 = new File(getRealPathFromURI(fileUri));
            //File file = new File(compressImage(getRealPathFromURI(fileUri)));
            File file = new Compressor.Builder(this)
                    .setMaxWidth(1280)
                    .setMaxHeight(768)
                    .setQuality(100)
                    .setCompressFormat(Bitmap.CompressFormat.PNG)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .build()
                    .compressToFile(file2);


            //creating request body for file
            requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), file);
            descBody = RequestBody.create(MediaType.parse("text/plain"), desc);
        }*/

        if(reqType ==CAMERA_REQUEST_CODE){
            //creating a file
            File file2 = new File(fileUri);
          /*  File file;
            if (Environment.getExternalStorageState() == null) {

                 file = new Compressor.Builder(this)
                        .setMaxWidth(1280)
                        .setMaxHeight(768)
                        .setQuality(100)
                        .setCompressFormat(Bitmap.CompressFormat.PNG)
                        .setDestinationDirectoryPath(Environment.getDataDirectory().getAbsolutePath())
                        //.setDestinationDirectoryPath()
                        .build()
                        .compressToFile(file2);
            }else {*/
                 File file = new Compressor.Builder(this)
                        .setMaxWidth(1280)
                        .setMaxHeight(768)
                        .setQuality(100)
                        .setCompressFormat(Bitmap.CompressFormat.PNG)
                        .setDestinationDirectoryPath(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        //.setDestinationDirectoryPath()
                        .build()
                        .compressToFile(file2);


            //creating request body for file
            requestFile = RequestBody.create(MediaType.parse(String.valueOf(fileUri)), file);
            descBody = RequestBody.create(MediaType.parse("text/plain"), desc);
            descLoc = RequestBody.create(MediaType.parse("text/plain"), location);
            Log.d("tag", "requestFile Url of Image is " + fileUri);
        }


        //The gson builder
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        //creating retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiImage.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //creating our api
        ApiImage api = retrofit.create(ApiImage.class);

        //creating a call and calling the upload image method
        //Call<Response> call = api.uploadImage(requestFile, descBody);
        Call<UpdateResponse> call = api.uploadImage(requestFile, descBody,descLoc);
        //finally performing the call
        call.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                Log.d("tag", "requestFile Url of Image is ok");
                UpdateResponse updateResponse = response.body();
                if (!updateResponse.isError()) {
                    Log.d("tag", "requestFile Url of Image is error ok");
                    imgRetVal= "SUCCESS";
                } else {
                    Log.d("tag", "requestFile Url of Image is error");
                    imgRetVal= "FAIL";
                }
            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return imgRetVal;
    }



    public String  uploadSig(Bitmap sigfile, String desc, String location) {
        RequestBody requestFile = null;
        RequestBody descBody= null;
        RequestBody descLoc= null;

            //creating a file
            File file2 = persistImage(sigfile,"signature");
            File file = new Compressor.Builder(this)
                    .setMaxWidth(1280)
                    .setMaxHeight(768)
                    .setQuality(100)
                    .setCompressFormat(Bitmap.CompressFormat.PNG)
                    .setDestinationDirectoryPath(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    //.setDestinationDirectoryPath()
                    .build()
                    .compressToFile(file2);


            requestFile = RequestBody.create(MediaType.parse(String.valueOf(file.getAbsolutePath())), file);
            descBody = RequestBody.create(MediaType.parse("text/plain"), desc);
            descLoc = RequestBody.create(MediaType.parse("text/plain"), location);
            Log.d("tag", "requestFile Url of Image is " + file.getAbsolutePath());

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiImage.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //creating our api
        ApiImage api = retrofit.create(ApiImage.class);
        Call<UpdateResponse> call = api.uploadImage(requestFile, descBody,descLoc);
        call.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                Log.d("tag", "requestFile Url of Image is ok");
                UpdateResponse updateResponse = response.body();
                if (!updateResponse.isError()) {
                    Log.d("tag", "requestFile Url of Image is error ok");
                    imgRetVal= "SUCCESS";
                } else {
                    Log.d("tag", "requestFile Url of Image is error");
                    imgRetVal= "FAIL";
                }
            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return imgRetVal;
    }


    private  File persistImage(Bitmap bitmap, String name) {
        File filesDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("myapp", "Error writing bitmap", e);
        }
        return imageFile;
    }


    public static Bitmap createCustomMarker(Context context) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.imageViewMap);
        Picasso.get().load("https://serviceportal.slt.lk/ApiNeylie/img/"+ SharedPrefManager.getInstance(context).getSid()+".png").placeholder(R.drawable.placeholder).into(markerImage);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }


}
