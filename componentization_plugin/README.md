组件化插件配置

## `build.gradle` 配置

顶级`build.gradle`

```groovy
buildscript {
  repositories {
    maven {
      url 'https://dl.bintray.com/gradle-plugin/maven'
    }
  }
  dependencies {
    classpath 'com.android.tools.build:gradle:3.2.1'
    classpath 'com.erge:componentization-plugin:1.0'
  }
}
```

子工程`build.gradle`配置

```groovy
apply plugin: 'com.erge.component'
//配置生命周期组件, 可选, 详见下文
appLike{
    appLikeInterface 'com.example.applike.IApplication'
    appLikeMgr{
        name 'com.example.applike.AppLifecycleManager'
    }
}
```

## `gradle.properties` 配置

顶级`gradle.properties` 配置主模块(没有配置默认为`:app` )

```properties
com.component.mainmodule=:app
```

主模块`gradle.properties` 配置

```properties
#主模块默认com.component.runningalone=true
com.component.runningalone=true
#依赖组件, 以工程路径的方式给出, 使用逗号分隔
com.component.dependencies=:component_video,:component_music,:component_login
```

组件模块`gradle.properties` 配置

```properties
#组件模块默认com.component.runningalone=false, 也就是不能独立运行
com.component.runningalone=true
```

组件模块代码结构:

```
component_video/src
├── androidTest
│   └── java
├── main
│   ├── AndroidManifest.xml
│   ├── java
│   └── res
├── runalone
│   ├── AndroidManifest.xml
│   ├── java
│   └── res
└── test
    └── java
```

其中`runalone`的结构和`main`一样(甚至理解成`runalone` 渠道都可以), 组件独立运行是包含该目录中的代码以及使用该目录下的`AndroidManifest.xml` 

## 生命周期相关配置

组件化开发中各个组件之间互相不可见(代码和资源都完全隔离, 不可访问), 当主模块整合应用打包时需要将各个组件进行合并, 组件可能需要在类似`Application.onCreate` 中做一些初始化(如果是组件独立运行就很简单), 因此提供了整合功能. 具体来讲, 首先定义一个组件生命周期接口和一个生命周期管理:

```java
package com.example.applike;
public interface IApplication {
  /** 组件初始化执行, 相当于在Application.onCreate*/
  void onCreate(Context context);
}

public class AppLifecycleManager {
  //需要一个静态代码块, 如果说明了静态变量会自动生成, 后续或优化该判断
  static{}

  public static void onCreate(Context context) { }

  // asm操作字节码调用
  private static void register(String classname){ }

  // asm操作字节码调用
  private static void init() { }
}
```

然后在主模块配置

```groovy
appLike{
    appLikeInterface 'com.example.applike.IApplication'
    appLikeMgr{
      name 'com.example.applike.AppLifecycleManager'
      register 'register'
      init 'init'
    }
}
```

各个组件如果需要初始化, 就编写`IApplication` 的实现类, 编译主模块时由插件进行代码注入, 注入后的代码反编译出来形如:

```java
//假设有两个组件需要生命周期初始化, 编写了两个实现类
public class AppLifecycleManager {
    static {
        register("a.b.c.IApplicationImpl");
        register("a.b.d.IApplicationImpl");
        init();
    }
  //... 其他方法
}
```

而您可以直接使用`AppLifecycleManager.onCreate()` 在`Application.onCreate` 中调用来初始化组件. 另外组件初始化可能需要优先级, 这些实现均没有做任何限制, 可以自由的实现. 现在应该容易理解appLike 脚本块的配置了:

* appLikeInterface  生命周期接口完整类名(使用'/'分割也可以, 这种叫做 internal name)

* appLikeMgr  生命周期接口管理类
  * name  生命周期接口管理类的完整类名
  * register 管理类中的生命周期实现类注册静态方法, 签名必须是 `(Ljava/lang/String;)V`, 如果不喜欢叫做默认值(`register`) 可以通过这个来配置
  * init  当所有生命周期实现类注册完后会调用 `init()`, 使用这个来配置一个新的方法名, 前提是签名必须为 `()V`

* 两个参考实现 [IApplication](src/test/java/com/example/applike/IApplication.java), [AppLifecycleManager](src/test/java/com/example/applike/AppLifecycleManager.java) (`IApplication` 实现类需做混淆保护)

  ```
  -keepclassmembers public class * extends com.example.applike.IApplication {
  }
  ```

  ​