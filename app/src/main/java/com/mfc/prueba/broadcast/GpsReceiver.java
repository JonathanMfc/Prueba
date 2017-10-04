package com.mfc.prueba.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.mfc.prueba.utils.App;

/**
 * Created by USUARIO on 03/10/2017.
 */

public class GpsReceiver extends BroadcastReceiver {


    public static GpsReceiverListerner gpsReceiverListener;

    public GpsReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        LocationManager manager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
        boolean isEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);


        if (gpsReceiverListener != null){
            gpsReceiverListener.onGpsConnectionChanged(isEnabled);
        }

    }

    public static boolean isEnabled(){
        LocationManager manager = (LocationManager) App.getInstance().getApplicationContext().getSystemService( Context.LOCATION_SERVICE );
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public interface GpsReceiverListerner{
        void onGpsConnectionChanged(boolean isEnabled);
    }
}
