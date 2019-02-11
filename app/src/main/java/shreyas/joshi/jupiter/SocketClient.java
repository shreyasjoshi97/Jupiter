package shreyas.joshi.jupiter;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class SocketClient extends AsyncTask<String, Void, String> {
    public AsyncResponse delegate = null;
    public String result = "";
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
            connection.connect();

            try
            {
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                bufferedWriter.write(message[0]);
                bufferedWriter.flush();
                //bufferedWriter.close();

                String line;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer stringBuffer = new StringBuffer();

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                    stringBuffer.append("\n");
                }
                bufferedWriter.close();
                bufferedReader.close();
                //Log.i(socketLog, result);
                extractResult(stringBuffer.toString());
            }
            finally {
                connection.disconnect();
            }
        }
        catch (Exception ex)
        {
            Log.e(socketLog, Log.getStackTraceString(ex));
        }
        return result;
    }

    protected void onPostExecute(String result) {
        Log.i(socketLog, "Attempting to send result: " + result);
        try {
            if (result != null && !result.equals("")) {
                delegate.processOutput(result);
            }
        } catch (Exception ex)
        {
            Log.i(socketLog, ex.getMessage());
        }
    }

    protected void extractResult(String input)
    {
        String[] lines = input.split("\n");

        for(String line : lines)
        {
            if(line.contains("Result Start"))
            {
                line = line.replace("Result Start ", "");
                line = line.replace(" Result End", "");
                line = line.replace("{", "");
                line = line.replace("}", "");
                line = line.replace("'", "");
                line = line.trim();
                result += line + "\n";
                Log.i(socketLog, line);

                break;
            }
        }

        //result = matcher.group(1);
    }
}

