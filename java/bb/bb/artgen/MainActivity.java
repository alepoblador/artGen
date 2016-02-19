package bb.bb.artgen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private PictureCallback mPicture;
    private Context myContext;
    private FrameLayout cameraPreview;
    private FloatingActionButton capture, switchCamera;
    private boolean cameraFront = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myContext = this;
        initialize();

/**        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });    **/

    }

    OnClickListener captureListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCamera.takePicture(null, null, mPicture);
        }
    };

    OnClickListener switchCameraListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int numberOfCameras = Camera.getNumberOfCameras();
            if (numberOfCameras > 1) {
                releaseCamera();
                switchCamera();
            } else {
                Toast toast = Toast.makeText(myContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    };

    public void switchCamera() {
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                mCamera = Camera.open(cameraId);
                mPicture = savePictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
         else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                mCamera = Camera.open(cameraId);
                mPicture = savePictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    public void initialize() {
        // place mPreview in FrameLayout
        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview = (FrameLayout) findViewById(R.id.camera_preview);
        cameraPreview.addView(mPreview);

        // set capture listener
        capture = (FloatingActionButton) findViewById(R.id.button_capture);
        capture.setOnClickListener(captureListener);

        // set switchCamera listener
        switchCamera = (FloatingActionButton) findViewById(R.id.button_switch);
        switchCamera.setOnClickListener(switchCameraListener);
    }

    // get access to camera and display live image data here
    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "No camera detected!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            mCamera = Camera.open(findBackFacingCamera());
            mPicture = savePictureCallback();
            mPreview.refreshCamera(mCamera);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    private boolean hasCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private PictureCallback savePictureCallback() {
        PictureCallback picture = new PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                File pictureFile = getOutputMediaFile();

                if (pictureFile == null) {
                    return;
                }
                try {
                    // write the picture data to the file
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    Toast toast = Toast.makeText(myContext, "Picture saved: " + pictureFile.getName(), Toast.LENGTH_LONG);
                    toast.show();
                } catch (FileNotFoundException e) {

                } catch (IOException e) {

                }

                // send image path to filter screen and start activity
                releaseCamera();
                Intent filterScreen = new Intent(getApplicationContext(), FilterActivity.class);
                filterScreen.putExtra("picturePath", pictureFile.getAbsolutePath());

                startActivity(filterScreen);
            }
        };
        return picture;
    }

    // make folder and file for picture
    private static File getOutputMediaFile() {
        // make a new directory inside "sdcard" folder
        String sdcard = Environment.getExternalStorageDirectory().getPath();
        File mediaStorageDir = new File(sdcard, "ArtGen");

        // if "ArtGen" folder does not exist
        if ( !mediaStorageDir.exists() ) {
            // if you cannot make the folder return
            if ( !mediaStorageDir.mkdirs() ) {
                return null;
            }
        }

        // take current timeStamp
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        // and make a new media file
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

}
