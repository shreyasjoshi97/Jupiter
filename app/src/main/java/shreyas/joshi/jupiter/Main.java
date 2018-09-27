package shreyas.joshi.jupiter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ScrollView;
import android.widget.TextView;

public class Main extends AppCompatActivity {
    DeviceInfo deviceInfo;
    ApplicationInfo applicationInfo;

    ScrollView scrollLogs;

    TextView txtVersion;
    TextView txtOSVersion;
    TextView txtSecurityPatch;
    TextView txtWifiSecurity;
    TextView txtBatteryHealth;
    TextView txtRAMUsage;
    TextView txtLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVersionNumber();

        txtVersion = findViewById(R.id.txtVersion);
        txtOSVersion = findViewById(R.id.txtOSVersion);
        txtSecurityPatch = findViewById(R.id.txtSecurityPatch);
        txtWifiSecurity = findViewById(R.id.txtWiFiSecurity);
        txtBatteryHealth = findViewById(R.id.txtBatteryHealth);
        txtRAMUsage = findViewById(R.id.txtRAMUsage);
        txtLogs = findViewById(R.id.txtLogs);

        scrollLogs = findViewById(R.id.scrollLogs);
        scrollLogs.post(new Runnable() {
            @Override
            public void run() {
                scrollLogs.smoothScrollTo(0, txtLogs.getTop());
            }
        });

        deviceInfo = new DeviceInfo(getApplicationContext());
        showDeviceInfo();

        applicationInfo = new ApplicationInfo(getApplicationContext(), deviceInfo);
        showApplicationInfo();
    }

    /**
     * Sets version number of the application for display
     * Format: MAJOR.MINOR.PATCH-BUILD
     */
    public void setVersionNumber()
    {
        String versionNumber = "0.2.0";
        String buildNumber = "180927";
        TextView txtVersion = findViewById(R.id.txtVersion);
        txtVersion.setText(versionNumber + "-" + buildNumber);
    }

    /**
     * Retrieves device information
     * OS Version, Security patch, Wi-Fi security, RAM usage, Battery health
     */
    private void showDeviceInfo()
    {
        txtOSVersion.setText(deviceInfo.versionName);
        txtSecurityPatch.setText(deviceInfo.getSecurityPatch());
        txtWifiSecurity.setText(deviceInfo.wifiSecurity);
        txtRAMUsage.setText(deviceInfo.getRamUsage());
        startReceiverBatteryHealth();
    }

    private void showApplicationInfo()
    {
        //applicationInfo.getInstalledApplications();
        //applicationInfo.getRunningApplications();
        txtLogs.setText(applicationInfo.getProcessLogs());
    }

    public void startReceiverBatteryHealth()
    {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                txtBatteryHealth.setText(deviceInfo.getBatteryHealth(intent));
            }
        };
        Main.this.registerReceiver(broadcastReceiver, intentFilter);
    }

    /*public void stopReceiverBatteryHealth(BroadcastReceiver broadcastReceiver)
    {
        Main.this.unregisterReceiver(broadcastReceiver);
    }*/
}
