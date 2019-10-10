package com.nevilleedwardrichardson.cityweather;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class WeatherAPI {

    private static final String OPEN_WEATHER_MAP_URL = "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric";
    private static final String OPEN_WEATHER_MAP_API = "2cdeac7d73b73615bc47be0196c66fe2";

    public interface AsyncResponse {

        void processFinish(Weather weather);

    }

    public static class placeIdTask extends AsyncTask<String, Void, JSONObject> {

        public AsyncResponse delegate = null;

        public placeIdTask(AsyncResponse asyncResponse) {

            delegate = asyncResponse;

        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject jsonWeather = null;

            try {

                jsonWeather = getWeatherJSON(params[0], params[1]);

            } catch (Exception e) {

                Log.d(Constants.LOG_TAG, "ERROR: Cannot process JSON results", e);

            }


            return jsonWeather;
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                if (json != null) {

                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");

                    Weather weather = new Weather();

                    weather.setCity(json.getString("name"));
                    weather.setTemperature(String.format(Locale.getDefault(), "%.1f", main.getDouble("temp"))+ "°");
                    weather.setDescription(upperFirstCharacter(details.getString("description")));
                    weather.setId(details.getInt("id"));

                    delegate.processFinish(weather);

                }

            } catch (JSONException e) {

                Log.d(Constants.LOG_TAG, "ERROR: Cannot process JSON results", e);

            }


        }
    }

    public static String upperFirstCharacter(String text) {

        String newText = text.substring(0, 1).toUpperCase() + text.substring(1);

        return(newText);

    }

    public static JSONObject getWeatherJSON(String lat, String lon) {

        try {

            URL url = new URL(String.format(OPEN_WEATHER_MAP_URL, lat, lon));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);

            String tmp = "";

            while ((tmp = reader.readLine()) != null) {

                json.append(tmp).append("\n");

            }

            reader.close();

            JSONObject data = new JSONObject(json.toString());

             if (data.getInt("cod") != 200) {

                return null;

            }

            return(data);

        } catch (Exception ex) {

            Log.d(Constants.LOG_TAG, ex.getMessage());
            return(null);

        }

    }

}
