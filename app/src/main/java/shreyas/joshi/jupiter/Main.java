package shreyas.joshi.jupiter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity implements UICallback {
    DeviceInfo deviceInfo;
    ApplicationInfo applicationInfo;
    AlarmManager alarmManager;
    //PendingIntent pendingIntent;
    List<AlarmManager> alarmManagers;
    List<PendingIntent> pendingIntents;

    ScrollView scrollLogs;
    TextView txtVersion;
    TextView txtOSVersion;
    TextView txtSecurityPatch;
    TextView txtWifiSecurity;
    TextView txtBatteryHealth;
    TextView txtRAMUsage;
    TextView txtActivity;
    TextView txtServerMsg;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtActivity = findViewById(R.id.txtLogs);
        alarmManagers = new ArrayList<AlarmManager>();
        pendingIntents = new ArrayList<PendingIntent>();

        txtActivity.setText("");

        applicationInfo = new ApplicationInfo(this.getApplicationContext(), this);
        showApplicationInfo();

        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "DONE!", Toast.LENGTH_SHORT).show();
                applicationInfo.sendLogs();
            }
        });

        scrollLogs = findViewById(R.id.scrollLogs);
        scrollLogs.post(new Runnable() {
            @Override
            public void run() {
                scrollLogs.smoothScrollTo(0, txtActivity.getTop());
            }
        });

        deviceInfo = new DeviceInfo(getApplicationContext());
        //showDeviceInfo();

        if(deviceInfo.isRooted)
        {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            setupJob("LogRecorder", LogRecorder.class);
            setupJob("DeltaRecorder", DeltaRecorder.class);
            setupJob("AverageRecorder", AverageRecorder.class);
            setupJob("DecisionRecorder", DecisionRecorder.class);
        }
    }

    public void updateActivity(String text)
    {
        txtActivity.setText(text);
    }

    /***
     *
     */
    public void setupJob(String recorder, Class object) {
        Intent intent = new Intent(this, object);
        PendingIntent pendingIntent;
        int requestCode = 0;
        long millis = System.currentTimeMillis();
        long minute = 60000;
        long interval = 0;

        switch (recorder)
        {
            case "LogRecorder":
                interval = minute * 1;
                requestCode = 0;
                break;
            case "DeltaRecorder":
                interval = minute * 20;
                requestCode = 1;
                break;
            case "AverageRecorder":
                interval = minute * 60;
                requestCode = 2;
                break;
            case "DecisionRecorder":
                interval = minute * 60 * 12;
                requestCode = 3;
                break;
        }

        millis += interval;
        pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, millis, interval, pendingIntent);
        alarmManagers.add(alarmManager);
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
        //startReceiverBatteryHealth();
    }

    private void showApplicationInfo()
    {
        //THIS LINE MUST ALWAYS GO BEFORE GET PERMISSIONS
        applicationInfo.getInstalledApplications();
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
