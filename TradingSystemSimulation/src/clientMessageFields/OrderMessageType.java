package clientMessageFields;

public class OrderMessageType {

	public static OrderMessageType GTC = new OrderMessageType( "GTC" );
	public static OrderMessageType CANCEL = new OrderMessageType( "CANCEL" );
	
	public static String ERROR_MSG = "Your message type specification, [%s], should be either GTC or CANCEL"; 
	protected String _description; 
	
	private OrderMessageType( String description ) {
		_description = description;
	}
	
	@Override
	public String toString() { return "OrderMessageType(" + _description + ")"; }
	
	public static OrderMessageType parse( String description ) throws Exception {
		if( description != null )
			if( description.equals( "GTC" ) )
				return GTC;
			else if ( description.equals( "CANCEL" ) )
				return CANCEL;
		throw new Exception( String.format( ERROR_MSG, description ) );
	}
	
}
