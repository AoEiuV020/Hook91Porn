package cc.aoeiuv020.hook91porn;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
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
                            homeBeanClass.getField("is_ads").setBoolean(result, true);
                        }
                    }
                });
    }
}
