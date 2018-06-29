package exchange;

import java.util.LinkedList;

import messagesToClient.AbstractMessageToClient;
import messagesToClient.CancelRejected;
import messagesToClient.Cancelled;
import messagesToClient.Fill;
import messagesToClient.OrderAccepted;
import messagesToClient.OrderRejected;
import clientMessageFields.ClientId;

public class Client {
	protected ClientId _clientId;
	protected LinkedList<AbstractMessageToClient> _messagesReceived;
	protected ExchangeConnection _exchangeConnection;
	
	public Client( ClientId clientId, ExchangeConnection exchangeConnection ) throws Exception {
		if( ( clientId == null ) || ( exchangeConnection == null ) )
			throw new Exception( "One of the arguments is null" );
		_clientId = clientId;
		_exchangeConnection = exchangeConnection;
		
		_messagesReceived = new LinkedList<AbstractMessageToClient>();
	}
	
	public LinkedList<AbstractMessageToClient> getMessagesFromExchange() {
		return _messagesReceived;
	}
	
	public void receiveMessage( AbstractMessageToClient messageFromExchange ) {
		messageFromExchange.getProcessedBy( this ); // Visitor pattern
	}
	
	public ClientId getClientId() { return _clientId; }

	public void processFill( Fill fill ) {
		_messagesReceived.addLast( fill );
	}

	public void processCancelled( Cancelled cancelled ) {
		_messagesReceived.addLast( cancelled );
	}

	public void processCancelRejected(CancelRejected cancelRejected) {
		_messagesReceived.addLast( cancelRejected );
	}

	public void processOrderAccepted(OrderAccepted orderAccepted) {
		_messagesReceived.addLast( orderAccepted );
	}

	public void processOrderRejected(OrderRejected orderRejected) {
		_messagesReceived.addLast( orderRejected );
	}

	public ExchangeConnection getExchangeConnection() {
		return _exchangeConnection;
	}

}
