package shreyas.joshi.jupiter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        try {
            FileIO fileIO = new FileIO(this);

            SeekBar seekActive = findViewById(R.id.seekActive);
            seekActive.setMax(10);
            if (fileIO.checkFileExists("Active Threshold.txt")) {
                int progress = getThreshold(fileIO.readFile("Active Threshold.txt"));
                progress = 10 - progress;
                seekActive.setProgress(progress);
            }

            SeekBar seekIdle = findViewById(R.id.seekIdle);
            seekIdle.setMax(10);
            if (fileIO.checkFileExists("Idle Threshold.txt")) {
                int progress = getThreshold(fileIO.readFile("Idle Threshold.txt"));
                progress = 10 - progress;
                seekIdle.setProgress(progress);
            }

            seekActive.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int finalProgress = 10 - progress;
                    if(finalProgress < 1)
                    {
                        finalProgress = 1;
                    }
                    FileIO fileIO = new FileIO(getApplicationContext());
                    fileIO.writeToFile(Integer.toString(finalProgress), "Active Threshold.txt", false);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });


            seekIdle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int finalProgress = 10 - progress;
                    if(finalProgress < 1)
                    {
                        finalProgress = 1;
                    }
                    FileIO fileIO = new FileIO(getApplicationContext());
                    fileIO.writeToFile(Integer.toString(finalProgress), "Idle Threshold.txt", false);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        } catch (Exception ex) {
            Log.i("SettingsInfo", ex.getMessage());
        }
    }

    public int getThreshold(String value)
    {
        value = value.replace("\n", "");
        int threshold = Integer.parseInt(value);
        return threshold;
    }

}
