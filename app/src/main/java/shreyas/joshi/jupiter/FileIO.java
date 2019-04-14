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
     * Appends data to appropriate file
     * @param data String to be written to file
     * @param fileName Name of file
     */
    public void writeToFile(String data, String fileName, boolean append)
    {
        try
        {
            File file = new File(folder + fileName);

            if(!checkFileExists(folder))
            {
                File file2 = new File(folder);
                file2.mkdirs();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(file, append);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.append(data);
            outputStreamWriter.close();
        }
        catch (Exception ex)
        {
            Log.i(fileInfo, ex.getMessage());
        }
    }

    /***
     * Reads file
     * @param fileName Name of file to be read
     * @return Contents of file
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

    /***
     *
     * @return Current timestamp
     */
    private Timestamp getTimeStamp() {
        Date date = new Date();
        long time = date.getTime();
        return new Timestamp(time);
    }

    /***
     * Renames file to filename + timestamp
     * @param fileName Name of file to be renamed
     */
    public void renameFile(String fileName)
    {
        String moveDir = folder + "/pastLogs" + fileName + getTimeStamp();
        File file = new File(folder + fileName);
        file.renameTo(new File(moveDir));
    }

    /***
     * Deletes file
     * @param fileName Name of file to be deleted
     */
    public void deleteFile(String fileName)
    {
        File file = new File(folder + fileName);
        file.delete();
    }

    /***
     * Checks if the file exists
     * @param fileName Name of file
     * @return True if the file exists, false if it does not
     */
    public boolean checkFileExists(String fileName)
    {
        File file = new File(folder + fileName);
        return file.exists();
    }
}
