package project.silkwormtester.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import project.silkwormtester.R;
import project.silkwormtester.localdata.Config;

/**
 * Author: ryan_wu
 * Email:  IMITATOR_WU@OUTLOOK.COM
 * Date:   16/4/17
 */
public class DetectionReaderActivity extends Activity {
    TextView reader;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detection_reader);

        reader = (TextView) findViewById(R.id.detection_reader_textview);
        loadDetectionText();
    }

    private void loadDetectionText() {
        String sdPath = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            sdPath = Environment.getExternalStorageDirectory().toString();
        }
        File file = new File(sdPath + Config.BASE_DIR + Config.DETECTION_DAT);
        if (!file.exists()){
            reader.setText("无检测数据");
            return;
        }
        StringBuffer bf = new StringBuffer();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            while((line = br.readLine()) != null) {
                bf.append(line + "\n");
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        reader.setText(bf.toString());
    }
}
