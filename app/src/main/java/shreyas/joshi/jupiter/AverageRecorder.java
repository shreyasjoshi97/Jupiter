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

    @Override
    public void onReceive(Context context, Intent intent) {
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

    public void processOutput(String result)
    {
        result = result.replace("$", "\n");
        file.writeToFile(result, averageFile);
        Log.i(avgLogs, result);
        file.moveFile(deltaFile);
        file.deleteFile(deltaFile);
    }
}
