package com.social.gal.xdailer.Utils;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by Yahav on 1/18/2018.
 */

public class AudioHelper {

    private static AudioManager audioManager;

    public AudioHelper(Context mContext) {
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public AudioHelper(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    public void enableSpeaker() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        sleep(2000);
                        audioManager.setMode(AudioManager.MODE_IN_CALL);
                        if (!audioManager.isSpeakerphoneOn())
                            audioManager.setSpeakerphoneOn(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }
}
