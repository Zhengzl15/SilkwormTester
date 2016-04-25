package project.silkwormtester.fragments;


import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import project.silkwormtester.R;
import project.silkwormtester.activities.MainActivity;
import project.silkwormtester.bleservice.BluetoothLeService;
import project.silkwormtester.bleservice.SilkwormCallback;
import project.silkwormtester.bleservice.SilkwormProtocol;

public class DetectionFragment extends Fragment implements View.OnClickListener, SilkwormCallback {
	private View view;
	private Button submitButton;
	private DetecFragment mDetecFragment;
	private TextView detectionTitle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view == null) {
			view = View.inflate(getActivity(), R.layout.detection_fragment, null);
			submitButton = (Button) view.findViewById(R.id.submit_button);
			submitButton.setOnClickListener(this);
			switchFragment('x');
			initial_bluetooth();
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

	// copy from DeviceControlActivity
	private final static String TAG = "DeviceControlActivity";

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private TextView mConnectionState;
	private TextView mDataField;
	private String mDeviceName;
	private String mDeviceAddress;
	private BluetoothLeService mBluetoothLeService;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
			new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private boolean mConnected = false;
	private BluetoothGattCharacteristic mNotifyCharacteristic;

	private final String CC2540_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb";
	private BluetoothGattCharacteristic CC2540_char;

	private SilkwormProtocol silkwormProtocol;

	//连接ble service的变量
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				//finish();
			}
			// 直接连接
			boolean conn = mBluetoothLeService.connect(mDeviceAddress);
			if (conn) {
				Log.i(TAG, "true");
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	//接收的广播
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	//接收service的广播信息
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			switch (action) {
				case BluetoothLeService.ACTION_DISCOVERED:
					Log.i(TAG, "discovered");
					initCharacteristic(mBluetoothLeService.getSupportedGattServices());
					break;
				case BluetoothLeService.ACTION_CONNECTED:
					mConnected = true;
					Log.i(TAG, "connected");
					final boolean isOk = true;
					Toast.makeText(getContext(), "成功连接蓝牙设备", Toast.LENGTH_LONG).show();
					((MainActivity)getActivity()).setButtonText("断开设备连接");
					//给一定时延
					new Handler().postDelayed(new Runnable(){
						public void run() {
							Log.i(TAG, "delay");
							silkwormProtocol.start();
						}
					}, 3000);
					break;
				case BluetoothLeService.ACTION_DISCONNECTED:
					mConnected = false;
					Log.i(TAG, "disconnected");
					((MainActivity)getActivity()).setButtonText("连接设备");
					silkwormProtocol.reset();
					//Toast.makeText(getContext(), "检测到蓝牙设备断开连接,请重新连接", Toast.LENGTH_LONG).show();
					new AlertDialog.Builder(getContext())
							.setTitle("提示" )
							.setMessage("检测到蓝牙设备断开连接,请重新连接" )
							.setPositiveButton("确定" ,  null )
							.show();
					break;
				case BluetoothLeService.ACTION_DATA_AVAILABLE:
					//收到数据时,传给protocl处理
					//displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
					silkwormProtocol.setRecvData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
					break;
				default:
					//no-op
			}
		}
	};

	// change from onCreate
	public void initial_bluetooth() {
		final Intent intent = getActivity().getIntent();
		//ble 名字
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		//ble 地址
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
		if(mDeviceAddress == null || mDeviceName == null) {
			return;
		}
//		((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
//		mConnectionState = (TextView) findViewById(R.id.connection_state);
//		mDataField = (TextView) findViewById(R.id.data_value);

		//绑定service, 必须
		Intent gattServiceIntent = new Intent(getContext(), BluetoothLeService.class);
		getActivity().bindService(gattServiceIntent, mServiceConnection, getContext().BIND_AUTO_CREATE);

		//测试,发数据
//		final EditText sendDataText = (EditText)findViewById(R.id.sendDataText);
//		Button sendDataBtn = (Button)findViewById(R.id.sendDataBtn);
//		sendDataBtn.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (!(TextUtils.isEmpty(sendDataText.getText()))) {
//					String data = sendDataText.getText().toString();
//					mBluetoothLeService.setCharacteristicNotification(CC2540_char, true);
//					CC2540_char.setValue(data.getBytes());
//					mBluetoothLeService.writeCharacteristic(CC2540_char);
//				}
//			}
//		});
		silkwormProtocol = new SilkwormProtocol();
		silkwormProtocol.setSilkwormCallback(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "resumre");
		getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());  //注册完后启动
		if (mBluetoothLeService != null) {
			final boolean result = mBluetoothLeService.connect(mDeviceAddress);
			Log.d(TAG, "Connect request result=" + result);
			if (result) {
				Log.i(TAG, "start");
				silkwormProtocol.start();
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			getActivity().unregisterReceiver(mGattUpdateReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return;
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unbindService(mServiceConnection);
		mBluetoothLeService = null;
	}

	//初始化ble设备,这个是根据cc2540/1写的,配置已经写死了
	private void initCharacteristic(List<BluetoothGattService> gattServices) {
		if (gattServices == null) {
			return;
		}

		for (BluetoothGattService gattService : gattServices) {
			List<BluetoothGattCharacteristic> gattCharacteristics =
					gattService.getCharacteristics();
			for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				if (gattCharacteristic.getUuid().toString().equals(CC2540_UUID)) {
					CC2540_char = gattCharacteristic;
					final int charaProp = CC2540_char.getProperties();
					if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
						if (mNotifyCharacteristic != null) {
							mBluetoothLeService.setCharacteristicNotification(
									mNotifyCharacteristic, false);
							mNotifyCharacteristic = null;
						}
						mBluetoothLeService.readCharacteristic(CC2540_char);
					}
					if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
						mNotifyCharacteristic = CC2540_char;
						mBluetoothLeService.setCharacteristicNotification(
								CC2540_char, true);
					}
				} else {
					//no-op
				}
			}
		}
	}

	//下面是必须实现的回调
	//定义怎么发送数据,这个是死的,可以照着写
	@Override
	public void onSendData(String data) {
		if (!data.isEmpty() && mBluetoothLeService != null) {
			data = data + "\r\n";
			mBluetoothLeService.setCharacteristicNotification(CC2540_char, true);
			CC2540_char.setValue(data.getBytes());
			mBluetoothLeService.writeCharacteristic(CC2540_char);
		}
	}

	//收到切换显示数据时的回调, 切换fragment?
	//view值在SilkwormConstrain中定义,具体参照协议的定义
	@Override
	public void onChangedView(String view) {
		Log.i(TAG, view);
		switchFragment(view.trim().charAt(1));
	}

	//收到要显示内容,为该内容的类型type, 数据data. 具体参照协议的定义
	@Override
	public void onContentAvai(String type, String data) {
		Log.i(TAG, "type: " + type + ", data: " + data);
		mDetecFragment.setData(type.trim().charAt(0), data.trim());
	}

	//评测仪的数据完全发送后的回调,保存内容?
	@Override
	public void onCompletedData() {
		Log.i(TAG, "Completed!");
		Toast.makeText(getContext(), "数据接受完成", Toast.LENGTH_LONG).show();
	}

	public void disconnectBle() {
		if (silkwormProtocol != null) {
			silkwormProtocol.stop();
			silkwormProtocol.reset();
		}
		if (mBluetoothLeService != null) {
			mBluetoothLeService.disconnect();
		}
		try {
			getActivity().unregisterReceiver(mGattUpdateReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return;
		}
	}

}
