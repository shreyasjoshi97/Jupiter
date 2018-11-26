package shreyas.joshi.jupiter;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class SocketClient extends AsyncTask<String, Void, Void> {

    public String result;
    String socketLog = "SocketInfo";
    String serverUrl = "https://amaltheaserver.herokuapp.com/";

    protected Void doInBackground(String... message)
    {
        URL url;
        try
        {
            url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(60000);
            connection.setConnectTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write("Message from Jupiter to Amalthea");

            bufferedWriter.flush();
            bufferedWriter.close();

            int responseCode = connection.getResponseCode();

            if(responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                String response = "";
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                while ((line = bufferedReader.readLine()) != null)
                {
                        response += line + "\n";
                }
                Log.i(socketLog, response);
            }

        }
        catch (Exception ex)
        {
            Log.e(socketLog, Log.getStackTraceString(ex));
        }
        return null;
    }
}

