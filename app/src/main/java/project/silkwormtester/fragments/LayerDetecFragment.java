package project.silkwormtester.fragments;


import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileWriter;
import java.io.IOException;

import project.silkwormtester.R;
import project.silkwormtester.localdata.Config;

/**
 * Author: ryan_wu
 * Email:  IMITATOR_WU@OUTLOOK.COM
 * Date:   16/3/19
 */
public class LayerDetecFragment extends DetecFragment {
    private EditText customer;
    private EditText id;
    private EditText gloss;
    private EditText badCocoon;
    private EditText shangPercentage;
    private EditText notGood;
    private EditText cocoonCount;
    private EditText goodPercentage;
    private EditText layerWeight;
    private EditText layerPercentage;
    private EditText water;
    private EditText level;
    private EditText soldWeight;

    private TextView badPostfix;

    private String str_customer;
    private String str_id;
    private String str_gloss;
    private String str_badCocoon;
    private String str_shangPercentage;
    private String str_notGood;
    private String str_cocoonCount;
    private String str_goodPercentage;
    private String str_layerWeight;
    private String str_layerPercnetage;
    private String str_water;
    private String str_level;
    private String str_soldWeight;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.layer_detec_fragment, container, false);
        customer = (EditText) view.findViewById(R.id.id_custom_content);
        id = (EditText) view.findViewById(R.id.id_id_content);
        layerWeight = (EditText) view.findViewById(R.id.id_layer_weight_content);
        layerPercentage = (EditText) view.findViewById(R.id.id_layer_percentage_content);
        soldWeight = (EditText) view.findViewById(R.id.id_sold_content);
        gloss = (EditText) view.findViewById(R.id.id_gloss_content);
        badCocoon = (EditText) view.findViewById(R.id.id_bad_cocoon_content);
        shangPercentage = (EditText) view.findViewById(R.id.id_shang_content);
        cocoonCount = (EditText) view.findViewById(R.id.id_cocoon_count_content);
        notGood = (EditText) view.findViewById(R.id.id_not_good_content);
        goodPercentage = (EditText) view.findViewById(R.id.id_good_percentage_content);
        water = (EditText) view.findViewById(R.id.id_water_content);
        level = (EditText) view.findViewById(R.id.id_level_content);
        badPostfix = (TextView) view.findViewById(R.id.id_bad_cocoon_postfix);
        badPostfix.setText("g");
        badPostfix.setTextSize(30);
        badCocoon.setHint("克重");
        id.setText(loadID());
        return view;
    }

    @Override
    public boolean saveDetection(){
        if(!strCheck()) {
            return false;
        }
        String str_detector = loadDetector();
        String content = str_detector + SPLITOR +
                str_customer + SPLITOR +
                str_id + SPLITOR +
                str_gloss + SPLITOR +
                str_badCocoon + SPLITOR +
                str_shangPercentage + SPLITOR +
                str_notGood + SPLITOR +
                str_cocoonCount + SPLITOR +
                str_goodPercentage + SPLITOR +
                str_layerWeight + SPLITOR +
                str_layerPercnetage + SPLITOR +
                str_water + SPLITOR +
                str_level + SPLITOR +
                str_soldWeight + "\n";
        String sdPath = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            sdPath = Environment.getExternalStorageDirectory().toString();
        }
        String fileName = sdPath + Config.BASE_DIR + Config.DETECTION_DAT;
        try  {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName,  true);
            writer.write(content);
            writer.close();
        } catch  (IOException e) {
            e.printStackTrace();
            return false;
        }
        int id_s = Integer.parseInt(str_id) + 1;
        saveID(String.valueOf(id_s));
        return true;
    }

    private boolean strCheck() {
        str_customer = customer.getText().toString();
        str_id = id.getText().toString();
        str_gloss = gloss.getText().toString();
        str_badCocoon = badCocoon.getText().toString();
        str_shangPercentage = shangPercentage.getText().toString();
        str_notGood = notGood.getText().toString();
        str_cocoonCount = cocoonCount.getText().toString();
        str_goodPercentage = goodPercentage.getText().toString();
        str_water = water.getText().toString();
        str_layerWeight = layerWeight.getText().toString();
        str_layerPercnetage = layerPercentage.getText().toString();
        str_level = level.getText().toString();
        str_soldWeight = soldWeight.getText().toString();
        if(str_customer == null || str_customer.trim().length() <= 0){
            Toast.makeText(getContext(), ErrorInfo.ERR_CUSTOM, Toast.LENGTH_LONG).show();
            return false;
        }
        if(str_id == null || str_id.trim().length() <= 0){
            Toast.makeText(getContext(), ErrorInfo.ERR_ID, Toast.LENGTH_LONG).show();
            return false;
        }
        if(str_gloss == null || str_gloss.trim().length() <= 0){
            Toast.makeText(getContext(), ErrorInfo.ERR_GLOSS, Toast.LENGTH_LONG).show();
            return false;
        }
        if(str_badCocoon == null || str_badCocoon.trim().length() <= 0){
            Toast.makeText(getContext(), ErrorInfo.ERR_BADCOCOON, Toast.LENGTH_LONG).show();
            return false;
        }
        if(str_shangPercentage == null || str_shangPercentage.trim().length() <= 0){
            Toast.makeText(getContext(), ErrorInfo.ERR_SHANGPERCENTAGE, Toast.LENGTH_LONG).show();
            return false;
        }
        if(str_notGood == null || str_notGood.trim().length() <= 0){
            Toast.makeText(getContext(), ErrorInfo.ERR_NOTGOOD, Toast.LENGTH_LONG).show();
            return false;
        }
        if(str_cocoonCount == null || str_cocoonCount.trim().length() <= 0){
            Toast.makeText(getContext(), ErrorInfo.ERR_COCOONCOUNT, Toast.LENGTH_LONG).show();
            return false;
        }
        if(str_goodPercentage == null || str_goodPercentage.trim().length() <= 0){
            Toast.makeText(getContext(), ErrorInfo.ERR_GOODPERCENTAGE, Toast.LENGTH_LONG).show();
            return false;
        }
        if(str_layerWeight == null || str_layerWeight.trim().length() <= 0){
            Toast.makeText(getContext(), ErrorInfo.ERR_LAYER, Toast.LENGTH_LONG).show();
            return false;
        }
        if(str_layerPercnetage == null || str_layerPercnetage.trim().length() <= 0){
            Toast.makeText(getContext(), ErrorInfo.ERR_LAYERPERCENTAGE, Toast.LENGTH_LONG).show();
            return false;
        }
        if(str_water == null || str_water.trim().length() <= 0){
            Toast.makeText(getContext(), ErrorInfo.ERR_WATER, Toast.LENGTH_LONG).show();
            return false;
        }
        if(str_level == null || str_level.trim().length() <= 0){
            Toast.makeText(getContext(), ErrorInfo.ERR_LEVEL, Toast.LENGTH_LONG).show();
            return false;
        }
        if(str_soldWeight == null || str_soldWeight.trim().length() <= 0){
            Toast.makeText(getContext(), ErrorInfo.ERR_SOLDWEIGHT, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void setData(char Type, String data) {
        switch (Type) {
            case '0':
                gloss.setText(data);
                break;
            case '1':
                badCocoon.setText(data);
                break;
            case '2':
                shangPercentage.setText(data);
                break;
            case '3':
                notGood.setText(data);
                break;
            case '4':
                goodPercentage.setText(data);
                break;
            case '7':
                layerWeight.setText(data);
                break;
            case '8':
                water.setText(data);
                break;
            case '9':
                level.setText(data);
                break;
            case 'a':
                soldWeight.setText(data);
                break;
            case 'c':
                layerPercentage.setText(data);
                break;
            case 'd':
                cocoonCount.setText(data);
                break;
            default:
                Toast.makeText(getContext(), ErrorInfo.ERR_TRANSDATA, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
