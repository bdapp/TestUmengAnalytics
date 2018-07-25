# Cordova集成Umeng统计 + Bugly全量更新（android）  
&nbsp;

&nbsp;
[TOC]

&nbsp;

# 一、Umeng统计

## 准备工作

    - 本地能正常运行cordova、android
    - 在友盟注册了应用 (http://mobile.umeng.com/apps)
    - 下载友盟移动统计SDK (https://developer.umeng.com/sdk/phonegap)(https://github.com/umeng/Umeng_Analytics_PhoneGap_SDK)
    或者直接下载本项目，已集成SDK

## 初始化工程

- 创建工程
    ```
        cordova create TestUmengAnalytics com.my.umeng TestUmengAnalytics
    ```

- 引用android平台

    ```
        cd TestUmengAnalytics/

        cordova platforms add android
    ```
    > 这里可以用android studio直接打开android项目，方便后面的文件修改。


## 复制umeng插件到工程

- 拷贝Umeng_Analytics_PhoneGap_SDK/SDK/umeng_plugin到工程到plugins目录

- 修改umeng_plugin/plugin.xml里的内容
    ```
        <!-- 第一处修改target-dir -->
        <source-file src="src/android/umeng-analytics-v6.0.4.jar" target-dir="app/src/main/libs" />

        <!-- 第二处修改umeng配置 -->
        <meta-data android:name="UMENG_APPKEY" android:value="替换成申请的appkey" />
        <meta-data android:name="UMENG_CHANNEL" android:value="自定义渠道名"/>
    ```

- 修改Api.js

    打开/platforms/android/cordova/Api.js，搜索“this.locations = ” ，修改成下面这样
    ```
        this.locations = {
            root: self.root,
            www: path.join(self.root, 'assets/www'),
            res: path.join(self.root, 'res'),
            platformWww: path.join(self.root, 'platform_www'),
            configXml: path.join(self.root, 'app/src/main/res/xml/config.xml'),
            defaultConfigXml: path.join(self.root, 'cordova/defaults.xml'),
            strings: path.join(self.root, 'app/src/main/res/values/strings.xml'),
            manifest: path.join(self.root, 'app/src/main/AndroidManifest.xml'),
            build: path.join(self.root, 'build'),
            javaSrc: path.join(self.root, 'app/src/main/java'),
            // NOTE: Due to platformApi spec we need to return relative paths here
            cordovaJs: 'bin/templates/project/assets/www/cordova.js',
            cordovaJsSrc: 'cordova-js-src'
        };
    ```
  
    

## 集成到Android

- 集成umeng插件
    ```
        cordova plugins add */umeng_plugin
    ```

    运行成功后，会在根目录/plugins下多出一个Umeng文件夹，之前复制的umeng_plugin文件夹可以删除


- 在 /platforms/android/app/build.gradle 里搜索“implementation fileTree”，引入jar包
    ```
        dependencies {
            implementation fileTree(dir: 'libs', include: '*.jar')
            // SUB-PROJECT DEPENDENCIES START
            implementation(project(path: ":CordovaLib"))
            // SUB-PROJECT DEPENDENCIES END
            implementation files('src/main/libs/umeng-analytics-v6.0.4.jar')
        }
    ```


- 在 MainActivity.java 主界面加入集成代码
    ```
        /**
        * 头部引入包
        */
        import com.umeng.analytics.MobclickAgent;
        import com.umeng.analytics.MobclickAgent.EScenarioType;


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
    ```


## 编译运行

```
    cordova build android
        
    cordova run android
```

&nbsp; 
&nbsp; 
---
# 二、Bugly全量更新

可参数官方配置文档  https://bugly.qq.com/docs/user-guide/instruction-manual-android-upgrade/?v=20180713114028

## 更新build.gradle
- 修改/platforms/android/app/build.gradle内容

    ```
        dependencies {
            implementation fileTree(dir: 'libs', include: '*.jar')
            // SUB-PROJECT DEPENDENCIES START
            implementation(project(path: ":CordovaLib"))
            // SUB-PROJECT DEPENDENCIES END
            //添加友盟统计
            implementation files('src/main/libs/umeng-analytics-v6.0.4.jar')
            //添加bugly热更新
            compile 'com.android.support:appcompat-v7:24.2.1'
            compile 'com.tencent.bugly:crashreport_upgrade:1.3.5'
        }
    ```

## 修改AndroidManifest.xml
- 权限和umeng相同，不用添加
- 在application层添加activity
    ```
        <activity
            android:name="com.tencent.bugly.beta.ui.BetaActivity"
            android:configChanges="keyboardHidden|orientationscreenSize|locale"
            android:theme="@android:style/Theme.Translucent" />
    ```

- 在application层添加provider
    ```
        <provider android:authorities="这里写包名.fileProvider" android:exported="false" android:grantUriPermissions="true" android:name="android.support.v4.content.FileProvider">
                <meta-data android:name="android.support.FILE_PROVIDER_PATHS" android:resource="@xml/provider_paths" />
        </provider>
    ```   

## 创建provider_paths.xml
- 在res目录新建xml文件夹，创建provider_paths.xml文件如下：
    ```
        <?xml version="1.0" encoding="utf-8"?>
        <paths xmlns:android="http://schemas.android.com/apk/res/android">
            <!-- /storage/emulated/0/Download/${applicationId}/.beta/apk-->
            <external-path name="beta_external_path" path="Download/"/>
            <!--/storage/emulated/0/Android/data/${applicationId}/files/apk/-->
            <external-path name="beta_external_files_path" path="Android/data/"/>
        </paths>
    ```


## 修改MainActivity.java
- 在onCreate()方法里初始化Bugly
    ```
        /**
        * 头部引入包
        */
        import com.tencent.bugly.Bugly;


        /**
        * onCreate()初始化
        */
        Bugly.init(this, "Bugly申请的appID", true);
    ```

## 编译运行

```
    cordova build android
        
    cordova run android
```    



&nbsp;

&nbsp;


# 三、 配置多渠道打包
- 手动：全局搜索“Channel-”，替换要加的渠道名
- 自动：http://www.qingpingshan.com/rjbc/az/123254.html


&nbsp;

&nbsp;

# 四、 修改版本号
- 修改根目录 config.xml 中的version的值
> version="1.2.0"

