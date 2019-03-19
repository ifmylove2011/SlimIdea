package com.xter.slimidea.common.util;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by XTER on 2017/9/19.
 * 相关工具类
 */
public class ActivityUtil {

	public static void showActivityStack(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;//获取到栈顶最顶层的activity所对应的应用
		String packageName = cn.getPackageName();//从ComponentName对象中获取到最顶层的应用包名
		L.w(packageName);
	}

	/**
	 * 判断某一Service是否正在运行
	 *
	 * @param context     上下文
	 * @param serviceName Service的全路径： 包名 + service的类名
	 * @return true 表示正在运行，false 表示没有运行
	 */
	public static boolean isServiceRunning(Context context, String serviceName) {
		ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(200);
		if (runningServiceInfos.size() <= 0) {
			return false;
		}
		for (ActivityManager.RunningServiceInfo serviceInfo : runningServiceInfos) {
			if (serviceInfo.service.getClassName().equals(serviceName)) {
				return true;
			}
		}
		return false;
	}

	public static void restartApplication(Context context, boolean needClean) {
		if (needClean)
			CleanUtil.cleanApplicationData(context);
		restartApplication(context);
	}

	public static void restartApplication(Context context) {
//		Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
//		if (intent != null) {
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		}
//		context.startActivity(intent);

//		Intent intent = new Intent(context, MainActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		context.startActivity(intent);
//		android.os.Process.killProcess(android.os.Process.myPid());

		restartApplication(context,50);
	}

	public static void restartApplication(Context context,long mills) {
//		Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
//		if (intent != null) {
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		}
//		context.startActivity(intent);

//		Intent intent = new Intent(context, MainActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		context.startActivity(intent);
//		android.os.Process.killProcess(android.os.Process.myPid());

		Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
		PendingIntent restartIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + mills, restartIntent);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public static void exit() {
		//获取PID
		android.os.Process.killProcess(android.os.Process.myPid());
		//常规java、c#的标准退出法，返回值为0代表正常退出
		System.exit(0);
	}

	/**
	 * 获取版本名
	 * @param context 环境
	 * @return 例如"2.0"
	 */
	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return verName;
	}

	/**
	 * APK安装
	 *
	 * @param path
	 *            apk路径
	 */
	public static void installAPK(Context context, String path) {
		if (path == null || path.length() < 0)
			return;
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(path)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 通过系统广播安装apk
	 */
	public static void installAPKBySystem(Context context) {
		Intent intent = new Intent();
		intent.setAction("com.jinxin.action.UPDATE_SYSTEM_APP");
		intent.putExtra("name", "com.jinxin.action.UPDATE_SYSTEM_APP");
		context.sendBroadcast(intent);
	}

}
