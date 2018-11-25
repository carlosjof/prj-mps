package com.example.carlosjof.testmaps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.sql.Date;
import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.location.LocationManager.NETWORK_PROVIDER;

public class MainActivity extends AppCompatActivity implements LocationListener, View.OnClickListener, AdapterView.OnItemSelectedListener{

    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;
    private FusedLocationProviderClient mFusedLocationClient;

    private Spinner spnDenun;
    private Spinner spnTipoDenun;
    private EditText editTextUsua;
    private EditText editTextFecha;
    private Button buttonDenunciar;
    private String img;
    private Switch aSwitch;
    private EditText editTextDetalle;

    private DatabaseReference mDatabase;


    TextView editTextLocation;
    TextView meditTextLocation;

    Button mybuttonMaps;

    private Geocoder geocoder;
    private List<Address> addresses;
    private LocationManager locationManager;
    private String address;

    private List<Address> addresses2;
    private String address2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        spnDenun = findViewById(R.id.spndenuncia);
        editTextFecha = findViewById(R.id.edtfecha);
        editTextUsua = findViewById(R.id.edtUsu);
        editTextDetalle = findViewById(R.id.edtDetalle);


        aSwitch = (Switch)findViewById(R.id.swtUser);

        spnTipoDenun = findViewById(R.id.spntipodenuncia);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_denuncia, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnDenun.setAdapter(adapter);
        spnDenun.setOnItemSelectedListener(this);


        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String currentDateandTime = dateFormat.format(Calendar.getInstance().getTime());

        editTextFecha.setText(currentDateandTime);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mybuttonMaps = findViewById(R.id.btnMapa);
        mybuttonMaps.setOnClickListener(this);


        buttonDenunciar = findViewById(R.id.btnPublicar);
        buttonDenunciar.setOnClickListener(this);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mylocation();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

    }



    private void mylocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(NETWORK_PROVIDER, 5000, 5, this);

        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    private void crearDenuncia(){
        LocationtoFirebase();
    }




    private void LocationtoFirebase() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            }

            return;
        }


        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            editTextLocation = findViewById(R.id.txtLocation);
                            geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

                            try {
                                addresses2 = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
                                address2 = addresses2.get(0).getAddressLine(0);
                                editTextLocation.setText(address2);

                                Map<String, Object> latlan = new HashMap<>();
                                latlan.put("latitud",location.getLatitude());
                                latlan.put("longitud",location.getLongitude());
                                latlan.put("denuncia", spnDenun.getSelectedItem().toString());

                                switch (spnDenun.getSelectedItem().toString()){
                                    case "Robo": latlan.put("imagen", "drawable/ladron.png");
                                                 break;
                                    default: latlan.put("imagen", "drawable/vacio.png");
                                             break;
                                }


                                latlan.put("tipodenuncia", spnTipoDenun.getSelectedItem().toString());
                                latlan.put("fecha", editTextFecha.getText().toString());

                                latlan.put("usuario", editTextUsua.getText().toString());
                                latlan.put("detalle", editTextDetalle.getText().toString());

                                mDatabase.child("denuncia").push().setValue(latlan);

                                Toast.makeText(MainActivity.this, "Denuncia Realizada", Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "Error al realizar la denuncia", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    @Override
    public void onLocationChanged(Location location) {
        meditTextLocation = findViewById(R.id.txtLocation2);
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            address = addresses.get(0).getAddressLine(0);
            meditTextLocation.setText(address);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Revise si tiene permisos GPS y conexion a INTERNET", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(this, "Revise si tiene permisos GPS y conexion a INTERNET", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnMapa : Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                                startActivity(intent);
                                finish();
                                break;
            case R.id.btnPublicar : crearDenuncia();
                                    break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int[] tipodenuncias = {R.array.array_robo, R.array.array_violenia, R.array.array_venta};

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, tipodenuncias[position], android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTipoDenun.setAdapter(adapter);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onClicked(View view) {
        if (view.getId()==R.id.swtUser){
            if (aSwitch.isChecked()){
                editTextUsua.setEnabled(false);
                Toast.makeText(MainActivity.this, "Anonimo", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(MainActivity.this, "Publico", Toast.LENGTH_SHORT).show();
                editTextUsua.setEnabled(true);
            }
        }
    }
}
