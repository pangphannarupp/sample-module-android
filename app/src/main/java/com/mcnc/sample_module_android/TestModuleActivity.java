package com.mcnc.sample_module_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TestModuleActivity extends AppCompatActivity {
    int LAUNCH_SECOND_ACTIVITY = 1;

    FloatingActionButton scan_qr_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        scan_qr_code = findViewById(R.id.scan_qr_code);

        scan_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = null;
                try {
                    intent = new Intent(TestModuleActivity.this,
                            Class.forName("com.mcnc.qr_code_scanner.MainActivity"));
                    intent.putExtra("APP_BAR_TITLE", "PPCBank Scanner");
                    intent.putExtra("APP_BAR_BACKGROUND_COLOR", "#004c97");
                    intent.putExtra("SCANNER_TITLE", "ស្កេន QR");
                    intent.putExtra("SCANNER_DESCRIPTION", "Align frame with QR code");
                    intent.putExtra("SCANNER_COLOR_MASK", "#CD004C97");
//            startActivity(intent);
                    startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(requestCode);
        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("RESULT");
                System.out.println(result);
                Toast.makeText(TestModuleActivity.this, result, Toast.LENGTH_SHORT).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }
}