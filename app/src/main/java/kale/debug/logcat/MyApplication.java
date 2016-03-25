package kale.debug.logcat;

import com.facebook.stetho.Stetho;

import android.app.Application;

/**
 * @author Kale
 * @date 2015/12/13
 */
public class MyApplication extends Application {

    
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build();
    }
}
