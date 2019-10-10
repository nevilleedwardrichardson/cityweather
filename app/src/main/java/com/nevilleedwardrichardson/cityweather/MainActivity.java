package com.nevilleedwardrichardson.cityweather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static int PERMISSION_REQUEST_CODE = 1000;

    private FusedLocationProviderClient fusedLocationClient;
    private TextView textViewCity;
    private TextView textViewTemperature;
    private TextView textViewWeather;
    private TextView textViewError;
    private TextView textViewLastUpdated;
    private ImageView imageViewWeather;
    private Button buttonUpdate;
    private FrameLayout progressBarLayout;
    private Weather weather = new Weather();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_DeviceDefault_NoActionBar);
        setContentView(R.layout.activity_main);

        textViewCity = findViewById(R.id.textViewCity);
        textViewTemperature = findViewById(R.id.textViewTemperature);
        textViewWeather = findViewById(R.id.textViewWeather);
        textViewError = findViewById(R.id.textViewError);
        textViewLastUpdated = findViewById(R.id.textViewLastUpdated);
        imageViewWeather = findViewById(R.id.imageViewWeather);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        progressBarLayout = (FrameLayout) findViewById(R.id.progressBarLayout);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (savedInstanceState == null) {

            getWeatherForCurrentLocation();

        } else {

            ;

        }

    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        try {

            updateUI((Weather)savedInstanceState.getSerializable("Weather"));

        } catch (Exception ex) {

            Log.d(Constants.LOG_TAG, ex.getMessage());

        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putSerializable("Weather", weather);

        super.onSaveInstanceState(outState);

     }

    public Boolean isNetworkConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Boolean connected = (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected());

        return (connected);

    }

    private void getWeatherForCurrentLocation() {

        showProgress(true);

        if (isNetworkConnected(this)) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                if (location != null) {

                                    getCityWeather(location);

                                } else {

                                    showError("Could not find location. Please check location services are enabled.");

                                }
                            }
                        });

            } else {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        PERMISSION_REQUEST_CODE);

            }

        } else {

            showError("Not connected to the internet. Please check settings.");

        }

    }

    private void getCityWeather(Location location) {

        WeatherAPI.placeIdTask asyncTask = new WeatherAPI.placeIdTask(new WeatherAPI.AsyncResponse() {

            public void processFinish(Weather latestWeather) {

                weather = latestWeather;

                setLastUpdated();
                updateUI(weather);
                showProgress(false);

            }
        });

        asyncTask.execute(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude())); //  asyncTask.execute("Latitude", "Longitude")

    }

    private void updateUI(Weather latestWeather) {

        weather = latestWeather;

        if (weather.getError().length() > 0) {

            showError(weather.getError(), false);

        } else {

            textViewCity.setText(weather.getCity());
            textViewTemperature.setText(weather.getTemperature());
            textViewWeather.setText(weather.getDescription());

            int drawableResourceId = getResources().getIdentifier(getWeatherIconName(weather.getId()), "drawable", getPackageName());
            imageViewWeather.setImageResource(drawableResourceId);
            imageViewWeather.setTag(drawableResourceId);

        }

        textViewLastUpdated.setText(weather.getLastUpdated());

    }

    private String getWeatherIconName(int condition) {

        String iconName = "dunno";

        if (condition <= 300) {

            iconName = "tstorm1";

        } else if (condition <= 500) {

            iconName = "light_rain";

        } else if (condition <= 600) {

            iconName = "shower3";

        } else if (condition >= 701 && condition <= 771) {

            iconName = "fog";

        }  else if (condition >= 772 && condition <= 779) {

            iconName = "tstorm3";

        } else if (condition == 800) {

            iconName = "sunny";

        } else if (condition >= 801 && condition <= 804) {

            iconName = "cloudy2";

        } else if (condition >= 900 && condition <= 903) {

            iconName = "tstorm3";

        } else if (condition >= 905 && condition <= 1000) {

            iconName = "tstorm3";

        } else if (condition == 903) {

            iconName = "snow5";

        }
        else if (condition == 904) {

            iconName = "sunny";

        }

        return(iconName);

    }

    public void onUpdateButtonClick(View v) {

        getWeatherForCurrentLocation();

    }

    private void showProgress(boolean show) {

        if (progressBarLayout != null) {

            progressBarLayout.setVisibility((!show) ? View.INVISIBLE : View.VISIBLE);
            buttonUpdate.setEnabled(!show);
            imageViewWeather.setVisibility((show) ? View.INVISIBLE : View.VISIBLE);
            textViewError.setVisibility(View.INVISIBLE);
            textViewError.setText("");

            if (show) {

                textViewCity.setText("");
                textViewTemperature.setText("");
                textViewWeather.setText("Searching...");
                imageViewWeather.setImageDrawable(null);

            }
        }

    }

    private void showError(String error, Boolean update) {

        if (update) {

            setLastUpdated();
        }

        showProgress(false);

        weather.setError(error);
        textViewError.setText(error);
        textViewError.setVisibility(View.VISIBLE);
        textViewLastUpdated.setText(weather.getLastUpdated());

    }

    private void showError(String error) {

        setLastUpdated();
        showError(error, true);
    }

    private void setLastUpdated() {

        SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy 'at' HH:mm:ss",
                Locale.getDefault());

        weather.setLastUpdated(sdfDate.format(new Date()));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                getWeatherForCurrentLocation();

            } else {

                showError("Location permission not setup.");

            }
        }
    }

}
