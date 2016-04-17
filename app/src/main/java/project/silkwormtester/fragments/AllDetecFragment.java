package project.silkwormtester.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import project.silkwormtester.R;

/**
 * Author: ryan_wu
 * Email:  IMITATOR_WU@OUTLOOK.COM
 * Date:   16/3/19
 */
public class AllDetecFragment extends Fragment {
    private EditText customer;
    private EditText id;
    private EditText cocoonWeight;
    private EditText soldWeight;
    private EditText gloss;
    private EditText badCocoon;
    private EditText shangPercentage;
    private EditText notGood;
    private EditText goodPercentage;
    private EditText water;
    private EditText level;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.all_detec_fragment, container, false);
        customer = (EditText) view.findViewById(R.id.id_custom_content);
        id = (EditText) view.findViewById(R.id.id_id_content);
        cocoonWeight = (EditText) view.findViewById(R.id.id_cocoon_weight_content);
        soldWeight = (EditText) view.findViewById(R.id.id_sold_content);
        gloss = (EditText) view.findViewById(R.id.id_gloss_content);
        badCocoon = (EditText) view.findViewById(R.id.id_bad_cocoon_content);
        shangPercentage = (EditText) view.findViewById(R.id.id_shang_content);
        notGood = (EditText) view.findViewById(R.id.id_not_good_content);
        goodPercentage = (EditText) view.findViewById(R.id.id_good_percentage_content);
        water = (EditText) view.findViewById(R.id.id_water_content);
        level = (EditText) view.findViewById(R.id.id_level_content);
        return view;
    }
}
