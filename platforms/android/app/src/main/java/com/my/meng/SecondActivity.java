package com.my.meng;

import android.os.Bundle;

import org.apache.cordova.CordovaActivity;

/**
 * @Info
 * @Auth Bello
 * @Time 18-7-25 下午1:57
 * @Ver
 */
public class SecondActivity extends CordovaActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
