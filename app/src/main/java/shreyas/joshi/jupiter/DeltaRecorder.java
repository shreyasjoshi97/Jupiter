package shreyas.joshi.jupiter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class DeltaRecorder extends BaseRecorder implements AsyncResponse {
    FileIO file;
    String logFile = "log.txt";
    String deltaFile = "delta.txt";
    String deltaLogs = "DeltaInfo";

    @Override
    public void onReceive(Context context, Intent intent) {
        file = new FileIO(context);
        if(file.checkFileExists(logFile)) {
            Toast.makeText(context.getApplicationContext(),
                    "In DELTA!", Toast.LENGTH_LONG).show();
            sendLogs();
        }
    }

    public void sendLogs() {
        try {
            SocketClient socketClient = new SocketClient();
            socketClient.delegate = this;
            String contents = file.readFile(logFile);
            String data = contents.replace("\n", "$");
            socketClient.execute("^" + data + "\n");
        } catch (Exception ex) {
            Log.i(deltaLogs, ex.getMessage());
        }
    }

    public void processOutput(String result)
    {
        result = result.replace("$", "\n");
        file.writeToFile(result, deltaFile, true);
        Log.i(deltaLogs, result);
        file.deleteFile(logFile);
    }
}
