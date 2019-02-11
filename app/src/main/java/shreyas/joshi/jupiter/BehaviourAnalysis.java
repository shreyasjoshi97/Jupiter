package shreyas.joshi.jupiter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/***
 *
 */
public class BehaviourAnalysis extends BroadcastReceiver implements AsyncResponse {
    String bInfo = "Behaviour Info";
    Context context;
    String logFile = "log.txt";
    String timeFile = "time.txt";
    Timestamp timestamp;
    FileIO file;
    List<String> installedApps;
    //behaviourTask bTask;

    @Override
    public void onReceive(Context context, Intent intent) {
        try
        {
            Log.i(bInfo, "Receiving");
            this.context = context;
            timestamp = getTimeStamp();
            ApplicationInfo a = new ApplicationInfo(context);
            installedApps = a.getInstalledApplications();
            file = new FileIO(context);

            if (file.checkFileExists(timeFile))
            {
                String time = file.readFile(timeFile);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date parseDate = sdf.parse(time);
                Timestamp startingTimeStamp = new Timestamp(parseDate.getTime());
                long diff = timestamp.getTime() - startingTimeStamp.getTime();
                diff = diff / (1000 * 60 * 60 * 2);
                if (diff >= 2)
                {
                    //sendLogs();
                    //file.deleteFile(timeFile);
                }
                else
                {
                    createProcessLogs();
                }
            }
            else
            {
                file.writeToFile(timestamp.toString(), timeFile);
                createProcessLogs();
            }
        }
        catch (Exception ex)
        {
            Log.i(bInfo, ex.getMessage());
        }
    }

    /***
     *
     */
    public void createProcessLogs()
    {
        StringBuilder stringBuilder = new StringBuilder();
        try
        {
            Process process = Runtime.getRuntime().exec(new String[] {"su", "-c", "top -n 1"});
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
            Log.d(bInfo, "IO Exception: " + ex.getMessage());
        }
        String topResults = stringBuilder.toString();
        parseTop(stringBuilder.toString());
    }

    /***
     *
     * @param data
     */
    public void parseTop(String data)
    {
        String serverMessage = "";
        String[] lines = data.split("\n");
        boolean startReading = false;
        for(String line : lines)
        {
            line = line.trim();
            if(startReading)
            {
                String[] split = line.split("\\s+");
                String name = split[split.length-1];
                if(installedApps.contains(name) && !name.equals("shreyas.joshi.jupiter"))
                {
                    String[] log = {name, split[4], split[7], split[8], split[9]};
                    line = TextUtils.join(",", log);
                    serverMessage += line + "," + timestamp + "\n";
                }
            }

            if(line.contains("PID"))
            {
                startReading = true;
            }
        }

        file.writeToFile(serverMessage, logFile);

        Log.i(bInfo, file.readFile(logFile));
    }

    /***
     *
     * @return
     */
    @NonNull
    private Timestamp getTimeStamp()
    {
        Date date = new Date();
        long time = date.getTime();
        return new Timestamp(time);
    }

    /***
     *
     */
    public void sendLogs()
    {
        try
        {
            SocketClient socketClient = new SocketClient();
            socketClient.delegate = this;
            String contents = file.readFile(logFile);
            String send = "";

            BufferedReader bufferedReader = new BufferedReader(new StringReader(contents));
            String line;
            while((line = bufferedReader.readLine()) != null)
            {
                for(String app : installedApps)
                {
                    if(line.contains(app))
                    {
                        send += line;
                    }
                }
                socketClient.execute("^" + send);
            }
        }
        catch (Exception ex)
        {

        }
    }

    public void processOutput(String result)
    {
        String[] columns = result.split(",");
        file.writeToFile(result, columns[0] + ".txt");
    }
}
