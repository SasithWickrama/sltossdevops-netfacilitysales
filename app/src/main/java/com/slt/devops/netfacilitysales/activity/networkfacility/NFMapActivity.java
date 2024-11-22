package com.slt.devops.netfacilitysales.activity.networkfacility;

import android.Manifest;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.slt.devops.netfacilitysales.R;
import com.slt.devops.netfacilitysales.api.ApiClient;
import com.slt.devops.netfacilitysales.models.BaseClass;
import com.slt.devops.netfacilitysales.models.DirectionsJSONParser;
import com.slt.devops.netfacilitysales.models.Equipment;
import com.slt.devops.netfacilitysales.models.EquipmentResponse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NFMapActivity extends BaseClass implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private String sid, rtom, eqipment;
    private Equipment[] equipmentList;
    private ValueAnimator vAnimator;
    private GroundOverlay groundOverlay1;
    private LatLng cushome;
    private Polyline path = null;
    private Float gdis;
    private Marker ClikedMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.netf_activity_maps);


        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");
        rtom = intent.getStringExtra("rtom");
        eqipment = intent.getStringExtra("dptype");
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
           // LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }
    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                 View infoWindow = getLayoutInflater().inflate(R.layout.netf_map_infow,null);
                 TextView title = infoWindow.findViewById(R.id.title);
                 title.setText(marker.getTitle());

               // Log.println(Log.INFO, "myapp", "snip " + Integer.parseInt(marker.getSnippet()));
              //  Log.println(Log.INFO, "myapp", "snip " + marker.getTitle().equalsIgnoreCase("Home"));
                if (!marker.getTitle().equalsIgnoreCase("Home")) {

                    Equipment eq = equipmentList[Integer.parseInt(marker.getSnippet())];

                    Log.println(Log.INFO, "myapp", "snip " + eq.getDISCRIPTION());
                    TextView dis = infoWindow.findViewById(R.id.discription);
                    dis.setText(eq.getDISCRIPTION());
                    TextView type = infoWindow.findViewById(R.id.type);
                    type.setText(eq.getType());
                    TextView used = infoWindow.findViewById(R.id.used);
                    used.setText(eq.getOCCUPIED());
                    TextView free = infoWindow.findViewById(R.id.free);
                    free.setText(eq.getFREE());
                    TextView distance = infoWindow.findViewById(R.id.distance);
                    distance.setText(gdis.toString()+"m");
                    return infoWindow;
               }else{
                    return null;
                }

            }

        });

        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                Log.println(Log.INFO,"myapp", "infowindow selected");

                if (!marker.getTitle().equalsIgnoreCase("Home")) {

                    Equipment eq = equipmentList[Integer.parseInt(marker.getSnippet())];
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("index", eq.getINDEX());
                    returnIntent.putExtra("dp", eq.getDISCRIPTION());
                    returnIntent.putExtra("location", eq.getLocation());
                    returnIntent.putExtra("distance", gdis.toString());
                    returnIntent.putExtra("cuslat", String.valueOf(cushome.latitude));
                    returnIntent.putExtra("cuslon", String.valueOf(cushome.longitude));
                    returnIntent.putExtra("lea", eq.getLEA());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                    return;
                }


            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latLng.latitude, latLng.longitude))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        .title("Home")
                );

                cushome = latLng;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        groundOverlay1 = mMap.addGroundOverlay(new
                                GroundOverlayOptions()
                                .position(latLng, 2000)
                                .transparency(0.5f)
                                .image(BitmapDescriptorFactory.fromResource(R.drawable.blue_ripple)));
                        OverLay(groundOverlay1);
                    }
                }, 0);


                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(latLng.latitude, latLng.longitude), 18));

                getDPlist(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                gdis = Float.valueOf(0);
                ClikedMarker = marker;
                String url = getDirectionsUrl(marker.getPosition(), cushome);
                Log.println(Log.INFO,"myapp", url);
                DownloadTask downloadTask = new DownloadTask();
                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
                return false;
            }
        });


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setSmallestDisplacement(0.1f);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }


    private void getDPlist(String lat, String lon) {
        Call<EquipmentResponse> call = ApiClient
                .getInstance().getApi().getDPlist(sid, rtom, eqipment, lat, lon);

        call.enqueue(new Callback<EquipmentResponse>() {
            @Override
            public void onResponse(Call<EquipmentResponse> call, Response<EquipmentResponse> response) {
                EquipmentResponse eqipments = response.body();

                if (!eqipments.isError()) {
                    equipmentList = eqipments.getEquipmentArray();

                    // Log.println(Log.INFO,"myapp", "array length"+String.valueOf(equipmentList.length));

                    for (int i = 0; i < 5; i++) {
                        // Log.println(Log.INFO, "myapp",equipmentList[i].getLocation()+"   "+ equipmentList[i].getFREE());
                        if (Integer.parseInt(equipmentList[i].getFREE()) == 0) {
                        if(equipmentList[i].getSTATUS().equalsIgnoreCase("PLANNED")){
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.valueOf(equipmentList[i].getLatitude()),
                                            Double.valueOf(equipmentList[i].getLontitude())))
                                    // .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.redp38))
                                    .title(equipmentList[i].getLocation().toString())
                                    .snippet(String.valueOf(i))
                            );
                        }else {
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.valueOf(equipmentList[i].getLatitude()),
                                            Double.valueOf(equipmentList[i].getLontitude())))
                                    // .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.red38))
                                    .title(equipmentList[i].getLocation().toString())
                                    .snippet(String.valueOf(i))
                            );
                        }
                    } else {
                            Log.println(Log.INFO,"myapp", "data "+equipmentList.length);
                            Log.println(Log.INFO,"myapp", "data "+equipmentList[i].getLocation());
                        if(equipmentList[i].getSTATUS().equalsIgnoreCase("PLANNED")) {
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.valueOf(equipmentList[i].getLatitude()),
                                            Double.valueOf(equipmentList[i].getLontitude())))
                                    //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.greenp38))
                                    .title(equipmentList[i].getLocation().toString())
                                    .snippet(String.valueOf(i))
                            );
                        }else {
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.valueOf(equipmentList[i].getLatitude()),
                                            Double.valueOf(equipmentList[i].getLontitude())))
                                    //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.green38))
                                    .title(equipmentList[i].getLocation().toString())
                                    .snippet(String.valueOf(i))
                            );
                        }
                    }
                    }


                    if (vAnimator.isRunning()) {
                        vAnimator.removeAllListeners();
                        vAnimator.removeAllUpdateListeners();
                        vAnimator.cancel();
                        groundOverlay1.setVisible(false);
                    }


                } else {
                    showOkMessage(eqipments.getMessage(), getResources().getString(R.string.ok));
                }
            }

            @Override
            public void onFailure(Call<EquipmentResponse> call, Throwable t) {

            }
        });

    }


    public void OverLay(final GroundOverlay groundOverlay) {
        vAnimator = ValueAnimator.ofInt(0, 2000);
        int r = 99999;
        vAnimator.setRepeatCount(r);
        //vAnimator.setIntValues(0, 500);
        vAnimator.setDuration(12000);
        vAnimator.setEvaluator(new IntEvaluator());
        vAnimator.setInterpolator(new LinearInterpolator());
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                Integer i = (Integer) valueAnimator.getAnimatedValue();
                groundOverlay.setDimensions(i);
            }
        });
        vAnimator.start();
    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        /*markerOptions.icon(BitmapDescriptorFactory.fromBitmap(
                BaseClass.createCustomMarker(this)));*/
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
                Log.println(Log.INFO,"myapp", "data "+data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;



            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
                gdis = parser.parseDistance(jObject);
                Log.println(Log.INFO,"myapp", "data "+ String.valueOf(gdis)+"m");


            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            Log.println(Log.INFO,"myapp",result.toString());
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            if(path != null){
                path.remove();
            }

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

            if(lineOptions!= null){
                path =  mMap.addPolyline(lineOptions);

               /* BitmapDescriptor invisibleMarker = BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
                Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(cushome)
                                .title("Home")
                                .snippet(gdis.toString())
                                .alpha(0f)
                                .icon(invisibleMarker)
                                .anchor(0f, 0f)
                );*/
                ClikedMarker.showInfoWindow();

            }else{
                showOkMessage(getResources().getString(R.string.err_nopath),getResources().getString(R.string.ok));
            }


        }
    }
}