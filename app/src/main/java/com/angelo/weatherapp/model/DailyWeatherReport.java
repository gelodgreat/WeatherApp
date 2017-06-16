package com.angelo.weatherapp.model;

/**
 * Created by crumali on 6/16/2017.
 */



public class DailyWeatherReport {
    private String cityName;
    private String country;
    private int currentTemp;
    private int minTemp;
    private int maxTemp;
    private String weather;
    private String rawDate;

    public static final String WEATHER_TYPE_CLOUDS = "Clouds";
    public static final String WEATHER_TYPE_CLEAR = "Clear";
    public static final String WEATHER_TYPE_RAIN = "Rain";
    public static final String WEATHER_TYPE_WIND = "Wind";
    public static final String WEATHER_TYPE_SNOW = "Snow";

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(int currentTemp) {
        this.currentTemp = currentTemp;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(int maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getRawDate() {
        return rawDate;
    }

    public void setRawDate(String rawDate) {
        this.rawDate = rawDate;
    }

    public static String getWeatherTypeClouds() {
        return WEATHER_TYPE_CLOUDS;
    }

    public static String getWeatherTypeClear() {
        return WEATHER_TYPE_CLEAR;
    }

    public static String getWeatherTypeRain() {
        return WEATHER_TYPE_RAIN;
    }

    public static String getWeatherTypeWind() {
        return WEATHER_TYPE_WIND;
    }

    public static String getWeatherTypeSnow() {
        return WEATHER_TYPE_SNOW;
    }

    public DailyWeatherReport(String cityName, String country, int currentTemp, int minTemp, int maxTemp, String weather, String rawDate) {
        this.cityName = cityName;
        this.country = country;
        this.currentTemp = currentTemp;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.weather = weather;
        this.rawDate = rawDatetoFormattedDate(rawDate);

    }

    public String rawDatetoFormattedDate(String rawDate) {
        //convert raw date to formattedDate
        return "May 1";
    }
}


