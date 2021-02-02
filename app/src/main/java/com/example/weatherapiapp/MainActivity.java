package com.example.weatherapiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    Button btn_getCityId, btn_getWeatherByCityId, btn_getWeatherByCityName;
    EditText et_dataInput;
    ListView lv_weatherReport;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assigning values to each UI component
        btn_getCityId = findViewById(R.id.btn_getCityId);
        btn_getWeatherByCityId = findViewById(R.id.btn_getWeatherByCityId);
        btn_getWeatherByCityName = findViewById(R.id.btn_getWeatherByCityName);

        et_dataInput = findViewById(R.id.et_dataInput);
        lv_weatherReport = findViewById(R.id.lv_weatherReport);

        final WeatherDataService weatherDataService = new WeatherDataService(MainActivity.this);

        btn_getCityId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                weatherDataService.getCityID(et_dataInput.getText().toString(),
                        new WeatherDataService.VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, "Something Wrong",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String cityID) {
                        Toast.makeText(MainActivity.this, "Returned an ID : " + cityID,
                                Toast.LENGTH_SHORT).show();
                        et_dataInput.setText(cityID);
                    }
                });

            }
        });

        btn_getWeatherByCityId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weatherDataService.getForecastById(et_dataInput.getText().toString(), new WeatherDataService.ForeCastByIDResponse() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModels) {
                        //put the entire list into list

                        ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,
                                weatherReportModels);
                        lv_weatherReport.setAdapter(arrayAdapter);


                    }
                });
            }
        });

        btn_getWeatherByCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weatherDataService.getCityForecastByName(et_dataInput.getText().toString(), new WeatherDataService.GetCityForecastByNameCallback() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModels) {
                        //put the entire list into list

                        ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,
                                weatherReportModels);
                        lv_weatherReport.setAdapter(arrayAdapter);


                    }
                });
            }
        });

    }
}