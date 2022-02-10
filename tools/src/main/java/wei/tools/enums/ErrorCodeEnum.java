package wei.tools.enums;

public enum ErrorCodeEnum {

    CALL_WRONG(101,"调用接口 : "),
    REQUEST_PARAMETER_MISSING(201,"请求缺失参数"),
    ;

    private int code;
    private String message;

    ErrorCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
