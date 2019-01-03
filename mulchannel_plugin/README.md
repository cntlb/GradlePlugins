apk v1签名多渠道打包

工程顶级`build.gradle` 配置:

```groovy
buildscript {
    repositories {
        maven {
            url "https://dl.bintray.com/gradle-plugin/maven/"
        }
    }
    dependencies {
        classpath 'com.erge:mulchannel-plugin:1.0'
    }
}
```

打包工程配置(建议新建空的工程专门做打包)

```groovy
apply plugin: 'com.erge.mulchannel'

mulchannel{
    baseApk "${projectDir}/test/app-v3.1.4-360zhushou-release.apk"
    baseChannel '360zhushou'
    output "${projectDir}/test"
    channelTxt "${projectDir}/test/0.channels.txt"
}
```

各参数说明:

* `baseApk` (必须)

  基本包, 以该包为基础apk打包出其他渠道, 基本包中应该包含一个渠道(上面渠道是`360zhushou`), 通过`baseChannel` 指定基本包的渠道, 打包替换旧渠道生成新渠道保存路径

* `baseChannel`  (可选)

  基本包中的渠道名, 如果不填默认使用整个文件名(不含`.apk`)作为渠道

* `output`(可选)

  新渠道包输出目录, 不填则使用`baseApk` 的目录

* `channelTxt` (必须)

  渠道配置, 一行一个渠道, 目前不支持注释. `0.channels.txt` 样例

  ```
  ...
  ppzhushou
  sanxingshichang
  sougouzhushou
  vivoyingyong
  xiaomi
  yingyongbao
  yiyonghui
  ...
  ```

执行任务`mulchannel` 打包:

```shell
$ ./gradlew mulchannel
> Task :mulchannel_test:mulchannel 
base apk: .../mulchannel_test/test/app-v3.1.4-360zhushou-release.apk
...
package channel meizu in 0.009s
package channel oppoyingyong in 0.009s
package channel ppzhushou in 0.017s
package channel sanxingshichang in 0.017s
package channel sougouzhushou in 0.009s
package channel vivoyingyong in 0.017s
package channel xiaomi in 0.017s
package channel yingyongbao in 0.017s
package channel yiyonghui in 0.02s
...
package 22 channels in 0.250s
output directory: .../mulchannel_test/test

BUILD SUCCESSFUL in 0s
```