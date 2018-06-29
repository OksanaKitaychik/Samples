package clientMessageFields;

public class OrderSide {
	
	public static OrderSide BUY  = new OrderSide("BUY");
	public static OrderSide SELL = new OrderSide("SELL");
	
	protected String _description;
	
	private OrderSide( String description ) {
		_description = description;
	}

}
