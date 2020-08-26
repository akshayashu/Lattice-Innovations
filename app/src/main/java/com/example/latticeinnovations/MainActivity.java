package com.example.latticeinnovations;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    EditText password, username, email, phoneNo, address;
    TextView location, markCurrentLocation, signInBtn;
    ImageView passwordCheck;
    String check = "show";
    SupportMapFragment supportMapFragment;
    GoogleMap map;

    Location gps_loc = null, network_loc = null, final_loc = null;
    double longitude = 0.0, latitude = 0.0;
    String zone, state;

    String name, emailStr, phoneNoStr, addressStr, passwordStr;

    boolean check1 = false, check2 = false, check3 = false, check4 = false, check5 = false;

    String[] permission = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService( LOCATION_SERVICE);

        init();
        askPermission();
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name = charSequence.toString().trim();
                check1 = charSequence.toString().trim().length() > 3;
                Log.d("Check1", String.valueOf(check1));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkConditions();
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailStr = charSequence.toString();
                Pattern pattern1 = Pattern.compile("^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+");
                Matcher matcher1 = pattern1.matcher(charSequence.toString().trim());
                check2 = matcher1.matches();
                Log.d("Check2", String.valueOf(check2));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkConditions();
            }
        });
        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                addressStr = charSequence.toString().trim();
                check3 = charSequence.toString().trim().length() > 9;
                Log.d("Check3", String.valueOf(check3));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkConditions();
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordStr = charSequence.toString().trim();
                Pattern pattern1 = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,15}");
                Matcher matcher1 = pattern1.matcher(charSequence.toString().trim());
                check4 = matcher1.matches();
                Log.d("Check4", String.valueOf(check4));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkConditions();
            }
        });
        phoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phoneNoStr = charSequence.toString().trim();
                Pattern pattern1 = Pattern.compile("^\\+[0-9]{10,13}$");
                Matcher matcher1 = pattern1.matcher(charSequence.toString().trim());
                check5 = matcher1.matches();
                Log.d("Check5", String.valueOf(check5));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkConditions();
            }
        });

        markCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermission();
            }
        });
        passwordCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (check){
                    case "show": passwordCheck.setImageResource(R.drawable.eye_show);
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    check = "hide";
                    break;

                    case  "hide": passwordCheck.setImageResource(R.drawable.eye_hide);
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    check = "show";
                    break;
                }
            }
        });
    }

    private void askPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this, permission,
                    100
            );
        }
        try {
            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch ( Exception e){
            Log.d("Location status", e.getLocalizedMessage());
        }
        getLocation();
    }

    private void getLocation(){

        if(gps_loc != null) {
            final_loc = gps_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        } else if (network_loc != null) {
            final_loc = network_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        } else {
            latitude = 0.0;
            longitude = 0.0;
        }

        getLocationAddress(latitude, longitude);
    }

    private void getLocationAddress(double lat, double lon){
        try {
            Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geoCoder.getFromLocation(lat, lon, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && !addresses.isEmpty()) {
                zone = addresses.get(0).getSubLocality();
                state = addresses.get(0).getAdminArea();
                if (zone != null) {
                    location.setText(zone + ", " + state);
                }else {
                    location.setText( state);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkConditions() {
        if(check1 && check2 && check3 && check4 && check5){
            signInBtn.setVisibility(View.VISIBLE);
            Log.d("CHECKING", "Visible");
        } else {
            signInBtn.setVisibility(View.INVISIBLE);
            Log.d("CHECKING", "InVisible");
        }
    }

    private void init() {
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phoneNo = findViewById(R.id.phoneNo);
        address = findViewById(R.id.address);
        password = findViewById(R.id.password);
        passwordCheck = findViewById(R.id.passwordCheck);
        signInBtn = findViewById(R.id.signInBtn);

        location = findViewById(R.id.location);
        markCurrentLocation = findViewById(R.id.setCurrent);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        final LatLng[] curLocation = new LatLng[1];

        if (latitude == 0.0 && longitude == 0.0) {
            curLocation[0] = new LatLng(29.447831, 77.032995);
        }else {
            curLocation[0] = new LatLng(latitude,longitude);
            Log.d("LOATION", String.valueOf(latitude)+","+String.valueOf(longitude));
        }
        map.addMarker(new MarkerOptions().position(curLocation[0]));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curLocation[0], 10.0f));

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                curLocation[0] = latLng;
                getLocationAddress(latLng.latitude, latLng.longitude);
                map.clear();
                map.addMarker(new MarkerOptions().position(curLocation[0]));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(curLocation[0], 12.0f));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
        }
    }
}