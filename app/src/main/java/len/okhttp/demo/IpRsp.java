package len.okhttp.demo;

import len.tools.android.model.JsonEntity;

public class IpRsp extends JsonEntity {

    private IpDetail data;
    private int code;
    private String msg;

    public boolean isSuccess() {
        return code == 0;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public IpDetail getData() {
        return data;
    }

    public void setData(IpDetail data) {
        this.data = data;
    }
    /*private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }*/
}
