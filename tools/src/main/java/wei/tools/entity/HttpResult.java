package wei.tools.entity;

import com.alibaba.fastjson.JSONObject;
import wei.tools.enums.ErrorCodeEnum;

/**
 * HTTP结果封装
 */
public class HttpResult {

	private int code = 200;
	//提示信息
	private String msg;
	private Object data;
	//错误信息
	private String errorMsg;
	
	public static HttpResult error() {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
	}
	
	public static HttpResult error(String msg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
	}

	public static HttpResult error(String msg, String errorMsg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR,msg,errorMsg);
	}
	
	public static HttpResult error(int code, String msg) {
		HttpResult r = new HttpResult();
		r.setCode(code);
		r.setMsg(msg);
		return r;
	}

	public static HttpResult error(int code, String msg, String errorMsg){
		HttpResult r = new HttpResult();
		r.setCode(code);
		r.setMsg(msg);
		r.setErrorMsg(errorMsg);
		return r;
	}

	public static HttpResult error(ErrorCodeEnum errorCodeEnum){
		HttpResult r = new HttpResult();
		r.setCode(errorCodeEnum.getCode());
		r.setMsg(errorCodeEnum.getMessage());
		return r;
	}

	public static HttpResult ok(String msg) {
		HttpResult r = new HttpResult();
		r.setMsg(msg);
		return r;
	}
	
	public static HttpResult ok(Object data) {
		HttpResult r = new HttpResult();
		r.setData(data);
		return r;
	}
	public static HttpResult ok(int code , String msg) {
		HttpResult r = new HttpResult();
		r.setMsg(msg);
		r.setCode(code);
		return r;
	}

	public JSONObject toJSONObject(){
		JSONObject resultJson = new JSONObject();
		resultJson.put("code",this.code);
		resultJson.put("msg",this.msg);
		resultJson.put("data",this.data);
		resultJson.put("errorMsg",this.errorMsg);
		return resultJson;
	}

	
	public static HttpResult ok() {
		return new HttpResult();
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

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String toString() {
		return "HttpResult{" +
				"code=" + code +
				", msg='" + msg + '\'' +
				", data=" + data +
				", errorMsg='" + errorMsg + '\'' +
				'}';
	}
}
