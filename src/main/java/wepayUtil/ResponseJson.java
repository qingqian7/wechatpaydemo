package wepayUtil;

public class ResponseJson {
	public int code;
	public String tip;
	public Object data;
	
	public ResponseJson() {
		super();
	}
	public ResponseJson(int code, String tip, Object data) {
		super();
		this.code = code;
		this.tip = tip;
		this.data = data;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getTip() {
		return tip;
	}
	public void setTip(String tip) {
		this.tip = tip;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
}
