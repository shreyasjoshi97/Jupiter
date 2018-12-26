package shreyas.joshi.jupiter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

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
        Log.i(appInfo, Integer.toString(installedApps.size()));
        for(int i = 0; i < installedApps.size() - 10; i++)
        {
            String appName = installedApps.get(i).loadLabel(context.getPackageManager()).toString();
            //Log.i(appInfo, appName);
        }
        //ResolveInfo lastapp = installedApps.get(installedApps.size());
        //String appName = lastapp.loadLabel(context.getPackageManager()).toString();
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

    public String getPermissions()
    {
        String permissions = "";
        try
        {
            for (ResolveInfo app : installedApps)
            {
                String packageName = app.activityInfo.applicationInfo.packageName;
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);

                for(int i = 0; i < packageInfo.requestedPermissions.length - 1; i++) {
                    if ((packageInfo.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0)
                    {
                        permissions += packageInfo.requestedPermissions[i] + "\n";
                    }
                }
            }

        }
        catch (Exception ex)
        {
            Log.i(appInfo, ex.getMessage());
        }
        return permissions;
    }

    public String getProcessLogs()
    {
        String command = "top -n 1";

        int versionNo = Integer.parseInt(String.valueOf(deviceInfo.versionNo.charAt(0)));
        /*if(versionNo >= 8)
        {
            command += " -o NAME";
        }*/

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
        String topResults = stringBuilder.toString();
        Log.i(appInfo, topResults);
        return stringBuilder.toString();
    }

    public void getRunningAppInfo()
    {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo appProcess : runningAppProcesses)
        {
            Log.i(appInfo, appProcess.processName);
        }
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