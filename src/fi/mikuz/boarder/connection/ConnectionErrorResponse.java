package fi.mikuz.boarder.connection;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class ConnectionErrorResponse implements ConnectionResponse {

	String connectionId;
	String errorMessage;
	
	public ConnectionErrorResponse(String errorMessage, String url) {
		this.errorMessage = errorMessage;
		this.connectionId = ConnectionUtils.getUrlConnectionId(url);
	}
	
	@Override
	public String getConnectionId() {
		return connectionId;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
}
