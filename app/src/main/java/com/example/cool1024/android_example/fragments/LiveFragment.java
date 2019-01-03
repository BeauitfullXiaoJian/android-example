package com.example.cool1024.android_example.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.example.cool1024.android_example.R;

import java.util.Arrays;


public class LiveFragment extends BaseTabFragment {

    public static final String TAG = "LiveFragmentLog";
    private static final String LIVE_URL = "LIVE_URL";
    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private Context mParentActivity;
    private String mLiveUrl;
    private CameraManager mCameraManager;
    private CameraDevice mCameraDevice;
    private TextureView mLivePreviewView;

    // 摄像头调用回调对象
    private final CameraDevice.StateCallback mTC = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {

        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {

        }

    };

    // 摄像头预览回调对象
    private final CameraCaptureSession.StateCallback mSC = new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {


        }

        @Override
        public void onConfigureFailed(
                @NonNull CameraCaptureSession cameraCaptureSession) {
            showToast("Failed");
        }
    };

    public LiveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param livUrl 直播地址.
     * @return A new instance of fragment LiveFragment.
     */
    public static LiveFragment newInstance(String livUrl) {
        LiveFragment fragment = new LiveFragment();
        Bundle args = new Bundle();
        args.putString(LIVE_URL, livUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParentActivity = getActivity();
            mLiveUrl = getArguments().getString(LIVE_URL);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_live, container, false);
        mLivePreviewView = mainView.findViewById(R.id.live_view);
        prepareCamera();
        return mainView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            prepareCamera();
        } else {
            requestCameraPermission();
        }
    }

    /**
     * 准备摄像头
     */
    private void prepareCamera() {
        // 判断是否有权限获取摄像头
        if (ContextCompat.checkSelfPermission(mParentActivity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        } else {
            try {
                // 获取摄像头管理对象
                mCameraManager = (CameraManager) mParentActivity
                        .getSystemService(Context.CAMERA_SERVICE);
                // 获取当前可用的摄像头
                String cameraId = getActiveCameraId();
                if (cameraId == null) {
                    showToast("当前没有可用摄像头");
                    return;
                }
                // 打开可用摄像头
                mCameraManager.openCamera(cameraId, mTC, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                Log.d(TAG, "摄像头获取失败");
            }
        }
    }

    /**
     * 开启预览
     */
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mLivePreviewView.getSurfaceTexture();
            texture.setDefaultBufferSize(600, 400);
            Surface surface = new Surface(texture);
            mCameraDevice.createCaptureSession(Arrays.asList(surface), mSC, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.d(TAG, "开启摄像头预览失败");
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.d(TAG, "使用了异常空对象");
        }

    }

    private String getActiveCameraId() throws CameraAccessException {
        String activeCameraId = null;
        for (String cameraId : mCameraManager.getCameraIdList()) {
            // 获取摄像头的特性信息
            CameraCharacteristics characteristics
                    = mCameraManager.getCameraCharacteristics(cameraId);
            // 如果是前置摄像头，弃用
            Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
            if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                continue;
            }
            // 获取图片输出的尺寸和预览画面输出的尺寸
            StreamConfigurationMap map = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (map == null) {
                continue;
            }
            activeCameraId = cameraId;
            break;
        }
        return activeCameraId;
    }

    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }
}
