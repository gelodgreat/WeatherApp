package com.angelo.weatherapp;


import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.angelo.weatherapp.model.DailyWeatherReport;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class WeatherActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {


    private GoogleApiClient mGoogleApiClient;
    private final int PERMISSION_LOCATION = 111;
    private ArrayList<DailyWeatherReport> weatherReportsList = new ArrayList<>();
    //14.560729, 121.026213

    private ImageView weatherIcon;
    private ImageView weatherIconMini;
    private TextView weatherDate;
    private TextView currentTemp;
    private TextView lowTemp;
    private TextView cityCountry;
    private TextView weatherDescription;

    WeatherAdapter mAdapter;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).enableAutoManage(this, this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        init();
    }

    private void init() {
        weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
        weatherIconMini = (ImageView) findViewById(R.id.weatherIconMini);
        weatherDate = (TextView) findViewById(R.id.weatherDate);
        currentTemp = (TextView) findViewById(R.id.currentTemp);
        lowTemp = (TextView) findViewById(R.id.lowTemp);
        cityCountry = (TextView) findViewById(R.id.cityCountry);
        weatherDescription = (TextView) findViewById(R.id.weatherDescription);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_weather_reports);
        mAdapter = new WeatherAdapter(weatherReportsList);

        recyclerView.setAdapter(mAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void downloadWeatherData(Location location) {
        String URL_BASE = "http://api.openweathermap.org/data/2.5/forecast";
        String URL_COORD = "/?lat=" + location.getLatitude() + "&lon=" + location.getLongitude();
        String URL_UNITS = "&units=imperial";
        String URL_API_KEY = "&APPID=dcbad9c3d3d1c73292e11eb990565a71";

        if (isNetworkAvailable()) {
            final String fullcoords = URL_COORD;
            final String url = URL_BASE + fullcoords + URL_UNITS + URL_API_KEY;
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        JSONObject city = response.getJSONObject("city");
                        String cityname = city.getString("name");
                        String country = city.getString("country");

                        JSONArray list = response.getJSONArray("list");


                        for (int x = 0; x < 5; x++) {
                            JSONObject object = list.getJSONObject(x);
                            JSONObject main = object.getJSONObject("main");

                            Double currentTemp = main.getDouble("temp");
                            Double minTemp = main.getDouble("temp_min");
                            Double maxTemp = main.getDouble("temp_max");

                            JSONArray weatherArray = object.getJSONArray("weather");
                            JSONObject weather = weatherArray.getJSONObject(0);
                            String weathertype = weather.getString("main");
                            String rawdate = object.getString("dt_txt");

                            DailyWeatherReport report = new DailyWeatherReport(cityname, country, currentTemp.intValue(), minTemp.intValue(), maxTemp.intValue(), weathertype, rawdate);

                            Log.v("JSON", "Printing from class: " + report.getWeather());


                            weatherReportsList.add(report);

                        }

                        Log.v("JSON", "Name: " + cityname + " - " + " Country: " + country);
                    } catch (JSONException e) {
                        Log.v("JSON", e.getLocalizedMessage());
                    }
                    updateUI();
                    mAdapter.notifyDataSetChanged();
                    Log.v("FUN", "RES: " + response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("FUN", error.getLocalizedMessage());
                }
            });

            Volley.newRequestQueue(this).add(jsonObjectRequest);
        } else {
            Toast.makeText(this, "Network unavailable....", Toast.LENGTH_SHORT).show();
        }


    }

    public void updateUI() {
        if (weatherReportsList.size() > 0) {
            DailyWeatherReport report = weatherReportsList.get(0);

            switch (report.getWeather()) {
                case DailyWeatherReport.WEATHER_TYPE_CLOUDS:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.cloudy));
                    weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_RAIN:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.rainy));
                    weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.rainy_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_SNOW:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.snow));
                    weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.snow_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_WIND:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.rainy));
                    weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.rainy_mini));
                    break;
                default:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
                    weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.sunny_mini));
                    break;
            }

            weatherDate.setText("Today, May 1");
            currentTemp.setText(Integer.toString(report.getCurrentTemp()) + "°");
            lowTemp.setText(Integer.toString(report.getMinTemp()) + "°");
            cityCountry.setText(report.getCityName() + ", " + report.getCountry());
            weatherDescription.setText(report.getWeather());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
        } else {
            startLocationServices();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        downloadWeatherData(location);
    }

    public void startLocationServices() {
        Log.v("DONKEY", "Starting Location Services Called");

        try {
            LocationRequest req = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, req, this);
            Log.v("DONKEY", "Requesting location updates");
        } catch (SecurityException exception) {
            //Show dialog to user saying we can't get location unless they give app permission
            Log.v("DONKEY", exception.toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationServices();
                    Log.v("DONKEY", "Permission Granted - starting services");
                } else {
                    Toast.makeText(this, "Can't find your location, Request Denied.", Toast.LENGTH_SHORT).show();
                    //show a dialog saying something like, "I can't run your location dummy - you denied permission!"
                    Log.v("DONKEY", "Permission not granted");
                }
            }
        }
    }


    public class WeatherAdapter extends RecyclerView.Adapter<weatherReportViewHolder> {

        private ArrayList<DailyWeatherReport> mDailyWeatherReports;

        public WeatherAdapter(ArrayList<DailyWeatherReport> mDailyWeatherReports) {
            this.mDailyWeatherReports = mDailyWeatherReports;
        }

        @Override
        public weatherReportViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View card = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardweather, viewGroup, false);

            return new weatherReportViewHolder(card);
        }

        @Override
        public void onBindViewHolder(weatherReportViewHolder weatherReportViewHolder, int i) {
            DailyWeatherReport report = mDailyWeatherReports.get(i);
            weatherReportViewHolder.updateUI(report);

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    public class weatherReportViewHolder extends RecyclerView.ViewHolder {

        private ImageView lweatherIcon;
        private TextView lweatherDate;
        private TextView lweatherDescription;
        private TextView ltempHigh;
        private TextView ltempLow;

        public weatherReportViewHolder(View itemView) {
            super(itemView);

            lweatherIcon = (ImageView) itemView.findViewById(R.id.list_weatherImg);
            lweatherDate = (TextView) itemView.findViewById(R.id.list_weather_day);
            lweatherDescription = (TextView) itemView.findViewById(R.id.list_weather_description);
            ltempHigh = (TextView) itemView.findViewById(R.id.list_weather_temp_high);
            ltempLow = (TextView) itemView.findViewById(R.id.list_weather_temp_low);

        }

        public void updateUI(DailyWeatherReport report) {

            lweatherDate.setText(report.getRawDate());
            lweatherDescription.setText(report.getWeather());
            ltempHigh.setText(Integer.toString(report.getMaxTemp()));
            ltempLow.setText(Integer.toString(report.getMinTemp()));

            switch (report.getWeather()) {
                case DailyWeatherReport.WEATHER_TYPE_CLOUDS:
                    lweatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_RAIN:
                    lweatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.rainy_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_SNOW:
                    lweatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.snow_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_WIND:
                    lweatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.rainy_mini));
                    break;
                default:
                    lweatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.sunny_mini));
                    break;
            }
        }
    }

}

