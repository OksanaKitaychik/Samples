package exchange;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import clientMessageFields.ClientId;
import clientMessageFields.ClientMessageId;
import clientMessageFields.MarketId;
import clientMessageFields.OrderSide;
import clientMessageFields.Quantity;
import exchangeMessageFields.ExchangeMessageType;
import exchangeMessageFields.ExchangeOrderId;
import exchangeMessageFields.Explanation;
import exchangeMessageFields.FillPrice;
import messagesToClient.CancelRejected;
import messagesToClient.Cancelled;
import messagesToClient.Fill;
import messagesToClient.OrderAccepted;
import messagesToClient.OrderRejected;
import messagesToExchange.AbstractMessageToExchange;
import messagesToExchange.CancelOrder;
import messagesToExchange.GTCOrder;

public class Exchange {
	
	protected ClientConnection _clientConnection;
	protected HashMap<ClientId,Client> _clients;
	protected HashMap<MarketId, OrderBook> _orderBooks;
	public LinkedList<AbstractMessageToExchange> _messagesReceived = new LinkedList<AbstractMessageToExchange>();
	private OrderBook _book;
	private MarketId _marketId;
	private Long _exchangeOrderId;
	
	public Exchange( ClientConnection clientConnection ) throws Exception {
		if( clientConnection == null )
			throw new Exception( "Argument is null" );
		_clientConnection = clientConnection;
		_orderBooks = new HashMap<MarketId,OrderBook>();
		_clients = new HashMap<ClientId,Client>();
		_book = new OrderBook();
		_exchangeOrderId = 1L;
	}
	
	public HashMap<MarketId,OrderBook> getOrderBooks() { return _orderBooks;}
	
	protected ClientConnection getClientConnection() { return _clientConnection; }
	
	public LinkedList<AbstractMessageToExchange> getMessagesReceived() { return _messagesReceived; }
	
	private MarketId getMarketId() { return _marketId; }
	
	public void receiveMessage( AbstractMessageToExchange messageToExchange ) throws Exception {
		messageToExchange.getProcessedBy( this );
	}

	/**
	 * Processes orders and its data submitted by the Client
	 * There are two types of orders: Buy and Sell. Each order is stored in the Order Book (i.e., Bid or Offer Book)
	 * If Market Id is not found the order is rejected
	 * 
	 * @param gtcOrder
	 * @throws Exception
	 */
	public void processGTC( GTCOrder gtcOrder ) throws Exception {
		//Check if Market ticker (e.g., IBM, DELL) is available for this order - possible action is Order Rejected
		AvailableMarkets availableMarkets = new AvailableMarkets();
		if (!availableMarkets.isAvailableMarket(gtcOrder.getMarketId())) {
			exchangeMessageAction(ExchangeMessageType.ORDER_REJECTED, gtcOrder);
		}
		else {
			if (OrderSide.BUY == gtcOrder.getOrderSide()) {
				//Add buy order to the Bid Book and sort the data via TreeSet/Comparator Log(n) efficiency
				BidBook bidBook = new BidBook(
						gtcOrder.getOrderQuantity().getValue(),
						gtcOrder.getLimitPrice().getValue(),
						gtcOrder.getClientId().getValue(),
						gtcOrder.getClientMessageId().getValue(),
						_exchangeOrderId);
				_orderBooks.put(gtcOrder.getMarketId(), _book.buyOrderSide(bidBook, _orderBooks.get(gtcOrder.getMarketId())));
				//Retain Market Id for future actions (e.g., cancel order)
				_marketId = gtcOrder.getMarketId();
				//There is NO Match and we simply accept an order
				if(!executeTradeMatchBid())
					exchangeMessageAction(ExchangeMessageType.ORDER_ACCEPTED, gtcOrder, bidBook.getExchangeOrderId());
			} 
			else if (OrderSide.SELL == gtcOrder.getOrderSide()) {
				//Add sell order to the Offer Book and sort the data via TreeSet/Comparator Log(n) efficiency
				OfferBook offerBook = new OfferBook(
						gtcOrder.getOrderQuantity().getValue(),
						gtcOrder.getLimitPrice().getValue(),
						gtcOrder.getClientId().getValue(),
						gtcOrder.getClientMessageId().getValue(),
						_exchangeOrderId);
				//_orderBooks.put(gtcOrder.getMarketId(), _book.sellOrderSide(offerBook, gtcOrder.getMarketId()));
				_orderBooks.put(gtcOrder.getMarketId(), _book.sellOrderSide(offerBook, _orderBooks.get(gtcOrder.getMarketId())));
				//Retain Market Id for future actions (e.g., cancel order)
				_marketId = gtcOrder.getMarketId();
				if(!executeTradeMatchOffer())
					exchangeMessageAction(ExchangeMessageType.ORDER_ACCEPTED, gtcOrder, offerBook.getExchangeOrderId());
			}
		}
		/*
		 * Since sort is via Price-Time Priority, each Exchange Order Id represents Time in a Set and incremented below
		 * This ensures that each record is unique, which is necessary since Bid Book and Offer Book data is represented by a Set
		 */
		_exchangeOrderId++; 
		_messagesReceived.addLast( gtcOrder );
	}
	
	/**
	 * Cancel order initiated by the Client and consists of two possible responses
	 * If Exchange Order ID is found, the exchange executes cancellation
	 * If Exchange Order ID is NOT found, the exchange executes cancel rejected 
	 * 
	 * @param cancelOrder
	 * @throws Exception 
	 */
	public void processCancel(CancelOrder cancelOrder) throws Exception {
		OrderBook orderBook = getOrderBooks().get(getMarketId());
		Iterator<BidBook> iterBidBook = orderBook.getBidBookSet().iterator();
		Iterator<OfferBook> iterOfferBook = orderBook.getOfferBookSet().iterator();
		boolean isOrderIdFound = false;
		//Iterate through Bid Book data and remove order with matched Exchange Order Id
		while (iterBidBook.hasNext()) {
			if(iterBidBook.next().getExchangeOrderId() == cancelOrder.getExchangeOrderId().getValue()) {
				iterBidBook.remove();
				orderCancelled(cancelOrder);
				isOrderIdFound = true;
				break;
			}
		}
		//Iterate through Offer Book data and remove Order with matched Exchanged Order Id
		if (!isOrderIdFound) {
			while (iterOfferBook.hasNext()) {
				if(iterOfferBook.next().getExchangeOrderId() == cancelOrder.getExchangeOrderId().getValue()) {
					iterOfferBook.remove();
					orderCancelled(cancelOrder);
					isOrderIdFound = true;
					break;
				}
			}
		}
		if (!isOrderIdFound)
			orderCancelRejected(cancelOrder);
		_messagesReceived.addLast( cancelOrder );
	}
	
	/**
	 * Utilized to scan Bid Book and Offer Book and match orders accordingly
	 * This specific method is called when the Buy Order Side is executed
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean executeTradeMatchBid() throws Exception {
		OrderBook orderBook = getOrderBooks().get(getMarketId());
		Iterator<BidBook> iterBidBook = orderBook.getBidBookSet().iterator();
		Iterator<OfferBook> iterOfferBook = orderBook.getOfferBookSet().iterator();
		boolean isMatch = false;
		while (iterBidBook.hasNext()) {
			BidBook bidBook = iterBidBook.next();
			while (iterOfferBook.hasNext() && orderBook.getBidBookSet().size() > 0) {
				OfferBook offerBook = iterOfferBook.next();
				if(bidBook.getLimitPrice() >= offerBook.getLimitPrice()) {
					if(bidBook.getQuantity() == offerBook.getQuantity()) {
						//Fill Bid Book and send a message to the client
						orderFill(bidBook.getClientId(),
								bidBook.getClientMessageId(),
								bidBook.getExchangeOrderId(),
								bidBook.getQuantity(),
								offerBook.getLimitPrice()); //note we fill/sell at seller's price
						//Fill Offer Book and send a message to the client
						orderFill(offerBook.getClientId(),
								offerBook.getClientMessageId(),
								offerBook.getExchangeOrderId(),
								offerBook.getQuantity(),
								offerBook.getLimitPrice());
						//Remove records since we filled from Bid and Offer Books
						iterBidBook.remove();
						iterOfferBook.remove();
						if (orderBook.getBidBookSet().size() > 0)
							bidBook = iterBidBook.next();
						isMatch = true;
					}
					else if (bidBook.getQuantity() > offerBook.getQuantity()) {
						//Fill Bid Book and send a message to the client
						orderFill(bidBook.getClientId(),
								bidBook.getClientMessageId(),
								bidBook.getExchangeOrderId(),
								bidBook.getQuantity() - offerBook.getQuantity(),
								offerBook.getLimitPrice()); //note we fill/sell at seller's price
						//Fill Offer Book and send a message to the client
						orderFill(offerBook.getClientId(),
								offerBook.getClientMessageId(),
								offerBook.getExchangeOrderId(),
								offerBook.getQuantity(),
								offerBook.getLimitPrice());
						//Update records
						bidBook.setQuantity(bidBook.getQuantity() - offerBook.getQuantity());
						iterOfferBook.remove();
						isMatch = true;
					}
					else if (bidBook.getQuantity() < offerBook.getQuantity()) {
						//Fill Bid Book and send a message to the client
						orderFill(bidBook.getClientId(),
								bidBook.getClientMessageId(),
								bidBook.getExchangeOrderId(),
								bidBook.getQuantity(),
								offerBook.getLimitPrice()); //note we fill/sell at seller's price
						//Fill Offer Book and send a message to the client
						orderFill(offerBook.getClientId(),
								offerBook.getClientMessageId(),
								offerBook.getExchangeOrderId(),
								offerBook.getQuantity() - bidBook.getQuantity(),
								offerBook.getLimitPrice());
						//Update records
						iterBidBook.remove();
						offerBook.setQuantity(offerBook.getQuantity() - bidBook.getQuantity());
						if (orderBook.getBidBookSet().size() > 0)
							bidBook = iterBidBook.next();
						isMatch = true;
					}
				}
			}
		}
		return isMatch;
	}
	
	/**
	 * Utilized to scan Bid Book and Offer Book and match orders accordingly
	 * This specific method is called when the Sell Order Side is executed
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean executeTradeMatchOffer() throws Exception {
		OrderBook orderBook = getOrderBooks().get(getMarketId());
		Iterator<BidBook> iterBidBook = orderBook.getBidBookSet().iterator();
		Iterator<OfferBook> iterOfferBook = orderBook.getOfferBookSet().iterator();
		boolean isMatch = false;
		while (iterOfferBook.hasNext()) {
			OfferBook offerBook = iterOfferBook.next();
			while (iterBidBook.hasNext() && orderBook.getOfferBookSet().size() > 0) {
				BidBook bidBook = iterBidBook.next();
				if(bidBook.getLimitPrice() >= offerBook.getLimitPrice()) {
					if(bidBook.getQuantity() == offerBook.getQuantity()) {
						//Fill Bid Book and send a message to the client
						orderFill(bidBook.getClientId(),
								bidBook.getClientMessageId(),
								bidBook.getExchangeOrderId(),
								bidBook.getQuantity(),
								bidBook.getLimitPrice()); 
						//Fill Offer Book and send a message to the client
						orderFill(offerBook.getClientId(),
								offerBook.getClientMessageId(),
								offerBook.getExchangeOrderId(),
								offerBook.getQuantity(),
								bidBook.getLimitPrice()); //note we fill/sell at buyer's price
						//Remove records since we filled from Bid and Offer Books
						iterBidBook.remove();
						iterOfferBook.remove();
						if (orderBook.getOfferBookSet().size() > 0)
							offerBook = iterOfferBook.next();
						isMatch = true;
					}
					else if (bidBook.getQuantity() > offerBook.getQuantity()) {
						//Fill Bid Book and send a message to the client
						orderFill(bidBook.getClientId(),
								bidBook.getClientMessageId(),
								bidBook.getExchangeOrderId(),
								bidBook.getQuantity() - offerBook.getQuantity(),
								bidBook.getLimitPrice()); 
						//Fill Offer Book and send a message to the client
						orderFill(offerBook.getClientId(),
								offerBook.getClientMessageId(),
								offerBook.getExchangeOrderId(),
								offerBook.getQuantity(),
								bidBook.getLimitPrice()); //note we fill/sell at seller's price
						//Update records
						bidBook.setQuantity(bidBook.getQuantity() - offerBook.getQuantity());
						iterOfferBook.remove();
						if (orderBook.getOfferBookSet().size() > 0)
							offerBook = iterOfferBook.next();
						isMatch = true;
					}
					else if (bidBook.getQuantity() < offerBook.getQuantity()) {
						//Fill Bid Book and send a message to the client
						orderFill(bidBook.getClientId(),
								bidBook.getClientMessageId(),
								bidBook.getExchangeOrderId(),
								bidBook.getQuantity(),
								bidBook.getLimitPrice()); 
						//Fill Offer Book and send a message to the client
						orderFill(offerBook.getClientId(),
								offerBook.getClientMessageId(),
								offerBook.getExchangeOrderId(),
								offerBook.getQuantity() - bidBook.getQuantity(),
								bidBook.getLimitPrice()); //note we fill/sell at seller's price
						//Update records
						iterBidBook.remove();
						offerBook.setQuantity(offerBook.getQuantity() - bidBook.getQuantity());
						isMatch = true;
					}
				}
			}
		}
		return isMatch;
	}
	
	/**
	 * Exchange action/response to the Client 
	 * 
	 * @param exchangeMessage Actual Message
	 * @param gtcOrder Instance of the GTCOrder with such order information as Client ID, Quantity, Price, etc
	 * @throws Exception
	 */
	private void exchangeMessageAction(ExchangeMessageType exchangeMessage, GTCOrder gtcOrder) throws Exception {
		if(exchangeMessage == ExchangeMessageType.ORDER_REJECTED) {
			orderRejected(gtcOrder);
		}
	}
	
	/**
	 * Exchange action/response to the Client 
	 * 
	 * @param exchangeMessage Actual Message
	 * @param gtcOrder Instance of the GTCOrder with such order information as Client ID, Quantity, Price, etc
	 * @param exchangeOrderId Generated Exchange Order ID
	 * @throws Exception
	 */
	private void exchangeMessageAction(ExchangeMessageType exchangeMessage, GTCOrder gtcOrder, Long exchangeOrderId) throws Exception {
		if(exchangeMessage == ExchangeMessageType.ORDER_REJECTED) {
			orderRejected(gtcOrder);
		}
		else if (exchangeMessage == ExchangeMessageType.ORDER_ACCEPTED) {
			orderAccepted(gtcOrder, exchangeOrderId);
		}
	}
	
	/**
	 * Fills all orders by sending message back to the client
	 * 
	 * @param clientId Client Id is part of the Order
	 * @param clientMessageId Client Message Id is part of the Order
	 * @param exchangeOrderId Randomly Generated Id in the AbstractBook constructor
	 * @param quantity Quantity specified by the client in each order
	 * @param price Price specified by the client in each order
	 * @throws Exception
	 */
	private void orderFill(Long clientId, Long clientMessageId, Long exchangeOrderId, Long quantity, Double price) throws Exception {
		ExchangeMessageType exchangeMessageType = ExchangeMessageType.FILL;
		
		//We instantiate a quantity field
		Quantity fillQuantity = new Quantity(quantity);
		
		//We instantiate a fill price field
		FillPrice fillPrice = new FillPrice(price);
		
		//We instantiate Client Id, Client Message Id, Exchange Order Id
		ClientId fillClientId = new ClientId(clientId);
		ClientMessageId fillClientMessageId = new ClientMessageId(clientMessageId);
		ExchangeOrderId fillExchangeOrderId = new ExchangeOrderId(exchangeOrderId);
		
		//We make sure fill price saved the fill price value
		Fill fill = new Fill(fillClientId, fillClientMessageId, exchangeMessageType, fillExchangeOrderId, fillQuantity, fillPrice);
	    
		//We try sending this message to the client
		getClientConnection().sendMessage(fill);
	}
	
	/**
	 * We cancel this order because Exchange Order Id was found
	 * 
	 * @param cancelOrder Contains such info client id, client message id, etc
	 * @throws Exception
	 */
	private void orderCancelled(CancelOrder cancelOrder) throws Exception {
		//Set Exchange Order Id
		ExchangeOrderId exchangeOrderId = new ExchangeOrderId(cancelOrder.getExchangeOrderId().getValue());
		
		//Set Message Type
		ExchangeMessageType exchangeMessageType = ExchangeMessageType.CANCELLED;
		
		//Set Cancelled data
		Cancelled cancelled = new Cancelled(cancelOrder.getClientId(), cancelOrder.getClientMessageId(), exchangeMessageType, exchangeOrderId);
	    
		//We try sending this message to the client
		getClientConnection().sendMessage(cancelled);
	}
	
	/**
	 * We reject cancel this order because Exchange Order Id was NOT found
	 * 
	 * @param cancelOrder
	 * @throws Exception
	 */
	private void orderCancelRejected(CancelOrder cancelOrder) throws Exception {
		ExchangeMessageType exchangeMessageType = ExchangeMessageType.CANCEL_REJECTED;
		
		// We instantiate a new explanation for why the error happened
		String explanationText = "Your CANCEL message was rejected because the Exchange Order ID "+cancelOrder.getExchangeOrderId().getValue() + " i.e., Exchange Order ID you specified does not exist";
		Explanation explanation = new Explanation(explanationText);
		
		//We make sure that the explanation text was saved
		CancelRejected cancelRejected = new CancelRejected(cancelOrder.getClientId(), cancelOrder.getClientMessageId(), exchangeMessageType, explanation);
	    
		//We try sending this message as if it were being sent by the exchange
		getClientConnection().sendMessage(cancelRejected);
	}
	
	/**
	 * We reject this order because Market ID was not found
	 * 
	 * @param gtcOrder Instance of the GTCOrder with such order information as Client ID, Quantity, Price, etc
	 * @throws Exception
	 */
	private void orderRejected(GTCOrder gtcOrder) throws Exception {
		ExchangeMessageType exchangeMessageType = ExchangeMessageType.ORDER_REJECTED;
		
		//We instantiate a new explanation
		String explanationText = "Your order was rejected as the ticker you specified, "+gtcOrder.getMarketId().getValue()+", does not exist in our exchange system ";
		Explanation explanation = new Explanation( explanationText );
		
		OrderRejected orderRejected = new OrderRejected( gtcOrder.getClientId(), gtcOrder.getClientMessageId(), exchangeMessageType, explanation );
		
		//We try sending this message to the client
		getClientConnection().sendMessage(orderRejected);
	}
	
	/**
	 * Order is accepted and corresponding message is sent
	 * 
	 * @param gtcOrder Instance of the GTCOrder with such order information as Client ID, Quantity, Price, etc 
	 * @param id Generated Exchange Order ID
	 * @throws Exception
	 */
	private void orderAccepted(GTCOrder gtcOrder, Long id) throws Exception {
		ExchangeMessageType exchangeMessageType = ExchangeMessageType.ORDER_ACCEPTED;
		
		//We retrieve the quantity passed from the Client
		Quantity quantity = new Quantity( gtcOrder.getOrderQuantity().getValue() );
		
		//Set generated Exchange Order Id
		ExchangeOrderId exchangeOrderId = new ExchangeOrderId( id );
		
		OrderAccepted orderAccepted = new OrderAccepted(
				gtcOrder.getClientId(), 
				gtcOrder.getClientMessageId(),
				exchangeMessageType,
				quantity, 
				exchangeOrderId 
		);
		
	    //We try sending the order to the client
		getClientConnection().sendMessage(orderAccepted);
	}
}
