package project.silkwormtester.fragments;

import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import project.silkwormtester.localdata.Config;

/**
 * Author: ryan_wu
 * Email:  IMITATOR_WU@OUTLOOK.COM
 * Date:   16/4/17
 */
public class DetecFragment extends Fragment {
    public static final String SPLITOR = "\t";

    public boolean saveDetection(){
        Log.i("Detection Fragment", "save content: ");
        return false;
    }

    public void saveID(String ID) {
        String sdPath = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            sdPath = Environment.getExternalStorageDirectory().toString();
        }
        String filePath = sdPath + Config.BASE_DIR + Config.ID_DAT;
        File file = new File(filePath);
        try {
            synchronized (file) {
                FileWriter fw = new FileWriter(filePath);
                fw.write(ID.trim());
                fw.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadID() {
        String sdPath = "";
        String ID = "1";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            sdPath = Environment.getExternalStorageDirectory().toString();
        }
        File file = new File(sdPath + Config.BASE_DIR + Config.ID_DAT);
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                if (line != null && line.trim().length() > 0) {
                    ID = line.trim();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ID;
    }

    public String loadDetector() {
        String sdPath = "";
        String str_detector = "N/E";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            sdPath = Environment.getExternalStorageDirectory().toString();
        }
        File file = new File(sdPath + Config.BASE_DIR + Config.USER_DAT);
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                if (line != null && line.trim().length() > 0) {
                    str_detector = line.trim();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str_detector;
    }

    public void setData(char Type, String data) {
        Log.i("Detection", "save data" + Type + data);
    }
}
