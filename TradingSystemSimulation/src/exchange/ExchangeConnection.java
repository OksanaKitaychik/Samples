package exchange;

import messagesToExchange.AbstractMessageToExchange;

public class ExchangeConnection {

	protected Exchange _exchange;
	
	public ExchangeConnection( Exchange exchange ) throws Exception {
		if( exchange == null )
			throw new Exception( "Argument is null" );
		_exchange = exchange;
	}
	
	public void sendMessage( AbstractMessageToExchange message ) throws Exception {
		_exchange.receiveMessage( message );
	}
	
}
