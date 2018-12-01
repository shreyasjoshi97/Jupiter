package shreyas.joshi.jupiter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ApplicationInfo {
    DeviceInfo deviceInfo;

    Context context;
    String appInfo = "ApplicationInfo";
    List<ResolveInfo> installedApps;

    public ApplicationInfo(Context mContext, DeviceInfo dInfo)
    {
        context = mContext;
        deviceInfo = dInfo;
    }

    public String getInstalledApplications()
    {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        installedApps = context.getPackageManager().queryIntentActivities(mainIntent, 0);

        for(ResolveInfo app : installedApps)
        {
            String appName = app.loadLabel(context.getPackageManager()).toString();
            Log.i(appInfo, appName);
        }

        return "";
    }

    public void getRunningApplications()
    {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfo = activityManager.getRunningAppProcesses();

        for(ActivityManager.RunningAppProcessInfo info : appProcessInfo)
        {
            Log.i(appInfo, info.processName);
        }
    }

    public void getPermissions()
    {
        try
        {
            for (ResolveInfo app : installedApps)
            {
                String packageName = app.activityInfo.applicationInfo.packageName;
                Log.i(appInfo, packageName);
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);

                for(int i = 0; i < packageInfo.requestedPermissions.length; i++) {
                    if ((packageInfo.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0)
                    {
                        String permission = app.resolvePackageName + packageInfo.requestedPermissions[i];
                        //Log.i(appInfo, permission);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            Log.i(appInfo, ex.getMessage());
        }
    }

    public String getProcessLogs()
    {
        String command = "ps";

        int versionNo = Integer.parseInt(String.valueOf(deviceInfo.versionNo.charAt(0)));
        if(versionNo >= 8)
        {
            command += " -o NAME";
        }

        return createProcessLogs(command);
    }

    public String createProcessLogs(String command)
    {
        StringBuilder stringBuilder = new StringBuilder();
        try
        {
            Process process = Runtime.getRuntime().exec(new String[] {"su", "-c", command});
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            while(!bufferedReader.ready())
            {
                //wait
            }

            String psLogs = "";

            while(bufferedReader.ready())
            {

                String psInfo;
                while ((psInfo = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(psInfo + "\n");
                }
            }
            bufferedReader.close();
        }
        catch (IOException ex)
        {
            Log.d(appInfo, "IO Exception: " + ex.getMessage());
        }

        return stringBuilder.toString();
    }

    /*public void writeToFile(String info)
    {
        try
        {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("D:\\top.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(info);
            outputStreamWriter.close();
        }
        catch (FileNotFoundException ex)
        {

        }
        catch (IOException ex)
        {

        }

    }*/

}