package com.darryncampbell.configuredatawedge;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import static com.darryncampbell.configuredatawedge.DataWedgeUtilities.ACTION_RESULT_DATAWEDGE_FROM_6_2;
import static com.darryncampbell.configuredatawedge.DataWedgeUtilities.ACTION_RESULT_NOTIFICATION;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "ConfigureDataWedge";

    private ArrayList mScanners;
    private ArrayList mProfiles;

    //  DataWedge Tab
    private Switch switchEnableDisableDataWedge;
    private TextView textViewVersion;
    private TextView textViewStatus;
    private TextView textViewDisabledAppList;
    private TextView textViewLastCallResult;
    private Button btnConfigureDataWedge;
    private Button btnResetDataWedge;

    //  Profiles / Scanners tab
    private RecyclerView scannersView;
    private RecyclerView.Adapter scannersAdapter;
    private RecyclerView.LayoutManager scannersLayoutManager;
    private RecyclerView profilesView;
    private RecyclerView.Adapter profilesAdapter;
    private RecyclerView.LayoutManager profilesLayoutManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    profilesView.setVisibility(View.GONE);
                    scannersView.setVisibility(View.GONE);
                    showDataWedgeViewWidgets(true);
                    return true;
                case R.id.navigation_profiles:
                    profilesView.setVisibility(View.VISIBLE);
                    scannersView.setVisibility(View.GONE);
                    showDataWedgeViewWidgets(false);
                    return true;
                case R.id.navigation_scanners:
                    profilesView.setVisibility(View.GONE);
                    scannersView.setVisibility(View.VISIBLE);
                    showDataWedgeViewWidgets(false);
                    return true;
            }
            return false;
        }
    };

    private void showDataWedgeViewWidgets(boolean bShow) {
        TextView txtEnableDisableDataWedge = findViewById(R.id.txtEnableDisableDW);
        txtEnableDisableDataWedge.setVisibility(bShow ? View.VISIBLE : View.GONE);
        TextView txtVersion = findViewById(R.id.txtVersion);
        txtVersion.setVisibility(bShow ? View.VISIBLE : View.GONE);
        TextView txtStatus = findViewById(R.id.txtStatus);
        txtStatus.setVisibility(bShow ? View.VISIBLE : View.GONE);
        TextView txtDisabledApps = findViewById(R.id.txtDisabledApps);
        txtDisabledApps.setVisibility(bShow ? View.VISIBLE : View.GONE);
        TextView txtLastCallResult = findViewById(R.id.txtLastCallResult);
        txtLastCallResult.setVisibility(bShow ? View.VISIBLE : View.GONE);
        Switch switchEnableDisableDataWedge = findViewById(R.id.switchEnableDisableDW);
        switchEnableDisableDataWedge.setVisibility(bShow ? View.VISIBLE : View.GONE);
        TextView version = findViewById(R.id.version);
        version.setVisibility(bShow ? View.VISIBLE : View.GONE);
        TextView status = findViewById(R.id.status);
        status.setVisibility(bShow ? View.VISIBLE : View.GONE);
        TextView disabledApps = findViewById(R.id.disabledApps);
        disabledApps.setVisibility(bShow ? View.VISIBLE : View.GONE);
        TextView lastCallResult = findViewById(R.id.lastCallResult);
        lastCallResult.setVisibility(bShow ? View.VISIBLE : View.GONE);
        Button btnConfigureDW = findViewById(R.id.btnConfigureDW);
        btnConfigureDW.setVisibility(bShow ? View.VISIBLE : View.GONE);
        Button btnResetDW = findViewById(R.id.btnResetDW);
        btnResetDW.setVisibility(bShow ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        switchEnableDisableDataWedge = findViewById(R.id.switchEnableDisableDW);
        textViewVersion = findViewById(R.id.version);
        textViewStatus = findViewById(R.id.status);
        textViewDisabledAppList = findViewById(R.id.disabledApps);
        textViewLastCallResult = findViewById(R.id.lastCallResult);
        btnConfigureDataWedge = findViewById(R.id.btnConfigureDW);
        btnResetDataWedge = findViewById(R.id.btnResetDW);
        btnConfigureDataWedge.setOnClickListener(this);
        btnResetDataWedge.setOnClickListener(this);

        profilesView = (RecyclerView) findViewById(R.id.profilesView);
        profilesView.setHasFixedSize(true);
        profilesLayoutManager = new LinearLayoutManager(this);
        profilesView.setLayoutManager(profilesLayoutManager);
        mProfiles = new ArrayList<String>();
        profilesAdapter = new ProfilesListAdapter(mProfiles);
        profilesView.setAdapter(profilesAdapter);

        scannersView = (RecyclerView) findViewById(R.id.scannersView);
        scannersView.setHasFixedSize(true);
        scannersLayoutManager = new LinearLayoutManager(this);
        scannersView.setLayoutManager(scannersLayoutManager);
        mScanners = new ArrayList<Scanner>();
        scannersAdapter = new ScannerListAdapter(mScanners);
        scannersView.setAdapter(scannersAdapter);

        //  Receive messages from DataWedge
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RESULT_DATAWEDGE_FROM_6_2);//  DW 6.2
        filter.addAction(ACTION_RESULT_NOTIFICATION);      //  DW 6.3 for notifications
        filter.addCategory(Intent.CATEGORY_DEFAULT);    //  NOTE: this IS REQUIRED for DW6.2 and up!
        registerReceiver(myBroadcastReceiver, filter);

        DataWedgeUtilities.GetProfilesList(this);
        DataWedgeUtilities.EnumerateScanners(this);
        DataWedgeUtilities.GetDataWedgeStatus(this);
        DataWedgeUtilities.GetVersionInfo(this);
        DataWedgeUtilities.GetDisabledAppList(this);
    }

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        //  Strings associated with the DataWedge API used in received broadcasts:
        private static final String EXTRA_RESULT_GET_PROFILE_LIST = "com.symbol.datawedge.api.RESULT_GET_PROFILES_LIST";
        private static final String EXTRA_RESULT_ENUMERATE_SCANNERS = "com.symbol.datawedge.api.RESULT_ENUMERATE_SCANNERS";
        private static final String EXTRA_RESULT_GET_DATAWEDGE_STATUS = "com.symbol.datawedge.api.RESULT_GET_DATAWEDGE_STATUS";
        private static final String EXTRA_RESULT_GET_VERSION_INFO = "com.symbol.datawedge.api.RESULT_GET_VERSION_INFO";
        private static final String EXTRA_RESULT_GET_DISABLED_APP_LIST = "com.symbol.datawedge.api.RESULT_GET_DISABLED_APP_LIST";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();
            //  This is useful for debugging to verify the format of received intents from DataWedge
            for (String key : b.keySet())
            {
                Log.v(LOG_TAG, key);
            }
            if (action.equals(ACTION_RESULT_DATAWEDGE_FROM_6_2))
            {
                //  Process any result codes
                if (intent.hasExtra("RESULT"))
                {
                    if (intent.hasExtra("COMMAND"))
                    {
                        String result = intent.getStringExtra("RESULT");
                        String command = intent.getStringExtra("COMMAND");
                        String formattedCommand = command.substring(command.lastIndexOf('.')+1);
                        String info = "";
                        if (intent.hasExtra("RESULT_INFO"))
                        {
                            Bundle result_info = intent.getBundleExtra("RESULT_INFO");
                            String result_code = result_info.getString("RESULT_CODE");
                            if (result_code != null)
                                info = " - " + result_code;
                        }
                        textViewLastCallResult.setText(formattedCommand + ": " + result + info);

/*                        switch(command)
                        {
                            case EXTRA_SET_DISABLED_APP_LIST:
                                textViewLastCallResult.setText("Set Disabled App List: " + result);
                                //  Because of how I set it up, this command will also have a
                                //  command identifier (SET_DISABLED_APP_LIST_UI) but it is not used here.
                                break;
                            default:
                                textViewLastCallResult.setText(command + ": " + result + info);
                                break;
                        }
                        */
                    }
                }
                if (intent.hasExtra(EXTRA_RESULT_GET_PROFILE_LIST))
                {
                    //  6.2 API to GetProfileList
                    String[] profilesList = intent.getStringArrayExtra(EXTRA_RESULT_GET_PROFILE_LIST);
                    mProfiles.clear();
                    mProfiles.addAll(Arrays.asList(profilesList));
                    profilesAdapter.notifyDataSetChanged();
                }
                else if (intent.hasExtra(EXTRA_RESULT_GET_VERSION_INFO))
                {
                    //  6.3 API for GetVersionInfo
                    String SimulScanVersion  = "Not supported";
                    String[] ScannerFirmware = {"Not available"};
                    Bundle versionInformation = intent.getBundleExtra(EXTRA_RESULT_GET_VERSION_INFO);
                    String DWVersion = versionInformation.getString("DATAWEDGE");
                    String BarcodeVersion = versionInformation.getString("BARCODE_SCANNING");
                    String DecoderVersion = versionInformation.getString("DECODER_LIBRARY");
                    if(versionInformation.containsKey("SCANNER_FIRMWARE")){
                        ScannerFirmware = versionInformation.getStringArray("SCANNER_FIRMWARE");
                    }
                    if(versionInformation.containsKey("SIMULSCAN")) {
                        SimulScanVersion = versionInformation.getString("SIMULSCAN");
                    }
                    String userReadableVersion = "DataWedge: " + DWVersion;
                    String userReadableVersionVerbose = "DataWedge: " + DWVersion + ", Barcode: " + BarcodeVersion +
                            ", DecoderVersion: " + DecoderVersion + ", SimulScan: " + SimulScanVersion +
                            ", Scanner Firmware: ";
                    for (int i = 0; i < ScannerFirmware.length; i++)
                        userReadableVersionVerbose += ScannerFirmware[i] + " ";
                    textViewVersion.setText(userReadableVersion);
                    Log.i(LOG_TAG, "DataWedge Version info: " + userReadableVersion);
                }
                else if (intent.hasExtra(EXTRA_RESULT_ENUMERATE_SCANNERS))
                {
                    //  6.3 API to EnumerateScanners.  Note the format is very different from 6.0.
                    ArrayList<Bundle> returned_scanners = (ArrayList<Bundle>) intent.getSerializableExtra(EXTRA_RESULT_ENUMERATE_SCANNERS);
                    //  Enumerate Scanners (6.3) returns a bundle array.  Each bundle has the following keys:
                    //  SCANNER_CONNECTION_STATE
                    //  SCANNER_NAME
                    //  SCANNER_INDEX
                    //  SCANNER_IDENTIFIER  (6.5+ only)
                    mScanners.clear();
                    if ((returned_scanners != null) && (returned_scanners.size() >0))
                    {
                        for (Bundle scannerBundle : returned_scanners)
                        {
                            String scannerName = scannerBundle.getString("SCANNER_NAME");
                            int scannerIndex = scannerBundle.getInt("SCANNER_INDEX");
                            boolean scannerConnected = scannerBundle.getBoolean("SCANNER_CONNECTION_STATE");
                            String scannerId = "";
                            if (scannerBundle.getString("SCANNER_IDENTIFIER") != null)
                                scannerId = scannerBundle.getString("SCANNER_IDENTIFIER");
                            Scanner newScanner = new Scanner(scannerName, scannerId, scannerIndex, scannerConnected);
                            mScanners.add(newScanner);
                        }
                    }
                    scannersAdapter.notifyDataSetChanged();
                }
                else if(intent.hasExtra(EXTRA_RESULT_GET_DATAWEDGE_STATUS))
                {
                    String datawedgeStatus = intent.getStringExtra(EXTRA_RESULT_GET_DATAWEDGE_STATUS);
                    Log.i(LOG_TAG, "Datawedge status is: " + datawedgeStatus);
                    textViewStatus.setText(datawedgeStatus);
                    switchEnableDisableDataWedge.setOnCheckedChangeListener(null);
                    if (datawedgeStatus.equalsIgnoreCase("enabled"))
                    {
                        switchEnableDisableDataWedge.setChecked(true);
                    }
                    switchEnableDisableDataWedge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            DataWedgeUtilities.EnableDataWedge(isChecked, getApplicationContext());
                            DataWedgeUtilities.GetDataWedgeStatus(getApplicationContext());
                        }
                    });
                }
                else if (intent.hasExtra(EXTRA_RESULT_GET_DISABLED_APP_LIST))
                {
                    ArrayList<Bundle> disabledAppList = intent.getParcelableArrayListExtra(EXTRA_RESULT_GET_DISABLED_APP_LIST);
                    if (disabledAppList == null || disabledAppList.size() == 0)
                        textViewDisabledAppList.setText("No disabled apps");
                    else
                    {
                        String disabledApps = "";
                        for (Bundle bundle:disabledAppList)
                        {
                            String packageName = bundle.getString("PACKAGE_NAME");
                            ArrayList<String> activityList = new ArrayList<>();
                            activityList = bundle.getStringArrayList("ACTIVITY_LIST");
                            for(String activityName : activityList)
                            {
                                disabledApps += packageName + '\n' + "[" + activityName + "]" + '\n';
                            }
                        }
                        textViewDisabledAppList.setText(disabledApps);
                    }
                }
            }
        }
    };



    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnConfigureDW)
        {
            String profileNameActivity1 = "CaptureDataFrontOfStore";
            String profileNameActivity2 = "CaptureDataBackRoom";

            //  Create a profile for the data capture application
            DataWedgeUtilities.CreateProfile(profileNameActivity1, getApplicationContext());

            DataWedgeUtilities.SetProfileConfig(profileNameActivity1, "com.darryncampbell.capturedata",
                    ".MainActivity", "true", "true", "false", "false",
                    "0",false, this);

            //  Clone the profile for the data capture application
            String[] profilesBeingCloned = {profileNameActivity1, "cloned_profile"};
            DataWedgeUtilities.CloneProfile(profilesBeingCloned, this);

            //  Rename the profile
            String[] profilesBeingRenamed = {"cloned_profile", profileNameActivity2};
            DataWedgeUtilities.RenameProfile(profilesBeingRenamed, this);

            //  todo do some BDF on the data (optionally).  e.g. ignore the first character
            DataWedgeUtilities.SetProfileConfig(profileNameActivity2, "com.darryncampbell.capturedata",
                    ".SecondActivity", "false", "false", "true", "true",
                    "2", true, this);

            DataWedgeUtilities.GetProfilesList(getApplicationContext());
            Toast.makeText(getApplicationContext(), "DataWedge profiles have now been created", Toast.LENGTH_SHORT).show();
        }
        else if (view.getId() == R.id.btnResetDW)
        {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            DataWedgeUtilities.RestoreConfig(getApplicationContext());
                            DataWedgeUtilities.GetProfilesList(getApplicationContext());
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //  DataWedge was not reset
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("This will completely reset DataWedge.  Proceed?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    }
}
