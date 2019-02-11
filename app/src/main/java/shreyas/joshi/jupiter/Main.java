package shreyas.joshi.jupiter;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends AppCompatActivity {
    DeviceInfo deviceInfo;
    ApplicationInfo applicationInfo;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    ScrollView scrollLogs;
    TextView txtVersion;
    TextView txtOSVersion;
    TextView txtSecurityPatch;
    TextView txtWifiSecurity;
    TextView txtBatteryHealth;
    TextView txtRAMUsage;
    TextView txtLogs;
    TextView txtServerMsg;
    Button btnSend;

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
        txtServerMsg = findViewById(R.id.txtServerMsg);

        applicationInfo = new ApplicationInfo(getApplicationContext());
        showApplicationInfo();

        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Toast.makeText(getApplicationContext(), "DONE!", Toast.LENGTH_SHORT).show();
            }
        });

        scrollLogs = findViewById(R.id.scrollLogs);
        scrollLogs.post(new Runnable() {
            @Override
            public void run() {
                scrollLogs.smoothScrollTo(0, txtLogs.getTop());
            }
        });

        deviceInfo = new DeviceInfo(getApplicationContext());
        showDeviceInfo();

        setupJob();
    }

    /***
     *
     */
    public void setupJob()
    {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, BehaviourAnalysis.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                15000, pendingIntent);
    }

    /***
     * Sets version number of the application
     * Format: MAJOR.MINOR.PATCH-BUILD
     */
    public void setVersionNumber()
    {
        String versionNumber = "0.2.1";
        String buildNumber = "181124" +
                "";
        TextView txtVersion = findViewById(R.id.txtVersion);
        txtVersion.setText(versionNumber + "-" + buildNumber);
    }

    /***
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
        applicationInfo.getInstalledApplications(); //THIS LINE MUST ALWAYS GO BEFORE GET PERMISSIONS
        /*if(deviceInfo.getRoot())
        {
            txtLogs.setText(applicationInfo.getProcessLogs());
        }
        else
        {
            txtLogs.setText(applicationInfo.getPermissions());
        }
        applicationInfo.getRunningAppInfo();*/

        //applicationInfo.getRunningApplications();
        //txtLogs.setText(applicationInfo.createProcessLogs(""));
        //applicationInfo.getInstalledApplications();
        //txtLogs.setText(applicationInfo.getPermissions());
    }

    private void transmitInfo()
    {
        new SocketClient().execute("Test\n");
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
}
