package com.example.cool1024.android_example.fragments;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cool1024.android_example.R;
import com.qs.helper.printer.BlueToothService;
import com.qs.helper.printer.PrinterClass;
import com.qs.helper.printer.bt.BtService;

import java.util.ArrayList;

public class BluetoothFragment extends Fragment implements
        BlueToothService.OnReceiveDataHandleEvent, View.OnClickListener {

    public final static String TAG = "BluetoothFragmentLog";
    private BlueToothService mBlueToothService;
    private BtService mBTService;
    private ArrayList<BluetoothDevice> mBluetoothDevices = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mBTService = new BtService(this.getContext(),
                BluetoothServiceHandler.getInstance(), null);
        this.mBlueToothService = BtService.mBTService;
        if (!this.mBlueToothService.IsOpen()) {
            this.mBlueToothService.OpenDevice();
        }
        this.mBlueToothService.setOnReceive(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mBlueToothService.StopScan();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        mainView.findViewById(R.id.scan_device).setOnClickListener(this);
        mainView.findViewById(R.id.con_device).setOnClickListener(this);
        mainView.findViewById(R.id.print_device).setOnClickListener(this);
        return mainView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.con_device: {
                Log.d(TAG, "连接到新设备");
                this.mBlueToothService.ConnectToDevice("66:12:9A:A2:1B:AD");
                break;
            }
            case R.id.scan_device: {
                Log.d(TAG, "重新扫描设备");
                this.mBluetoothDevices.clear();
                this.mBlueToothService.ScanDevice();
            }
            case R.id.print_device: {
                Log.d(TAG, "发送打印数据到设备");
                this.mBTService.printText("测试打印机数据");
                this.mBTService.printText("\n");
                this.mBTService.write(new byte[]{0x1d, 0x0c});
            }
        }
    }

    @Override
    public void OnReceive(BluetoothDevice device) {
        if (device != null) {
            this.mBluetoothDevices.add(device);
            Log.d(TAG, "设备数量:" + this.mBluetoothDevices.size());
            Log.d(TAG, String.format("设备名称:%s,设备地址:%s", device.getName(), device.getAddress()));
        } else {
            Log.d(TAG, "没有找到合适的设备");
        }
    }

    static class BluetoothServiceHandler extends Handler {

        private static BluetoothServiceHandler sInstance;

        public static BluetoothServiceHandler getInstance() {
            if (sInstance == null) {
                sInstance = new BluetoothServiceHandler();
            }
            return sInstance;
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "接收到蓝牙服务消息,状态码" + msg.what);
            if (msg.what == 1) {
                switch (msg.arg1) {
                    case PrinterClass.STATE_CONNECTING: {
                        Log.d(TAG, "正在连接到设备");
                        break;
                    }
                    case PrinterClass.SUCCESS_CONNECT: {
                        Log.d(TAG, "设备连接成功");
                        break;
                    }
                    case PrinterClass.FAILED_CONNECT: {
                        Log.d(TAG, "设备连接失败");
                        break;
                    }
                    case PrinterClass.LOSE_CONNECT: {
                        Log.d(TAG, "设备断开连接");
                        break;
                    }
                }
            }
        }
    }
}
