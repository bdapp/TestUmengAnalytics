# Cordova集成Umeng统计（android）

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

## 复制umeng插件到工程

- 拷贝Umeng_Analytics_PhoneGap_SDK/SDK/umeng_plugin到工程到plugins目录

- 修改umeng_plugin/plugin.xml里的内容
    ```
    <!-- appkey -->
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

## 集成

- 集成umeng插件
    ```
    cordova plugins add ./umeng_plugin
    ```

    运行成功后，会在根目录/plugins下多出一个Umeng文件夹，之前复制的umeng_plugin文件夹可以删除

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

## 编译运行

```
cordova build android
    
cordova run android
```

