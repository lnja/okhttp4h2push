package len.okhttp.demo;

import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.LoggingEventListener;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpClientWrapper {
    private static final Object SYNC_OBJECT = new Object();
    private static final int MAX_RETRY_TIMES = 3;
    private static volatile OkHttpClientWrapper INSTANCE;
    private static OkHttpClient okHttpClient = null;
    private EventListener mEventListener = null;

    public static OkHttpClientWrapper getInstance() {
        if (INSTANCE == null) {
            synchronized (SYNC_OBJECT) {
                if (INSTANCE == null) {
                    INSTANCE = new OkHttpClientWrapper();
                }
            }
        }
        return INSTANCE;
    }

    public boolean initOKHttpForHttps(Map<String, String> headerParams, EventListener eventListener, Interceptor... interceptors) {
        mEventListener = eventListener;
        TrustManager[] trustAllCerts = createTrustManagers();
        SSLSocketFactory sslSocketFactory = createSSLSocketFactory(trustAllCerts);
        if (sslSocketFactory == null) return false;
        HostnameVerifier verifiedAllHostname = createHostnameVerifier();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (interceptors != null && interceptors.length > 0) {
            for (int i = 0; i < interceptors.length; i++) {
                Interceptor interceptor = interceptors[i];
                if (interceptor != null) {
                    builder.addInterceptor(interceptor);
                }
            }
        }
        builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                .hostnameVerifier(verifiedAllHostname)
                .connectTimeout(60, TimeUnit.MINUTES)
                .readTimeout(60, TimeUnit.MINUTES)
                .writeTimeout(60, TimeUnit.SECONDS)
                .pingInterval(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(createBasicParamsInterceptor(headerParams))
                .addInterceptor(createRetryInterceptor());
        //eventListener()会覆盖eventListenerFactory()设置的EventListener
        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(createLogInterceptor());
            builder.eventListenerFactory(new LoggingEventListener.Factory());
        }
        if (mEventListener != null) {
            builder.eventListener(mEventListener);
        }
        okHttpClient = builder.build();
        return true;
    }

    public boolean initOKHttpForHttps(Map<String, String> headerParams, Interceptor... interceptors) {
        return initOKHttpForHttps(headerParams, null, interceptors);
    }

    public boolean initOKHttpForHttps(Interceptor... interceptors) {
        return initOKHttpForHttps(null, null, interceptors);
    }

    public boolean initOkHttp(Map<String, String> headerParams, EventListener eventListener, Interceptor... interceptors) {
        mEventListener = eventListener;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (interceptors != null && interceptors.length > 0) {
            for (int i = 0; i < interceptors.length; i++) {
                Interceptor interceptor = interceptors[i];
                if (interceptor != null) {
                    builder.addInterceptor(interceptor);
                }
            }
        }
        //设置超时
        builder.connectTimeout(8, TimeUnit.SECONDS);
        builder.readTimeout(8, TimeUnit.SECONDS);
        builder.writeTimeout(8, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);
        builder.addInterceptor(createBasicParamsInterceptor(headerParams));
        builder.addInterceptor(createRetryInterceptor());
        //eventListener()会覆盖eventListenerFactory()设置的EventListener
        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(createLogInterceptor());
            builder.eventListenerFactory(new LoggingEventListener.Factory());
        }
        if (mEventListener != null) {
            builder.eventListener(mEventListener);
        }
        okHttpClient = builder.build();
        return true;
    }

    public void initOkHttp(Map<String, String> headerParams, Interceptor... interceptors) {
        initOkHttp(headerParams, null, interceptors);
    }

    public void initOkHttp(Interceptor... interceptors) {
        initOkHttp(null, null, interceptors);
    }

    private TrustManager[] createTrustManagers() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
    }

    private HostnameVerifier createHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    private SSLSocketFactory createSSLSocketFactory(TrustManager[] trustAllCerts) {
        SSLSocketFactory sslSocketFactory = null;
        try {
            SSLContext sslContext = null;
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } finally {
            return sslSocketFactory;
        }
    }

    /**
     * 网络拦截器，用来处理失败重试逻辑
     */

    private Interceptor createRetryInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                int tryCount = 0;
                while (!response.isSuccessful() && tryCount < MAX_RETRY_TIMES) {
                    tryCount++;
                    response = chain.proceed(request);
                }
                return response;
            }
        };
    }

    /**
     * 网络拦截器，用来打印请求的详细信息
     */
    private Interceptor createLogInterceptor() {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logInterceptor;
    }

    /**
     * 网络拦截器，用来添加统一的请求头
     */
    private Interceptor createBasicParamsInterceptor(Map<String, String> headerParams) {
        BasicParamsInterceptor.Builder builder = new BasicParamsInterceptor.Builder();
//                builder.addParam("from", "android") //添加公共参数到 post 请求体
//                .addQueryParam("version","1")  // 添加公共版本号，加在 URL 后面
//                .addHeaderParam("client-id", "abcdefg")  // 示例： 添加公共消息头
//                .addHeaderParam("authorization", "token-key")
//                .addHeaderParam("User-Agent","custom user-agent");
        if (headerParams != null) {
            builder.addHeaderParamsMap(headerParams);
        }
        return builder.build();
    }

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public void setEventListener(EventListener eventListener) {
        this.mEventListener = eventListener;
    }
}
