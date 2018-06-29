package messagesToClient;

import clientMessageFields.ClientId;
import clientMessageFields.ClientMessageId;
import exchange.Client;
import exchangeMessageFields.ExchangeMessageType;
import exchangeMessageFields.ExchangeOrderId;

public abstract class AbstractMessageToClient {

	protected ClientId            _clientId;
	protected ClientMessageId     _clientMessageId;
	protected ExchangeMessageType _exchangeMessageType;
	protected ExchangeOrderId     _exchangeOrderId;
	
	public AbstractMessageToClient (
		ClientId            clientId,
		ClientMessageId     clientMessageId,
		ExchangeMessageType exchangeMessageType,
		ExchangeOrderId     exchangeOrderId
	) {
		_clientId            = clientId;
		_clientMessageId     = clientMessageId;
		_exchangeMessageType = exchangeMessageType;
		_exchangeOrderId     = exchangeOrderId;
	}
	
	public AbstractMessageToClient (
		ClientId            clientId,
		ClientMessageId     clientMessageId,
		ExchangeMessageType exchangeMessageType
	) {
		_clientId            = clientId;
		_clientMessageId     = clientMessageId;
		_exchangeMessageType = exchangeMessageType;
	}

	public ClientId getClientId() {
		return _clientId;
	}

	public ClientMessageId getClientMessageId() {
		return _clientMessageId;
	}

	public ExchangeMessageType getMessageType() {
		return _exchangeMessageType;
	}
	
	public ExchangeOrderId getExchangeOrderId() {
		return _exchangeOrderId;
	}
	
	public abstract void getProcessedBy( Client client );
	
}
