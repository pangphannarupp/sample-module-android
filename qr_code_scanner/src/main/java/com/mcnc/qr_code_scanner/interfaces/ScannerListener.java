package com.mcnc.qr_code_scanner.interfaces;

public interface ScannerListener {
    void onCompleted(String result);
    void onFailed();
}
