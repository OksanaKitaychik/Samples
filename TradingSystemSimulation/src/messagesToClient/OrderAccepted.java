package messagesToClient;

import clientMessageFields.ClientId;
import clientMessageFields.ClientMessageId;
import clientMessageFields.Quantity;
import exchange.Client;
import exchangeMessageFields.ExchangeMessageType;
import exchangeMessageFields.ExchangeOrderId;

public class OrderAccepted extends AbstractMessageToClient {

	protected Quantity _quantity;
	protected ExchangeOrderId _exchangeOrderId;
	
	public OrderAccepted(
		ClientId clientId, 
		ClientMessageId clientMessageId,
		ExchangeMessageType exchangeMessageType,
		Quantity quantity,
		ExchangeOrderId exchangeOrderId
	) throws Exception {
		super(clientId, clientMessageId, exchangeMessageType, exchangeOrderId);
		if( (quantity == null ) || ( exchangeOrderId == null) )
			throw new Exception( "One of the arguments is null" );
		_quantity = quantity;
		_exchangeOrderId = exchangeOrderId;
	}
	
	public Quantity getQuantity() {
		return _quantity;
	}

	public ExchangeOrderId getExchangeOrderId() {
		return _exchangeOrderId;
	}

	@Override
	public void getProcessedBy(Client client) {
		client.processOrderAccepted( this );
	}

}
