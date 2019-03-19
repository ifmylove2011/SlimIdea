package com.xter.slimidea.common.util;

import android.text.TextUtils;
import android.util.Log;

public class L {

	public static boolean DEBUG;

	public static void v(String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg)) {
			Log.v(getMethodPath(4, 4), msg);
		}
	}

	public static void d(String msg) {
//		EventBus.getDefault().post(new LogEvent(msg));
		if (DEBUG && !TextUtils.isEmpty(msg)) {
			Log.d(getMethodPath(4, 4), msg);
		}
	}

	public static void i(String msg) {
//		EventBus.getDefault().post(new LogEvent(msg));
		if (DEBUG && !TextUtils.isEmpty(msg)) {
			Log.i(getMethodPath(4, 4), msg);
		}
	}

	public static void w(String msg) {
//		EventBus.getDefault().post(new LogEvent(msg));
		if (DEBUG && !TextUtils.isEmpty(msg)) {
			Log.w(getMethodPath(4, 4), msg);
		}
	}

	public static void e(String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg)) {
			Log.e(getMethodPath(4, 4), msg);
		}
	}


	public static void v(String tag, String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.v(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.d(tag, msg);
	}

	public static void i(String tag, String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.i(tag, msg);
	}

	public static void w(String tag, String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.w(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.e(tag, msg);
	}

	public static void v(int classPrior, int methodPriorint, String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.v(getMethodPath(classPrior, methodPriorint), msg);
	}

	public static void d(int classPrior, int methodPrior, String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.d(getMethodPath(classPrior, methodPrior), msg);
	}

	public static void i(int classPrior, int methodPrior, String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.i(getMethodPath(classPrior, methodPrior), msg);
	}

	public static void w(int classPrior, int methodPrior, String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.w(getMethodPath(classPrior, methodPrior), msg);
	}

	public static void e(int classPrior, int methodPrior, String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.e(getMethodPath(classPrior, methodPrior), msg);
	}

	public static void v(int prior, String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.v(getMethodPath(prior, prior), msg);
	}

	public static void d(int prior, String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.d(getMethodPath(prior, prior), msg);
	}

	public static void i(int prior, String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.i(getMethodPath(prior, prior), msg);
	}

	public static void w(int prior, String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.w(getMethodPath(prior, prior), msg);
	}

	public static void e(int prior, String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.e(getMethodPath(prior, prior), msg);
	}


	/**
	 * 得到调用此方法的类名与方法名
	 *
	 * @param classPrior  类级
	 * @param methodPrior 方法级
	 * @return string
	 */
	public static String getMethodPath(int classPrior, int methodPrior) {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

		StackTraceElement targetElement = stackTrace[classPrior];
		String fileName = targetElement.getFileName();
//		String className = targetElement.getClassName();
//		String[] classNameInfo = className.split("\\.");
//		if (classNameInfo.length > 0) {
//			className = classNameInfo[classNameInfo.length - 1] + SUFFIX;
//		}
//
//		if (className.contains("$")) {
//			className = className.split("\\$")[0] + SUFFIX;
//		}

		String methodName = targetElement.getMethodName();
		int lineNumber = targetElement.getLineNumber();

		if (lineNumber < 0) {
			lineNumber = 0;
		}
		int length = Thread.currentThread().getStackTrace().length;
		if (classPrior > length || methodPrior > length) {
			return "";
		} else
			return "(" + fileName + ":" + lineNumber + ")#" + methodName + "-->";
	}

	/**
	 * 得到调用此方法的类名与方法名（带包名）
	 *
	 * @param classPrior  类级
	 * @param methodPrior 方法级
	 * @return string
	 */
	public static String getPackageMethodPath(int classPrior, int methodPrior) {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

		StackTraceElement targetElement = stackTrace[classPrior];
		String className = targetElement.getClassName();
		String methodName = targetElement.getMethodName();
		int lineNumber = targetElement.getLineNumber();

		if (lineNumber < 0) {
			lineNumber = 0;
		}
		int length = Thread.currentThread().getStackTrace().length;
		if (classPrior > length || methodPrior > length) {
			return "";
		} else
			return "(" + className + ":" + lineNumber + ")#" + methodName + "-->";
	}

	/**
	 * 测试方法，将线程中的序列全部输出
	 */
	public static void logThreadSequence() {
		int length = Thread.currentThread().getStackTrace().length;
		for (int i = 0; i < length; i++) {
			Log.i(Thread.currentThread().getStackTrace()[i].getClassName(),
					Thread.currentThread().getStackTrace()[i].getMethodName());
		}
	}

}
