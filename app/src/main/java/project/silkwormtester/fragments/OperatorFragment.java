package project.silkwormtester.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import project.silkwormtester.R;
import project.silkwormtester.activities.DetectionReaderActivity;
import project.silkwormtester.activities.DeviceScanActivity;
import project.silkwormtester.localdata.Config;

public class OperatorFragment extends Fragment implements View.OnClickListener{
	private static final String ERR_USERNAME_EMPTY = "检测人员姓名不能为空";

	private View view;
	private TextView detector;
	private EditText detector_input;
	private Button login;
	private Button look_up_history;
	private Button scan_bluetooth_devices;
	private String username = "请登录";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = View.inflate(getActivity(), R.layout.operator_fragment, null);
		setWidge();
		loadUserName();
		setOnClickListener();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void setOnClickListener() {
		login.setOnClickListener(this);
		look_up_history.setOnClickListener(this);
		scan_bluetooth_devices.setOnClickListener(this);
	}

	private void setWidge() {
		detector = (TextView) view.findViewById(R.id.text_detector);
		detector_input = (EditText) view.findViewById(R.id.input_username);
		login = (Button) view.findViewById(R.id.button_login);
		look_up_history = (Button) view.findViewById(R.id.button_look_up_history);
		scan_bluetooth_devices = (Button) view.findViewById(R.id.button_scan_bluetooth_device);
	}

	private void loadUserName() {
		String sdPath = "";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			sdPath = Environment.getExternalStorageDirectory().toString();
		}
		File file = new File(sdPath + Config.BASE_DIR + Config.USER_DAT);
		if (file.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = reader.readLine();
				if (line != null && line.trim().length() > 0) {
					username = line.trim();
					detector.setText(username);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void saveUserName(String name) {
		detector.setText(name);
		username = name;
		String sdPath = "";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			sdPath = Environment.getExternalStorageDirectory().toString();
		}
		String filePath = sdPath + Config.BASE_DIR + Config.USER_DAT;
		File file = new File(filePath);
		try {
			synchronized (file) {
				FileWriter fw = new FileWriter(filePath);
				fw.write(name);
				fw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_login:
				String name = detector_input.getText().toString();
				if(name != null && name.trim().length() > 0){
					saveUserName(name.trim());
					detector_input.setText("");
				} else {
					Toast.makeText(getContext(), ERR_USERNAME_EMPTY, Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.button_look_up_history:
				Intent intent = new Intent();
				intent.setClass(getActivity(), DetectionReaderActivity.class);
				startActivity(intent);
				break;
			case R.id.button_scan_bluetooth_device:
				Intent intent_scan = new Intent();
				intent_scan.setClass(getActivity(), DeviceScanActivity.class);
				startActivity(intent_scan);
//				getActivity().finish();//停止当前的Activity,如果不写,则按返回键会跳转回原来的Activity
				break;
		}
	}
}
