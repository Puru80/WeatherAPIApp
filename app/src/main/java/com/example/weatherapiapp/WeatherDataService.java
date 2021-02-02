package com.example.weatherapiapp;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataService
{

    public static final String QUERY_FOR_CITYIID = "https://www.metaweather.com/api/location/search/?query=";
    public static final String QUERY_FOR_CITYI_WEATHER_BY_ID = "https://www.metaweather.com/api/location/";

    Context context;
    String cityID;

    public WeatherDataService(Context context) {
        this.context = context;
    }

    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(String cityID);
    }

    public void getCityID(String cityName, VolleyResponseListener volleyResponseListener)
    {
        String url = QUERY_FOR_CITYIID + cityName;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        cityID = "";
                        try {
                            JSONObject obj = response.getJSONObject(0);
                            cityID = obj.getString("woeid");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //this worked, but didn't return cityID to MainActivity.
//                        Toast.makeText(context, "City ID : " + cityID,
//                                Toast.LENGTH_SHORT).show();
                        volleyResponseListener.onResponse(cityID);
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(context, "Something Wrong",
//                        Toast.LENGTH_SHORT).show();
                volleyResponseListener.onError("Something wrong");
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);

        //This returned nul. Problem!!
//        return cityID;
    }

    public interface ForeCastByIDResponse {
        void onError(String message);

        void onResponse(List<WeatherReportModel> weatherReportModels);
    }

    public void getForecastById(String cityID, ForeCastByIDResponse foreCastByIDResponse)
    {
        List<WeatherReportModel> weatherReportModels = new ArrayList<>();
        String url = QUERY_FOR_CITYI_WEATHER_BY_ID + cityID;

        // Get the json object
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Get the property called "consolidated weather" which is an array
                            JSONArray consolidated_weather_list = response.getJSONArray("consolidated_weather");

                            // Get each item in the array and assign it to a new WeatherReport
                            for(int i=0;i<consolidated_weather_list.length(); i++)
                            {
                                JSONObject first_day_from_api = (JSONObject) consolidated_weather_list.get(i);

                                WeatherReportModel one_day_weather = new WeatherReportModel();

                                one_day_weather.setId(first_day_from_api.getInt("id"));
                                one_day_weather.setWeather_state_name(first_day_from_api.getString("weather_state_name"));
                                one_day_weather.setWeather_state_abbr(first_day_from_api.getString("weather_state_abbr"));
                                one_day_weather.setWind_direction_compass(first_day_from_api.getString("wind_direction_compass"));
                                one_day_weather.setCreated(first_day_from_api.getString("created"));
                                one_day_weather.setApplicable_date(first_day_from_api.getString("applicable_date"));
                                one_day_weather.setMin_temp(first_day_from_api.getLong("min_temp"));
                                one_day_weather.setMax_temp(first_day_from_api.getLong("max_temp"));
                                one_day_weather.setThe_temp(first_day_from_api.getLong("the_temp"));
                                one_day_weather.setWind_speed(first_day_from_api.getLong("wind_speed"));
                                one_day_weather.setWind_direction(first_day_from_api.getLong("wind_direction"));
                                one_day_weather.setAir_pressure(first_day_from_api.getInt("air_pressure"));
                                one_day_weather.setHumidity(first_day_from_api.getInt("humidity"));
                                one_day_weather.setVisibility(first_day_from_api.getLong("visibility"));
                                one_day_weather.setPredictability(first_day_from_api.getInt("predictability"));

                                weatherReportModels.add(one_day_weather);
                            }

                            foreCastByIDResponse.onResponse(weatherReportModels);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public interface GetCityForecastByNameCallback{
        void onError(String message);
        void onResponse(List<WeatherReportModel> weatherReportModels);
    }

    public void getCityForecastByName(String cityName, GetCityForecastByNameCallback getCityForecastByNameCallback)
    {
        // fetch the city id given the name
        getCityID(cityName, new VolleyResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(String cityID) {
                //now we have the city ID
                getForecastById(cityID, new ForeCastByIDResponse() {
                    @Override
                    public void onError(String message) {

                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModels) {
                        //we have the weather report!!
                        getCityForecastByNameCallback.onResponse(weatherReportModels);
                    }
                });
            }
        });

        // fetch teh forecast given the city id
    }
}
