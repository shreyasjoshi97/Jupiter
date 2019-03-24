package shreyas.joshi.jupiter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AverageRecorder extends BaseRecorder implements AsyncResponse {
    FileIO file;
    String averageFile = "averages.txt";
    String deltaFile = "delta.txt";
    String avgLogs = "AverageInfo";
    int maxEntries = 7;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        file = new FileIO(context);
        sendLogs();
    }

    public void sendLogs()
    {
        SocketClient socketClient = new SocketClient();
        socketClient.delegate = this;
        String contents = file.readFile(deltaFile);
        String data = contents.replace("\n", "$");
        socketClient.execute("+" + data + "\n");
    }

    public boolean maxNumber(String data)
    {
        String[] splitData = data.split("\n");
        if(splitData.length > maxEntries)
        {
            return true;
        }
        return false;
    }

    public void processOutput(String result)
    {
        String[] resultArray = result.split("$");
        DecisionRecorder decisionRecorder = new DecisionRecorder(context);

        for(String average : resultArray) {
            String[] splitAverage = average.split(",");
            String name = splitAverage[0];
            file.writeToFile(average, name);

            if (maxNumber(file.readFile(name)))
            {
                decisionRecorder.sendLogs(name);
            }
        }

        result = result.replace("$", "\n");
        file.writeToFile(result, averageFile);
        Log.i(avgLogs, result);
        file.moveFile(deltaFile);
        file.deleteFile(deltaFile);
    }
}
