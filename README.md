# erge_plugin

添加仓库或自行本地编译

```groovy
buildscript{
  repositories {
    maven {
      url "https://dl.bintray.com/gradle-plugin/maven/"
    }
  }
  dependencies {
    classpath "com.erge:util-plugin:1.0"
  }
}
```



## com.erge.timecost

```groovy
apply plugin:'com.erge.timecost'
```

任务耗时插件, 方便查看各个任务执行的时间, 并对耗时最多的5个任务进行排序输出, 效果如:


```
:app:preBuild UP-TO-DATE
				0.000s
:lib-av:preBuild UP-TO-DATE
				0.000s
:lib-av:preDebugBuild UP-TO-DATE
				0.000s
:lib-av:checkDebugManifest UP-TO-DATE
...


BUILD SUCCESSFUL in 1s
116 actionable tasks: 116 up-to-date
============================执行任务165个,耗时Top10============================
:app:merge360zhushouDebugResources -> 0.044s
:app:pre360zhushouDebugBuild -> 0.032s
:app:process360zhushouDebugResources -> 0.010s
:lib-common:compileDebugAidl -> 0.007s
:lib-download:compileDebugAidl -> 0.006s
:lib-media:compileDebugAidl -> 0.006s
:app:compile360zhushouDebugRenderscript -> 0.006s
:lib-pushsdk:processDebugResources -> 0.006s
:lib-common:processDebugResources -> 0.006s
:lib-skin:compileDebugAidl -> 0.005s
Top10耗时:0.128s
==========================================================================
```


## com.erge.umeng

针对umeng统计中的渠道号获取方式进行修改代码. 

Umeng统计需要设置渠道号, 通常在Androidmanifest.xml中添加

```xml
<meta-data android:name="UMENG_CHANNEL" android:value="${UMENG_CHANNEL_VALUE}" />
```

获取渠道号则通过`'com.umeng.sdk:common:1.5.3'` 中的`UMUtils#getChannelByXML`获取, 代码如下:

```java
public static String getChannelByXML(Context var0) {
  Object var1 = null;
  try {
    PackageManager var2 = var0.getPackageManager();
    ApplicationInfo var3 = var2.getApplicationInfo(var0.getPackageName(), 128);
    if (var3 != null && var3.metaData != null) {
      Object var4 = var3.metaData.get("UMENG_CHANNEL");
      if (var4 != null) {
        String var5 = var4.toString();
        if (var5 != null) {
          return var5.trim();
        }

        if (AnalyticsConstants.UM_DEBUG) {
          MLog.i("MobclickAgent", new Object[]{"Could not read UMENG_CHANNEL meta-data from AndroidManifest.xml."});
        }
      }
    }
  } catch (Throwable var6) {
    MLog.e("MobclickAgent", "Could not read UMENG_CHANNEL meta-data from AndroidManifest.xml.", var6);
  }

  return null;
}

```

`com.erge.umeng` 插件通过配置自定义的方法来插入到`getChannelByXML` 所有代码执行前, 

```groovy
apply plugin:'com.erge.umeng'
android{...}
channelInject{
  ownerClass = 'a/b/c' // 注意静态方法所属的类,.换成/
  name = 'someMethod'   //静态方法, 方法签名必须和UMUtils#getChannelByXML保持一致
}
```

最终打包进apk的代码如:

```java
public static String getChannelByXML(Context var0) {
  Object var1 = a.b.c.someMethod();
  if (var1 != null){
    return var1;
  }
  //下面是原有的正常getChannelByXML代码
}
```

> getChannelByXML方法基本在启动app是调用一次, 之后边写入shared preferences中通过 getChannel获取, 因此代码植入也不是必须的操作

