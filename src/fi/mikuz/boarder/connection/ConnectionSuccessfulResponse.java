package fi.mikuz.boarder.connection;

import org.json.JSONObject;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class ConnectionSuccessfulResponse implements ConnectionResponse {
	
	String connectionId;
	JSONObject jObject;
	
	public ConnectionSuccessfulResponse(JSONObject jObject, String url) {
		this.jObject = jObject;
		this.connectionId = ConnectionUtils.getUrlConnectionId(url);
	}
	
	@Override
	public String getConnectionId() {
		return connectionId;
	}
	
	public JSONObject getJSONObject() {
		return jObject;
	}

}
