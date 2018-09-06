package shreyas.joshi.jupiter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setVersionNumber();

        txtVersion = findViewById(R.id.txtVersion);
        txtOSVersion = findViewById(R.id.txtOSVersion);
        txtSecurityPatch = findViewById(R.id.txtSecurityPatch);
        txtWifiSecurity = findViewById(R.id.txtWiFiSecurity);
        txtBatteryHealth = findViewById(R.id.txtBatteryHealth);
        txtRAMUsage = findViewById(R.id.txtRAMUsage);

        deviceInfo = new DeviceInfo(getApplicationContext());
        /*final FloatingActionButton btnRun = findViewById(R.id.btnRun);
        btnRun.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showDeviceInfo();
            }
        });*/
        showDeviceInfo();

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


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

    private void showDeviceInfo()
    {
        txtOSVersion.setText(deviceInfo.getOSVersion());
        txtSecurityPatch.setText(deviceInfo.getSecurityPatch());
        txtWifiSecurity.setText(deviceInfo.getWifiSecurity());
        txtRAMUsage.setText(deviceInfo.getRamUsage());
        BroadcastReceiver batteryBroadcast = startReceiverBatteryHealth();
    }

    public BroadcastReceiver startReceiverBatteryHealth()
    {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                txtBatteryHealth.setText(deviceInfo.getBatteryHealth(intent));
            }
        };
        Main.this.registerReceiver(broadcastReceiver, intentFilter);
        return broadcastReceiver;
    }

    /*public void stopReceiverBatteryHealth(BroadcastReceiver broadcastReceiver)
    {
        Main.this.unregisterReceiver(broadcastReceiver);
    }*/
}
