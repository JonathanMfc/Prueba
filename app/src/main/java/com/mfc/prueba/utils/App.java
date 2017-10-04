package com.mfc.prueba.utils;

import android.app.Application;

import com.mfc.prueba.broadcast.ConnectivityReceiver;
import com.mfc.prueba.broadcast.GpsReceiver;

/**
 * Created by USUARIO on 03/10/2017.
 */

public class App extends Application {


    private static App mInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public void setGpsListener(GpsReceiver.GpsReceiverListerner listener) {
        GpsReceiver.gpsReceiverListener = listener;
    }

}