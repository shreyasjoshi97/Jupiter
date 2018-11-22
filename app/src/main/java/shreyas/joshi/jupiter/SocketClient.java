package shreyas.joshi.jupiter;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

public class SocketClient extends AsyncTask<String, Void, Void> {

    public String result;
    String socketLog = "SocketInfo";
    String serverUrl = "https://amaltheaserver.herokuapp.com/";

    protected Void doInBackground(String... message)
    {
        URL url;
        StringBuilder stringBuilder = new StringBuilder();
        try
        {
            url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(30000);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Keep-Alive", "Header");
            connection.setDoInput(true);
            connection.setDoOutput(true);


            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            outputStreamWriter.write(message[0]);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            if(connection.getResponseCode() == 200)
            {
                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            String result;

            while (!bufferedReader.ready()) {
                //Wait
            }

            while (bufferedReader.ready()) {
                while ((result = bufferedReader.readLine()) != null) {
                    stringBuilder.append(result + "\n");
                }
            }
            bufferedReader.close();
            outputStreamWriter.flush();
            outputStreamWriter.close();
            result = stringBuilder.toString();
            Log.i(socketLog, result);
        }
        catch (Exception ex)
        {
            Log.e(socketLog, Log.getStackTraceString(ex));
        }
        return null;
    }
}

