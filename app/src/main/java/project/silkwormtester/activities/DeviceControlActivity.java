/**
 * @Authon Zhilong Zheng
 * @Email zhengzl0715@163.com
 * @Date 10:52 16/1/16
 */
package project.silkwormtester.activities;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import project.silkwormtester.R;
import project.silkwormtester.bleservice.BluetoothLeService;
import project.silkwormtester.bleservice.SilkwormCallback;
import project.silkwormtester.bleservice.SilkwormProtocol;

//测试蓝牙通信的activity
public class DeviceControlActivity extends Activity implements SilkwormCallback {
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
                finish();
            }
            // 直接连接
            mBluetoothLeService.connect(mDeviceAddress);
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
                    updateConnectionState(R.string.connected);
                    Toast.makeText(getApplication(), "检测到蓝牙设备断开连接,请重新连接", Toast.LENGTH_LONG).show();
                    break;
                case BluetoothLeService.ACTION_DISCONNECTED:
                    mConnected = false;
                    updateConnectionState(R.string.disconnected);
                    break;
                case BluetoothLeService.ACTION_DATA_AVAILABLE:
                    //收到数据时,传给protocl处理
                    displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                    silkwormProtocol.setRecvData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                    break;
                default:
                    //no-op
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        //ble 名字
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        //ble 地址
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);

        //绑定service, 必须
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        //测试,发数据
        final EditText sendDataText = (EditText)findViewById(R.id.sendDataText);
        Button sendDataBtn = (Button)findViewById(R.id.sendDataBtn);
        sendDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(TextUtils.isEmpty(sendDataText.getText()))) {
                    String data = sendDataText.getText().toString();
                    mBluetoothLeService.setCharacteristicNotification(CC2540_char, true);
                    CC2540_char.setValue(data.getBytes());
                    mBluetoothLeService.writeCharacteristic(CC2540_char);
                }
            }
        });

        silkwormProtocol = new SilkwormProtocol();
        silkwormProtocol.setSilkwormCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());  //注册完后启动
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
        silkwormProtocol.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
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
    }

    //收到要显示内容,为该内容的类型type, 数据data. 具体参照协议的定义
    @Override
    public void onContentAvai(String type, String data) {
        Log.i(TAG, "type: " + type + ", data: " + data);
    }

    //评测仪的数据完全发送后的回调,保存内容?
    @Override
    public void onCompletedData() {
        Log.i(TAG, "Completed!");
    }
}
