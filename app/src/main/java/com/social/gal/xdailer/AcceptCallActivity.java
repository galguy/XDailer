package com.social.gal.xdailer;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.social.gal.xdailer.Utils.AudioHelper;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Yahav on 1/18/2018.
 */

public class AcceptCallActivity extends Activity {
    private static Logger logger = Logger.getLogger(String.valueOf(AcceptCallActivity.class));

    private static final String MANUFACTURER_HTC = "HTC";

    private KeyguardManager keyguardManager;
    private AudioManager audioManager;
    private CallStateReceiver callStateReceiver;
    private AudioHelper audioHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioHelper = new AudioHelper(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        registerCallStateReceiver();
        updateWindowFlags();
        acceptCall();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (callStateReceiver != null) {
            unregisterReceiver(callStateReceiver);
            callStateReceiver = null;
        }
    }

    private void registerCallStateReceiver() {
        callStateReceiver = new CallStateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(callStateReceiver, intentFilter);
    }

    private void updateWindowFlags() {
        if (keyguardManager.inKeyguardRestrictedInputMode()) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        } else {
            getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }
    }
    /*
    public void acceptCall() {
        if (Build.VERSION.SDK_INT >= 21) {
            Intent answerCalintent = new Intent(context, AcceptCallActivity.class);
            answerCalintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            answerCalintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(answerCalintent);
        } else {
            Intent intent = new Intent(context, AcceptCallActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
    */

    private void acceptCall() {

        // for HTC devices we need to broadcast a connected headset
        boolean broadcastConnected = MANUFACTURER_HTC.equalsIgnoreCase(Build.MANUFACTURER)
                && !audioManager.isWiredHeadsetOn();

        if (broadcastConnected) {
            broadcastHeadsetConnected(false);
        }

        try {

            try {
                // logger.debug("execute input keycode headset hook");
                Runtime.getRuntime().exec("input keyevent " +
                        Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));

            } catch (IOException e) {
                // Runtime.exec(String) had an I/O problem, try to fall back
                //    logger.debug("send keycode headset hook intents");
                String enforcedPerm = "android.permission.CALL_PRIVILEGED";
                Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                                KeyEvent.KEYCODE_HEADSETHOOK));
                Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                                KeyEvent.KEYCODE_HEADSETHOOK));

                sendOrderedBroadcast(btnDown, enforcedPerm);
                sendOrderedBroadcast(btnUp, enforcedPerm);
            }
        } finally {
            if (broadcastConnected) {
                broadcastHeadsetConnected(false);
            }
        }
    }

    private void broadcastHeadsetConnected(boolean connected) {
        Intent i = new Intent(Intent.ACTION_HEADSET_PLUG);
        i.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        i.putExtra("state", connected ? 1 : 0);
        i.putExtra("name", "mysms");
        try {
            sendOrderedBroadcast(i, null);
            // enable speaker
            audioHelper.enableSpeaker();
        } catch (Exception e) {
        }
    }

    public static class CallStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent incomingCallIntent = new Intent(context,AcceptCallActivity.class);
            incomingCallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(incomingCallIntent);
        }
    }
}
