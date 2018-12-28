package shreyas.joshi.jupiter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.Date;

public class BehaviourAnalysis extends BroadcastReceiver {
    String bInfo = "Behaviour Info";
    Context context;
    String fileName = "log.txt";
    Timestamp timestamp;
    //behaviourTask bTask;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(bInfo, "Receiving");
        this.context = context;
        timestamp = getTimeStamp();
        createProcessLogs();    }

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
                line = TextUtils.join(",", split);
                serverMessage += line + "," + timestamp + "\n";
            }

            if(line.contains("PID"))
            {
                startReading = true;
            }
        }

        if(!checkFileExists())
        {
            serverMessage = timestamp + "\n" + serverMessage;

        }

        writeToFile(serverMessage);

        Log.i(bInfo, readFile());
    }

    private Timestamp getTimeStamp()
    {
        Date date = new Date();
        long time = date.getTime();
        return new Timestamp(time);
    }

    public boolean checkFileExists()
    {
        File file = new File(context.getExternalFilesDir("/sampleLogs/"), fileName);
        return file.exists();
    }

    public void writeToFile(String data)
    {
        try
        {
            File file = new File(context.getExternalFilesDir(null).getAbsolutePath() + "/sampleLogs", fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            //OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("C:\\Documents\\log.txt", Context.MODE_PRIVATE));
            outputStreamWriter.append(data);
            outputStreamWriter.close();
        }
        catch (Exception ex)
        {

        }
    }

    public String readFile()
    {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(fileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    if(receiveString.contains("shreyas.joshi.resourcehog"))
                    {
                        Log.i("ResourceHog", receiveString);
                    }
                    else if(receiveString.contains("shreyas.joshi.jupiter"))
                    {
                        Log.i("JoshiJupiter", receiveString);
                    }

                    stringBuilder.append(receiveString).append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (Exception e) {
        }

        return ret;
    }
}
