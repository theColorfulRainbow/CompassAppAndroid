package com.example.compassexample;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Class responsible for updating the Compass Ui
 */
public class UpdateUI {
    String TAG = "UpdateUI";                                                                        // log TAG
    Activity activity;                                                                              // super Activity
    ImageView img_arrow;                                                                            // arrow image
    TextView txt_azimuth;                                                                           // bearing text
    int currentAzimuth;                                                                             // current Azimuth value displayed

    /**
     * Initializing the class and variables
     * @param activity: super activity reference
     */
    public UpdateUI(Activity activity){
        this.activity = activity;
        img_arrow = activity.findViewById(R.id.main_image_hands);
        txt_azimuth = activity.findViewById(R.id.sotw_label);
        currentAzimuth = 0;                                                                         // initialize as 0
    }

    /**
     * Function used to update the Ui
     * @param mAzimuth: azimuth value to be displayed
     * @return: String with azimuth value and direction
     */
    public String updateCompassUI(int mAzimuth){
        adjustArrow(mAzimuth);
        return adjustTxt(mAzimuth);
    }

    /**
     * Adjust arrow image
     * @param mAzimuth: value of orientation
     */
    private void adjustArrow(float mAzimuth) {
        img_arrow.setRotation(-mAzimuth);
    }

    /**
     * Adjust the text to be siplayed
     * @param mAzimuth: azimuth value
     * @return: String containing bearing and orientation
     */
    private String adjustTxt(int mAzimuth) {
        String result = mAzimuth+"° "+getNESW(mAzimuth);;
        txt_azimuth.setText(result);
        return result;
    }

/*
    private void adjustArrow(int azimuth) {
        Log.d(TAG, "will set rotation from " + currentAzimuth + " to "
                + azimuth);

        Animation an = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = azimuth;

        an.setDuration(100);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        img_arrow.startAnimation(an);
    }
*/


    /**
     * Find the orientation and given bearing
     * @param mAzimuth: int of current bearing
     * @return: string of orientation
     */
    private String getNESW(int mAzimuth) {
        String where = "--";
        if(mAzimuth >= 350 || mAzimuth <= 10){where = "N";}
        if(mAzimuth < 350 && mAzimuth > 280){where = "NW";}
        if(mAzimuth <= 280 && mAzimuth > 260){where = "W";}
        if(mAzimuth <= 260 && mAzimuth > 190){where = "SW";}
        if(mAzimuth <= 190 && mAzimuth > 170){where = "S";}
        if(mAzimuth <= 170 && mAzimuth > 100){where = "SE";}
        if(mAzimuth <= 100 && mAzimuth > 80){where = "E";}
        if(mAzimuth <= 80 && mAzimuth > 10){where = "NE";}
        return where;
    }
}
