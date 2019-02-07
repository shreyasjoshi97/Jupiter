package shreyas.joshi.jupiter;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SocketClient extends AsyncTask<String, Void, String> {
    public AsyncResponse delegate = null;
    public String result;
    String socketLog = "SocketInfo";
    String serverUrl = "https://amaltheaserver.herokuapp.com/";


    /***
     *
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... message)
    {
        URL url;
        try
        {
            url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(300000);
            connection.setConnectTimeout(300000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(message[0]);

            bufferedWriter.flush();
            bufferedWriter.close();

            int responseCode = connection.getResponseCode();

            if(responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                while ((line = bufferedReader.readLine()) != null)
                {
                        result += line + "\n";
                }
                Log.i(socketLog, result);
            }
        }
        catch (Exception ex)
        {
            Log.e(socketLog, Log.getStackTraceString(ex));
        }
        return result;
    }

    protected void onPostExecute(String result)
    {
        delegate.processOutput(result);
    }
}

