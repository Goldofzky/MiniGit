package site.keyu.minigit.pojo;

public class SuccessInfo {
    private Integer code;
    private Object data;

    public SuccessInfo(Object data) {
        this.code = 0;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
