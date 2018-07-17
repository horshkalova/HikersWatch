package com.example.marynahorshkalova.hikerswatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    public void updateLocationInfo(Location location) {

        Log.i("Location", location.toString());

        TextView latitudeTextView = findViewById(R.id.latitudeTextView);
        TextView longitudeTextView = findViewById(R.id.longitudeTextView);
        TextView accuracyTextView = findViewById(R.id.accuracyTextView);
        TextView altitudeTextView = findViewById(R.id.altitudeTextView);
        TextView addressTextView = findViewById(R.id.addressTextView);

        latitudeTextView.setText("Latitude: " + location.getLatitude());
        longitudeTextView.setText("Longitude: " + location.getLongitude());
        accuracyTextView.setText("Accuracy: " + location.getAccuracy());
        altitudeTextView.setText("Altitude: " + location.getAltitude());

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        List<Address> addressList = null;

        try {

            addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

        } catch (IOException e) {

            e.printStackTrace();
        }

        String address = "Could not find address";

        if (addressList != null && addressList.size() > 0) {

            Log.i("Location Info", addressList.get(0).toString());

            address = "";

            if (addressList.get(0).getThoroughfare() != null && addressList.get(0).getFeatureName() != null) {

                address += addressList.get(0).getThoroughfare() + " " + addressList.get(0).getFeatureName() + "," + "\n";
            }
            if (addressList.get(0).getLocality() != null) {

                address += addressList.get(0).getLocality() + ", ";
            }
            if (addressList.get(0).getCountryName() != null) {

                address += addressList.get(0).getCountryName();
            }
        }

        addressTextView.setText("Address: " + address);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startListeningLocation();
        }
    }

    public void startListeningLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (Build.VERSION.SDK_INT < 23) {

            startListeningLocation();

        } else {

            // check if permission is granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // ask for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                // we have permission!
                startListeningLocation();

                // show user's current location when the app is lounged
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastKnownLocation != null) {

                    updateLocationInfo(lastKnownLocation);
                }

            }
        }
    }
}
