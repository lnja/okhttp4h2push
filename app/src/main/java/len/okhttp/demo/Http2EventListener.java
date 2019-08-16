package len.okhttp.demo;

import android.support.annotation.Nullable;
import len.tools.android.Log;
import okhttp3.*;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.Header;
import okio.BufferedSource;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Http2EventListener extends EventListener {
    private long startNs;

    public Http2EventListener() {
    }

    @Override
    public void callStart(Call call) {
        startNs = System.nanoTime();

        logWithTime("callStart: " + call.request());
    }

    @Override
    public void dnsStart(Call call, String domainName) {
        logWithTime("dnsStart: " + domainName);
    }

    @Override
    public void dnsEnd(Call call, String domainName, List<InetAddress> inetAddressList) {
        logWithTime("dnsEnd: " + inetAddressList);
    }

    @Override
    public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
        logWithTime("connectStart: " + inetSocketAddress + " " + proxy);
    }

    @Override
    public void secureConnectStart(Call call) {
        logWithTime("secureConnectStart");
    }

    @Override
    public void secureConnectEnd(Call call, @Nullable Handshake handshake) {
        logWithTime("secureConnectEnd: " + handshake);
    }

    @Override
    public void connectEnd(
            Call call, InetSocketAddress inetSocketAddress, Proxy proxy, @Nullable Protocol protocol) {
        logWithTime("connectEnd: " + protocol);
    }

    @Override
    public void connectFailed(
            Call call,
            InetSocketAddress inetSocketAddress,
            Proxy proxy,
            @Nullable Protocol protocol,
            IOException ioe) {
        logWithTime("connectFailed: " + protocol + " " + ioe);
    }

    @Override
    public void connectionAcquired(Call call, Connection connection) {
        logWithTime("connectionAcquired: " + connection);
    }

    @Override
    public void connectionReleased(Call call, Connection connection) {
        logWithTime("connectionReleased");
    }

    @Override
    public void requestHeadersStart(Call call) {
        logWithTime("requestHeadersStart");
    }

    @Override
    public void requestHeadersEnd(Call call, Request request) {
        logWithTime("requestHeadersEnd");
    }

    @Override
    public void requestBodyStart(Call call) {
        logWithTime("requestBodyStart");
    }

    @Override
    public void requestBodyEnd(Call call, long byteCount) {
        logWithTime("requestBodyEnd: byteCount=" + byteCount);
    }

    @Override
    public void responseHeadersStart(Call call) {
        logWithTime("responseHeadersStart");
    }

    @Override
    public void responseHeadersEnd(Call call, Response response) {
        logWithTime("responseHeadersEnd: " + response);
    }

    @Override
    public void responseBodyStart(Call call) {
        logWithTime("responseBodyStart");
    }

    @Override
    public void responseBodyEnd(Call call, long byteCount) {
        logWithTime("responseBodyEnd: byteCount=" + byteCount);
    }

    @Override
    public void callEnd(Call call) {
        logWithTime("callEnd");
    }

    @Override
    public void callFailed(Call call, IOException ioe) {
        logWithTime("callFailed: " + ioe);
    }

    private void logWithTime(String message) {
        long timeMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        Log.d("[" + timeMs + " ms] " + message);
    }

    @Override
    public void onRequest(int streamId, List<Header> requestHeaders) {
        super.onRequest(streamId, requestHeaders);
        StringBuffer log = new StringBuffer();
        log.append("PUSH onRequest -> streamId = ");
        log.append(streamId);
        for (int i = 0; i < requestHeaders.size(); i++) {
            log.append("\n");
            log.append(requestHeaders.get(i).name.utf8());
            log.append(":");
            log.append(requestHeaders.get(i).value.utf8());
        }
        Log.d(log.toString());
    }

    @Override
    public void onHeaders(int streamId, List<Header> responseHeaders, boolean last) {
        super.onHeaders(streamId, responseHeaders, last);
        StringBuffer log = new StringBuffer();
        log.append("PUSH onHeaders -> streamId = ");
        log.append(streamId);
        log.append("  last = ");
        log.append(last);
        for (int i = 0; i < responseHeaders.size(); i++) {
            log.append("\n");
            log.append(responseHeaders.get(i).name.utf8());
            log.append(":");
            log.append(responseHeaders.get(i).value.utf8());
        }
        Log.d(log.toString());
    }

    @Override
    public void onData(int streamId, BufferedSource source, int byteCount, boolean last) throws IOException {
        super.onData(streamId, source, byteCount, last);
        StringBuffer log = new StringBuffer();
        log.append("PUSH onData -> streamId = ");
        log.append(streamId);
        log.append("  last = ");
        log.append(last);
        log.append("  byteCount = ");
        log.append(byteCount);
        log.append("  sourceDataSize = ");
        log.append(source.buffer().size());
        log.append("\n");
        String data = source.readUtf8();
//        String temp = source.readByteString().utf8();
        if(byteCount == 0){
            Log.e(log.toString());
            return;
        }
        log.append(data);
        Log.e(log.toString());
    }

    @Override
    public void onReset(int streamId, ErrorCode errorCode) {
        super.onReset(streamId, errorCode);
        StringBuffer log = new StringBuffer();
        log.append("PUSH onReset - >    streamId = ");
        log.append(streamId);
        log.append("  errorCode = ");
        log.append(errorCode.name());
        Log.d(log.toString());
    }
}
