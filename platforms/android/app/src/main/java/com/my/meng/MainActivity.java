/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.my.meng;

import android.os.Bundle;

import com.tencent.bugly.Bugly;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;

import org.apache.cordova.CordovaActivity;

public class MainActivity extends CordovaActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // enable Cordova apps to be started in the background
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
            moveTaskToBack(true);
        }

        // Set by <content src="index.html" /> in config.xml
        loadUrl(launchUrl);

        initUmengSDK();

//        Toast.makeText(this, "markToast", Toast.LENGTH_LONG).show();
//        Intent intent = new Intent(this, SecondActivity.class);
//        startActivity(intent);

        Bugly.init(this, "dc2e9f5514", true);
    }

    /**
    * onCreate中调用
    */
    private void initUmengSDK() {
        MobclickAgent.setScenarioType(this, EScenarioType.E_UM_NORMAL);
        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        // MobclickAgent.setSessionContinueMillis(1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
