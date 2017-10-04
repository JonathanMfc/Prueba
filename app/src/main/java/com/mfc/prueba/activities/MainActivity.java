package com.mfc.prueba.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mfc.prueba.BuildConfig;
import com.mfc.prueba.R;
import com.mfc.prueba.adapter.WeekWeatherAdapter;
import com.mfc.prueba.broadcast.ConnectivityReceiver;
import com.mfc.prueba.broadcast.GpsReceiver;
import com.mfc.prueba.model.ResponseApi;
import com.mfc.prueba.service.PruebaService;
import com.mfc.prueba.service.Service;
import com.mfc.prueba.utils.ProgressLottie;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, ConnectivityReceiver.ConnectivityReceiverListener, GpsReceiver.GpsReceiverListerner, EasyPermissions.PermissionCallbacks {

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Service service;
    ProgressLottie progressLottie;
    @BindView(R.id.tem_currently)
    TextView temCurrently;
    @BindView(R.id.icon_temp)
    ImageView iconTemp;
    @BindView(R.id.day)
    TextView day;
    @BindView(R.id.city)
    TextView city;
    @BindView(R.id.windSpeed)
    TextView windSpeed;
    @BindView(R.id.humidity)
    TextView humidity;
    @BindView(R.id.summary)
    TextView summary;
    @BindView(R.id.list_date)
    RecyclerView listDate;
    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;
    public static final int LOCATION_PERM = 0;
    String latlng;

    WeekWeatherAdapter weekWeatherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        progressLottie = new ProgressLottie(this);

        buildGoogleApiClient();
        checkConnection();
        checkGps();

        service = PruebaService.createService(Service.class);
        weekWeatherAdapter = new WeekWeatherAdapter(this);
        listDate.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        listDate.setAdapter(weekWeatherAdapter);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        latlng = mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();

        progressLottie.show();
        final SimpleDateFormat sdf = new SimpleDateFormat("EEEE,dd MMMM");


        Call<ResponseApi> responseApiCall = service.getWeather(BuildConfig.DARK_SKY_API_KEY, latlng);
        responseApiCall.enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                if (response.isSuccessful()) {
                    progressLottie.dismiss();
                    temCurrently.setText(String.valueOf(response.body().getCurrently().getTemperature()));

                    day.setText(sdf.format(new Date(response.body().getCurrently().getTime()* 1000L)));
                    city.setText(response.body().getTimezone());

                    windSpeed.setText(String.valueOf(response.body().getCurrently().getWindSpeed()));
                    humidity.setText(String.valueOf(response.body().getCurrently().getHumidity()));
                    summary.setText(response.body().getCurrently().getSummary());

                    weekWeatherAdapter.addAll(response.body().getDaily().getData());
                }
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {
                Log.e("error", t.getMessage());
            }
        });

        Toast.makeText(this, "ub " + mLastLocation.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @AfterPermissionGranted(LOCATION_PERM)
    protected synchronized void buildGoogleApiClient() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();


        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.location_perms), LOCATION_PERM, Manifest.permission.ACCESS_FINE_LOCATION);
        }

    }


    private void checkConnection() {

        boolean isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected) {
            alertDialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            alertDialogBuilder.setTitle(getApplicationContext().getString(R.string.errorInternet));
            alertDialogBuilder.setMessage(getApplicationContext().getString(R.string.no_internet));
            alertDialogBuilder.setCancelable(false);
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            //  alertDialog.dismiss();
        }

    }


    private void checkGps() {
        boolean isEnabled = GpsReceiver.isEnabled();
        if (!isEnabled) {
            alertDialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            alertDialogBuilder.setTitle(getApplicationContext().getString(R.string.error_gps));
            alertDialogBuilder.setMessage(getApplicationContext().getString(R.string.no_gps));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton(getApplicationContext().getString(R.string.habilitar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });

            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            //   alertDialog.dismiss();
        }
    }


    @Override
    public void onGpsConnectionChanged(boolean isEnabled) {

        if (!isEnabled) {
            alertDialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            alertDialogBuilder.setTitle(getApplicationContext().getString(R.string.error_gps));
            alertDialogBuilder.setMessage(getApplicationContext().getString(R.string.no_gps));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton(getApplicationContext().getString(R.string.habilitar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            alertDialog.dismiss();
        }

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            alertDialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            alertDialogBuilder.setTitle(getApplicationContext().getString(R.string.errorInternet));
            alertDialogBuilder.setMessage(getApplicationContext().getString(R.string.no_internet));
            alertDialogBuilder.setCancelable(false);
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        } else {
            alertDialog.dismiss();
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        EasyPermissions.requestPermissions(this, getString(R.string.location_perms), LOCATION_PERM, Manifest.permission.ACCESS_FINE_LOCATION);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

    }
}
