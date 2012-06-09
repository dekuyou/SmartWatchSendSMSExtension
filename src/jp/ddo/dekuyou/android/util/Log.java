package jp.ddo.dekuyou.android.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class Log {

	private static boolean isDebuggable = false;
	private static String versionName = "";
	private static String packageName = "";
	
	public static void initialize(Context context){
		
		isDebuggable(context);
		getVersionName(context);
		getPackageName(context);
		
	}

	/**
	 * マニフェストファイルからデバッグモードかどうかを取得する
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isDebuggable(Context ctx) {
		PackageManager manager = ctx.getPackageManager();
		ApplicationInfo appInfo = null;
		try {
			appInfo = manager.getApplicationInfo(ctx.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			return false;
		}
		if ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE) {
			isDebuggable = true;

			return true;
		}
		return false;
	}

	/**
	 * マニフェストファイルからバージョン名を取得する
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getVersionName(Context ctx) {

		PackageManager pm = ctx.getPackageManager();
		try {
			PackageInfo info = null;
			info = pm.getPackageInfo(ctx.getPackageName(), 0);
			versionName = info.versionName;
		} catch (NameNotFoundException e) {
		}

		return versionName;
	}

	/**
	 * マニフェストファイルからパッケージ名を取得する
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getPackageName(Context ctx) {
		packageName = ctx.getPackageName();
		return ctx.getPackageName();
	}
	
	
	
	

	public static void d(String msg) {
		if (isDebuggable) {
			android.util.Log.d(packageName, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (isDebuggable) {
			android.util.Log.d(tag, msg);
		}
	}
	
	public static void d(Throwable tr) {
		d(packageName,versionName,tr);
	}


	public static void d(String tag, String msg, Throwable tr) {
		if (isDebuggable) {
			android.util.Log.d(tag, msg, tr);
		}
	}

	public static void e(String msg) {
		if (isDebuggable) {
			android.util.Log.e(packageName, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (isDebuggable) {
			android.util.Log.e(tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (isDebuggable) {
			android.util.Log.e(tag, msg, tr);
		}
	}
	
	public static void e(Throwable tr) {
		e(packageName,versionName,tr);
	}
	
	public static void w(String msg) {
		if (isDebuggable) {
			android.util.Log.w(packageName, msg);
		}
	}
	public static void w(String tag, String msg) {
		if (isDebuggable) {
			android.util.Log.w(tag, msg);
		}
	}

	public static void w(String tag, String msg, Throwable tr) {
		if (isDebuggable) {
			android.util.Log.w(tag, msg, tr);
		}
	}
	
	public static void w(Throwable tr) {
		w(packageName,versionName,tr);
	}
	
	public static void i( String msg) {
		if (isDebuggable) {
			android.util.Log.i(packageName, msg);
		}
	}
	public static void i(String tag, String msg) {
		if (isDebuggable) {
			android.util.Log.i(tag, msg);
		}
	}

	public static void i(String tag, String msg, Throwable tr) {
		if (isDebuggable) {
			android.util.Log.i(tag, msg, tr);
		}
	}
	
	public static void i(Throwable tr) {
		i(packageName,versionName,tr);
	}
	
	public static void v( String msg) {
		if (isDebuggable) {
			android.util.Log.v(packageName, msg);
		}
	}


	public static void v(String tag, String msg) {
		if (isDebuggable) {
			android.util.Log.v(tag, msg);
		}
	}

	public static void v(String tag, String msg, Throwable tr) {
		if (isDebuggable) {
			android.util.Log.v(tag, msg, tr);
		}
	}
	
	public static void v(Throwable tr) {
		v(packageName,versionName,tr);
	}
	

}
