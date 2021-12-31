package com.mcnc.qr_code_scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
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
    //Guide
    private Guideline topGuide, bottomGuide;
    //Flash
    private ImageView flashImage;
    private TextView flashTitle;
    private boolean isShowFlashLight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide();

        getSupportActionBar().setElevation(0);
        setContentView(R.layout.activity_main);

        scanner_title = findViewById(R.id.scanner_title);
        scanner_description = findViewById(R.id.scanner_description);
        scannerView = findViewById(R.id.scanner_view);
        //Guide
        topGuide = findViewById(R.id.top_guide);
        bottomGuide = findViewById(R.id.bottom_guide);
        //Flash
        flashImage = findViewById(R.id.icon_flash);
        flashTitle = findViewById(R.id.title_flash);

        Intent intent = getIntent();
        String appBarTitle = intent.getStringExtra("APP_BAR_TITLE");
        String appBarBackgroundColor = intent.getStringExtra("APP_BAR_BACKGROUND_COLOR");
        String scannerTitle = intent.getStringExtra("SCANNER_TITLE");
        String scannerDescription = intent.getStringExtra("SCANNER_DESCRIPTION");
        dataScannerColorMask = intent.getStringExtra("SCANNER_COLOR_MASK");

        getSupportActionBar().setTitle(appBarTitle);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(appBarBackgroundColor)));
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#CD020202")));
        scanner_title.setText(scannerTitle);
        scanner_description.setText(scannerDescription);
        scannerView.setMaskColor(Color.parseColor(dataScannerColorMask));

        //Guide
        setAspectRatio();


        // the code below appears in onCreate() method
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 123);
        } else {
            startScanning();
        }

        setFlashEvent();
    }

    private void setFlashEvent() {
        flashImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check Flash Is Available On Device
                if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                    mCodeScanner.setFlashEnabled(!isShowFlashLight);
                    flashImage.setImageResource(isShowFlashLight ? R.drawable.flash_on : R.drawable.flash_off);
                    flashTitle.setText(isShowFlashLight ? "Turn on the flashlight" : "Turn off the flashlight");
                    isShowFlashLight = !isShowFlashLight;
                } else {
                    Toast.makeText(MainActivity.this, "Your device does not have flash", Toast.LENGTH_SHORT).show();
                }
                System.out.println();
            }
        });
    }

    private void setAspectRatio() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float ratio = ((float)metrics.heightPixels / (float)metrics.widthPixels);
        float topR = 0.15f;//0.15f because 0.5 - 0.7 / 2 (size of qrcode)
        float topRatio = (float) ((topR * (float)metrics.heightPixels) / (float)metrics.widthPixels);
        float endRatio = (float) ((0.65 * (float)metrics.heightPixels) / (float)metrics.widthPixels);
        endRatio = (float) (1 - topRatio);//move reverse of middle screen
        topGuide.setGuidelinePercent(topRatio);
        bottomGuide.setGuidelinePercent(endRatio);
    }

    private void startScanning() {
        mCodeScanner = new CodeScanner(this, scannerView);
        //config option
//        scannerView.setElevation(4);
        mCodeScanner.setFlashEnabled(false);

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