package com.openworld.me.localadb;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.os.Process;

/** Installed manifest identity. applicationId placeholders are already expanded in a built APK. */
final class PrivilegedTarget {
    final String component, authority;

    private PrivilegedTarget(String component, String authority) {
        this.component = component; this.authority = authority;
    }

    static PrivilegedTarget resolve(Context context) throws PrivilegedBridge.ActivationFailure {
        try {
            PackageManager pm = context.getPackageManager();
            String packageName = context.getPackageName();
            String component = PrivilegedProvider.class.getName();
            String apk = context.getApplicationInfo().sourceDir;
            ProviderInfo declared = pm.getProviderInfo(new ComponentName(packageName, component), 0);
            if (!matches(declared, packageName, Process.myUid(), apk, component)
                    || !context.getApplicationInfo().processName.equals(declared.processName))
                throw new PrivilegedBridge.ActivationFailure("component_invalid",
                        "ADB 回传组件未启用或不在当前应用主进程。请检查改包工具是否保留了完整组件配置。");
            if (declared.authority != null) for (String authority : declared.authority.split(";")) {
                if (!authority.isEmpty() && matches(pm.resolveContentProvider(authority, 0),
                        packageName, Process.myUid(), apk, component))
                    return new PrivilegedTarget(component, authority);
            }
            throw new PrivilegedBridge.ActivationFailure("provider_unresolved",
                    "ADB 回传地址未注册到当前应用。请修复改包后的 Provider 配置；重新配对不能修复此问题。");
        } catch (PackageManager.NameNotFoundException missing) {
            throw new PrivilegedBridge.ActivationFailure("component_missing",
                    "安装包缺少 ADB 回传组件，或组件类名与代码不一致。请检查改包工具并重新安装完整 APK。");
        } catch (RuntimeException unavailable) {
            throw new PrivilegedBridge.ActivationFailure("component_unavailable",
                    "暂时无法读取当前应用的 ADB 组件配置，请稍后重试。");
        }
    }

    static boolean matches(ProviderInfo info, String packageName, int uid, String apk, String component) {
        return info != null && info.enabled && info.exported && info.applicationInfo != null
                && info.applicationInfo.enabled && info.applicationInfo.uid == uid
                && packageName.equals(info.packageName) && packageName.equals(info.applicationInfo.packageName)
                && apk.equals(info.applicationInfo.sourceDir) && component.equals(info.name);
    }
}
