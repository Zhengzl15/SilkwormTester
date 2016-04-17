package project.silkwormtester.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import project.silkwormtester.R;

/**
 * Author: ryan_wu
 * Email:  IMITATOR_WU@OUTLOOK.COM
 * Date:   16/3/19
 */
public class LayerDetecFragment extends Fragment {
    private EditText customer;
    private EditText id;
    private EditText cocoonWeight;
    private EditText layerPercentage;
    private EditText soldWeight;
    private EditText gloss;
    private EditText badCocoon;
    private EditText shangPercentage;
    private EditText cocoonCount;
    private EditText notGood;
    private EditText goodPercentage;
    private EditText water;
    private EditText level;

    private TextView badPostfix;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.layer_detec_fragment, container, false);
        customer = (EditText) view.findViewById(R.id.id_custom_content);
        id = (EditText) view.findViewById(R.id.id_id_content);
        cocoonWeight = (EditText) view.findViewById(R.id.id_cocoon_weight_content);
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
        return view;
    }
}
