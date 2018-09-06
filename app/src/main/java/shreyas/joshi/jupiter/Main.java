package shreyas.joshi.jupiter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Main extends AppCompatActivity {

    private TextView mTextMessage;
    private DeviceInfo deviceInfo;

    TextView txtVersion;
    TextView txtOSVersion;
    TextView txtSecurityPatch;
    TextView txtWifiSecurity;
    TextView txtBatteryHealth;
    TextView txtRAMUsage;

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

        deviceInfo = new DeviceInfo(getApplicationContext());
        showDeviceInfo();
    }

    /**
     * Sets version number of the application for display
     * Format: MAJOR.MINOR.PATCH-BUILD
     */
    public void setVersionNumber()
    {
        String versionNumber = "0.1.1";
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        String buildNumber = dateFormat.format(date);
        TextView txtVersion = findViewById(R.id.txtVersion);
        txtVersion.setText(versionNumber + "-" + buildNumber);
    }

    /**
     * Retrieves device information
     * OS Version, Security patch, Wi-Fi security, RAM usage, Battery health
     */
    private void showDeviceInfo()
    {
        txtOSVersion.setText(deviceInfo.getOSVersion());
        txtSecurityPatch.setText(deviceInfo.getSecurityPatch());
        txtWifiSecurity.setText(deviceInfo.getWifiSecurity());
        txtRAMUsage.setText(deviceInfo.getRamUsage());
        startReceiverBatteryHealth();
        deviceInfo.getInstalledApplications();
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
