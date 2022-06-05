package com.example.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

public class Camera2Service extends Service {
    protected static final int CAMERA_CALIBRATION_DELAY = 500;
    protected static final String TAG = "myLog";
    public static int CAMERACHOICE = CameraCharacteristics.LENS_FACING_BACK;
    private static final String BROADCAST_ACTION = "bc";
    protected static long cameraCaptureStartTime;
    protected CameraDevice cameraDevice;
    protected static CameraCaptureSession session;
    protected ImageReader imageReader;
    public int sizeBuf;
    int myBufferSize = 8192;
    AudioRecord audioRecord;
    boolean isReading = false;
    boolean create = false;
    final String IP_SERV="192.168.0.96";
    boolean isSending = false;
    protected static CameraCaptureSession ccs;
    int BB;
    byte [] BBB;
    int Q1;
    int Q2;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    File pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    File file = new File(pictures, "myphoto0.jpg");
    File pictures1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    File file1 = new File(pictures1, "1.txt");
    File file2 = new File(pictures1, "2.txt");

    protected CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.d(TAG, "CameraDevice.StateCallback onOpened");
            cameraDevice = camera;
            actOnReadyCameraDevice();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.w(TAG, "CameraDevice.StateCallback onDisconnected");
            camera.close();
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e(TAG, "CameraDevice.StateCallback onError " + error);
            camera.close();
            cameraDevice = null;
        }
    };

    protected CameraCaptureSession.StateCallback sessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onReady(CameraCaptureSession session) {
            Camera2Service.this.session = session;
            try {
                session.setRepeatingRequest(createCaptureRequest(), null, null);
                cameraCaptureStartTime = System.currentTimeMillis();
            } catch (CameraAccessException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        @Override
        public void onConfigured(CameraCaptureSession session) {
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        }
    };

    protected ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Log.d(TAG, "onImageAvailable");
            Image img = reader.acquireLatestImage();
            if (img != null ) {
                if (System.currentTimeMillis() > cameraCaptureStartTime + CAMERA_CALIBRATION_DELAY) {
                    processImage(img);
                }
                img.close();
            }
        }
    };

    public  void readyCamera()  {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
                String pickedCamera = getCamera(manager);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (session!=null) {session.close(); session=null;}
                if (cameraDevice!=null) {cameraDevice.close(); cameraDevice=null;}
                manager.openCamera(pickedCamera, cameraStateCallback, null);
                imageReader = ImageReader.newInstance(Q1, Q2, ImageFormat.YUV_420_888, 10 /* images buffered */);
                imageReader.setOnImageAvailableListener(onImageAvailableListener, null);

        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public String getCamera(CameraManager manager) {
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (cOrientation == CAMERACHOICE) {
                    return cameraId;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand flags " + flags + " startId " + startId);
        File f = new File(String.valueOf(file1));
        BufferedReader buf = null;
        try {
            buf = new BufferedReader(new FileReader(f));
            String s;
            while((s = buf.readLine()) != null){
                if (s.equals("0")) {Q1=1920; Q2=1080;}
                if (s.equals("1")) {Q1=1280;Q2=720;}
                if (s.equals("2")) {Q1=640;Q2=480;}
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File f1 = new File(String.valueOf(file2));
        BufferedReader buf1 = null;
        try {
            buf1 = new BufferedReader(new FileReader(f1));
            String s;
            while((s = buf1.readLine()) != null){
                if (s.equals("0")) {CAMERACHOICE=CameraCharacteristics.LENS_FACING_BACK;}
                else {CAMERACHOICE=CameraCharacteristics.LENS_FACING_FRONT;}
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        readyCamera();
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate service");
        super.onCreate();
        MyLocationListener.SetUpLocationListener(this);
        Timer timer = new Timer();
        MyTimerTask mt = new MyTimerTask();
        timer.schedule(mt, 0, 5000);
        createAudioRecorder();
        Log.d(TAG, "init state = " + audioRecord.getState());
        audioRecord.startRecording();
        int recordingState = audioRecord.getRecordingState();
        Log.d(TAG, "recordingState = " + recordingState);
        readStart();
        Q q = new Q();
        q.start();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    public void actOnReadyCameraDevice() {
        try {
            cameraDevice.createCaptureSession(Arrays.asList(imageReader.getSurface()), sessionStateCallback, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent("bc");
        sendBroadcast(intent);
        session.close();
    }

    synchronized  private void processImage(Image image) {
      //Process image data
      File pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
      File file = new File(pictures, "myphoto0.jpg");

      if (image.getFormat() == ImageFormat.YUV_420_888) {
          ByteArrayOutputStream outputbytes = new ByteArrayOutputStream();
          ByteBuffer bufferY = image.getPlanes()[0].getBuffer();
          byte[] data0 = new byte[bufferY.remaining()];
          bufferY.get(data0);
          ByteBuffer bufferU = image.getPlanes()[1].getBuffer();
          byte[] data1 = new byte[bufferU.remaining()];
          bufferU.get(data1);
          ByteBuffer bufferV = image.getPlanes()[2].getBuffer();
          byte[] data2 = new byte[bufferV.remaining()];
          bufferV.get(data2);
          try
          {
              outputbytes.write(data0);
              outputbytes.write(data2);
              outputbytes.write(data1);
              final YuvImage yuvImage = new YuvImage(outputbytes.toByteArray(), ImageFormat.NV21, image.getWidth(),image.getHeight(), null);
              ByteArrayOutputStream outBitmap = new ByteArrayOutputStream();
              yuvImage.compressToJpeg(new Rect(0, 0,image.getWidth(), image.getHeight()), 10, outBitmap);
              FileOutputStream outputfile = null;
              outputfile = new FileOutputStream(file);
              outputfile.write(outBitmap.toByteArray());
          }
          catch (IOException e)
          {
              e.printStackTrace();
          }
          finally
          {}
      }

      TT t = new TT();
      t.start();
      TT1 t1 = new TT1();
      t1.start();
      image.close();
    }

    protected CaptureRequest createCaptureRequest() {
        try {
            CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            builder.addTarget(imageReader.getSurface());
            return builder.build();
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class TT extends Thread {
        public void run() {
            super.run();
            try{
               Socket socket = new Socket(IP_SERV,6666);
               DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
               FileInputStream fileStream = new FileInputStream(file);
               byte[] buffer = new byte[ (int)file.length()];
               int bytesRead = 0;
               outStream.writeInt((int)file.length());
               while ((bytesRead=fileStream.read(buffer)) > 0)
               {
                   outStream.write(buffer,0,bytesRead);
               }
               outStream.close();
               fileStream.close();
            }
             catch (IOException e) {}
        }
    }

class TT1 extends Thread {
    public void run() {
        super.run();
        try {
            Socket socket = new Socket(InetAddress.getByName(IP_SERV), 6667);
        } catch (IOException e) {}
    }
}

    static class MyLocationListener implements LocationListener {
        static Location imHere;
        static boolean enable;
        public static void SetUpLocationListener(Context context) // это нужно запустить в самом начале работы программы
        {
            final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new MyLocationListener();
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    10,
                    locationListener);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        enable = locationManager
                                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                        imHere = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
            }
            ).start();
        }

        @Override
        public void onLocationChanged(Location loc) {
            imHere = loc;
        }
        @Override
        public void onProviderDisabled(String provider) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    public void readStart() {
        Log.d(TAG, "read start");
        isReading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (audioRecord == null)
                    return;
                byte[] myBuffer = new byte[myBufferSize];
                int readCount = 0;
                int totalCount = 0;
                while (isReading) {
                    readCount = audioRecord.read(myBuffer, 0, myBufferSize);
                    try {
                        Socket socket = new Socket(InetAddress.getByName(IP_SERV), 6669);
                        socket.getOutputStream().write(myBuffer, 0, readCount);
                        socket.getOutputStream().flush();
                        socket.getOutputStream().close();
                    }
                    catch (IOException e) {}
                    //-----------------------------------------------------------------------
                    totalCount += readCount;
                    Log.d(TAG, "readCount = " + readCount + ", totalCount = " + totalCount);
                }
            }
        }).start();
    }

    void createAudioRecorder() {
        int sampleRate = 8000;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int minInternalBufferSize = AudioRecord.getMinBufferSize(sampleRate,
                channelConfig, audioFormat);
        int internalBufferSize = minInternalBufferSize * 4;
        sizeBuf=internalBufferSize;
        Log.d(TAG, "minInternalBufferSize = " + minInternalBufferSize
                + ", internalBufferSize = " + internalBufferSize
                + ", myBufferSize = " + myBufferSize);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, internalBufferSize);
    }

    class TTT extends Thread {
        public void run() {
            super.run();
            while(true) {
                try {
                String geoURI = String.format(Locale.ENGLISH, "geo:%f,%f?z=10&q=", MyLocationListener.imHere.getLatitude(), MyLocationListener.imHere.getLongitude());
                geoURI = geoURI.replaceAll(",", ".");
                geoURI = geoURI.replaceAll(";", ",");
                Socket socket1 = new Socket(InetAddress.getByName(IP_SERV), 6668);
                DataOutputStream out = new DataOutputStream(socket1.getOutputStream());
                out.writeUTF(geoURI);
                out.flush();
                socket1.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public  class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            if (MyLocationListener.enable)
            { if (create==false) {
                TTT ttt = new TTT();
                ttt.start();
                create=true;
            }
            } else {if (create==true) create=false;}
        }
    }

    class Q extends Thread {
        public void run() {
            super.run();
            try {
                ServerSocket ss00 = new ServerSocket(6665);
                while (true) {
                    Socket socket10 = ss00.accept();
                    DataInputStream in = new DataInputStream(socket10.getInputStream());
                    String cam = in.readUTF();
                    try (FileWriter writer = new FileWriter(file2, false)) {
                        String text = cam;
                        writer.write(text);
                        writer.flush();
                    } catch (IOException ex) {

                        System.out.println(ex.getMessage());
                    }
                    if (cam.equals("0")) {
                        CAMERACHOICE = CameraCharacteristics.LENS_FACING_BACK;
                    } else {
                        CAMERACHOICE = CameraCharacteristics.LENS_FACING_FRONT;
                    }
                    String qeulity = in.readUTF();
                    if (qeulity.equals("0")) {
                        Q1 = 1920;
                        Q2 = 1080;
                    }
                    if (qeulity.equals("1")) {
                        Q1 = 1280;
                        Q2 = 720;
                    }
                    if (qeulity.equals("2")) {
                        Q1 = 640;
                        Q2 = 480;
                    }
                    try (FileWriter writer = new FileWriter(file1, false)) {
                        String text = qeulity;
                        writer.write(text);
                        writer.flush();
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                     Intent intent = new Intent("bc");
                     sendBroadcast(intent);}
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
    }
}
