package shreyas.joshi.jupiter;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

public class DecisionRecorder implements AsyncResponse {
    FileIO file;
    String averageFile = "averages.txt";
    // String logFile = "delta.txt";
    String decisionFile = "decisions.txt";
    String decisionLogs = "DecisionInfo";
    float activeThreshold;
    float idleThreshold;
    Context context;

    public DecisionRecorder(Context context)
    {
        this.context = context;
        file = new FileIO(context);
        Toast.makeText(context.getApplicationContext(),
                "In Decision", Toast.LENGTH_LONG).show();
        activeThreshold = getThreshold(file.readFile("Active Threshold.txt"));
        idleThreshold = getThreshold(file.readFile("Idle Threshold.txt"));

    }

    public float getThreshold(String value)
    {
        value = value.replace("\n", "");
        float threshold = Float.parseFloat(value);
        threshold /= 10;
        return threshold;
    }

    public void sendLogs(String fileName)
    {
        this.averageFile = fileName;
        SocketClient socketClient = new SocketClient();
        socketClient.delegate = this;
        String contents = file.readFile(fileName);
        String data = contents.replace("\n", "$");
        socketClient.execute("~" + data + "\n");
    }

    public HashMap<String, String> setHashMap()
    {
        HashMap<String, String> staticMap = new HashMap<String, String>();

        if(!file.checkFileExists("Static Results.txt"))
        {
            StaticAnalysis appInfo = new StaticAnalysis(context);
            appInfo.sendLogs();
        }

        String staticResults = file.readFile("Static Results.txt");
        String[] resultsArray = staticResults.split("\n");

        for(String result : resultsArray)
        {
            result = result.trim();
            String[] splitResult = result.split(": ");
            staticMap.put(splitResult[0], splitResult[1]);
        }

        return staticMap;
    }

    public void processOutput(String result)
    {
        boolean found = false;
        HashMap<String, String> staticMap = setHashMap();
        DeviceInfo deviceInfo = new DeviceInfo(context);

        result = result.replace("$", "\n");

        String[] entries = result.split("\n");
        for(String entry : entries)
        {
            String[] columns = entry.split(",");
            String name = columns[0];
            float active_cpu_q1 = Float.parseFloat(columns[1]);
            float active_cpu_q2 = Float.parseFloat(columns[2]);
            float active_cpu_q3 = Float.parseFloat(columns[3]);
            float idle_cpu_q1 = Float.parseFloat(columns[4]);
            float idle_cpu_q2 = Float.parseFloat(columns[5]);
            float idle_cpu_q3 = Float.parseFloat(columns[6]);
            float active_mem_q1 = Float.parseFloat(columns[7]);
            float active_mem_q2 = Float.parseFloat(columns[8]);
            float active_mem_q3 = Float.parseFloat(columns[9]);
            float idle_mem_q1 = Float.parseFloat(columns[10]);
            float idle_mem_q2 = Float.parseFloat(columns[11]);
            float idle_mem_q3 = Float.parseFloat(columns[12]);

            if(active_cpu_q1 > activeThreshold || active_cpu_q2 > activeThreshold || active_cpu_q3 > activeThreshold
                    || idle_cpu_q1 > idleThreshold || idle_cpu_q2 > idleThreshold || idle_cpu_q3 > idleThreshold
                    || active_mem_q1 > activeThreshold || active_mem_q2 > activeThreshold || active_mem_q3 > activeThreshold
                    || idle_mem_q1 > idleThreshold || idle_mem_q2 > idleThreshold || idle_mem_q3 > idleThreshold)
            {
                String appName = deviceInfo.getApplicationName(name);
                String decision = "";
                if(staticMap.get(name) == "1")
                {
                    //SEND HELP
                    found = true;
                    decision = appName + " - MALICIOUS";
                }
                else if(staticMap.get(name) == "0")
                {
                    found = true;
                    decision = appName + " - SUSPICIOUS";
                }
                file.writeToFile(decision, decisionFile, true);
            }
        }

        file.writeToFile(result, decisionFile, true);
        Log.i(decisionLogs, result);
        file.renameFile(averageFile);
        file.deleteFile(averageFile);

        if(found) {
            sendNotification("Suspicious application behaviour!",
                    "Click 'Recent' button to find out more");
        }
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
