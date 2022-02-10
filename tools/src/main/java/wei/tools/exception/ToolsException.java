package wei.tools.exception;

/**
 * 业务异常
 */
public class ToolsException extends RuntimeException{

    private int errorCode;

    private String errorMsg;

    public ToolsException(){
        super();
    }


    public ToolsException(int errorCode, String errorMsg ) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ToolsException(String errorMsg ) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
