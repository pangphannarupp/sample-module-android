package com.mcnc.qr_code_scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class MainActivity extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private CodeScannerView scannerView;

    private TextView scanner_title, scanner_description;
    private int scannerColorMask;
    private String dataScannerColorMask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0);
        setContentView(R.layout.activity_main);

        scanner_title = findViewById(R.id.scanner_title);
        scanner_description = findViewById(R.id.scanner_description);
        scannerView = findViewById(R.id.scanner_view);

        Intent intent = getIntent();
        String appBarTitle = intent.getStringExtra("APP_BAR_TITLE");
        String appBarBackgroundColor = intent.getStringExtra("APP_BAR_BACKGROUND_COLOR");
        String scannerTitle = intent.getStringExtra("SCANNER_TITLE");
        String scannerDescription = intent.getStringExtra("SCANNER_DESCRIPTION");
        dataScannerColorMask = intent.getStringExtra("SCANNER_COLOR_MASK");

        getSupportActionBar().setTitle(appBarTitle);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(appBarBackgroundColor)));
        scanner_title.setText(scannerTitle);
        scanner_description.setText(scannerDescription);
        scannerView.setMaskColor(Color.parseColor(dataScannerColorMask));


        // the code below appears in onCreate() method
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 123);
        } else {
            startScanning();
        }
    }

    private void startScanning() {
        mCodeScanner = new CodeScanner(this, scannerView);
        //config option
//        scannerView.setElevation(4);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("RESULT", result.getText());
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mCodeScanner != null) {
            mCodeScanner.startPreview();
        }
    }

    @Override
    protected void onPause() {
        if(mCodeScanner != null) {
            mCodeScanner.releaseResources();
        }
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show();
                startScanning();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
}