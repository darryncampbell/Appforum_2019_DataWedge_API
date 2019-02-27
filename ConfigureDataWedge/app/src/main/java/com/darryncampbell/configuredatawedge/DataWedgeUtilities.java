package com.darryncampbell.configuredatawedge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class DataWedgeUtilities {

    //  DataWedge API
    public static final String ACTION_DATAWEDGE_FROM_6_2 = "com.symbol.datawedge.api.ACTION";
    public static final String ACTION_RESULT_DATAWEDGE_FROM_6_2 = "com.symbol.datawedge.api.RESULT_ACTION";
    public static final String ACTION_RESULT_NOTIFICATION = "com.symbol.datawedge.api.NOTIFICATION_ACTION";
    private static final String EXTRA_GET_PROFILES_LIST = "com.symbol.datawedge.api.GET_PROFILES_LIST";
    private static final String EXTRA_ENUMERATESCANNERS_FROM_6_3 = "com.symbol.datawedge.api.ENUMERATE_SCANNERS";
    private static final String EXTRA_GET_DATAWEDGE_STATUS = "com.symbol.datawedge.api.GET_DATAWEDGE_STATUS";
    private static final String EXTRA_ENABLE_DATAWEDGE = "com.symbol.datawedge.api.ENABLE_DATAWEDGE";
    private static final String EXTRA_GET_VERSION_INFO = "com.symbol.datawedge.api.GET_VERSION_INFO";
    private static final String EXTRA_GET_DISABLED_APP_LIST = "com.symbol.datawedge.api.GET_DISABLED_APP_LIST";
    private static final String EXTRA_RESTORE_CONFIG = "com.symbol.datawedge.api.RESTORE_CONFIG";
    private static final String EXTRA_SET_CONFIG = "com.symbol.datawedge.api.SET_CONFIG";
    private static final String EXTRA_CLONE_PROFILE = "com.symbol.datawedge.api.CLONE_PROFILE";
    private static final String EXTRA_RENAME_PROFILE = "com.symbol.datawedge.api.RENAME_PROFILE";
    private static final String EXTRA_CREATE_PROFILE = "com.symbol.datawedge.api.CREATE_PROFILE";


    public static void CreateProfile(String profileName, Context context)
    {
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_CREATE_PROFILE, profileName, context);
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_GET_PROFILES_LIST, "", context);
    }

    public static void GetProfilesList(Context context) {
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_GET_PROFILES_LIST, "", context);
    }

    public static void EnumerateScanners(Context context) {
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_ENUMERATESCANNERS_FROM_6_3, "", context);
    }

    public static void GetDataWedgeStatus(Context context) {
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_GET_DATAWEDGE_STATUS, "", context);
    }

    public static void GetVersionInfo(Context context) {
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_GET_VERSION_INFO, "", context);
    }

    public static void GetDisabledAppList(Context context) {
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_GET_DISABLED_APP_LIST, "", context);
    }

    public static void EnableDataWedge(boolean isChecked, Context context) {
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_ENABLE_DATAWEDGE, isChecked, context);
    }

    public static void SetProfileConfig(String profileName, String packageName, String activityName,
                                        String ean8, String ean13, String code39, String code128, String picklist,
                                        boolean setBDF, Context context) {
        Bundle profileConfig = new Bundle();
        profileConfig.putString("PROFILE_NAME", profileName);
        profileConfig.putString("PROFILE_ENABLED", "true");
        profileConfig.putString("CONFIG_MODE", "UPDATE");
        Bundle appConfig = new Bundle();
        appConfig.putString("PACKAGE_NAME", packageName);
        appConfig.putStringArray("ACTIVITY_LIST", new String[]{packageName + activityName});
        profileConfig.putParcelableArray("APP_LIST", new Bundle[]{appConfig});

        Bundle barcodeConfig = new Bundle();
        barcodeConfig.putString("PLUGIN_NAME", "BARCODE");
        barcodeConfig.putString("RESET_CONFIG", "true");
        Bundle barcodeProps = new Bundle();
        // Can either use scanner_selection here or scanner_selection_by_identifier
        barcodeProps.putString("scanner_selection", "auto");   //  Requires DW 6.4
        barcodeProps.putString("scanner_input_enabled", "true");
        barcodeProps.putString("decoder_code128", code128);
        barcodeProps.putString("decoder_code39", code39);
        barcodeProps.putString("decoder_ean8", ean8);
        barcodeProps.putString("decoder_ean13", ean13);
        barcodeProps.putString("picklist", picklist);
        barcodeConfig.putBundle("PARAM_LIST", barcodeProps);
        // Note: DW 6.6 supports the ability to define multiple plugin configs in the same intent but we separate it into multiple calls to be compatible with earlier versions, from 6.4
        profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig);
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_SET_CONFIG, profileConfig, context);

        profileConfig.remove("APP_LIST");
        profileConfig.remove("PLUGIN_CONFIG");
        Bundle intentConfig = new Bundle();
        intentConfig.putString("PLUGIN_NAME", "INTENT");
        intentConfig.putString("RESET_CONFIG", "true");
        Bundle intentProps = new Bundle();
        intentProps.putString("intent_output_enabled", "true");
        intentProps.putString("intent_action", "com.darryncampbell.datacapture.ACTION");
        intentProps.putString("intent_delivery", "2");
        intentConfig.putBundle("PARAM_LIST", intentProps);
        profileConfig.putBundle("PLUGIN_CONFIG", intentConfig);
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_SET_CONFIG, profileConfig, context);

        if (setBDF)
        {
            //  Set Basic Data formatting
            Bundle bdfConfig = new Bundle();
            bdfConfig.putString("PLUGIN_NAME", "BDF");
            bdfConfig.putString("RESET_CONFIG", "true");
            bdfConfig.putString("OUTPUT_PLUGIN_NAME", "INTENT");
            Bundle bdfProps = new Bundle();
            bdfProps.putString("bdf_enabled", "true");
            bdfProps.putString("bdf_prefix", "x");
            bdfConfig.putBundle("PARAM_LIST", bdfProps);
            profileConfig.putBundle("PLUGIN_CONFIG", bdfConfig);
            sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_SET_CONFIG, profileConfig, context);
        }

    }

    public static void CloneProfile(String[] profilesBeingCloned, Context context) {
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_CLONE_PROFILE, profilesBeingCloned, context);
    }

    public static void RenameProfile(String[] profilesBeingRenamed, Context context) {
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_RENAME_PROFILE, profilesBeingRenamed, context);
    }

    public static void RestoreConfig(Context context) {
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE_FROM_6_2, EXTRA_RESTORE_CONFIG, "", context);
    }

    private static void sendDataWedgeIntentWithExtra(String action, String extraKey, String extraValue, Context context)
    {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extraValue);
        //  Could also specify 'COMPLETE_RESULT' if you want the result from every plugin you specify in the same call
        dwIntent.putExtra("SEND_RESULT", "TRUE");
        //dwIntent.putExtra("COMMAND_IDENTIFIER", "123");
        dwIntent.putExtra("SEND_RESULT", "LAST_RESULT");  //  7.1+ only
        context.sendBroadcast(dwIntent);
    }

    private static void sendDataWedgeIntentWithExtra(String action, String extraKey, boolean extraValue, Context context)
    {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extraValue);
        //  Could also specify 'COMPLETE_RESULT' if you want the result from every plugin you specify in the same call
        dwIntent.putExtra("SEND_RESULT", "TRUE");
        //dwIntent.putExtra("COMMAND_IDENTIFIER", "123");
        //dwIntent.putExtra("SEND_RESULT", "LAST_RESULT");  //  7.1+ only
        context.sendBroadcast(dwIntent);
    }

    private static void sendDataWedgeIntentWithExtra(String action, String extraKey, Bundle extraValue, Context context)
    {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extraValue);
        //  Could also specify 'COMPLETE_RESULT' if you want the result from every plugin you specify in the same call
        dwIntent.putExtra("SEND_RESULT", "TRUE");
        //dwIntent.putExtra("COMMAND_IDENTIFIER", "123");
        //dwIntent.putExtra("SEND_RESULT", "LAST_RESULT");  //  7.1+ only
        context.sendBroadcast(dwIntent);
    }

    private static void sendDataWedgeIntentWithExtra(String action, String extraKey, String[] extraValue, Context context)
    {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extraValue);
        //  Could also specify 'COMPLETE_RESULT' if you want the result from every plugin you specify in the same call
        dwIntent.putExtra("SEND_RESULT", "TRUE");
        //dwIntent.putExtra("COMMAND_IDENTIFIER", "123");
        //dwIntent.putExtra("SEND_RESULT", "LAST_RESULT");  //  7.1+ only
        context.sendBroadcast(dwIntent);
    }


}
