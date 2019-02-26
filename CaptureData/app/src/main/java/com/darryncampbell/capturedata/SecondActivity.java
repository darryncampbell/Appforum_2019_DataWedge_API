package com.darryncampbell.capturedata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import static com.darryncampbell.capturedata.DataWedgeUtilities.DATAWEDGE_SCAN_ACTION;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnFloor;
    private TextView txtBarcode;
    private TextView txtScanningStatus;
    private TextView txtActiveProfile;
    IntentFilter filter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        btnFloor = findViewById(R.id.btnFloor);
        txtBarcode = findViewById(R.id.txtBarcode2);
        txtScanningStatus = findViewById(R.id.txtScanningStatus2);
        txtActiveProfile = findViewById(R.id.txtActiveProfile2);
        btnFloor.setOnClickListener(this);

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
    }

    private BroadcastReceiver dwBroadcastReceiver = new BroadcastReceiver() {
        private static final String EXTRA_RESULT_GET_ACTIVE_PROFILE = "com.symbol.datawedge.api.RESULT_GET_ACTIVE_PROFILE";
        private static final String EXTRA_RESULT_GET_SCANNER_STATUS = "com.symbol.datawedge.api.RESULT_SCANNER_STATUS";
        private static final String ACTION_RESULT_NOTIFICATION = "com.symbol.datawedge.api.NOTIFICATION_ACTION";
        private static final String EXTRA_RESULT_NOTIFICATION = "com.symbol.datawedge.api.NOTIFICATION";

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
                }
                else if (intent.hasExtra(EXTRA_RESULT_GET_SCANNER_STATUS))
                {
                    String scannerStatus = intent.getStringExtra(EXTRA_RESULT_GET_SCANNER_STATUS);
                    txtScanningStatus.setText(scannerStatus);
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
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnFloor)
        {
            Intent firstActivity = new Intent(this, MainActivity.class);
            firstActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(firstActivity);
        }

    }
}
