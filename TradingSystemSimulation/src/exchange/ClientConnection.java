package exchange;

import java.util.HashMap;

import clientMessageFields.ClientId;
import messagesToClient.AbstractMessageToClient;

public class ClientConnection {

	protected String ERROR_CLIENT_EXISTS = "Client id [%s] already exists in client connections map";
	protected String ERROR_NO_SUCH_CLIENT = "No client matches id [%s]";
	
	protected HashMap<ClientId, Client> _clientsMap;
	
	public ClientConnection() {
		_clientsMap = new HashMap<ClientId,Client>();
	}
	
	public void sendMessage( AbstractMessageToClient messageToClient ) throws Exception {
		Client client = _clientsMap.get( messageToClient.getClientId() );
		if( client == null )
			throw new Exception(
				String.format( 
					ERROR_NO_SUCH_CLIENT, 
					messageToClient.getClientId().toString()
				)
			);
		client.receiveMessage( messageToClient );
	}

	public void addClient(Client client) throws Exception {
		if( _clientsMap.containsKey( client.getClientId() ) )
			throw new Exception(
				String.format(
					ERROR_CLIENT_EXISTS,
					client.getClientId().toString()
				)
			);
		_clientsMap.put( client.getClientId() , client );
	}

	public Client getClient( ClientId clientId ) {
		return _clientsMap.get( clientId );
	}
	
}
