package messagesToClient;

import clientMessageFields.ClientId;
import clientMessageFields.ClientMessageId;
import exchange.Client;
import exchangeMessageFields.ExchangeMessageType;
import exchangeMessageFields.ExchangeOrderId;

public class Cancelled extends AbstractMessageToClient {

	protected ExchangeOrderId _exchangeOrderId;
	
	public Cancelled(
		ClientId            clientId, 
		ClientMessageId     clientMessageId,
		ExchangeMessageType exchangeMessageType,
		ExchangeOrderId     exchangeOrderId
	) throws Exception {
		super(clientId, clientMessageId, exchangeMessageType, exchangeOrderId);
		if( exchangeOrderId == null )
			throw new Exception( "One of the arguments is null" );
		_exchangeOrderId = exchangeOrderId;
	}

	@Override
	public void getProcessedBy( Client client ) {
		client.processCancelled( this );
	}
	
	public ExchangeOrderId getExchangeOrderId() {
		return _exchangeOrderId;
	}


}
