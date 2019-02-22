package shreyas.joshi.jupiter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class DecisionRecorder extends BaseRecorder implements AsyncResponse {
    FileIO file;
    String averageFile = "delta.txt";
    // String deltaFile = "delta.txt";
    String decisionFile = "decisions.txt";
    String decisionLogs = "DecisionInfo";
    float threshold = 0.5f;

    @Override
    public void onReceive(Context context, Intent intent) {
        file = new FileIO(context);
        Toast.makeText(context.getApplicationContext(),
                "In Decision!", Toast.LENGTH_LONG).show();
        sendLogs();
    }

    public void sendLogs()
    {
        SocketClient socketClient = new SocketClient();
        socketClient.delegate = this;
        String contents = file.readFile(averageFile);
        String data = contents.replace("\n", "$");
        socketClient.execute("~" + data + "\n");
    }

    public void processOutput(String result)
    {
        result = result.replace("$", "\n");

        String[] entries = result.split("\n");
        for(String entry : entries)
        {
            String[] columns = entry.split(",");
            String name = columns[0];
            float activeCPU = Float.parseFloat(columns[1]);
            float idleCPU = Float.parseFloat(columns[2]);
            float activeMem = Float.parseFloat(columns[3]);
            float idleMem = Float.parseFloat(columns[4]);

            if(activeCPU > threshold || idleCPU > threshold || activeMem > threshold
                    || idleMem > threshold)
            {
                String reply = name + " is behaving suspiciously";
            }
        }

        file.writeToFile(result, decisionFile);
        Log.i(decisionLogs, result);
        file.moveFile(averageFile);
        file.deleteFile(averageFile);
    }

}
