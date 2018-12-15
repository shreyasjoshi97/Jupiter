package shreyas.joshi.jupiter;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BehaviourAnalysis {
    List<String> topResults;
    String bInfo = "Behaviour Info";
    public BehaviourAnalysis()
    {
        topResults = new ArrayList<String>();
    }

    public void parseTop(String data)
    {
        String[] lines = data.split("\n");
        boolean startReading = false;
        for(String line : lines)
        {
            if(startReading)
            {
                String[] split = line.split("\\s+");
                line = TextUtils.join(",", split);
                Log.i(bInfo, line);
                //topResults.add(line);
            }

            if(line.contains("PID"))
            {
                startReading = true;
            }
        }
    }
}
