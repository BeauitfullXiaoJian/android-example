package com.example.cool1024.android_example.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.cool1024.android_example.R;
import com.example.cool1024.android_example.components.AutoFitCameraTextureView;

import java.util.Arrays;
import java.util.Collections;


public class CameraFragment extends BaseFragment {

    public static final String TAG = "LiveFragmentLog";
    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private Activity mParentActivity;
    private CameraManager mCameraManager;
    private CameraDevice mCameraDevice;
    private Size mCameraSize;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CameraCaptureSession mCameraCaptureSession;
    private AutoFitCameraTextureView mLivePreviewView;

    // 视频流显示视图监听对象
    private final TextureView.SurfaceTextureListener mTL = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            prepareCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    // 摄像头调用回调对象
    private final CameraDevice.StateCallback mTC = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            closeCamera();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            showToast("打开相机失败，请检查您的相机是否可用～");
        }

    };

    // 摄像头会话回调对象
    private final CameraCaptureSession.StateCallback mSC = new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
            mCameraCaptureSession = cameraCaptureSession;
            // Finally, we start displaying the camera preview.
            CaptureRequest captureRequest = mPreviewRequestBuilder.build();
            try {
                mCameraCaptureSession.setRepeatingRequest(captureRequest,
                        mCC, null);
            } catch (Exception e) {
                Log.d(TAG, "捕捉摄像头数据失败");
            }
        }

        @Override
        public void onConfigureFailed(
                @NonNull CameraCaptureSession cameraCaptureSession) {
            showToast("开启相机会话失败");
        }
    };

    // 摄像头数据捕获回调对象
    private final CameraCaptureSession.CaptureCallback mCC = new CameraCaptureSession.CaptureCallback() {

    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CameraFragment.
     */
    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        mParentActivity = getActivity();
        mLivePreviewView = view.findViewById(R.id.live_view);
        mLivePreviewView.setSurfaceTextureListener(mTL);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLivePreviewView.isAvailable()) {
            prepareCamera();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pausePreview();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
         if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            prepareCamera();
         }
//         else {
//             requestCameraPermission();
//         }
    }

    /**
     * 准备摄像头
     */
    private void prepareCamera() {
        Log.d(TAG, "准备摄像头");
        if (ContextCompat.checkSelfPermission(mParentActivity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // 如果没有摄像头权限去获取权限
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
                // 设置显示视图尺寸4160x3120
                Log.d(TAG, "设置相机尺寸为" + mCameraSize.toString());
                mLivePreviewView.setCameraSize(new Size(3120, 4160));
                // 打开摄像头
                mCameraManager.openCamera(cameraId, mTC, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                Log.d(TAG, "摄像头获取失败");
                openPermissionSetting();
            } catch (NullPointerException e) {
                e.printStackTrace();
                Log.d(TAG, "设备不支持Camera2API");
            }
        }
    }

    /**
     * 创建预览对话
     */
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mLivePreviewView.getSurfaceTexture();
            texture.setDefaultBufferSize(mCameraSize.getWidth(), mCameraSize.getHeight());
            Surface surface = new Surface(texture);
            Surface[] surfaces = {surface};
            // 创建一个预览请求
            createCameraPreviewRequest(surface);
            // 创建一个预览会话
            mCameraDevice.createCaptureSession(Arrays.asList(surfaces), mSC, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.d(TAG, "开启摄像头预览失败");
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.d(TAG, "使用了异常空对象");
        }
        Log.d(TAG, "开启摄像头");

    }

    /**
     * 创建预览请求
     */
    private void createCameraPreviewRequest(Surface surface) throws CameraAccessException {
        mPreviewRequestBuilder
                = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        // 启用自动对焦
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
        // 启用闪光灯
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        mPreviewRequestBuilder.addTarget(surface);
    }

    /**
     * 关闭相机
     */
    private void closeCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
            Log.d(TAG, "关闭相机");
        }
    }

    /**
     * 关闭预览
     */
    private void pausePreview() {
        if (mCameraCaptureSession != null) {
            try {
                mCameraCaptureSession.stopRepeating();
                Log.d(TAG, "停止预览");
            } catch (CameraAccessException e) {
                e.printStackTrace();
                Log.d(TAG, "停止预览异常");
            }
        }
    }

    /**
     * 获取后置摄像头
     *
     * @return {String} 摄像头编号
     * @throws CameraAccessException 相机获取异常
     */
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
            // 摄像头尺寸为可拍摄的最大值
            mCameraSize = Collections.max(
                    Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    (Size lhs, Size rhs) ->
                            Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                                    (long) rhs.getWidth() * rhs.getHeight())

            );
            activeCameraId = cameraId;
            break;
        }
        return activeCameraId;
    }

    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    private void openPermissionSetting(){
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", mParentActivity.getPackageName(), null));
        startActivity(intent);
    }
}