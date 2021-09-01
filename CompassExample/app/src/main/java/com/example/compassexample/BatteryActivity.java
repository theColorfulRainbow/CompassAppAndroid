package com.example.compassexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.TextView;

import java.math.BigDecimal;

/**
 * Battery activity
 */
public class BatteryActivity extends AppCompatActivity {
    private IntentFilter ifilter;
    TextView batteryInof_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);
        batteryInof_textView = findViewById(R.id.batteryInfo_textView);

        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);                                  // selects the Intent broadcasts to be received.

        BroadcastReceiver mBatInfoReciver = new BroadcastReceiver() {
            // Variables
            public int BatteryL;                                                                    // level
            public int BatteryV;                                                                    // voltage
            public int BatteryT;                                                                    // temperature
            public String BatteryTe;                                                                // technology
            public String BatteryStatus;                                                            // status
            public String BatteryHealth;                                                            // health
            public String BatteryPlugged;                                                           // plugged

            @Override
            /**
             * Get battery information and display it
             */
            public void onReceive(Context context, Intent intent) {                                 // method used to display battery status
                String action = intent.getAction();
                if(Intent.ACTION_BATTERY_CHANGED.equals(action)){
                    final String ACTION_BATTERY_CHANGED;
                    BatteryL = intent.getIntExtra("level",0);
                    BatteryV = intent.getIntExtra("voltage",0);
                    BatteryT = intent.getIntExtra("temperature",0);
                    BatteryTe = intent.getStringExtra("technology");
                }
                // CHARGE CASES
                switch (intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN)){
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        BatteryStatus = "Charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        BatteryStatus = "Discharging";
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        BatteryStatus = "Not Charging ";
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        BatteryStatus = "Fully Charged";
                        break;
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        BatteryStatus = "UNKNOWN Status";
                        break;
                }
                // HEALTH CASES
                switch (intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN)){
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        BatteryHealth = "Good status";
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        BatteryHealth = "Dead status";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        BatteryHealth = "Over Voltage ";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        BatteryHealth = "Overheat";
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        BatteryHealth = "UNKNOWN Status";
                        break;
                }
                // info of power resources
                switch (intent.getIntExtra("plugged",0)){
                    case BatteryManager.BATTERY_PLUGGED_AC :
                        BatteryPlugged = "Plugged to AC";
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        BatteryPlugged = "Plugged to USB";
                        break;
                    case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                        BatteryPlugged = "Plugged to Wireless";
                        break;
                    default:
                        BatteryPlugged = "UNKNOWN";
                }

                batteryInof_textView.setText( "Battery Level: " + BatteryL + "%" + "\n" + "\n"+
                        "Battery Status: " + BatteryStatus   + "\n" + "\n"+
                        "Battery Plugged: " + BatteryPlugged + "\n" + "\n"+
                        "Battery Health: " + BatteryHealth + "\n" + "\n"+
                        "Battery Voltage: " + BatteryV/1000 + "V" + "\n" + "\n"+
                        "Battery Temperature: " +  round((float) (BatteryT*0.1),2) + " Celsius" + "\n" + "\n"+
                        "Battery Technology: " + BatteryTe);
            }
        };
        registerReceiver(mBatInfoReciver,ifilter);
    }

    /**
     * Choose how many decimal places to display in a float
     * @param d: float to be dipslayed
     * @param decimalPlace: how manu decimal places
     * @return: float up to n decimal places
     */
    private static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }
}