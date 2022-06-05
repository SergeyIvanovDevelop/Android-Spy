package com.example.server;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class AndroidSpyServer extends AppCompatActivity {
public  ImageView imageView;
public Uri urii;
public String qeulity="0";
public String location="0.0";
public String ip="000";
 public int bytesRead;
public int sizeBuf =2560;
public int myBufferSize = 8192;
final String TAG ="myLog";
public String cam = "0";
int Rot=0;
int img=0;
File picture = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
final   File file = new File(picture, "2.jpg");
public boolean isReading = false;
public String[] u= new String[10];

@Override
protected void onCreate(Bundle savedInstanceState) {
    for (int i=0;i<u.length;i++) {u[i]="0";}
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_server);
    ImageButton imageButton1 = (ImageButton)findViewById(R.id.imageButton4);
     imageView = findViewById(R.id.imageView);
    imageButton1.setOnClickListener(radioButtonClickListener);
    for (int i=1;i<u.length;i++)
    {
      u[i]="0";
    }
    u[0]="Enter here for start!";
    AdapterView.OnItemClickListener itemClickListener =
        new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> listView,
                                    View v,
                                    int position,
                                    long id) {
                final TextView item = (TextView) v;
                final  TextView textView = findViewById(R.id.textView);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(item.getText());
                        textView.setVisibility(View.VISIBLE);
                        ImageView imageView = findViewById(R.id.imageView);
                        imageView.setVisibility(View.VISIBLE);
                        TextView textView1 = findViewById(R.id.textView2);
                        TextView textView2 = findViewById(R.id.textView3);
                        TextView textView3 = findViewById(R.id.textView4);
                        TextView textView4 = findViewById(R.id.textView5);
                        textView1.setVisibility(View.VISIBLE);
                        textView3.setVisibility(View.VISIBLE);
                        textView4.setVisibility(View.INVISIBLE);
                        textView2.setVisibility(View.VISIBLE);
                        Button button = findViewById(R.id.button2);
                        button.setVisibility(View.VISIBLE);
                        ImageButton imageButton = (ImageButton)findViewById(R.id.imageButton2);
                        imageButton.setVisibility(View.VISIBLE);
                        ImageButton imageButton2 = (ImageButton)findViewById(R.id.imageButton4);
                        imageButton2.setVisibility(View.VISIBLE);
                        RadioButton redRadioButton = findViewById(R.id.radio_red);
                        redRadioButton.setVisibility(View.VISIBLE);

                        RadioButton greenRadioButton = findViewById(R.id.radio_green);
                        greenRadioButton.setVisibility(View.VISIBLE);

                        RadioButton blueRadioButton = findViewById(R.id.radio_blue);
                        blueRadioButton.setVisibility(View.VISIBLE);

                        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
                        radioGroup.setVisibility(View.VISIBLE);
                        Button button1 = (Button)findViewById(R.id.button);
                        button1.setVisibility(View.VISIBLE);

                    }
                });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ListView listView = findViewById(R.id.list);
                            listView.setVisibility(View.INVISIBLE);
                        }
                    });
                    ip=textView.getText().toString();
                }
            };
    ListView listView = findViewById(R.id.list);
    listView.setOnItemClickListener(itemClickListener);
    Timer timer = new Timer();
    UpdateTimeTask ut = new UpdateTimeTask();
    Timer timer1 = new Timer();
    UpdateTimeTask1 ut1 = new UpdateTimeTask1();
    timer.schedule(ut, 0, 1500);
    timer1.schedule(ut1, 0, 10000);
    ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, u);
    listView.setAdapter(adapter);

    TT t = new TT();
    t.start();
    TT1 t1 = new TT1();
    t1.start();
    TT2 tt2 = new TT2();
    tt2.start();
    Play();

    imageView = findViewById(R.id.imageView);
    imageView.setRotation(90);

    RadioButton redRadioButton = findViewById(R.id.radio_red);
    redRadioButton.setOnClickListener(radioButtonClickListener);

    RadioButton greenRadioButton = findViewById(R.id.radio_green);
    greenRadioButton.setOnClickListener(radioButtonClickListener);

    RadioButton blueRadioButton = findViewById(R.id.radio_blue);
    blueRadioButton.setOnClickListener(radioButtonClickListener);

    ImageButton imageButton = (ImageButton)findViewById(R.id.imageButton2);
    imageButton.setOnClickListener(radioButtonClickListener);

}

public void hds(View view) {
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            ListView listView = findViewById(R.id.list);
            TextView textView = findViewById(R.id.textView);
            listView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.INVISIBLE);
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setVisibility(View.INVISIBLE);
            TextView textView1 = findViewById(R.id.textView2);
            TextView textView2 = findViewById(R.id.textView3);
            textView1.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.INVISIBLE);
            Button button = findViewById(R.id.button2);
            button.setVisibility(View.INVISIBLE);
            TextView textView3 = findViewById(R.id.textView4);
            TextView textView4 = findViewById(R.id.textView5);
            textView3.setVisibility(View.INVISIBLE);
            textView4.setVisibility(View.VISIBLE);
            ImageButton imageButton = (ImageButton)findViewById(R.id.imageButton2);
            imageButton.setVisibility(View.INVISIBLE);
            ImageButton imageButton2 = (ImageButton)findViewById(R.id.imageButton4);
            imageButton2.setVisibility(View.INVISIBLE);
            RadioButton redRadioButton = findViewById(R.id.radio_red);
            redRadioButton.setVisibility(View.INVISIBLE);
            RadioButton greenRadioButton = findViewById(R.id.radio_green);
            greenRadioButton.setVisibility(View.INVISIBLE);
            RadioButton blueRadioButton = findViewById(R.id.radio_blue);
            blueRadioButton.setVisibility(View.INVISIBLE);
            RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
            radioGroup.setVisibility(View.INVISIBLE);
            Button button1 = (Button)findViewById(R.id.button);
            button1.setVisibility(View.INVISIBLE);
        }
    });
}

public void map(View view) {
    Intent geoMap = new Intent(Intent.ACTION_VIEW, urii);
    startActivity(geoMap);
}

synchronized public void large(View view) {
    if (img==0) {
        ImageButton imageButton = (ImageButton)findViewById(R.id.imageButton2);
        imageButton.setVisibility(View.INVISIBLE);
        ImageButton imageButton1 = (ImageButton)findViewById(R.id.imageButton4);
        imageButton1.setVisibility(View.INVISIBLE);
        Button button = findViewById(R.id.button2);
        button.setVisibility(View.INVISIBLE);
        Button button1 = findViewById(R.id.button);
        button1.setVisibility(View.INVISIBLE);
        ImageButton imageButton2 = (ImageButton)findViewById(R.id.imageButton);
        imageButton2.setVisibility(View.VISIBLE);
        imageView = (ImageView) findViewById(R.id.imageView2);
        imageView.setVisibility(View.VISIBLE);
        imageView.animate().scaleX(1.5f).scaleY(1.5f).setDuration(500);
        img=1;
    } else {
        imageView.setVisibility(View.INVISIBLE);
        imageView = (ImageView) findViewById(R.id.imageView);
        img=0;
        ImageButton imageButton = (ImageButton)findViewById(R.id.imageButton2);
        imageButton.setVisibility(View.VISIBLE);
        ImageButton imageButton1 = (ImageButton)findViewById(R.id.imageButton4);
        imageButton1.setVisibility(View.VISIBLE);
        ImageButton imageButton2 = (ImageButton)findViewById(R.id.imageButton);
        imageButton2.setVisibility(View.INVISIBLE);
        Button button2 = findViewById(R.id.button2);
        button2.setVisibility(View.VISIBLE);
        Button button21 = findViewById(R.id.button);
        button21.setVisibility(View.VISIBLE);
    }
}

class UpdateTimeTask extends TimerTask {
    public void run() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter<String> adapter = new ArrayAdapter(AndroidSpyServer.this,
                        android.R.layout.simple_list_item_1, u);
                ListView listView = findViewById(R.id.list);
                listView.setAdapter(adapter);
            }
        });
    }
}

class UpdateTimeTask1 extends TimerTask {
    public void run() {
        for (int i=0;i<u.length;i++)
        {
            u[i]="0";
        }
    }
}

class TTT extends Thread {
    public void run() {
        super.run();
    }
}

class TT2 extends Thread {
    public void run() {
        super.run();
        try {
            ServerSocket ss1 = new ServerSocket(6668);
            while (true) {
                Socket socket1 = ss1.accept();
                String sss = socket1.getInetAddress().toString();
                if (sss.equals(ip)) {
                    DataInputStream in = new DataInputStream(socket1.getInputStream());
                    location = in.readUTF();
                    Log.d(TAG, location);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView = findViewById(R.id.textView2);
                            textView.setText(location);
                            Uri geo = Uri.parse(location);
                            urii = geo;
                        }
                    });
                }
                socket1.close();
            }
        } catch (IOException e) {}
    }
}

class TT extends Thread {
    public void run() {
        super.run();
       setPriority(MAX_PRIORITY);
    try {
        ServerSocket ss = new ServerSocket(6666);
        while (true) {
            Socket socket = ss.accept();
            String sss = socket.getInetAddress().toString();
              if (sss.equals(ip))
              {
                  DataInputStream inStream = new DataInputStream(socket.getInputStream());
                  FileOutputStream fileStream = new FileOutputStream(file);
                  while (inStream.available()<4) {}
                  int imagesize = inStream.readInt();
                  byte[] buffer = new byte[imagesize];
                  while (inStream.available()<imagesize) {}
                  inStream.readFully(buffer);
                  fileStream.write(buffer,0,imagesize);
                  fileStream.flush();
                  inStream.close();
                  fileStream.close();
                  runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                          imageView.setImageBitmap(bitmap);
                      }
                  });
              }
        }

    }
    catch (IOException e) {}
    }
}

class TT1 extends Thread {
    public void run() {
        super.run();
        try {
            ServerSocket ss = new ServerSocket(6667);
           while (true) {
                Socket socket = ss.accept();
                final String sss = socket.getInetAddress().toString(); //getRemoteSocketAddress()
                final String myIP = socket.getLocalAddress().toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = findViewById(R.id.textView7);
                        textView.setText(myIP);
                        //Toast toast = Toast.makeText(Server.this, sss, Toast.LENGTH_LONG);
                        //toast.show();
                    }
                });
                int k = 0;
                for (int i = 0; i < u.length; i++) {
                    if (u[i].equals(sss)) {
                        k = 1;
                    }
                }
                if (k==0) {
                    for (int i = 0; i < u.length; i++) {

                        if (u[i].equals("0")) {
                            u[i] = sss;
                            break;
                        }
                    }
                }
                socket.close();
            }
        }
        catch (IOException e){}
    }
}

void Play() {
   new Thread(new Runnable() {
        @Override
        public void run() {
            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, sizeBuf, AudioTrack.MODE_STREAM);
            audioTrack.play();
            Log.d(TAG,"creating audioTrack");
            byte[] data = new byte[myBufferSize];
            int n = 0;
            try {
                ServerSocket serverSocket = new ServerSocket(6669);
                while (true) {
                    Socket s = serverSocket.accept();
                    String sss = s.getInetAddress().toString();
                    if (sss.equals(ip)) {
                        while ((n = s.getInputStream().read(data)) != -1)
                            audioTrack.write(data, 0, n);
                        Log.d(TAG, "reading from socket");
                    }
                }
            }
            catch (IOException e) {
                return;
            }
        }
    }).start();
}

void rotate(View v) {
  if (Rot == 0) {
      Rot = 1;
      ImageButton imageButton = (ImageButton)findViewById(R.id.imageButton4);
      imageButton.setVisibility(View.INVISIBLE);
      ImageButton imageButton1 = (ImageButton)findViewById(R.id.imageButton);
      imageView.animate().rotation(imageView.getRotation() + 90);
      imageButton.setVisibility(View.VISIBLE);
      Rot=0;
  }
}

class Q extends Thread {
    public void run() {
        super.run();
    try {
        Socket socket1 = new Socket(InetAddress. getByName(ip.substring(1)), 6665);
        DataOutputStream out = new DataOutputStream(socket1.getOutputStream());
        out.writeUTF(cam);
        out.flush();
        out.writeUTF(qeulity);
        out.flush();
        socket1.close();
    } catch (IOException e) {Log.d("error","sending failed"); }
    }
}


View.OnClickListener radioButtonClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        RadioButton rb = (RadioButton)v;
        switch (v.getId()) {
            case R.id.radio_red: {qeulity="2";Q q = new Q();
                q.start();}
                break;
            case R.id.radio_green: {qeulity="1"; Q q = new Q();
                q.start();}
            break;
            case R.id.radio_blue: { qeulity="0"; Q q = new Q();
                q.start();}
            break;
            case R.id.imageButton2 : {
                if (cam=="0") {cam="1"; }
                  else {cam="0"; }
                Q q = new Q();
                q.start();
            }
            break;
            case R.id.imageButton4 : {
                rotate(v);
                }
            break;
            default:
                break;
        }
    }
};

}

