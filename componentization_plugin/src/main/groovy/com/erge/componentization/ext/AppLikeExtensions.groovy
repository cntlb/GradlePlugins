package com.erge.componentization.ext

import org.gradle.api.Action

/**
 * gradle 拓展:
 *
 * <pre>
 *   appLike{
 *       appLikeInterface 'a.b.c.IApplication'
 *       appLikeMgr{
 *          name 'a.b.c.AppLifecycleManager'
 *          register 'register
 *          init 'init
 *       }
 *   }
 * </pre>
 * 其中 `IApplication` 定义生命周期组件公共接口, `AppLifecycleManager`用于管理`IApplication`.
 * 经过以上配置的 `AppLifecycleManager`必须拥有下面的结构:
 * <pre>
 *  public class AppLifecycleManager {
 *      //appLikeMgr中register对应的静态方法名, 参数固定为String
 *      private static void register(String classname) {
 *      }
 *
 *      //appLikeMgr中init对应的静态方法名, 无参数
 *      private static void init() {
 *      }
 *  }
 * </pre>
 * 当app构建时由插件自动生成代码:
 * <pre>
 *  public class AppLifecycleManager {
 *      static {
 *          register("a.b.d.LoginApplication");
 *          register("a.b.e.MineApplication");
 *          init();
 *      }
 *
 *      private static void register(String classname);
 *      private static void init();
 *  }
 * </pre>
 */
class AppLikeExtensions {
    String appLikeInterface
    AppLikeManager appLikeMgr = new AppLikeManager()

    void appLikeMgr(Action<AppLikeManager> action){
        action.execute(appLikeMgr)
    }

    void checkNotNull() {
        if (!appLikeInterface || !appLikeMgr) {
            throw new RuntimeException('appLikeInterface or appLikeMgr must not null!')
        }
        appLikeMgr.checkNotNull()
    }

    void normalize() {
        appLikeInterface = appLikeInterface.replace('.' as char, '/' as char)
        appLikeMgr.name = appLikeMgr.name.replace('.' as char, '/' as char)
    }


    @Override
    public String toString() {
        return "AppLikeExtensions{" +
                "appLikeInterface='" + appLikeInterface + '\'' +
                ", appLikeMgr=" + appLikeMgr +
                '}';
    }
}



