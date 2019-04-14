package shreyas.joshi.jupiter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/***
 *
 */
public class LogRecorder extends BroadcastReceiver {
    String bInfo = "Behaviour Info";
    Context context;
    String logFile = "log.txt";
    Timestamp timestamp;
    FileIO file;
    List<String> installedApps;
    DeviceInfo deviceInfo;
    int i = 0;
    //behaviourTask bTask;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.i(bInfo, "Receiving");
            this.context = context;
            timestamp = getTimeStamp();
            deviceInfo = new DeviceInfo(context);
            List<ResolveInfo> installedPackages = deviceInfo.getInstalledPackages();
            setInstalledApps(installedPackages);
            file = new FileIO(context);
            createProcessLogs();
        } catch (Exception ex) {
            Log.i(bInfo, ex.getMessage());
        }
    }

    public void setInstalledApps(List<ResolveInfo> packages)
    {
        installedApps = new ArrayList<String>();
        for(ResolveInfo package1 : packages)
        {
            String packageName = package1.activityInfo.applicationInfo.packageName;
            installedApps.add(packageName);

        }
    }


    /***
     *
     */
    public void createProcessLogs() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", "top -n 1"});
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            while (!bufferedReader.ready()) {
                //wait
            }

            while (bufferedReader.ready()) {

                String psInfo;
                while ((psInfo = bufferedReader.readLine()) != null) {
                    stringBuilder.append(psInfo + "\n");
                }
            }
            bufferedReader.close();
        } catch (IOException ex) {
            Log.d(bInfo, "IO Exception: " + ex.getMessage());
        }
        parseTop(stringBuilder.toString());
    }

    /***
     *
     * @param data
     */
    public void parseTop(String data) {
        String serverMessage = "";
        String[] lines = data.split("\n");
        boolean startReading = false;
        for (String line : lines) {
            line = line.trim();
            if (startReading) {
                String[] split = line.split("\\s+");
                String name = split[split.length - 1];
                if (installedApps.contains(name) && !name.equals("shreyas.joshi.jupiter")) {
                    String[] log = {name, split[4], split[7], split[8], split[9]};
                    line = TextUtils.join(",", log);
                    serverMessage += line + "," + timestamp + "\n";
                }
            }

            if (line.contains("PID")) {
                startReading = true;
            }
        }

        file.writeToFile(serverMessage, logFile, true);

        Log.i(bInfo, file.readFile(logFile));
    }

    public String getAppName()
    {
        return "";
    }


    /***
     *
     * @return
     */
    @NonNull
    private Timestamp getTimeStamp() {
        Date date = new Date();
        long time = date.getTime();
        return new Timestamp(time);
    }
}
