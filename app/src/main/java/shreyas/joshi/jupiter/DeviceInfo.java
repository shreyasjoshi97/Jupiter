package shreyas.joshi.jupiter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class DeviceInfo
{
    Context context;
    String deviceInfo = "DeviceInfo";
    String wifiSecurity;
    String networkSSID;

    public String versionNo;
    public String versionName;
    public String versionCode;
    public boolean isRooted;

    public DeviceInfo(Context mContext)
    {
        context = mContext;
        versionNo = Build.VERSION.RELEASE;
        versionCode = Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName();
        setOSVersion();
        setWifiSecurity();
        checkRoot();
    }

    private void checkRoot()
    {
        ApplicationInfo appInfo = new ApplicationInfo(context);
        String testRoot = appInfo.createProcessLogs("top -n 1");

        if(testRoot == "" || testRoot == null)
        {
            isRooted = false;
        }
        else
        {
            isRooted = true;
        }
    }

    public String getSecurityPatch()
    {
        String secPatch = "Security Patch Version: " + Build.VERSION.SECURITY_PATCH;
        return secPatch;
    }

    public boolean getRoot()
    {
        return isRooted;
    }

    public void setOSVersion()
    {
        String version = "";
        String versionCode = Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName();
        char code = versionNo.charAt(0);

        //Match with codename
        switch(code)
        {
            case '5':
                version = "Lollipop";
                break;
            case '6':
                version = "Marshmallow";
                break;
            case '7':
                version = "Nougat";
                break;
            case '8':
                version = "Oreo";
                break;
            case '9':
                version = "Pie";
                break;
        }

        versionName = version;
    }

    public void setWifiSecurity()
    {
        String WifiType = "";

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
                networkSSID = network.SSID;
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

        wifiSecurity = WifiType;
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

        return ramUsage;
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

        return healthStatus;
    }
}