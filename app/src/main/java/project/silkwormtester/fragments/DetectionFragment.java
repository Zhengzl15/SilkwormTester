package project.silkwormtester.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import project.silkwormtester.R;

public class DetectionFragment extends Fragment implements View.OnClickListener{
	private View view;
	private Button submitButton;
	private AllDetecFragment mAllDetecFragment;
	private DryDetecFragment mDryDetecFragment;
	private LayerDetecFragment mLayerDetecFragment;
	private TextView detectionTitle;

	private static final String TAG = "detection";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view == null) {
			view = View.inflate(getActivity(), R.layout.detection_fragment, null);
			submitButton = (Button) view.findViewById(R.id.submit_button);
			submitButton.setOnClickListener(this);
			setDefaultLayout();
		}else if(view.getParent() != null){
			((ViewGroup) view.getParent()).removeView(view);
		}
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.submit_button:
				Log.i(TAG, String.format("set......."));
				break;
		}
		if (mDryDetecFragment == null) {
			mDryDetecFragment = new DryDetecFragment();
		}
		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.id_content, mDryDetecFragment);
		transaction.commit();
		detectionTitle.setText("干壳量法");
	}

	public void setTitle(TextView detectionTitle) {
		this.detectionTitle = detectionTitle;
	}

	private void setDefaultLayout() {
		mAllDetecFragment = new AllDetecFragment();
		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.add(R.id.id_content, mAllDetecFragment, "all");
		transaction.commit();
	}
}
