package shreyas.joshi.jupiter;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.List;

public class DeviceInfo
{
    Context context;
    String deviceInfo = "DeviceInfo";

    public DeviceInfo(Context mContext)
    {
        context = mContext;
    }

    public String getSecurityPatch()
    {
        String secPatch = "Security Patch Version: " + Build.VERSION.SECURITY_PATCH;
        Log.i(deviceInfo, secPatch);
        return secPatch;
    }

    public String getOSVersion()
    {
        String version = "";
        String versionCode = Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName();
        char code = versionCode.charAt(0);

        //Match with codename
        switch(code)
        {
            case 'L':
                version = "Lollipop";
                break;
            case 'M':
                version = "Marshmallow";
                break;
            case 'N':
                version = "Nougat";
                break;
            case 'O':
                version = "Oreo";
                break;
            case 'P':
                version = "Pie";
                break;
        }

        Log.i(deviceInfo,"Running Android " + Build.VERSION.RELEASE + " (" + version + ")");
        return "Running Android " + Build.VERSION.RELEASE + " (" + version + ")";
    }

    public String getWifiSecurity()
    {
        String WifiType = "";
        String result;

        //Get access to WiFi service
        WifiManager wifi = (WifiManager)context.getSystemService(context.WIFI_SERVICE);
        //Get all nearby WiFis - WILL NEED LOCATION ACCESS
        List<ScanResult> networkList = wifi.getScanResults();

        //Current connected WiFi info
        WifiInfo wifiInfo = wifi.getConnectionInfo();
        String currentSSID = wifiInfo.getSSID();

        if(networkList != null)
        {
            //Get capabilities of the connected WiFi network
            for(ScanResult network : networkList)
            {
                String networkSSID = network.SSID;
                if(currentSSID.contains(networkSSID))
                {
                    String networkCapabilities = network.capabilities;

                    if(networkCapabilities.contains("WPA"))
                    {
                        if(networkCapabilities.contains("WPA2"))
                        {
                            WifiType = "WPA2";
                        }
                        //Do not include WPA3 yet (for later release)
                        /*else if(networkCapabilities.contains("WPA3"))
                        {
                            WifiType = "WPA3";
                        }*/
                        else
                        {
                            WifiType = "WPA";
                        }
                    }
                    else if(networkCapabilities.contains("WEP"))
                    {
                        WifiType = "WEP";
                    }
                    break;
                }
            }
        }

        result = "WiFi Security Protocol for " + currentSSID + ": " + WifiType;

        Log.i(deviceInfo, result);
        return result;
    }

    public String getBatteryHealth(Intent intent)
    {
        String healthStatus = "Battery Health: ";
        int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                healthStatus += "Cold";
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                healthStatus += "Dead";
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                healthStatus += "Good";
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                healthStatus += "Over Voltage";
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                healthStatus += "Overheated";
                break;
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                healthStatus += "Unknown";
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                healthStatus += "Unspecified Failure";
                break;
        }

        Log.i(deviceInfo, "Battery Health: " + healthStatus);

        return healthStatus;
    }

    public String getRamUsage()
    {
        String ramUsage;
        DecimalFormat df = new DecimalFormat("###.##");

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        double totalMemory = memoryInfo.totalMem;
        totalMemory /= 1000000000;
        double availableMemory = memoryInfo.availMem;
        availableMemory /= 1000000000;

        ramUsage = "Ram Usage: " + df.format(availableMemory) + " GB available out of " + df.format(totalMemory) + "GB";

        Log.i(deviceInfo, ramUsage);
        return ramUsage;
    }
}

