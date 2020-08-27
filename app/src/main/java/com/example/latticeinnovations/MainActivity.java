package com.example.latticeinnovations;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.Manifest;
import android.content.Intent;
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

import com.example.latticeinnovations.RoomDB.UserDB;
import com.example.latticeinnovations.RoomDB.UserEntity;
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

import static io.reactivex.internal.schedulers.SchedulerPoolFactory.start;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    //Views
    EditText password, username, email, phoneNo, address;
    TextView setCurrent, location, signInBtn;
    ImageView passwordCheck, logo;
    String check = "show";

    //GoogleMap
    SupportMapFragment supportMapFragment;
    GoogleMap map;

    //Location variables
    LocationManager locationManager;
    Location gps_loc = null,
            network_loc = null,
            final_loc = null;
    double longitude = 0.0,
            latitude = 0.0;
    String zone, state;

    //Condition variables
    String name, emailStr, phoneNoStr, addressStr, passwordStr;
    boolean[] checkFields = {false,false,false,false,false,false};

    String[] permission = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final UserDB db = Room.databaseBuilder(getApplicationContext(), UserDB.class, "userDB").build();

        init();
        checkFields();

        locationManager = (LocationManager) getSystemService( LOCATION_SERVICE);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        UserEntity userEntity = new UserEntity(emailStr, name, passwordStr, phoneNoStr, addressStr);
                        try {
                            db.userDAO().saveUser(userEntity);

                        } catch (Exception e) {
                            Log.d("STARTING ERROR", e.getLocalizedMessage());
                        }
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
                startActivity(new Intent(getApplicationContext(), UserList.class));
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

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UserList.class));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        askPermission();
        map = googleMap;

        final LatLng[] curLocation = new LatLng[1];

        if (latitude == 0.0 && longitude == 0.0) {
            curLocation[0] = new LatLng(29.447831, 77.032995);
        }else {
            curLocation[0] = new LatLng(latitude,longitude);
            Log.d("LOCATION",latitude +","+ longitude);
        }

        map.clear();
        map.addMarker(new MarkerOptions().position(curLocation[0]));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curLocation[0], 10.0f));

        setCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermission();
                if (latitude == 0.0 && longitude == 0.0) {
                    curLocation[0] = new LatLng(29.447831, 77.032995);
                }else {
                    curLocation[0] = new LatLng(latitude,longitude);
                    Log.d("LOCATION",latitude +","+ longitude);
                }
                map.clear();
                map.addMarker(new MarkerOptions().position(curLocation[0]));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(curLocation[0], 10.0f));

            }
        });

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

    //Conditions for SignUp button to be visible
    private void checkFields() {

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name = charSequence.toString().trim();
                checkFields[0] = charSequence.toString().trim().length() > 3;
                Log.d("Check1", String.valueOf(checkFields[0]));
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
                checkFields[1] = matcher1.matches();
                Log.d("Check2", String.valueOf(checkFields[1]));
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
                checkFields[2] = charSequence.toString().trim().length() > 9;
                Log.d("Check3", String.valueOf(checkFields[2]));
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
                checkFields[3] = matcher1.matches();
                Log.d("Check4", String.valueOf(checkFields[3]));
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
                checkFields[4] = matcher1.matches();
                Log.d("Check5", String.valueOf(checkFields[4]));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkConditions();
            }
        });
    }

    //Validating SignUp button
    private void checkConditions() {
        if(checkFields[0] && checkFields[1] && checkFields[2] && checkFields[3] && checkFields[4]){
            signInBtn.setVisibility(View.VISIBLE);
            Log.d("CHECKING", "Visible");
        } else {
            signInBtn.setVisibility(View.GONE);
            Log.d("CHECKING", "InVisible");
        }
    }

    //initialising Views
    private void init() {
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phoneNo = findViewById(R.id.phoneNo);
        address = findViewById(R.id.address);
        password = findViewById(R.id.password);
        passwordCheck = findViewById(R.id.passwordCheck);
        signInBtn = findViewById(R.id.signInBtn);
        logo = findViewById(R.id.imageView);

        location = findViewById(R.id.location);
        setCurrent = findViewById(R.id.setCurrent);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view);
        supportMapFragment.getMapAsync(this);
    }

    //Asking location-access Permission
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
            getLocation();
        } catch ( Exception e){
            Log.d("Location status", e.getLocalizedMessage());
        }
    }

    //Acquiring latitude and longitude
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

    //Getting Location String
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