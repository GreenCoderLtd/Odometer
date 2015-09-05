package greencoder.com.odometer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class OdometerService extends Service {

    public static String LOG_TAG=OdometerService.class.getSimpleName();

    private IBinder binder=new OdometerBinder();
    private double distanceInMeters;
    private Location lastLocation;
    LocationListener listener;
    LocationManager locManager;

    public class OdometerBinder extends Binder
    {
        OdometerService getOdometer()
        {
            return OdometerService.this;
        }

    }

    @Override
    public void onCreate() {

        super.onCreate();

        Log.i(LOG_TAG,"Service Started");

        listener=new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                if(lastLocation==null)
                    lastLocation=location;

                distanceInMeters +=location.distanceTo(lastLocation);

                lastLocation=location;

                Log.i(LOG_TAG,"Location changed");
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        locManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1,listener);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public double getMiles()
    {
        return distanceInMeters/1609.344;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locManager.removeUpdates(listener);
        Log.i(LOG_TAG, "Service Destroyed");
    }
}
