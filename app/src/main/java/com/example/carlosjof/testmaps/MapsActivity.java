package com.example.carlosjof.testmaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private GeoFire geoFire;
    private ArrayList<Marker> tmpRealTimeMarker = new ArrayList<>();

    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    //Hacer logica dentro del mapa
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object

                            LatLng userLoc = new LatLng(location.getLatitude(),location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 18));

                            mMap.setInfoWindowAdapter(new infowindowAdapter( MapsActivity.this));



//NOTIFICACION
                            LatLng zonasPeligrosas = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addCircle(new CircleOptions()
                                    .center(zonasPeligrosas)
                                    .radius(500) //en metros
                                    .strokeColor(Color.RED)
                                    .fillColor(0x220000FF)
                                    .strokeWidth(5.0f)
                            );


                        }
                    }
                });

        mDatabase.child("denuncia").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    //snipet descripcion;

                    for (Marker marker: realTimeMarkers){
                        marker.remove();
                    }

                    MapsModel mp = snapshot.getValue(MapsModel.class);
                    Double latitud = mp.getLatitud();
                    Double longitud = mp.getLongitud();
                    String denuncia = mp.getDenuncia();
                    String tipodenuncia = mp.getTipodenuncia();
                    String fecha = mp.getFecha();
                    String usuario = mp.getUsuario();
                    String detalle = mp.getDetalle();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(latitud, longitud));
                    markerOptions.title(denuncia + " " + tipodenuncia);
                    markerOptions.snippet("Usuario " + usuario + " \n" + "Fecha " + fecha + " \n" + "Detalle " + detalle);

                    switch (denuncia){
                        case "Robo": markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ladron));
                                     break;
                        default: markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.violencia));
                                 break;
                    }
                    tmpRealTimeMarker.add(mMap.addMarker(markerOptions));



                }

                realTimeMarkers.clear();
                realTimeMarkers.addAll(tmpRealTimeMarker);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        marker.showInfoWindow();
    }

}
