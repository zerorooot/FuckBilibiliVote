package github.zerorooot.fuckbilibilivote;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Xposed implements IXposedHookLoadPackage {
    private final String bili = "tv.danmaku.bili";
    private final String biliHd = "tv.danmaku.bilibilihd";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (bili.equals(lpparam.packageName)) {
            getConfigAndHook("bili_class_name", "bili_class_method", lpparam);
        }

        if (biliHd.equals(lpparam.packageName)) {
            getConfigAndHook("bili_hd_class_name", "bili_hd_class_method", lpparam);
        }
    }

    private void getConfigAndHook(String classNameSp, String methodNameSp, XC_LoadPackage.LoadPackageParam lpparam) throws NoSuchMethodException {
        XSharedPreferences xsp = new XSharedPreferences(BuildConfig.APPLICATION_ID, "bili");
        if (xsp != null) {
            String className = xsp.getString(classNameSp, "");
            String methodName = xsp.getString(methodNameSp, "");
            XposedBridge.log("get bili class name:" + className + " method name:" + methodName);
            biliHook(className, methodName, lpparam);
        }else {
            XposedBridge.log("bili config not found");
        }

    }

    private void biliHook(String className, String methodName, XC_LoadPackage.LoadPackageParam lpparam) throws NoSuchMethodException {
        Class<?> hookClass = XposedHelpers.findClass(className, lpparam.classLoader);
        Class<?> viewProgressReplyClass = XposedHelpers.findClass("com.bapis.bilibili.app.view.v1.ViewProgressReply", lpparam.classLoader);
        Method method = hookClass.getDeclaredMethod(methodName, viewProgressReplyClass, long.class, long.class);
        method.setAccessible(true);
        XposedBridge.hookMethod(method, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                XposedBridge.log("fuck bili vote " + lpparam.packageName + "  " + param.args[0] + "  " + param.args[1] + " " + param.args[2]);
                param.args[0] = null;
            }
        });
    }

}
