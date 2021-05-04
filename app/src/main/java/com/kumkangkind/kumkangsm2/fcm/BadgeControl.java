package com.kumkangkind.kumkangsm2.fcm;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

/**
 * Hoo sik Kim
 */
public class BadgeControl {
    public static int badgeCnt = 0;

    public static void addBadgeCount(Context ctx){

        badgeCnt++;
        setBadge(ctx);
    }

    public static void clearBadgeCount(Context ctx){

        badgeCnt = 0;
        setBadge(ctx);
    }

    private static void sendBadgeCount(Context ctx){

        Intent intent1 = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent1.putExtra("badge_count", badgeCnt);
        intent1.putExtra("badge_count_package_name", ctx.getPackageName());
        intent1.putExtra("badge_count_class_name", ctx.getPackageName());
        ctx.sendBroadcast(intent1);


    }

    public static void setBadge(Context context) {
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", badgeCnt);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }

    public static String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }
}