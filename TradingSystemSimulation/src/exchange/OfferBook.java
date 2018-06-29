package exchange;

/**
 * Stores order data of the Sell Order Side
 * 
 * @author Oksana Kitaychik
 *
 */
public class OfferBook extends AbstractBook {
	
	public OfferBook(Long quantity, Double limitPrice, Long clientId, Long clientMessageId, Long exchangeOrderId) throws Exception {
		super(quantity, limitPrice, clientId, clientMessageId, exchangeOrderId);
	}
}
