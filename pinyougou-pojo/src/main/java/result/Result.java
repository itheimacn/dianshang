package result;

import java.io.Serializable;

public class Result implements Serializable{
 public boolean isSuccess() {
		return success;
	}

 
	public Result(boolean success, String message) {
	super();
	this.success = success;
	this.message = message;
}


	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

private boolean success;
 
 private String message;
 
 
}
