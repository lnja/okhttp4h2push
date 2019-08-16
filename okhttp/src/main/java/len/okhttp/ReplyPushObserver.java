package len.okhttp;

import okhttp3.EventListener;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.Header;
import okhttp3.internal.http2.PushObserver;
import okio.BufferedSource;

import java.io.IOException;
import java.util.List;

public class ReplyPushObserver implements PushObserver {

    private EventListener eventListener;

    public ReplyPushObserver(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public boolean onRequest(int streamId, List<Header> requestHeaders) {
        if(eventListener !=null)eventListener.onRequest(streamId,requestHeaders);
        return false;
    }

    @Override
    public boolean onHeaders(int streamId, List<Header> responseHeaders, boolean last) {
        if(eventListener !=null)eventListener.onHeaders(streamId,responseHeaders,last);
        return false;
    }

    @Override
    public boolean onData(int streamId, BufferedSource source, int byteCount,
                          boolean last) throws IOException {
        if(eventListener !=null)eventListener.onData(streamId,source,byteCount,last);
        source.skip(byteCount);
        return false;
    }

    @Override
    public void onReset(int streamId, ErrorCode errorCode) {
        if(eventListener !=null)eventListener.onReset(streamId,errorCode);
    }
}
