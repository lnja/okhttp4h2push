package len.okhttp.demo;

import com.orhanobut.logger.Logger;
import len.tools.android.JsonUtils;
import okhttp3.logging.HttpLoggingInterceptor;

public class HttpLogger implements HttpLoggingInterceptor.Logger {
    private StringBuffer mMessage = new StringBuffer();
    @Override
    public void log(String message) {
        // 请求或者响应开始
        if (message.startsWith("--> POST")||message.startsWith("--> GET")) {
            mMessage.delete(0,mMessage.length());
        }
        // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
        if ((message.startsWith("{") && message.endsWith("}"))
                || (message.startsWith("[") && message.endsWith("]"))) {
            message = JsonUtils.toJsonViewStr(JsonUtils.decodeUnicode(message));
        }
        mMessage.append(message.concat("\n"));
        // 响应结束，打印整条日志
        if (message.startsWith("<-- END HTTP")) {
            Logger.d(mMessage.toString());
        }
    }
}