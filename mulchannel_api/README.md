## LeRandomAccessFile

RandomAccessFile的小端序版本

## MulChannel

读写apk/zip注释.

添加maven仓库:

```groovy
repositories {
  maven {
    url "https://dl.bintray.com/gradle-plugin/maven/"
  }
}
```

添加依赖:

```groovy
dependencies{
  implementation 'com.erge:mulchannel-api:1.0'
}
```

代码示例:

```java
public class UM {
  public static String getChannel(Context context) {
    try {
      String baseApk = context.getApplicationInfo().sourceDir;
      // 从apk中读取渠道信息
      String comment = MulChannel.readComment(baseApk);
      if (comment != null && (comment = comment.trim()).length() > 0) {
        return comment;
      }
    } catch (Exception ignore) {
    }
    return null;
  }
}
```

通过umeng-common包初始化渠道时调用以下方法:

```java
UMConfigure.init(mContext, null, UM.getChannel(mContext), UMConfigure.DEVICE_TYPE_PHONE, null);
```

这样可以避免渠道传`null` 而自动获取`AndroidManifest.xml` 中的渠道.