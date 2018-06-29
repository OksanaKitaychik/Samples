package messagesToExchange;

import clientMessageFields.ClientId;
import clientMessageFields.ClientMessageId;
import clientMessageFields.LimitPrice;
import clientMessageFields.MarketId;
import clientMessageFields.OrderMessageType;
import clientMessageFields.OrderSide;
import clientMessageFields.Quantity;
import exchange.Exchange;

public class GTCOrder extends AbstractMessageToExchange {
	
	protected Quantity   _orderQuantity;
	protected OrderSide  _orderSide;
	protected LimitPrice _limitPrice;
	protected MarketId   _marketId;

	public GTCOrder(
		ClientId orderClientId,
		ClientMessageId clientMessageId,
		OrderMessageType orderMessageType,
		MarketId marketId,
		OrderSide orderSide,
		Quantity orderQuantity,
		LimitPrice limitPrice
	) throws Exception {
		super(orderClientId, clientMessageId, orderMessageType);
		if( (orderQuantity == null) || (orderSide == null) || (limitPrice == null) || (marketId == null) )
			throw new Exception( "One of the arguments is null" );
		_orderQuantity = orderQuantity;
		_orderSide = orderSide;
		_limitPrice = limitPrice;
		_marketId = marketId;
	}

	public Quantity getOrderQuantity() {
		return _orderQuantity;
	}

	public OrderSide getOrderSide() {
		return _orderSide;
	}
	
	@Override
	public void getProcessedBy(Exchange exchange) throws Exception {
		exchange.processGTC( this );
	}
	
	public LimitPrice getLimitPrice() {
		return _limitPrice;
	}
	
	public MarketId getMarketId() { return _marketId; }
	
}
