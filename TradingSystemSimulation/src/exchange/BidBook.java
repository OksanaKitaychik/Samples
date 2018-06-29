package exchange;

import java.lang.Long;

/**
 * Stores order data of the Buy Order Side
 * 
 * @author Oksana Kitaychik
 *
 */
public class BidBook extends AbstractBook {

	public BidBook(Long quantity, Double limitPrice, Long clientId, Long clientMessageId, Long exchangeOrderId) throws Exception {
		super(quantity, limitPrice, clientId, clientMessageId, exchangeOrderId);
	}
}
