package messagesToExchange;

import clientMessageFields.ClientId;
import clientMessageFields.ClientMessageId;
import clientMessageFields.OrderMessageType;
import exchange.Exchange;
import exchangeMessageFields.ExchangeOrderId;

public class CancelOrder extends AbstractMessageToExchange {
	
	protected ExchangeOrderId _exchangeOrderId;

	public CancelOrder(
		ClientId         orderClientId,
		ClientMessageId  orderClientMessageId,
		OrderMessageType orderMessageType,
		ExchangeOrderId  exchangeOrderId
	) throws Exception {
		super( orderClientId, orderClientMessageId, orderMessageType );
		if( exchangeOrderId == null)
			throw new Exception( "One of the arguments is null" );
		_exchangeOrderId = exchangeOrderId;
	}

	public ExchangeOrderId getExchangeOrderId() {
		return _exchangeOrderId;
	}

	@Override
	public void getProcessedBy(Exchange exchange) throws Exception {
		exchange.processCancel( this );
	}
	

}
