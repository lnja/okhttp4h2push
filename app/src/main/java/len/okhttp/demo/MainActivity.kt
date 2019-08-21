package len.okhttp.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import len.tools.android.Log
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        commitPostTaoBao()
//        commitGet360()
        commitGetPush()
    }

    private fun commitPostTaoBao() {
        Thread(Runnable {
            try {
                Log.e(postTaoBao())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()
    }

    private fun commitGet360() {
        Thread(Runnable {
            try {
                Log.e(get360())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()
    }

    private fun commitGetPush() {
        Thread(Runnable {
            try {
                Log.e(getPush())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()
    }

    @Throws(IOException::class)
    private fun postTaoBao(): String {
        App.getInstance().initOkHttpTaoBao()
        val jsonType = MediaType.get("application/json; charset=utf-8")
        val body = RequestBody.create(jsonType, "")
        val request = Request.Builder()
            .url(Config.SERVER_HOST_TAO_BAO + "service/getIpInfo.php?ip=115.159.152.210")
            .post(body)
            .build()
        val response = OkHttpClientWrapper.getOkHttpClient().newCall(request).execute()
        return response.body()!!.string()
    }

    @Throws(IOException::class)
    private fun get360(): String {
        App.getInstance().initOkHttp360()
        val request = Request.Builder()
            .url(Config.SERVER_HOST_360 + "IPQuery/ipquery?ip=115.159.152.210")
            .get()
            .build()
        val response = OkHttpClientWrapper.getOkHttpClient().newCall(request).execute()
        return response.body()!!.string()
    }

    @Throws(IOException::class)
    private fun getPush(): String {
        App.getInstance().initRetrofitPush()
        val request = Request.Builder()
            .url(Config.SERVER_HOST_PUSH + "demo/")
            .get()
            .build()
        val response = OkHttpClientWrapper.getOkHttpClient().newCall(request).execute()
        return response.body()!!.string()
    }
}
