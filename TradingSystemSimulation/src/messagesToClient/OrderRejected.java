package messagesToClient;

import clientMessageFields.ClientId;
import clientMessageFields.ClientMessageId;
import exchange.Client;
import exchangeMessageFields.ExchangeMessageType;
import exchangeMessageFields.Explanation;

public class OrderRejected extends AbstractMessageToClient {

	protected Explanation _explanation;
	
	public OrderRejected(
		ClientId clientId, 
		ClientMessageId clientMessageId,
		ExchangeMessageType exchangeMessageType,
		Explanation explanation
	) throws Exception {
		super(clientId, clientMessageId, exchangeMessageType);
		if( explanation == null )
			throw new Exception( "One of the arguments is null" );
		_explanation = explanation;
	}
	
	public Explanation getExplanation() { return _explanation; }

	@Override
	public void getProcessedBy(Client client) {
		client.processOrderRejected( this );
	}

}
