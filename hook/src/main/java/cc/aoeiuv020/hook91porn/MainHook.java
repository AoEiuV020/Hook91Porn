package cc.aoeiuv020.hook91porn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

@SuppressWarnings("RedundantThrows")
public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("handleLoadPackage: " + lpparam.processName + ", " + lpparam.processName);
        Class<?> homeBeanClass = XposedHelpers.findClassIfExists("com.dft.shot.android.bean.HomeBean", lpparam.classLoader);
        if (homeBeanClass == null) {
            // 有两个进程会被hook，其中一个反射不到类，
            return;
        }
        XposedBridge.log("find class: " + homeBeanClass);
        XposedHelpers.findAndHookMethod(
                "com.chad.library.adapter.base.BaseQuickAdapter",
                lpparam.classLoader,
                "getItem",
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object result = param.getResult();
                        if (result == null) {
                            return;
                        }
                        if (homeBeanClass.isInstance(result)) {
                            homeBeanClass.getField("coins").setInt(result, 0);
                        }
                    }
                });
        XposedHelpers.findAndHookMethod(
                "com.dft.shot.android.h.F",
                lpparam.classLoader,
                "q",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return true;
                    }
                });
        Class<?> appStartBeanClass = XposedHelpers.findClassIfExists("com.dft.shot.android.bean.AppStartBean", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(
                "com.dft.shot.android.ui.LaunchActivity",
                lpparam.classLoader,
                "a",
                appStartBeanClass,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Object appStartBean = param.args[0];
                        appStartBeanClass.getField("isVip").setBoolean(appStartBean, true);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object a = param.thisObject.getClass().getField("a").get(param.thisObject);
                        View G = (View)a.getClass().getField("G").get(a);
                        G.setEnabled(true);
                        G.performClick();
                    }
                });
        XposedHelpers.findAndHookMethod(
                "com.dft.shot.android.ui.dialog.AdMessageDialog",
                lpparam.classLoader,
                "onStart",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.thisObject.getClass().getMethod("dismiss").invoke(param.thisObject);
                    }
                });
    }
}
