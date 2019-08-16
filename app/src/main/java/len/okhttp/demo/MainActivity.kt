package len.okhttp.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import len.tools.android.AndroidUtils
import len.tools.android.Log
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        commitGet()
    }

    private fun commitPost() {
        Thread(Runnable { Log.e(post()) }).start()
    }

    private fun commitGet() {
        Thread(Runnable { Log.e(get()) }).start()
    }

    @Throws(IOException::class)
    private fun post(): String {
        val jsonType = MediaType.get("application/json; charset=utf-8")
        val body = RequestBody.create(jsonType, "")
        val request = Request.Builder()
            .url(Config.SERVER_HOST+"service/getIpInfo.php?ip=115.159.152.210")
            .post(body)
            .build()
        val response = OkHttpClientWrapper.getOkHttpClient().newCall(request).execute()
        return response.body()!!.string()
    }

    @Throws(IOException::class)
    private fun get(): String {
        val request = Request.Builder()
            .url(Config.SERVER_HOST+"service/getIpInfo.php?ip=115.159.152.210")
            .get()
            .build()
        val response = OkHttpClientWrapper.getOkHttpClient().newCall(request).execute()
        return response.body()!!.string()
    }
}
