package com.xter.slimidea.presentation.app;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.xter.slimidea.BuildConfig;
import com.xter.slimidea.common.util.L;

/**
 * Created by XTER on 2019/3/19.
 * 应用入口
 */

public class SlimApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		L.DEBUG = BuildConfig.LOG;


		Stetho.initializeWithDefaults(this);
	}
}
