package exchangeMessageFields;

public class ExchangeMessageType {

	public static ExchangeMessageType CANCEL_REJECTED = new ExchangeMessageType( "CANCEL_REJECTED" );
	public static ExchangeMessageType CANCELLED = new ExchangeMessageType( "CANCELLED" );
	public static ExchangeMessageType FILL = new ExchangeMessageType( "FILL" );
	public static ExchangeMessageType ORDER_REJECTED = new ExchangeMessageType( "ORDER_REJECTED" );
	public static ExchangeMessageType ORDER_ACCEPTED = new ExchangeMessageType( "ORDER_ACCEPTED" );
	
	protected String _description;
	
	private ExchangeMessageType( String description ) {
		_description = description;
	}
	
}
