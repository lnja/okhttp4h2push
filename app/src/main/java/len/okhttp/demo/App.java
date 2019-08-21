package len.okhttp.demo;

import android.app.Application;
import com.orhanobut.logger.*;
import len.tools.android.AndroidUtils;
import len.tools.android.Log;
import len.tools.android.StorageUtils;
import len.tools.android.extend.LnjaCsvFormatStrategy;

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
                initLog();
            } else if (processName.endsWith(":other")) {

            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.e("APP -> onTerminate");
    }

    private void initLog(){
        if(BuildConfig.DEBUG){
            Log.init("lnja",android.util.Log.VERBOSE);
        }else {
            Log.init("lnja",android.util.Log.INFO);
        }

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag("lnja")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                if(BuildConfig.DEBUG){
                    return true;
                }else {
                    if(priority < Logger.INFO){
                        return false;
                    }else {
                        return true;
                    }
                }
            }
        });
        FormatStrategy csvFormatStrategy = LnjaCsvFormatStrategy.newBuilder()
                .tag("lnja")
                .logPath(StorageUtils.getExtendDir(this,"logs").getAbsolutePath())
                .build();
        Logger.addLogAdapter(new DiskLogAdapter(csvFormatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                if(BuildConfig.DEBUG){
                    return true;
                }else {
                    return false;
                }
            }
        });
    }

    public void initOkHttp360() {
        Map<String, String> headerParms = new HashMap<>();
        headerParms.put("Connection", "keep-alive");
        Http2EventListener http2EventListener = new Http2EventListener();
        String apiHost = Config.SERVER_HOST_360;
        if (apiHost.startsWith("https://")) {
            OkHttpClientWrapper.getInstance().initOKHttpForHttps(headerParms,http2EventListener);
        } else {
            OkHttpClientWrapper.getInstance().initOkHttp(headerParms,http2EventListener);
        }
    }

    public void initOkHttpTaoBao() {
        Map<String, String> headerParms = new HashMap<>();
        headerParms.put("Connection", "keep-alive");
        Http2EventListener http2EventListener = new Http2EventListener();
        String apiHost = Config.SERVER_HOST_TAO_BAO;
        if (apiHost.startsWith("https://")) {
            OkHttpClientWrapper.getInstance().initOKHttpForHttps(headerParms,http2EventListener);
        } else {
            OkHttpClientWrapper.getInstance().initOkHttp(headerParms,http2EventListener);
        }
    }

    public void initRetrofitPush() {
        Map<String, String> headerParms = new HashMap<>();
        headerParms.put("Connection", "keep-alive");
        Http2EventListener http2EventListener = new Http2EventListener();
        String apiHost = Config.SERVER_HOST_PUSH;
        if (apiHost.startsWith("https://")) {
            OkHttpClientWrapper.getInstance().initOKHttpForHttps(headerParms,http2EventListener);
        } else {
            OkHttpClientWrapper.getInstance().initOkHttp(headerParms,http2EventListener);
        }
    }
}