package com.darryncampbell.capturedata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import static com.darryncampbell.capturedata.DataWedgeUtilities.DATAWEDGE_SCAN_ACTION;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private ImageButton btnMuteScanner;
    private ImageButton btnSwitchScanner;
    private ImageButton btnDisableScanner;
    private ImageButton btnScan;
    private ImageButton btnRoom;
    private boolean scannerSilenced = false;
    private boolean scannerIsDisabled = false;
    private boolean cameraScanner = false;
    private TextView txtBarcode;
    private TextView txtScanningStatus;
    private TextView txtActiveProfile;
    private TextView txtScannerInfo;
    IntentFilter filter = new IntentFilter();
    private String mProfileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnMuteScanner = findViewById(R.id.btnMuteScanner);
        btnSwitchScanner = findViewById(R.id.btnSwitchScanner);
        btnDisableScanner = findViewById(R.id.btnDisableScanner);
        btnRoom = findViewById(R.id.btnRoom);
        btnScan = findViewById(R.id.btnScan);
        btnMuteScanner.setOnClickListener(this);
        btnSwitchScanner.setOnClickListener(this);
        btnDisableScanner.setOnClickListener(this);
        btnScan.setOnTouchListener(this);
        btnRoom.setOnClickListener(this);
        txtBarcode = findViewById(R.id.txtBarcode);
        txtScanningStatus = findViewById(R.id.txtScanningStatus);
        txtActiveProfile = findViewById(R.id.txtActiveProfile);
        txtScannerInfo = findViewById(R.id.txtScannerInfo);
        mProfileName = "not initialised";

        filter.addAction(DataWedgeUtilities.ACTION_RESULT_DATAWEDGE_FROM_6_2);//  DW 6.2
        filter.addAction(DataWedgeUtilities.ACTION_RESULT_NOTIFICATION);      //  DW 6.3 for notifications
        filter.addAction(DATAWEDGE_SCAN_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);    //  NOTE: this IS REQUIRED for DW6.2 and up!
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //  Register for broadcasts from DataWedge
        registerReceiver(dwBroadcastReceiver, filter);
        DataWedgeUtilities.GetActiveProfile(this);
        DataWedgeUtilities.GetScannerStatus(this);
        DataWedgeUtilities.RegisterForNotification(this, getPackageName());

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //  Unregister for broadcasts from DataWedge
        unregisterReceiver(dwBroadcastReceiver);
        DataWedgeUtilities.UnregisterForNotification(this, getPackageName());
        //  Reset the activity UI since leaving this profile will reset the temporary parameters
        scannerSilenced = false;
        scannerIsDisabled = false;
        cameraScanner = false;
        btnMuteScanner.setImageDrawable(getDrawable(R.drawable.ic_mute));
        btnSwitchScanner.setImageDrawable(getDrawable(R.drawable.ic_camera_barcode));
        btnScan.setBackground(getDrawable(R.drawable.scan_border_enabled));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnMuteScanner)
        {
            scannerSilenced = !scannerSilenced;
            if (scannerSilenced)
            {
                btnMuteScanner.setImageDrawable(getDrawable(R.drawable.ic_volume));
                DataWedgeUtilities.ChangeScannerSound(this, true);
            }
            else
            {
                btnMuteScanner.setImageDrawable(getDrawable(R.drawable.ic_mute));
                DataWedgeUtilities.ChangeScannerSound(this, false);
            }
        }
        else if (view.getId() == R.id.btnSwitchScanner)
        {
            cameraScanner = !cameraScanner;
            if (cameraScanner)
            {
                btnSwitchScanner.setImageDrawable(getDrawable(R.drawable.ic_focus));
                DataWedgeUtilities.SwitchScanner(getApplicationContext(), false);
            }
            else
            {

                btnSwitchScanner.setImageDrawable(getDrawable(R.drawable.ic_camera_barcode));
                DataWedgeUtilities.SwitchScanner(getApplicationContext(), true);
            }
        }
        else if (view.getId() == R.id.btnDisableScanner)
        {
            scannerIsDisabled = !scannerIsDisabled;
            if (scannerIsDisabled)
            {
                btnScan.setBackground(getDrawable(R.drawable.scan_border_disabled));
                DataWedgeUtilities.DisableScannerInputPlugin(getApplicationContext(), true);
            }
            else
            {
                btnScan.setBackground(getDrawable(R.drawable.scan_border_enabled));
                DataWedgeUtilities.DisableScannerInputPlugin(getApplicationContext(), false);
            }
        }
        else if (view.getId() == R.id.btnRoom)
        {
            Intent secondActivity = new Intent(this, SecondActivity.class);
            secondActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(secondActivity);
        }
    }

    private BroadcastReceiver dwBroadcastReceiver = new BroadcastReceiver() {
        private static final String EXTRA_RESULT_GET_ACTIVE_PROFILE = "com.symbol.datawedge.api.RESULT_GET_ACTIVE_PROFILE";
        private static final String EXTRA_RESULT_GET_SCANNER_STATUS = "com.symbol.datawedge.api.RESULT_SCANNER_STATUS";
        private static final String ACTION_RESULT_NOTIFICATION = "com.symbol.datawedge.api.NOTIFICATION_ACTION";
        private static final String EXTRA_RESULT_NOTIFICATION = "com.symbol.datawedge.api.NOTIFICATION";
        private static final String EXTRA_RESULT_GET_CONFIG = "com.symbol.datawedge.api.RESULT_GET_CONFIG";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DATAWEDGE_SCAN_ACTION))
            {
                txtBarcode.setText(intent.getStringExtra("com.symbol.datawedge.data_string"));
            }
            else if (action.equals(DataWedgeUtilities.ACTION_RESULT_DATAWEDGE_FROM_6_2))
            {
                if (intent.hasExtra(EXTRA_RESULT_GET_ACTIVE_PROFILE))
                {
                    String activeProfile = intent.getStringExtra(EXTRA_RESULT_GET_ACTIVE_PROFILE);
                    txtActiveProfile.setText("Profile: " + activeProfile);
                    mProfileName = activeProfile;
                    DataWedgeUtilities.GetScannerSetting(getApplicationContext(), mProfileName);
                }
                else if (intent.hasExtra(EXTRA_RESULT_GET_SCANNER_STATUS))
                {
                    String scannerStatus = intent.getStringExtra(EXTRA_RESULT_GET_SCANNER_STATUS);
                    txtScanningStatus.setText(scannerStatus);
                }
                else if (intent.hasExtra(EXTRA_RESULT_GET_CONFIG) && intent.hasExtra("PLUGIN_CONFIG"))
                {
                    //  Only returns the actual profile data, not the temporary profile data
                    Bundle result = intent.getBundleExtra(EXTRA_RESULT_GET_CONFIG);
                    ArrayList<Bundle> pluginConfig = result.getParcelableArrayList("PLUGIN_CONFIG");
                    //  In the call to Get_Config we only requested the barcode plugin config (which will be index 0)
                    Bundle barcodeProps = pluginConfig.get(0).getBundle("PARAM_LIST");
                    String ean13IsEnabled = barcodeProps.getString("decoder_ean13");
                    String userFriendlyText = "EAN13 is enabled? " + ean13IsEnabled;
                    txtScannerInfo.setText(userFriendlyText);
                }
            }
            else if (action.equals(ACTION_RESULT_NOTIFICATION))
            {
                //  6.3 API for RegisterForNotification
                if (intent.hasExtra(EXTRA_RESULT_NOTIFICATION))
                {
                    Bundle extras = intent.getBundleExtra(EXTRA_RESULT_NOTIFICATION);
                    String notificationType = extras.getString("NOTIFICATION_TYPE");
                    if (notificationType != null && notificationType.equals("SCANNER_STATUS"))
                    {
                        //  We have received a change in Scanner status
                        String userReadableScannerStatus = "Scanner status: " + extras.getString("STATUS") +
                                ", profile: " + extras.getString("PROFILE_NAME");
                        txtScanningStatus.setText(extras.getString("STATUS"));
                    }
                    else if (notificationType != null && notificationType.equals("PROFILE_SWITCH"))
                    {
                        //  The profile has changed (Note, this example app does NOT register for this)
                    }
                    else if (notificationType != null && notificationType.equals("CONFIGURATION_UPDATE"))
                    {
                        //  Future enhancement only
                    }
                }
            }
        }
    };

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            //  Scan button pressed
            DataWedgeUtilities.SoftScanTrigger(getApplicationContext(), true);
        }
        else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
        {
            //  Scan button released
            DataWedgeUtilities.SoftScanTrigger(getApplicationContext(), false);
        }
        return true;
    }
}
