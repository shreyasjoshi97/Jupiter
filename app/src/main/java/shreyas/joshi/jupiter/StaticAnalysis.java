package shreyas.joshi.jupiter;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/***
 *
 */
public class StaticAnalysis implements AsyncResponse{
    DeviceInfo deviceInfo;
    FileIO file;
    Context context;
    UICallback uiCallback;

    String appInfo = "StaticAnalysis";
    String fileName = "permissions.txt";
    String storeName = "Static Results.txt";
    boolean wait;
    String combinedResults = "";
    List<ResolveInfo> installedApps;

    /***
     *
     * @param mContext
     */
    public StaticAnalysis(Context mContext, UICallback callback)
    {
        context = mContext;
        uiCallback = callback;
        file = new FileIO(mContext);
        deviceInfo = new DeviceInfo(mContext);
    }

    public StaticAnalysis(Context mContext)
    {
        context = mContext;
        file = new FileIO(mContext);
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
    public void sendLogs()
    {
        try
        {
            if(file.checkFileExists(storeName))
            {
                file.deleteFile(storeName);
            }
            deviceInfo.getPermissions();
            String contents = file.readFile(fileName);
            file.deleteFile(storeName);

            BufferedReader bufferedReader = new BufferedReader(new StringReader(contents));
            String line;
            while((line = bufferedReader.readLine()) != null)
            {
                String data = "|" + line + "\n";
                SocketClient socketClient = new SocketClient();
                socketClient.delegate = this;
                socketClient.execute(data);
            }
            //sendNotification("Finished scan", "The Quick Scan has been completed");
        }
        catch (Exception ex)
        {

        }
    }

    public void processOutput(String result)
    {
        uiCallback.updateActivity("");
        result = result.replace("{", "");
        result = result.replace("}", "");
        result = result.replace("'", "");
        result += "\n";
        file.writeToFile(result, storeName, true);
        if(result.contains(",1"))
        {
            result = result.replace(",1", " - Malicious");
            uiCallback.updateActivity(result);
        }
        Log.i(appInfo, result);
    }


    public void sendNotification(String title, String text)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(title)
                .setContentText(text);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(001, mBuilder.build());
    }
}