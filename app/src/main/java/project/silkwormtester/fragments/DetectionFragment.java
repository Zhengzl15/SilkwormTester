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
	private DetecFragment mDetecFragment;
	private TextView detectionTitle;

	private static final String TAG = "detection";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view == null) {
			view = View.inflate(getActivity(), R.layout.detection_fragment, null);
			submitButton = (Button) view.findViewById(R.id.submit_button);
			submitButton.setOnClickListener(this);
			switchFragment('z');
		}else if(view.getParent() != null){
			((ViewGroup) view.getParent()).removeView(view);
		}
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.submit_button:
				if(mDetecFragment.saveDetection()) {
					switchFragment('x');
				}
				Log.i(TAG, String.format("set......."));
				break;
		}
	}

	private void switchFragment(char type) {
		if(mDetecFragment != null) {
			mDetecFragment.onDestroy();
		}
		switch (type) {
			case 'x': {   //全茧量法
				mDetecFragment = new AllDetecFragment();
				FragmentManager fm = getChildFragmentManager();
				FragmentTransaction transaction = fm.beginTransaction();
				transaction.replace(R.id.id_content, mDetecFragment);
				transaction.commit();
				detectionTitle.setText("全茧量法");
				break;
			}
			case 'y': {   //干壳量法
				mDetecFragment = new DryDetecFragment();
				FragmentManager fm = getChildFragmentManager();
				FragmentTransaction transaction = fm.beginTransaction();
				transaction.replace(R.id.id_content, mDetecFragment);
				transaction.commit();
				detectionTitle.setText("干壳量法");
				break;
			}
			case 'z': {   //茧层量法
				mDetecFragment = new LayerDetecFragment();
				FragmentManager fm = getChildFragmentManager();
				FragmentTransaction transaction = fm.beginTransaction();
				transaction.replace(R.id.id_content, mDetecFragment);
				transaction.commit();
				detectionTitle.setText("茧层量法");
				break;
			}
		}
	}

	public void setTitle(TextView detectionTitle) {
		this.detectionTitle = detectionTitle;
	}

	@Override
	public void onResume() {
		super.onResume();
		setLayer();
	}

	private void setAll() {
		mDetecFragment.setData('0', "好");
		mDetecFragment.setData('1', "10");
		mDetecFragment.setData('2', "98");
		mDetecFragment.setData('3', "12");
		mDetecFragment.setData('4', "80");
		mDetecFragment.setData('5', "101");
		mDetecFragment.setData('8', "21");
		mDetecFragment.setData('9', "12");
		mDetecFragment.setData('a', "123");
	}

	private void setDry() {
		mDetecFragment.setData('0', "好");
		mDetecFragment.setData('1', "10");
		mDetecFragment.setData('2', "98");
		mDetecFragment.setData('3', "12");
		mDetecFragment.setData('4', "80");
		mDetecFragment.setData('6', "101");
		mDetecFragment.setData('8', "21");
		mDetecFragment.setData('9', "12");
		mDetecFragment.setData('a', "123");
		mDetecFragment.setData('b', "32");
		mDetecFragment.setData('d', "23");
	}

	private void setLayer() {
		mDetecFragment.setData('0', "好");
		mDetecFragment.setData('1', "10");
		mDetecFragment.setData('2', "98");
		mDetecFragment.setData('3', "12");
		mDetecFragment.setData('4', "80");
		mDetecFragment.setData('7', "101");
		mDetecFragment.setData('8', "21");
		mDetecFragment.setData('9', "12");
		mDetecFragment.setData('a', "123");
		mDetecFragment.setData('c', "78");
		mDetecFragment.setData('d', "25");
	}
}
