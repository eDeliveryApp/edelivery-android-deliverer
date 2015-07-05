package io.hackathon.edelivery.office;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends Activity implements Button.OnClickListener, ApiService.OrderListener, LocationListener {
    EditText txtPhone, txtId, txtLat, txtLng;
    Button btnSend;
    CheckBox inputLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtPhone = (EditText) findViewById(R.id.orderPhone);
        txtId = (EditText) findViewById(R.id.orderId);
        txtLat = (EditText) findViewById(R.id.orderLat);
        txtLng = (EditText) findViewById(R.id.orderLng);
        btnSend = (Button) findViewById(R.id.send);
        inputLocation = (CheckBox) findViewById(R.id.inputLocation);
        getLocation();
    }

    boolean isGPSEnabled, isNetworkEnabled, canGetLocation;
    int MIN_TIME_BW_UPDATES = 1000, MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    Location location;

    private void getLocation() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                Toast.makeText(this, "No location provider is enabled.", Toast.LENGTH_LONG).show();
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                if (isGPSEnabled && location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        btnSend.setOnClickListener(this);
        inputLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                txtLat.setEnabled(b);
                txtLng.setEnabled(b);
                btnSend.setEnabled(b);
                if (!b) {
                    getLocation();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        try {
            if (!inputLocation.isChecked()) {
                txtLat.setText(location.getLatitude() + "");
                txtLng.setText(location.getLongitude() + "");
                onClick(btnSend);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onClick(View view) {
        if (view.getId() == btnSend.getId()) {

            InputMethodManager img = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
            View currFocus = getCurrentFocus();
            if (currFocus != null) {
                img.hideSoftInputFromWindow(currFocus.getWindowToken(), 0);
            }
            LatLng latLng = new LatLng(Double.parseDouble(txtLat.getText().toString()), Double.parseDouble(txtLng.getText().toString()));

            Order order = new Order(latLng, txtId.getText().toString(), txtPhone.getText().toString());
            ApiService.updateOrder(this, order, this);
        }
    }

    @Override
    public void onSuccess(Object object) {
        Toast.makeText(this, "Update success", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(Object object) {
        Toast.makeText(this, (String) object, Toast.LENGTH_LONG).show();
    }
}
