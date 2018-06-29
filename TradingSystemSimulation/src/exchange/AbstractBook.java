package exchange;

import java.util.Date;

/**
 * Parent class for BidBook and OfferBook classes. Utilized to store order information sent by the client to exchange
 * 
 * @author Oksana Kitaychik
 *
 */
public abstract class AbstractBook {
	
	private Long _quantity;
	private Double _limitPrice;
	private Date _orderDate;
	private Long _exchangeOrderId;
	private Long _clientId;
	private Long _clientMessageId;
	
	public Long getQuantity() {
		return _quantity;
	}
	
	public Double getLimitPrice() {
		return _limitPrice;
	}
	
	public Long getExchangeOrderId() {
		return _exchangeOrderId;
	}
	
	public Date getOrderDate() {
		return _orderDate;
	}
	
	public Long getClientId() {
		return _clientId;
	}
	
	public Long getClientMessageId() {
		return _clientMessageId;
	}
	
	public AbstractBook(Long quantity, Double limitPrice, Long clientId, Long clientMessageId, Long exchangeOrderId) throws Exception {
		_quantity = quantity;
		_limitPrice = limitPrice;
		_orderDate = new Date();
		_clientId = clientId;
		_clientMessageId = clientMessageId;
		_exchangeOrderId = exchangeOrderId;
	}
	
	public void setQuantity(Long quantity) {
		_quantity = quantity;
	}

	public void setLimitPrice(Double limitPrice) {
		_limitPrice = limitPrice;
	}

	public void setOrderDate(Date orderDate) {
		_orderDate = orderDate;
	}

	public void setExchangeOrderId(Long exchangeOrderId) {
		_exchangeOrderId = exchangeOrderId;
	}

	@Override
	public String toString() {
		return getQuantity() + " " + getLimitPrice() + " " + getExchangeOrderId() + " " + getOrderDate();
	}
}
