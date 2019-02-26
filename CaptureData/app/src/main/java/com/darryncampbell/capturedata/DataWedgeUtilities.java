package com.darryncampbell.capturedata;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class DataWedgeUtilities {
    //  DataWedge API
    public static final String ACTION_DATAWEDGE_FROM_6_2 = "com.symbol.datawedge.api.ACTION";
    public static final String ACTION_RESULT_DATAWEDGE_FROM_6_2 = "com.symbol.datawedge.api.RESULT_ACTION";
    public static final String ACTION_RESULT_NOTIFICATION = "com.symbol.datawedge.api.NOTIFICATION_ACTION";
    private static final String EXTRA_GET_ACTIVE_PROFILE = "com.symbol.datawedge.api.GET_ACTIVE_PROFILE";
    private static final String EXTRA_GET_SCANNER_STATUS = "com.symbol.datawedge.api.GET_SCANNER_STATUS";
    private static final String EXTRA_SWITCH_SCANNER_PARAMS = "com.symbol.datawedge.api.SWITCH_SCANNER_PARAMS";
    private static final String EXTRA_REGISTER_NOTIFICATION = "com.symbol.datawedge.api.REGISTER_FOR_NOTIFICATION";
    private static final String EXTRA_UNREGISTER_NOTIFICATION = "com.symbol.datawedge.api.UNREGISTER_FOR_NOTIFICATION";
    private static final String EXTRA_KEY_APPLICATION_NAME = "com.symbol.datawedge.api.APPLICATION_NAME";
    private static final String EXTRA_KEY_NOTIFICATION_TYPE = "com.symbol.datawedge.api.NOTIFICATION_TYPE";
    private static final String EXTRA_KEY_VALUE_SCANNER_STATUS = "SCANNER_STATUS";
    private static final String EXTRA_SOFTSCANTRIGGER_FROM_6_3 = "com.symbol.datawedge.api.SOFT_SCAN_TRIGGER";
    private static final String EXTRA_SCANNERINPUTPLUGIN_FROM_6_3 = "com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN";
    private static final String EXTRA_SWITCH_SCANNER_EX = "com.symbol.datawedge.api.SWITCH_SCANNER_EX";  //  DW 6.5

    public static final String DATAWEDGE_SCAN_ACTION = "com.darryncampbell.datacapture.ACTION";

    public static void GetActiveProfile(Context context)
    {
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_GET_ACTIVE_PROFILE, "", context);
    }

    public static void GetScannerStatus(Context context)
    {
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_GET_SCANNER_STATUS, "", context);
    }

    public static void ChangeScannerSound(Context context, boolean quieter)
    {
        Bundle barcodeProps = new Bundle();
        if (quieter)
            barcodeProps.putString("decode_audio_feedback_uri", "Altair");
        else
            barcodeProps.putString("decode_audio_feedback_uri", "optimized-beep");
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_SWITCH_SCANNER_PARAMS, barcodeProps, context);
    }

    public static void RegisterForNotification(Context context, String packageName)
    {
        Bundle extras = new Bundle();
        extras.putString(EXTRA_KEY_APPLICATION_NAME, packageName); //  The package name of this application
        extras.putString(EXTRA_KEY_NOTIFICATION_TYPE, EXTRA_KEY_VALUE_SCANNER_STATUS);  //  Register for changes to scanner status.  Could also register for profile switches
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_REGISTER_NOTIFICATION, extras, context);
    }

    public static void UnregisterForNotification(Context context, String packageName)
    {
        Bundle extras = new Bundle();
        extras.putString(EXTRA_KEY_APPLICATION_NAME, packageName); //  The package name of this application
        extras.putString(EXTRA_KEY_NOTIFICATION_TYPE, EXTRA_KEY_VALUE_SCANNER_STATUS);  //  Register for changes to scanner status.  Could also register for profile switches
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_UNREGISTER_NOTIFICATION, extras, context);
    }

    public static void SoftScanTrigger(Context context, boolean bScan) {
        if (bScan)
        {
            sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_SOFTSCANTRIGGER_FROM_6_3, "START_SCANNING", context);
        }
        else
        {
            sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_SOFTSCANTRIGGER_FROM_6_3, "STOP_SCANNING", context);
        }
    }

    public static void DisableScannerInputPlugin(Context context, boolean bDisableScanner) {
        if (bDisableScanner)
        {
            sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_SCANNERINPUTPLUGIN_FROM_6_3, "DISABLE_PLUGIN", context);
        }
        else
        {
            sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_SCANNERINPUTPLUGIN_FROM_6_3, "ENABLE_PLUGIN", context);
        }
    }

    public static void SwitchScanner(Context context, boolean bImager) {
        if (bImager)
        {
            sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_SWITCH_SCANNER_EX, "INTERNAL_IMAGER", context);
        }
        else
        {
            sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_SWITCH_SCANNER_EX, "INTERNAL_CAMERA", context);
        }
    }

    private static void sendDataWedgeIntentWithExtra(String action, String extraKey, String extraValue, Context context)
    {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extraValue);
        context.sendBroadcast(dwIntent);
    }

    private static void sendDataWedgeIntentWithExtra(String action, String extraKey, Bundle extras, Context context)
    {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extras);
        context.sendBroadcast(dwIntent);
    }

}
