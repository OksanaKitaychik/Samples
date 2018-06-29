package messagesToExchange;

import clientMessageFields.ClientId;
import clientMessageFields.ClientMessageId;
import clientMessageFields.OrderMessageType;
import exchange.Exchange;

public abstract class AbstractMessageToExchange {

	protected ClientId         _clientId;
	protected ClientMessageId  _clientMessageId;
	protected OrderMessageType _orderMessageType;
	
	public AbstractMessageToExchange (
		ClientId         clientId,
		ClientMessageId  clientMessageId,
		OrderMessageType orderMessageType	
	) {
		_clientId = clientId;
		_clientMessageId = clientMessageId;
		_orderMessageType = orderMessageType;
	}

	public ClientId getClientId() {
		return _clientId;
	}

	public ClientMessageId getClientMessageId() {
		return _clientMessageId;
	}

	public OrderMessageType getOrderMessageType() {
		return _orderMessageType;
	}

	public abstract void getProcessedBy( Exchange exchange ) throws Exception;
	
}
