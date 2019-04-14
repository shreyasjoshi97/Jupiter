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
            final TextView txtActive = findViewById(R.id.txtActive);
            seekActive.setMax(10);
            if (fileIO.checkFileExists("Active Threshold.txt")) {
                int progress = getThreshold(fileIO.readFile("Active Threshold.txt"));
                seekActive.setProgress(progress);
            }

            SeekBar seekIdle = findViewById(R.id.seekIdle);
            final TextView txtIdle = findViewById(R.id.txtIdle);
            seekIdle.setMax(10);
            if (fileIO.checkFileExists("Idle Threshold.txt")) {
                int progress = getThreshold(fileIO.readFile("Idle Threshold.txt"));
                seekIdle.setProgress(progress);
            }

            seekActive.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    txtActive.setText(String.valueOf(progress));
                    FileIO fileIO = new FileIO(getApplicationContext());
                    fileIO.writeToFile(Integer.toString(progress), "Active Threshold.txt", false);

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
                    txtIdle.setText(String.valueOf(progress));
                    FileIO fileIO = new FileIO(getApplicationContext());
                    fileIO.writeToFile(Integer.toString(progress), "Idle Threshold.txt", false);
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
