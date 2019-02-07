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
import java.util.ArrayList;
import java.util.List;

/***
 *
 */
public class ApplicationInfo {
    DeviceInfo deviceInfo;
    FileIO file;
    Context context;
    String appInfo = "ApplicationInfo";
    String fileName = "permissions.txt";
    List<ResolveInfo> installedApps;

    /***
     *
     * @param mContext
     */
    public ApplicationInfo(Context mContext)
    {
        context = mContext.getApplicationContext();
        file = new FileIO(mContext);
    }

    /***
     *
     * @return
     */
    public List<String> getInstalledApplications()
    {
        List<String> packageNames = new ArrayList<String>();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        installedApps = context.getPackageManager().queryIntentActivities(mainIntent, 0);
        for (ResolveInfo app : installedApps)
        {
            packageNames.add(app.activityInfo.applicationInfo.packageName);
        }
        return packageNames;
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

    /***
     *
     * @return
     */
    public void getPermissions()
    {
        String permissions;
        for (ResolveInfo app : installedApps) {
            try {
                String packageName = app.activityInfo.applicationInfo.packageName;
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
                permissions = packageName + ",";
                int permissionLength = packageInfo.requestedPermissions.length - 1;

                for (int i = 0; i < permissionLength; i++) {
                    if ((packageInfo.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                        permissions += packageInfo.requestedPermissions[i] + ",";
                    }
                }

                permissions += "\n";
                file.writeToFile(permissions, fileName);
            }
            catch (Exception ex)
            {
                Log.i(appInfo, ex.getMessage());
            }
        }
    }

    /***
     *
     * @param command
     * @return
     */
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
        return stringBuilder.toString();
    }

    /***
     *
     */
    public void getRunningAppInfo()
    {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo appProcess : runningAppProcesses)
        {
            Log.i(appInfo, appProcess.processName);
        }
    }

    /***
     *
     */
    public void sendLogs()
    {
        SocketClient socketClient = new SocketClient();
        String content = "|" + file.readFile(fileName);
        socketClient.execute(content + "~");
    }

}