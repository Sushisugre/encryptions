package encryptor.main;

import java.util.EventObject;

public class EncryptEvent extends EventObject {

	private String filePath;
	private byte key;
	private int method;
	
	
	public int getMethod() {
		return method;
	}
	public void setMethod(int method) {
		this.method = method;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Byte getKey() {
		return key;
	}
	public void setKey(Byte key) {
		this.key = key;
	}
	public EncryptEvent(Object source,String path,int method,byte key) {
		super(source);
		// TODO Auto-generated constructor stub
		filePath=path;
		this.key=key;
		this.method=method;
	
	}

}
