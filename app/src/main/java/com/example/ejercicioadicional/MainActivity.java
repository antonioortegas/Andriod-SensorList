package com.example.ejercicioadicional;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView sensorValueText;
    private Sensor selectedSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorValueText = findViewById(R.id.sensor_value);
        Spinner sensorSelector = findViewById(R.id.sensor_selector);

        // Get a list of sensors
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        List<String> sensorNames = new ArrayList<>();
        for (Sensor sensor : sensors) {
            sensorNames.add(sensor.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sensorNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sensorSelector.setAdapter(adapter);

        sensorSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSensor = sensors.get(position);
                sensorValueText.setText(R.string.waiting_for_sensor_values);
                sensorManager.unregisterListener(MainActivity.this);
                sensorManager.registerListener(MainActivity.this, selectedSensor, SensorManager.SENSOR_DELAY_UI);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sensorValueText.setText(R.string.select_a_sensor_to_see_values);
                sensorManager.unregisterListener(MainActivity.this);
            }
        });
    }

    //when values change on the sensor, update UI
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.equals(selectedSensor)) {
            StringBuilder values = new StringBuilder();
            for (float value : event.values) {
                values.append(value).append("\n");
            }
            sensorValueText.setText(values.toString().trim());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
