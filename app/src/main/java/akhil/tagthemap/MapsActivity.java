package akhil.tagthemap;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    DatabaseHelper myDB;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;
    ArrayList<LatLng> listOfPoints = new ArrayList<>();
    public boolean isMapReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        myDB = new DatabaseHelper(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        isMapReady = true;
        myMethod();
    }

    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);


        MarkerOptions mp = new MarkerOptions();
        mp.position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        mp.title("My Current Location");

        mMap.addMarker(mp);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 16));

        myDB.insertData(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        if(isMapReady){
            Cursor res = myDB.getAllData();
            if(res.getCount() != 0){
                while (res.moveToNext()){
                    double latitude = res.getDouble(1);
                    double longitude = res.getDouble(2);
                    listOfPoints.add(new LatLng(latitude, longitude));
                }
                loadMarkers(listOfPoints);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();

    }

    protected void onPause() {
        super.onPause();
    }

    protected void myMethod(){
        if(isMapReady){
            Cursor res = myDB.getAllData();
            if(res.getCount() != 0){
                while (res.moveToNext()){
                    double latitude = res.getDouble(1);
                    double longitude = res.getDouble(2);
                    listOfPoints.add(new LatLng(latitude, longitude));
                }
                loadMarkers(listOfPoints);
            }
        }
    }

    protected void onResume(){
        super.onResume();
        myMethod();
    }

    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("places",  listOfPoints);
    }
    private void restore(Bundle outState){
        if (outState != null) {
            listOfPoints = (ArrayList<LatLng>) outState.getSerializable("places");
        }
    }
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        restore(outState);
        isMapReady=true;
    }

    private void loadMarkers(List<LatLng> listOfPoints) {
        int i=listOfPoints.size();
        while(i>0){
            i--;
            double Lat=listOfPoints.get(i).latitude;
            double Lon=listOfPoints.get(i).longitude;
            MarkerOptions mp = new MarkerOptions();

            mp.position(new LatLng(Lat, Lon)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

            mp.title("I have been here");

            mMap.addMarker(mp);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(Lat, Lon), 16));
        }
    }

}
