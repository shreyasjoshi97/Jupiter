package shreyas.joshi.jupiter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

//1344
public class Main extends AppCompatActivity implements UICallback {
    DeviceInfo deviceInfo;
    StaticAnalysis staticAnalysis;
    AlarmManager alarmManager;
    //PendingIntent pendingIntent;
    List<AlarmManager> alarmManagers;
    List<PendingIntent> pendingIntents;

    ScrollView scrollLogs;
    TextView txtOSVersion;
    TextView txtSecurityPatch;
    TextView txtWifiSecurity;
    TextView txtRAMUsage;
    TextView txtActivity;
    TextView txtServerMsg;
    Button btnSend;
    Button btnSettings;
    Button btnRecent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtActivity = findViewById(R.id.txtLogs);
        alarmManagers = new ArrayList<AlarmManager>();
        pendingIntents = new ArrayList<PendingIntent>();
        deviceInfo = new DeviceInfo(getApplicationContext());

        txtActivity.setText("SCAN - Perform a quick scan\n" +
                "RECENT - View most recent results (ROOT)\n" +
                "SETTINGS - Change sensitivity (FOR ADVANCED USERS ONLY)");

        staticAnalysis = new StaticAnalysis(this.getApplicationContext(), this);

        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                txtActivity.setText("List of suspicious applications:\n\n");
                Toast.makeText(getApplicationContext(), "DONE!", Toast.LENGTH_SHORT).show();
                staticAnalysis.sendLogs();
            }
        });

        btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(deviceInfo.isRooted) {
                    Intent intent = new Intent(Main.this, Settings.class);
                    startActivity(intent);
                } else {
                    txtActivity.setText("THIS FEATURE IS FOR ROOTED DEVICES ONLY");
                }
            }
        });

        btnRecent = findViewById(R.id.btnRecentResults);
        btnRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deviceInfo.isRooted) {
                    FileIO file = new FileIO(getApplicationContext());
                    String results = file.readFile("decisions.txt");
                    String[] lines = results.split("\n");
                    for(String line : lines)
                    {
                        updateActivity(line);
                    }
                    //txtActivity.setText(results);
                } else {
                    txtActivity.setText("THIS FEATURE IS FOR ROOTED DEVICES ONLY");
                }
            }
        });

        scrollLogs = findViewById(R.id.scrollLogs);
        scrollLogs.post(new Runnable() {
            @Override
            public void run() {
                scrollLogs.smoothScrollTo(0, txtActivity.getTop());
            }
        });
        //showDeviceInfo();

        if(deviceInfo.isRooted)
        {
            alarmManager = (AlarmManager) getSystemService(getApplicationContext().ALARM_SERVICE);
            setupJob("LogRecorder", LogRecorder.class);
            /*setupJob("AverageRecorder", AverageRecorder.class);
            setupJob("DecisionRecorder", DecisionRecorder.class);*/
        }
    }

    public void updateActivity(String text)
    {
        ForegroundColorSpan fgSpan = new ForegroundColorSpan(Color.DKGRAY);
        SpannableString spannableString = new SpannableString(text);
        int startIndex = 0;
        int endIndex = 0;
        if(text.contains("suspicious"))
        {
            fgSpan = new ForegroundColorSpan(Color.parseColor("#FF8B00"));
            startIndex = text.indexOf("suspicious");
            endIndex = startIndex + 10;
        }
        else if(text.contains("malicious"))
        {
            fgSpan = new ForegroundColorSpan(Color.RED);
            startIndex = text.indexOf("malicious");
            endIndex = startIndex + 9;
        }
        spannableString.setSpan(fgSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtActivity.append(spannableString);
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
}
