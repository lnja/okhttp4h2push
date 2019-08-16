package len.okhttp.demo;

import android.app.Application;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import len.tools.android.AndroidUtils;
import len.tools.android.Log;

import java.util.HashMap;
import java.util.Map;

public class App extends Application {
    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("APP -> onCreate");
        instance = this;
        String processName = AndroidUtils.getProcessName(this, android.os.Process.myPid());
        if (processName != null) {
            boolean defaultProcess = processName.equals(getPackageName());
            if (defaultProcess) {
                Log.enableLog(BuildConfig.DEBUG);
                initOkHttp();
            } else if (processName.endsWith(":other")) {

            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.e("APP -> onTerminate");
    }

    private void initOkHttp() {
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
        Map<String, String> headerParms = new HashMap<>();
        headerParms.put("Connection", "keep-alive");
        Http2EventListener http2EventListener = new Http2EventListener();
        String apiHost = Config.SERVER_HOST;
        if (apiHost.startsWith("https://")) {
            OkHttpClientWrapper.getInstance().initOKHttpForHttps(headerParms,http2EventListener);
        } else {
            OkHttpClientWrapper.getInstance().initOkHttp(headerParms,http2EventListener);
        }
    }
}