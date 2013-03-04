/* ========================================================================= *
 * Boarder                                                                   *
 * http://boarder.mikuz.org/                                                 *
 * ========================================================================= *
 * Copyright (C) 2013 Boarder                                                *
 *                                                                           *
 * Licensed under the Apache License, Version 2.0 (the "License");           *
 * you may not use this file except in compliance with the License.          *
 * You may obtain a copy of the License at                                   *
 *                                                                           *
 *     http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                           *
 * Unless required by applicable law or agreed to in writing, software       *
 * distributed under the License is distributed on an "AS IS" BASIS,         *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 * See the License for the specific language governing permissions and       *
 * limitations under the License.                                            *
 * ========================================================================= */

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
