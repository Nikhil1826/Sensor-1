package com.fasttech.saveme;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class ActivityOne extends AppCompatActivity implements SensorEventListener, LocationListener, View.OnClickListener {
    TextView tn;
    Button btn,btn1;
    LocationManager locationManager;
    SensorManager sensorManager;
    Sensor sensor;
    ProgressDialog pro;

    void initViews() {
        tn = (TextView) findViewById(R.id.textView);
        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(this);
        btn1 = (Button)findViewById(R.id.button2);
        btn1.setOnClickListener(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        pro = new ProgressDialog(this);
        pro.setMessage("Sending Message...");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        initViews();
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String name ="Nikhil Chauhan";
        String phone ="+91 9914005560";

        //REVERSE GEOCODING
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> list = geocoder.getFromLocation(latitude,longitude,5);

            if(list!=null && list.size()>0){
                Address address = list.get(0);
                StringBuffer buffer = new StringBuffer();

                for(int i=0;i<address.getMaxAddressLineIndex();i++){
                    buffer.append(address.getAddressLine(i)+"\n");
                }
                tn.setText(buffer.toString());
                String msg = tn.getText().toString().trim();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone,null,name+" is at "+msg,null,null);
                sensorManager.unregisterListener(this);
                pro.dismiss();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        locationManager.removeUpdates(this);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float values[] = sensorEvent.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float cal = ((x * x) + (y * y) + (z * z)) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);



        if (cal > 10) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Please Grant Permission", Toast.LENGTH_SHORT).show();
            } else {
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                   locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 5, this);
                   pro.show();
                }else{
                    Toast.makeText(this, "Please Enable GPS", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }

            }


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

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
        int id = view.getId();
        switch (id){
            case R.id.button:
                sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
                Toast.makeText(this,"Sensor activated",Toast.LENGTH_LONG).show();
                break;
            case R.id.button2:
                sensorManager.unregisterListener(this);
                Toast.makeText(this, "Sensor Deactivated", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }
}
