package messagesToClient;

import clientMessageFields.ClientId;
import clientMessageFields.ClientMessageId;
import clientMessageFields.Quantity;
import exchange.Client;
import exchangeMessageFields.ExchangeMessageType;
import exchangeMessageFields.ExchangeOrderId;
import exchangeMessageFields.FillPrice;

public class Fill extends AbstractMessageToClient {
	
	protected ExchangeOrderId _exchangeOrderId;
	protected Quantity        _quantity;
	protected FillPrice       _fillPrice;

	public Fill(
		ClientId            clientId, 
		ClientMessageId     clientMessageId,
		ExchangeMessageType exchangeMessageType,
		ExchangeOrderId     exchangeOrderId,
		Quantity            quantity,
		FillPrice           fillPrice
	) throws Exception {
		super(clientId, clientMessageId, exchangeMessageType, exchangeOrderId);
		if( ( exchangeOrderId == null ) || (quantity == null) || (fillPrice == null) )
			throw new Exception( "One of the arguments is null" );
		_exchangeOrderId = exchangeOrderId;
		_quantity = quantity;
		_fillPrice = fillPrice;
	}

	@Override
	public void getProcessedBy( Client client ) {
		client.processFill( this );
	}
	
	public ExchangeOrderId getExchangeOrderId() {
		return _exchangeOrderId;
	}

	public Quantity getQuantity() {
		return _quantity;
	}

}
