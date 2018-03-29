package com.social.gal.xdailer.Recievers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.social.gal.xdailer.MainActivity;
import com.social.gal.xdailer.MainActivity_;
import com.social.gal.xdailer.Utils.AudioHelper;
import com.social.gal.xdailer.XDailerActivity;

/**
 * Created by Yahav on 1/16/2018.
 */

public class IncomingCall extends BroadcastReceiver {

    Context mContext;
    AudioHelper audioHelper;


    private enum  OPERATION_MODE {
        AUTO,
        MANUAL
    }

    private OPERATION_MODE appMode = OPERATION_MODE.AUTO;

    @Override
    public void onReceive(Context mContext, Intent intent) {

        audioHelper = new AudioHelper(mContext);

        try
        {

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING))
            {
                Toast.makeText(mContext, "Phone Is Ringing", Toast.LENGTH_LONG).show();
                // Your Code
                switch (appMode) {
                    case AUTO:
                        // Assume thisActivity is the current activity
                        int permissionCheck = ContextCompat.checkSelfPermission(mContext,
                                Manifest.permission.ANSWER_PHONE_CALLS);
                        /*if(permissionCheck == PackageManager.PERMISSION_DENIED) {
                            return;
                        }
                        */
                        // enable speaker after answering call
                        audioHelper.enableSpeaker();
                        /*
                        TelecomManager tm = (TelecomManager) mContext
                                .getSystemService(Context.TELECOM_SERVICE);

                        if (tm == null) {
                            // whether you want to handle this is up to you really
                            throw new NullPointerException("tm == null");
                        }
                        tm.acceptRingingCall();
                        */
                        break;
                    case MANUAL:
                        Intent incomingCallIntent = new Intent(mContext,XDailerActivity.class);
                        incomingCallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(incomingCallIntent);
                        break;
                }
            }

            if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
            {
                Toast.makeText(mContext, "Call Recieved", Toast.LENGTH_LONG).show();
                // Your Code
            }

            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
            {

                Toast.makeText(mContext, "Phone Is Idle", Toast.LENGTH_LONG).show();
                // Your Code

            }
        }
        catch(Exception e)
        {
            //your custom message
            Toast.makeText(mContext, "Failed to answer incoming call, mode is: " + appMode.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
