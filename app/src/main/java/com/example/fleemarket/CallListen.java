package com.example.fleemarket;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallListen extends BroadcastReceiver {
    private Context context;
    private boolean pause;
    private String state;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context= context;


        this.state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        Intent musicIntent = new Intent(context,Srvmusic.class);

        Log.d("zzz","Enter rcv");
        if (this.state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            Log.d("zzz", "Ring");

                context.stopService(musicIntent);

        }
        if (this.state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            Log.d("zzz", "IDLE");
            if ( !isMyServiceRunning(Srvmusic.class)&&!pause)
                context.startService(musicIntent);
        }


    }

    /**
     * check if the service is running
     * in order to not run it while its running already
     * @param serviceClass the service its checking if running
     * @return boolean if the service is running
     */
    private boolean isMyServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * used when activity is on pause
     * in order to stop the service
     */
    public void stop()
    {
        this.pause=true;
    }
    /**
     * used when activity is on Resume
     * in order to start the service
     */
    public void start()
    {
        this.pause=false;
    }
}