package project.silkwormtester.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import project.silkwormtester.R;


public class ScanDeviceActivity extends AppCompatActivity implements MyItemClickListener {

    private Toolbar mToolbar;
    private ImageButton mFabButton;
    private List<String> itemList;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 5000;

    private BluetoothAdapter mBluetoothAdapter;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private volatile boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppThemeRed);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_one);

        initToolbar();
        mFabButton = (ImageButton) findViewById(R.id.fabButton);
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    scanLeDevice(true);
                    mFabButton.setImageResource(R.drawable.ic_not_interested_white_24dp);
                    isRunning = true;
                } else {
                    scanLeDevice(false);
                    mFabButton.setImageResource(R.drawable.ic_autorenew_white_24dp);
                    isRunning = false;
                }
            }
        });

        mHandler = new Handler();
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "不支持BLE", Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "不支持BLE", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //initRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {

                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        }

        initRecyclerView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mFabButton.setImageResource(R.drawable.ic_autorenew_white_24dp);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            mFabButton.setImageResource(R.drawable.ic_not_interested_white_24dp);
            Toast.makeText(getApplicationContext(), "正在扫描设备...5秒后自动停止扫描,可点击右下角按钮手动停止", Toast.LENGTH_LONG).show();
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mFabButton.setImageResource(R.drawable.ic_autorenew_white_24dp);
        }
    }


    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle("设备扫描");
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    }


    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        mLeDeviceListAdapter.setMyItemClickListener(this);
        recyclerView.setAdapter(mLeDeviceListAdapter);

        recyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });
        scanLeDevice(true);
        mLeDeviceListAdapter.setMyItemClickListener(this);

    }

    private void hideViews() {
        mToolbar.animate().translationY(-mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFabButton.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        mFabButton.animate().translationY(mFabButton.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showViews() {
        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        mFabButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    @Override
    public void onItemClick(View view, int postion) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(postion-1);
        if (device == null) return;
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        startActivity(intent);
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLeDeviceListAdapter.addDevice(device);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };

    class RecyclerHeaderViewHolder extends RecyclerView.ViewHolder {
        public RecyclerHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }


    class RecyclerItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mDeviceName;
        private final TextView mDeviceAddress;
        private MyItemClickListener myItemClickListener;

        public RecyclerItemViewHolder(View args, MyItemClickListener listener) {
            super(args);
            mDeviceName = (TextView) args.findViewById(R.id.device_name);
            mDeviceAddress = (TextView) args.findViewById(R.id.device_address);
            myItemClickListener = listener;
            args.setOnClickListener(this);
        }

        /*
            public static RecyclerItemViewHolder newInstance(View parent) {
                TextView itemTextView = (TextView) parent.findViewById(R.id.itemTextView);
                return new RecyclerItemViewHolder(parent, itemTextView);
            }
        */
        public void setNameAddress(String name, String address) {
            mDeviceName.setText(name);
            mDeviceAddress.setText(address);
        }

        @Override
        public void onClick(View v) {
            if (myItemClickListener != null) {
                myItemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    class LeDeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_HEADER = 2;
        private static final int TYPE_ITEM = 1;
        private List<BluetoothDevice> mLeDevices;
        private MyItemClickListener myItemClickListener;

        public LeDeviceListAdapter() {
            mLeDevices = new ArrayList<BluetoothDevice>();
        }
        /*
        public LeDeviceListAdapter(List<String> itemList) {
            mItemList = itemList;
        }*/

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            if (viewType == TYPE_ITEM) {
                final View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
                RecyclerItemViewHolder recyclerItemViewHolder = new RecyclerItemViewHolder(view, myItemClickListener);
                return recyclerItemViewHolder;
            } else if (viewType == TYPE_HEADER) {
                final View view = LayoutInflater.from(context).inflate(R.layout.recycler_header, parent, false);
                return new RecyclerHeaderViewHolder(view);
            }
            throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            if (!isPositionHeader(position)) {
                RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;

                BluetoothDevice device = mLeDevices.get(position-1);
                final String deviceName = device.getName();
                final String deviceAddress = device.getAddress();
                if (deviceName != null && deviceName.length() > 0) {
                    holder.setNameAddress(deviceName, deviceAddress);
                }
                else {
                    holder.setNameAddress("Unknown device", "FF:FF:FF:FF:FF:FF");
                }
            }
        }

        public int getBasicItemCount() {
            return mLeDevices == null ? 0 : mLeDevices.size();
        }


        @Override
        public int getItemViewType(int position) {
            if (isPositionHeader(position)) {
                return TYPE_HEADER;
            }

            return TYPE_ITEM;
        }

        @Override
        public int getItemCount() {
            return getBasicItemCount() + 1; // header
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }

        public void setMyItemClickListener(MyItemClickListener myItemClickListener) {
            this.myItemClickListener = myItemClickListener;
        }
    }


    abstract class HidingScrollListener extends RecyclerView.OnScrollListener {

        private static final int HIDE_THRESHOLD = 20;

        private int mScrolledDistance = 0;
        private boolean mControlsVisible = true;


        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

            if (firstVisibleItem == 0) {
                if (!mControlsVisible) {
                    onShow();
                    mControlsVisible = true;
                }
            } else {
                if (mScrolledDistance > HIDE_THRESHOLD && mControlsVisible) {
                    onHide();
                    mControlsVisible = false;
                    mScrolledDistance = 0;
                } else if (mScrolledDistance < -HIDE_THRESHOLD && !mControlsVisible) {
                    onShow();
                    mControlsVisible = true;
                    mScrolledDistance = 0;
                }
            }
            if ((mControlsVisible && dy > 0) || (!mControlsVisible && dy < 0)) {
                mScrolledDistance += dy;
            }
        }

        public abstract void onHide();

        public abstract void onShow();
    }
}