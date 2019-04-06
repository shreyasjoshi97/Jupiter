package shreyas.joshi.jupiter;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.Date;

/***
 *
 */
public class FileIO {
    String folder = "/storage/emulated/0/Android/data/shreyas.joshi.jupiter/files/sampleLogs/";
    String fileInfo = "FileInfo";
    Context context;

    public FileIO(Context context)
    {
        this.context = context.getApplicationContext();
    }

    /***
     *
     * @param data
     * @param fileName
     */
    public void writeToFile(String data, String fileName)
    {
        try
        {
            File file = new File(folder + fileName);

            if(!checkFileExists(folder))
            {
                File file2 = new File(folder);
                file2.mkdirs();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            //OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("C:\\Documents\\log.txt", Context.MODE_PRIVATE));
            outputStreamWriter.append(data);
            outputStreamWriter.close();
        }
        catch (Exception ex)
        {
            Log.i(fileInfo, ex.getMessage());
        }
    }

    /***
     *
     * @param fileName
     * @return
     */
    public String readFile(String fileName)
    {
        String ret = "";

        try {
            FileInputStream inputStream = new FileInputStream(folder + fileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString).append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
                Log.i(fileInfo, ret);
            }
        }
        catch (Exception e) {
            Log.i(fileInfo, e.getMessage());
        }

        return ret;
    }

    private Timestamp getTimeStamp() {
        Date date = new Date();
        long time = date.getTime();
        return new Timestamp(time);
    }

    public void moveFile(String fileName)
    {
        String moveDir = folder + "/pastLogs" + fileName + getTimeStamp();
        File file = new File(folder + fileName);
        file.renameTo(new File(moveDir));
    }

    /***
     *
     * @param fileName
     */
    public void deleteFile(String fileName)
    {
        File file = new File(folder + fileName);
        file.delete();
    }

    /***
     *
     * @param fileName
     * @return
     */
    public boolean checkFileExists(String fileName)
    {
        File file = new File(folder + fileName);
        return file.exists();
    }
}
