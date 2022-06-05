package com.example.service;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class service extends AppCompatActivity {
    File pictures1 = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    File file1 = new File(pictures1, "1.txt");
    File file2 = new File(pictures1, "2.txt");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        try(FileWriter writer = new FileWriter(file1, false)) {
            String text = "2";
            writer.write(text);
            writer.flush();
        }
        catch(IOException ex) {
            System.out.println(ex.getMessage());
        }

        try(FileWriter writer = new FileWriter(file2, false)) {
            String cam="0";
            String text = cam;
            writer.write(text);
            writer.flush();
        }
        catch(IOException ex) {
            System.out.println(ex.getMessage());
        }

        Intent intent = new Intent(this, Camera2Service.class);
        startService(intent);
        Log.d("log_", "start service");
    }
}
