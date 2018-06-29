package exchange;

import messagesToClient.CancelRejected;
import messagesToClient.Cancelled;
import messagesToClient.Fill;
import messagesToClient.OrderAccepted;
import messagesToClient.OrderRejected;
import messagesToExchange.CancelOrder;
import messagesToExchange.GTCOrder;

import java.util.Iterator;

import clientMessageFields.ClientId;
import clientMessageFields.ClientMessageId;
import clientMessageFields.LimitPrice;
import clientMessageFields.MarketId;
import clientMessageFields.OrderMessageType;
import clientMessageFields.OrderSide;
import clientMessageFields.Quantity;
import exchangeMessageFields.ExchangeMessageType;
import exchangeMessageFields.ExchangeOrderId;
import exchangeMessageFields.Explanation;
import exchangeMessageFields.FillPrice;

public class Test_Exchange extends junit.framework.TestCase {
	
	
	/**
	 * Test Various Scenarios of the Bid Book and Offer Book. 
	 * Below is an example of one of the Tests that was executed.
	 * Order 1 (Sell 200 @ $100, Sell 100 @ $200) Order 2 (Buy 100 @ $100); Bid Book order matches one record in Offer Book
	 * Result Expected: (Sell 100 (decreased quantity) @ $100, Sell 100 @ $200) Bid Book is empty
	 * Corresponding messages were sent to each client
	 * 
	 * @throws Exception
	 */
	public void testExchangeMatching() throws Exception {
		//Instantiate client connection, which is used by exchange to communicate with clients
		ClientConnection clientConnection = new ClientConnection();
		
		//Instantiate exchange using client connection
		Exchange exchange = new Exchange( clientConnection );
		
		//Make sure exchange saves client connection
		assertTrue( exchange.getClientConnection() == clientConnection );
		
		//Instantiate exchange connection, which is used by clients to communicate with exchange
		ExchangeConnection exchangeConnection = new ExchangeConnection(exchange);
		
		//Instantiate one client
		ClientId clientId1 = new ClientId( 13472L );
		Client client1 = new Client( clientId1, exchangeConnection );
		
		//Instantiate second client
		ClientId clientId2 = new ClientId( 23472L );
		Client client2 = new Client( clientId2, exchangeConnection );
		
		//Make sure client saves client id
		assertTrue( client1.getClientId().equals( clientId1 ) );
		assertTrue( client2.getClientId().equals( clientId2 ) );
		
		//Make sure client saved exchange connection
		assertTrue( client1.getExchangeConnection() == exchangeConnection );
		assertTrue( client2.getExchangeConnection() == exchangeConnection );
		
		//Add client to client connection
		clientConnection.addClient( client1 );
		clientConnection.addClient( client2 );
		
		//Make sure client connection saved client
		Client retrievedClient = clientConnection.getClient( clientId1 );
		Client retrievedClient2 = clientConnection.getClient( clientId2 );
		assertTrue( retrievedClient == client1 );
		assertTrue( retrievedClient2 == client2 );
		
		Long clientMessageIdValue1 = new Long( 999L );
		ClientMessageId clientMessageId1 = new ClientMessageId( clientMessageIdValue1 );
		Long clientMessageIdValue2 = new Long( 199L );
		ClientMessageId clientMessageId2 = new ClientMessageId( clientMessageIdValue2 );
		
		// Make sure client message id saved the id value
		assertTrue( clientMessageId1.getValue().equals( clientMessageIdValue1 ) );
		assertTrue( clientMessageId2.getValue().equals( clientMessageIdValue2 ) );
		
		// Instantiate a market id
		String marketName1 = "DELL";
		MarketId marketId1 = new MarketId( marketName1 );
		String marketName2 = "IBM";
		MarketId marketId2 = new MarketId( marketName2 );
		
		// Make sure market id saved market name
		assertTrue( marketId1.getValue().equals( marketName1 ) );
		assertTrue( marketId2.getValue().equals( marketName2 ) );
		
		// Instantiate an order side
		OrderSide orderSide1 = OrderSide.BUY;
		OrderSide orderSide2 = OrderSide.SELL;
		
		// Instantiate an order quantity
		Long orderQuantityValue1 = 200L;
		Quantity quantity1 = new Quantity( orderQuantityValue1 );
		Long orderQuantityValue2 = 100L;
		Quantity quantity2 = new Quantity( orderQuantityValue2 );
		Long orderQuantityValue3 = 40L;
		Quantity quantity3 = new Quantity( orderQuantityValue3 );
		
		// Make sure order quantity save order quantity value
		assertTrue( quantity1.getValue().equals( orderQuantityValue1 ) );
		assertTrue( quantity2.getValue().equals( orderQuantityValue2 ) );
		assertTrue( quantity3.getValue().equals( orderQuantityValue3 ) );
		
		// Instantiate a limit price
		Double limitPriceValue1 = new Double( 100D );
		LimitPrice limitPrice1 = new LimitPrice( limitPriceValue1 );
		Double limitPriceValue2 = new Double( 200D );
		LimitPrice limitPrice2 = new LimitPrice( limitPriceValue2 );
		Double limitPriceValue3 = new Double( 50D );
		LimitPrice limitPrice3 = new LimitPrice( limitPriceValue3 );
		
		// Make sure that limit price saved the limit price value
		assertEquals( limitPrice1.getValue(), limitPriceValue1, 0.001 );
		assertEquals( limitPrice2.getValue(), limitPriceValue2, 0.001 );
		
		GTCOrder gtcOrder1 = new GTCOrder(
			clientId1, 
			clientMessageId1, 
			OrderMessageType.GTC, 
			marketId1, 
			orderSide1, //BUY
			quantity1, //200
			limitPrice1 //$100
		);
		
		//Send message to exchange
		exchangeConnection.sendMessage( gtcOrder1 );
		
		//Make sure that data is stored in the appropriate place in the book 
		assertTrue( exchange.getOrderBooks().get(marketId1).getBidBookSet().size() > 0 );
		
		//Make sure that exchange received the message we sent to it
		GTCOrder receivedOrder = (GTCOrder) exchange.getMessagesReceived().get(0);
		assertTrue( receivedOrder == gtcOrder1 );
		assertTrue( ExchangeMessageType.ORDER_ACCEPTED == client1.getMessagesFromExchange().get(0).getMessageType() );
		assertTrue( client1.getMessagesFromExchange().get(0).getExchangeOrderId() != null );
		
		GTCOrder gtcOrder2 = new GTCOrder(
			clientId2, //New client
			clientMessageId2, //New client message
			OrderMessageType.GTC, 
			marketId1, 
			orderSide2, //Sell Order Side
			quantity1, //Quantity is now set to 200
			limitPrice1 //Price is set to $100
		);
		
		//Send message to exchange
		exchangeConnection.sendMessage( gtcOrder2 );
		
		//Make sure that exchange received the message we sent to it
		receivedOrder = (GTCOrder) exchange.getMessagesReceived().get(1);
		assertTrue( receivedOrder == gtcOrder2 );
		
		//Since we matched Bid Book and Sell Book, the data was removed
		assertTrue( exchange.getOrderBooks().get(marketId1).getBidBookSet().size() == 0 );
		assertTrue( exchange.getOrderBooks().get(marketId1).getOfferBookSet().size() == 0 );
		
		//For client 1, the 1st message is Order Accepted and 2nd message is Fill, testing for 2nd message
		assertTrue( ExchangeMessageType.FILL == client1.getMessagesFromExchange().get(1).getMessageType() );
		
		//For client 2, the 1st message at this point in time is Fill
		assertTrue( ExchangeMessageType.FILL == client2.getMessagesFromExchange().get(0).getMessageType() );
		
		GTCOrder gtcOrder3 = new GTCOrder(
			clientId2, //New client
			clientMessageId2, //New client message
			OrderMessageType.GTC, 
			marketId1, 
			orderSide2, //Sell Order Side
			quantity2, //Quantity is now set to 100
			limitPrice2 //Price set to $200
		);
		
		//Send message to exchange
		exchangeConnection.sendMessage( gtcOrder3 );
		
		assertTrue( exchange.getOrderBooks().get(marketId1).getOfferBookSet().size() == 1 );
		
		//Send message to exchange
		exchangeConnection.sendMessage( gtcOrder2 );
		
		//Last 2 messages are now Order Accepted
		assertTrue( ExchangeMessageType.ORDER_ACCEPTED == client2.getMessagesFromExchange().get(1).getMessageType() );
		assertTrue( ExchangeMessageType.ORDER_ACCEPTED == client2.getMessagesFromExchange().get(2).getMessageType() );
		
		assertTrue( exchange.getOrderBooks().get(marketId1).getBidBookSet().size() == 0 );
		
		GTCOrder gtcOrder4 = new GTCOrder(
			clientId1, 
			clientMessageId1, 
			OrderMessageType.GTC, 
			marketId1, 
			orderSide1, //Buy Order Side
			quantity2, //Quantity is now set to 100
			limitPrice1 //Price set to $100
		);
		
		OfferBook offerBook = exchange.getOrderBooks().get(marketId1).getOfferBookSet().iterator().next();
		
		//Check in position one(first element) quantity and price values
		assertEquals( limitPrice1.getValue(), offerBook.getLimitPrice(), 0.001 ); //$100
		assertEquals( quantity1.getValue(), offerBook.getQuantity(), 0.001 ); //200
		
		//Send message to exchange
		exchangeConnection.sendMessage( gtcOrder4 );
		
		offerBook = exchange.getOrderBooks().get(marketId1).getOfferBookSet().iterator().next();
		
		//Check that in position one(first element) quantity was decreased and price is correct
		assertEquals( limitPrice1.getValue(), limitPrice1.getValue(), 0.001 ); //$100
		//This value should be 100 now. We decreased by 100 because buy order was entered
		assertEquals( quantity2.getValue(), offerBook.getQuantity(), 0.001 ); 
		//The Bid Book was updated right away and after match it has no records
		assertTrue( exchange.getOrderBooks().get(marketId1).getBidBookSet().size() == 0 );
		//Last message for client 1 is now fill
		assertTrue( ExchangeMessageType.FILL == client1.getMessagesFromExchange().get(2).getMessageType() );
		
		GTCOrder gtcOrder5 = new GTCOrder(
			clientId1, 
			clientMessageId1, 
			OrderMessageType.GTC, 
			marketId1, 
			orderSide1, //Buy Order Side
			quantity2, //Quantity is now set to 100
			limitPrice3 //Price set to $50
		);
		
		//Send message to exchange
		exchangeConnection.sendMessage( gtcOrder5 );
		//The Bid Book was updated as there is no Match
		/*
		 * Currently in Offer Book we have (Sell 100 @ $100, Sell 100 $200) and in Bid Book (Buy 100 @ $50)
		 */
		assertTrue( exchange.getOrderBooks().get(marketId1).getBidBookSet().size() == 1 );
		
		BidBook bidBook = exchange.getOrderBooks().get(marketId1).getBidBookSet().iterator().next();
		
		//Check that in position one(first element) quantity is now $50 as there is no match
		assertEquals( limitPrice3.getValue(), bidBook.getLimitPrice(), 0.001 ); //$50
		//This value should be 100 now. We decreased by 100 because buy order was entered
		assertEquals( quantity2.getValue(), bidBook.getQuantity(), 0.001 ); 
		
		/*
		 * Let's now enter (Sell 100 @ $50) in Offer Book. This will match the previous Bid Order
		 * Expected new values: (Sell 100 @ $100, Sell 100 @ $200) and no records in Bid Book
		 */
		GTCOrder gtcOrder6 = new GTCOrder(
			clientId2, //update Client ID 
			clientMessageId2, 
			OrderMessageType.GTC, 
			marketId1, 
			orderSide2, //Sell Order Side
			quantity2, //Quantity is now set to 100
			limitPrice3 //Price set to $50
		);
		
		//Send message to exchange
		exchangeConnection.sendMessage( gtcOrder6 );
				
		//The expected size of the Bid Book is now 0
		assertTrue( exchange.getOrderBooks().get(marketId1).getBidBookSet().size() == 0 );
		
		offerBook = exchange.getOrderBooks().get(marketId1).getOfferBookSet().iterator().next();
		
		//Check that in position one(first element) quantity was decreased and price is correct
		assertEquals( limitPrice1.getValue(), offerBook.getLimitPrice(), 0.001 ); //$100
		//This value should be 100 now. We decreased by 100 because buy order was entered
		assertEquals( quantity2.getValue(), offerBook.getQuantity(), 0.001 ); 
		
		/*
		 * Let's now enter (Sell 100 @ $200) in Offer Book. 
		 * Expected new values: (Sell 100 @ $100, Sell 100 @ $200, Sell 100 @ $200) and no records in Bid Book
		 */
		exchangeConnection.sendMessage( gtcOrder3 );
		
        Iterator<OfferBook> iterOfferBook = exchange.getOrderBooks().get(marketId1).getOfferBookSet().iterator();
		
        Double price = 0D;
		int i = 0;
		while (iterOfferBook.hasNext()) {
			OfferBook offerBook2 = iterOfferBook.next();
			price = offerBook2.getLimitPrice();
			if (i == 3)
				break; 
		}
		
		//Check that in position one(first element) quantity was decreased and price is correct
		assertEquals( limitPrice2.getValue(), price, 0.001 ); //$200
		
		//Ensuring that size is now 3
		assertEquals( exchange.getOrderBooks().get(marketId1).getOfferBookSet().size(), 3, 0.001);
		
		/*
		 * Let's now enter (Buy 40 @ $200) in Offer Book. 
		 * Current: (Sell 100 @ $100, Sell 100 @ $200, Sell 100 @ $200)
		 * Expected new values: (Sell 60 @ $100, Sell 100 @ $200, Sell 100 @ $200) and no records in Bid Book
		 */
		
		GTCOrder gtcOrder7 = new GTCOrder(
			clientId1, 
			clientMessageId1, 
			OrderMessageType.GTC, 
			marketId1, 
			orderSide1, //Buy Order Side
			quantity3, //Quantity is now set to 40
			limitPrice2 //Price set to $200
		);
		
		exchangeConnection.sendMessage( gtcOrder7 );
		
		offerBook = exchange.getOrderBooks().get(marketId1).getOfferBookSet().iterator().next();
		
		//Check that in position one(first element) quantity was decreased by 100L - 40L
		assertEquals( 60L, offerBook.getQuantity(), 0.001 ); //60
		
		exchangeConnection.sendMessage( gtcOrder7 );
		
		offerBook = exchange.getOrderBooks().get(marketId1).getOfferBookSet().iterator().next();
		
		//Check that in position one(first element) quantity was decreased by 60L - 40L
		assertEquals( 20L, offerBook.getQuantity(), 0.001 ); //20
		
		/*
		 * Let's now enter (Buy 40 @ $200) in Offer Book.
		 * Current: (Sell 20 @ $100, Sell 100 @ $200, Sell 100 @ $200)
		 * Expected new values: (Sell 80 @ $200, Sell 100 @ $200) and no records in Bid Book
		 */
		
		exchangeConnection.sendMessage( gtcOrder7 );
		
		offerBook = exchange.getOrderBooks().get(marketId1).getOfferBookSet().iterator().next();
		
		//Check that in position one(first element) quantity was decreased by 100L - 20L
		assertEquals( 80L, offerBook.getQuantity(), 0.001 ); //80
		
		/*
		 * Let's now enter (Buy 40 @ $100) in Offer Book.
		 * Current: (Sell 80 @ $200, Sell 100 @ $200)
		 * Expected new values: (Sell 80 @ $200, Sell 100 @ $200) and (Buy 40 @ $100)
		 */
		
		GTCOrder gtcOrder8 = new GTCOrder(
			clientId1, 
			clientMessageId1, 
			OrderMessageType.GTC, 
			marketId1, 
			orderSide1, //Buy Order Side
			quantity3, //Quantity is now set to 40
			limitPrice1 //Price set to $100
		);
		
		exchangeConnection.sendMessage( gtcOrder8 );
		
		//Retrieve first element
		bidBook = exchange.getOrderBooks().get(marketId1).getBidBookSet().iterator().next();
		
		//Check that in position one(first element) quantity is now 40
		assertEquals( 40L, bidBook.getQuantity(), 0.001 ); //40
		assertEquals( 100D, bidBook.getLimitPrice(), 0.001 ); //100
		//Ensuring that size is now 2
		assertEquals( exchange.getOrderBooks().get(marketId1).getOfferBookSet().size(), 2, 0.001);
		//Ensuring that size is now 1
		assertEquals( exchange.getOrderBooks().get(marketId1).getBidBookSet().size(), 1, 0.001);
		
		/*
		 * Let's now enter another (Buy 40 @ $100) in Offer Book.
		 * Current: (Sell 80 @ $200, Sell 100 @ $200) and (Buy 40 @ $100)
		 * Expected new values: (Sell 80 @ $200, Sell 100 @ $200) and (Buy 40 @ $100, Buy 40 @ $100)
		 */
		exchangeConnection.sendMessage( gtcOrder8 );
		//Ensuring that size is still 2
		assertEquals( exchange.getOrderBooks().get(marketId1).getOfferBookSet().size(), 2, 0.001);
		//Ensuring that size is now 2, updated from 1
		assertEquals( exchange.getOrderBooks().get(marketId1).getBidBookSet().size(), 2, 0.001);
		
		/*
		 * Let's now enter another (Buy 100 @ $200) in Offer Book.
		 * Current: (Sell 80 @ $200, Sell 100 @ $200) and (Buy 40 @ $100, Buy 40 @ $100)
		 * Expected new values: (Sell 80 @ $200) and (Buy 40 @ $100, Buy 40 @ $100)
		 */
		
		GTCOrder gtcOrder9 = new GTCOrder(
			clientId1, 
			clientMessageId1, 
			OrderMessageType.GTC, 
			marketId1, 
			orderSide1, //Buy Order Side
			quantity2, //Quantity is now set to 100
			limitPrice2 //Price set to $200
		);
		
		exchangeConnection.sendMessage( gtcOrder9 );
		//Ensuring that size is now 1
		assertEquals( exchange.getOrderBooks().get(marketId1).getOfferBookSet().size(), 1, 0.001);
		//Ensuring that size is now 2
		assertEquals( exchange.getOrderBooks().get(marketId1).getBidBookSet().size(), 2, 0.001);
		
		//For client 1, expected last message to be Fill message type
		assertTrue( ExchangeMessageType.FILL == client1.getMessagesFromExchange().removeLast().getMessageType() );
		//For client 2, expected last message to be Fill message type
		assertTrue( ExchangeMessageType.FILL == client2.getMessagesFromExchange().removeLast().getMessageType() );
		
		//Retrieve first element
		bidBook = exchange.getOrderBooks().get(marketId1).getBidBookSet().iterator().next();
		offerBook = exchange.getOrderBooks().get(marketId1).getOfferBookSet().iterator().next();
		
		assertEquals( 40L, bidBook.getQuantity(), 0.001 ); //expected 40 
		assertEquals( 100D, bidBook.getLimitPrice(), 0.001 ); //expected 100
		
		assertEquals( 80L, offerBook.getQuantity(), 0.001 ); //expected 80
		assertEquals( 200D, offerBook.getLimitPrice(), 0.001 ); //expected 200
		
		/*
		 * Let's now enter 1000 (Buy 40 @ $100) in Offer Book. In our Test we are using the same Client Id
		 * Current: (Sell 80 @ $200) and (Buy 40 @ $100, Buy 40 @ $100)
		 * Expected new values: (Sell 80 @ $200) and (Buy 40 @ $100, Buy 40 @ $100, + 1000 Buy 40 @ $100)
		 */
		
		for(int j = 1; j<=1000;j++) {
			exchangeConnection.sendMessage( gtcOrder8 );
		}
		//Expect to have 1002 Buy orders now in the Bid Book
		assertEquals( exchange.getOrderBooks().get(marketId1).getBidBookSet().size(), 1002, 0.001);
		
		//Expect to have 1 Sell order now in the Sell Book
		assertEquals( exchange.getOrderBooks().get(marketId1).getOfferBookSet().size(), 1, 0.001);
		
		/*
		 * Lets try to match 300 of these 1002 Buy Orders
		 * We will be using the same Client Id in our Test
		 * Enter: 300 (Sell 40 @ $100) 
		 * Current: (Sell 80 @ $200) and (Buy 40 @ $100, Buy 40 @ $100, + 1000 Buy 40 @ $100)
		 * Expected: (Sell 80 @ $200) and (702 Buy 40 @ $100)
		 */
		
		GTCOrder gtcOrder10 = new GTCOrder(
			clientId2, //update Client ID 
			clientMessageId2, 
			OrderMessageType.GTC, 
			marketId1, 
			orderSide2, //Sell Order Side
			quantity3, //Quantity is now set to 40
			limitPrice1 //Price set to $100
		);
		
		for(int j = 1; j<=300;j++) {
			exchangeConnection.sendMessage( gtcOrder10 );
		}
		
		//Expect to have 702 Buy orders now in the Bid Book
		assertEquals( exchange.getOrderBooks().get(marketId1).getBidBookSet().size(), 702, 0.001);
		
		//Expect to still have 1 Sell order now in the Sell Book
		assertEquals( exchange.getOrderBooks().get(marketId1).getOfferBookSet().size(), 1, 0.001);
		
		offerBook = exchange.getOrderBooks().get(marketId1).getOfferBookSet().iterator().next();
		
		assertEquals( 80L, offerBook.getQuantity(), 0.001 ); //expected 80
		assertEquals( 200D, offerBook.getLimitPrice(), 0.001 ); //expected 200
		
		/*
		 * Let's now create a more comprehensive test
		 * Loading Buy Data first for Client 1
			(quantity,price)
			(1,$1)
			(5,$5)
			(10,$10)
			(15,$15)
			(20,$20)
			(25,$25)
			(30,$30)
			(35,$35)
			(40,$40)
			(45,$45)
			(50,$50)
			
			Loading Sell Data after Buy Data Client 2
			(quantity,price)
			(1,$1)
			(10,$10)
			(20,$20)
			(30,$30)
			(40,$40)
			(50,$50)
			(60,$60)
			(70,$70)
			(80,$80)
			(90,$90)
			(100,$100)
			
			Expected Results:
			Bid List/Buy Data - sorted now for Client 1
			(quantity,price)
			(34,$40)
			(35,$35)
			(30,$30)
			(25,$25)
			(20,$20)
			(15,$15)
			(10,$10)
			(5,$5)
			(1,$1)
			
			Offer List/Sell Data for Client 2
			(quantity,price)
			(50,$50)
			(60,$60)
			(70,$70)
			(80,$80)
			(90,$90)
			(100,$100)
		 */
		
		//Clear the lists first
		exchange.getOrderBooks().get(marketId1).getBidBookSet().clear();
		exchange.getOrderBooks().get(marketId1).getOfferBookSet().clear();
		
		//Expect to have 0 Bid orders now
		assertEquals( exchange.getOrderBooks().get(marketId1).getBidBookSet().size(), 0, 0.001);
		
		//Expect to have 0 Offer orders now
		assertEquals( exchange.getOrderBooks().get(marketId1).getOfferBookSet().size(), 0, 0.001);
		
		Long orderQuantityValue4 = 1L;
		Double limitPriceValue4 = new Double( 1D );
		
		for(int j=0;j<=50;j+=5){
			if (j > 1) {
				orderQuantityValue4 = (long) j;
				limitPriceValue4 = (double) j;
			}
			GTCOrder gtcOrder11 = new GTCOrder(
				clientId1, //update Client ID 
				clientMessageId1, 
				OrderMessageType.GTC, 
				marketId1, 
				orderSide1, //Buy Order Side
				new Quantity( orderQuantityValue4 ), //Quantity is incremented 
				new LimitPrice( limitPriceValue4 ) //Price is incremented
			);
			exchangeConnection.sendMessage( gtcOrder11 );
		}
		
		//Expect to have 11 Bid orders now
		assertEquals( exchange.getOrderBooks().get(marketId1).getBidBookSet().size(), 11, 0.001);
		
		Long orderQuantityValue5 = 1L;
		Double limitPriceValue5 = new Double( 1D );
		
		for(int j=0;j<=100;j+=10){
			if (j > 1) {
				orderQuantityValue5 = (long) j;
				limitPriceValue5 = (double) j;
			}
			GTCOrder gtcOrder12 = new GTCOrder(
				clientId2, //update Client ID 
				clientMessageId2, 
				OrderMessageType.GTC, 
				marketId1, 
				orderSide2, //Sell Order Side
				new Quantity( orderQuantityValue5 ), //Quantity is incremented 
				new LimitPrice( limitPriceValue5 ) //Price is incremented
			);
			exchangeConnection.sendMessage( gtcOrder12 );
		}
		
		//Expect to have 9 Bid orders now
		assertEquals( exchange.getOrderBooks().get(marketId1).getBidBookSet().size(), 9, 0.001);
		
		//Expect to have 6 Offer orders now
		assertEquals( exchange.getOrderBooks().get(marketId1).getOfferBookSet().size(), 6, 0.001);
		
		//Retrieve first element
		bidBook = exchange.getOrderBooks().get(marketId1).getBidBookSet().iterator().next();
		offerBook = exchange.getOrderBooks().get(marketId1).getOfferBookSet().iterator().next();
		
		assertEquals( 34L, bidBook.getQuantity(), 0.001 ); //expected 34 
		assertEquals( 40D, bidBook.getLimitPrice(), 0.001 ); //expected 40
		
		assertEquals( 50L, offerBook.getQuantity(), 0.001 ); //expected 50
		assertEquals( 50D, offerBook.getLimitPrice(), 0.001 ); //expected 50
		
		//For client 1, expected last message to be Fill message type
		assertTrue( ExchangeMessageType.FILL == client1.getMessagesFromExchange().removeLast().getMessageType() );
		//For client 2, expected last message to be Order Accepted message type
		assertTrue( ExchangeMessageType.ORDER_ACCEPTED == client2.getMessagesFromExchange().removeLast().getMessageType() );
		
		/*
		 * Let's now test Buy and Sell with different market tickers
		 * These should not match
		 * 
		 */
		//Clear the lists first
		
		exchange.getOrderBooks().get(marketId1).getBidBookSet().clear();
		exchange.getOrderBooks().get(marketId1).getOfferBookSet().clear();
		
		GTCOrder gtcOrder11 = new GTCOrder(
			clientId2, //update Client ID 
			clientMessageId2, 
			OrderMessageType.GTC, 
			marketId1, //DELL
			orderSide2, //Sell Order Side
			quantity3, //Quantity is now set to 40
			limitPrice1 //Price set to $100
		);
	
		//Quantity 40, Price $100, Ticker Dell, Order side is Sell
		exchangeConnection.sendMessage( gtcOrder11 );
		
		GTCOrder gtcOrder12 = new GTCOrder(
			clientId1, //update Client ID 
			clientMessageId1, 
			OrderMessageType.GTC, 
			marketId2, //IBM 
			orderSide1, //Buy Order Side
			quantity3, //Quantity is now set to 40
			limitPrice1 //Price set to $100
		);
		
		//Quantity 40, Price $100, Ticker IBM, Order side is BUY
		exchangeConnection.sendMessage( gtcOrder12 );
		
		//Expect to have 1 Offer order now
		assertEquals( exchange.getOrderBooks().get(marketId1).getOfferBookSet().size(), 1, 0.001);
		
		//Expect to have 1 Bid order now
		assertEquals( exchange.getOrderBooks().get(marketId2).getBidBookSet().size(), 1, 0.001);
	}
	
	/**
	 * Test two possible cancel scenarios:
	 * If order is in fact cancelled when client initiates CANCEL and Order Exchange Id is FOUND
	 * If cancel rejected is sent back to the client when client initiates CANCEL for Order Exchange Id that DOESN'T exist
	 * 
	 * @throws Exception
	 */
	public void testCancelRejected() throws Exception {
		
		//Instantiate client connection, which is used by exchange to communicate with clients
		ClientConnection clientConnection = new ClientConnection();
		
		//Instantiate exchange using client connection
		Exchange exchange = new Exchange( clientConnection );
		
		//Make sure exchange saves client connection
		assertTrue( exchange.getClientConnection() == clientConnection );
		
		//Instantiate exchange connection, which is used by clients to communicate with exchange
		ExchangeConnection exchangeConnection = new ExchangeConnection(exchange);
		
		//Instantiate one client
		ClientId clientId1 = new ClientId( 13472L );
		Client client1 = new Client( clientId1, exchangeConnection );
		
		//Make sure client saves client id
		assertTrue( client1.getClientId().equals( clientId1 ) );
		
		//Make sure client saved echange connection
		assertTrue( client1.getExchangeConnection() == exchangeConnection );
		
		//Add client to client connection
		clientConnection.addClient( client1 );
		
		//Make sure client connection saved client
		Client retrievedClient = clientConnection.getClient( clientId1 );
		assertTrue( retrievedClient == client1 );
		
		//Instantiate a good till cancelled order to send to exchange
		//Instantiate client message id - This is the id that clients will use
		//to identify their message when the exchange sends them some
		//response regarding their message
		Long clientMessageIdValue1 = new Long( 999L );
		ClientMessageId clientMessageId1 = new ClientMessageId( clientMessageIdValue1 );
		
		// Make sure client message id saved the id value
		assertTrue( clientMessageId1.getValue().equals( clientMessageIdValue1 ) );
		
		// Instantiate a market id
		String marketName1 = "DELL";
		MarketId marketId1 = new MarketId( marketName1 );
		
		// Make sure market id saved market name
		assertTrue( marketId1.getValue().equals( marketName1 ) );
		
		// Instantiate an order side
		OrderSide orderSide1 = OrderSide.BUY;
		
		// Instantiate an order quantity
		Long orderQuantityValue1 = 200L;
		Quantity quantity1 = new Quantity( orderQuantityValue1 );
		
		// Make sure order quantity save order quantity value
		assertTrue( quantity1.getValue().equals( orderQuantityValue1 ) );
		
		// Instantiate a limit price
		Double limitPriceValue1 = new Double( 100D );
		LimitPrice limitPrice1 = new LimitPrice( limitPriceValue1 );
		
		// Make sure that limit price saved the limit price value
		assertEquals( limitPrice1.getValue(), limitPriceValue1, 0.001 );
		GTCOrder gtcOrder1 = new GTCOrder(
			clientId1, 
			clientMessageId1, 
			OrderMessageType.GTC, 
			marketId1, 
			orderSide1, 
			quantity1, 
			limitPrice1
		);
		
		//Send message to exchange
		exchangeConnection.sendMessage( gtcOrder1 );
		
		//Make sure that data is stored in the appropriate place in the book 
		assertTrue( exchange.getOrderBooks().get(marketId1).getBidBookSet().size() > 0 );
		
		//Make sure that exchange received the message we sent to it
		GTCOrder receivedOrder = (GTCOrder) exchange.getMessagesReceived().get(0);
		assertTrue( receivedOrder == gtcOrder1 );
		assertTrue( ExchangeMessageType.ORDER_ACCEPTED == client1.getMessagesFromExchange().get(0).getMessageType() );
		assertTrue( client1.getMessagesFromExchange().get(0).getExchangeOrderId() != null );
				
		OrderMessageType messageType2 = OrderMessageType.CANCEL;
		//Assign an exchange order id
		Long exchangeOrderIdValue1 = client1.getMessagesFromExchange().get(0).getExchangeOrderId().getValue(); 
		ExchangeOrderId exchangeOrderId1 = new ExchangeOrderId( exchangeOrderIdValue1 );
		// Make sure the exchange order id saved the exchange order id value
		assertTrue( exchangeOrderId1.getValue().equals( exchangeOrderIdValue1 ) );
		CancelOrder cancelOrder1 = new CancelOrder( clientId1, clientMessageId1, messageType2, exchangeOrderId1 );
		
	    // We send the cancel order to the exchange
		exchangeConnection.sendMessage( cancelOrder1 );
	    // Did the exchange receive and save this cancel order?
		assertTrue( exchange.getMessagesReceived().get(1) == cancelOrder1 );
		
		//Make sure the Order was removed for specific Exchange Order ID that was returned back to the Client after Order Side Buy
		assertTrue( exchange.getOrderBooks().get(marketId1).getBidBookSet().size() == 0 );
		
		//Make sure that the order was canceled by retrieving last message sent back to the Client
		assertTrue( ExchangeMessageType.CANCELLED == client1.getMessagesFromExchange().get(1).getMessageType() );
		
		//We now test "Cancel Rejected" by trying to cancel order with Order Market ID that doesn't exist
		
		//Send message to exchange
		exchangeConnection.sendMessage( gtcOrder1 );
		
		OrderMessageType messageType3 = OrderMessageType.CANCEL;
		//Assign some arbitrary exchange order id
		Long exchangeOrderIdValue2 = 334255L;
		ExchangeOrderId exchangeOrderId2 = new ExchangeOrderId( exchangeOrderIdValue2 );
		// Make sure the exchange order id saved the exchange order id value
		assertTrue( exchangeOrderId2.getValue().equals( exchangeOrderIdValue2 ) );
		CancelOrder cancelOrder2 = new CancelOrder( clientId1, clientMessageId1, messageType3, exchangeOrderId2 );
		
	    // We send the cancel order to the exchange
		exchangeConnection.sendMessage( cancelOrder2 );
	    // Did the exchange receive and save this cancel order?
		assertTrue( exchange.getMessagesReceived().get(3) == cancelOrder2 );
		
		//Make sure the Order was NOT removed as it wasn't found
		assertTrue( exchange.getOrderBooks().get(marketId1).getBidBookSet().size() > 0 );
		
		//Make sure that the order was Cancel Rejected by retrieving last message sent back to the Client
		assertTrue( ExchangeMessageType.CANCEL_REJECTED == client1.getMessagesFromExchange().get(3).getMessageType() );
	}
	
	
	/**
	 * Test if orders are in fact accepted and are stored in the appropriate place in the book (i.e., Bid Book or Offer Book)
	 * Example: If order side is Buy, the order will be stored in the Bid Book assigned to Order Book and Offer Book will be of size 0
	 * 
	 * @throws Exception
	 */
	public void testOrderAccepted() throws Exception {
		//Instantiate client connection, which is used by exchange to communicate with clients
		ClientConnection clientConnection = new ClientConnection();
		
		//Instantiate exchange using client connection
		Exchange exchange = new Exchange( clientConnection );
		
		//Make sure exchange saves client connection
		assertTrue( exchange.getClientConnection() == clientConnection );
		
		//Instantiate exchange connection, which is used by clients to communicate with exchange
		ExchangeConnection exchangeConnection = new ExchangeConnection(exchange);
		
		//Instantiate one client
		ClientId clientId1 = new ClientId( 13472L );
		Client client1 = new Client( clientId1, exchangeConnection );
		
		//Make sure client saves client id
		assertTrue( client1.getClientId().equals( clientId1 ) );
		
		//Make sure client saved echange connection
		assertTrue( client1.getExchangeConnection() == exchangeConnection );
		
		//Add client to client connection
		clientConnection.addClient( client1 );
		
		//Make sure client connection saved client
		Client retrievedClient = clientConnection.getClient( clientId1 );
		assertTrue( retrievedClient == client1 );
		
		//Instantiate a good till cancelled order to send to exchange
		//Instantiate client message id - This is the id that clients will use
		//to identify their message when the exchange sends them some
		//response regarding their message
		Long clientMessageIdValue1 = new Long( 999L );
		ClientMessageId clientMessageId1 = new ClientMessageId( clientMessageIdValue1 );
		
		// Make sure client message id saved the id value
		assertTrue( clientMessageId1.getValue().equals( clientMessageIdValue1 ) );
		
		// Instantiate a market id
		String marketName1 = "DELL";
		MarketId marketId1 = new MarketId( marketName1 );
		
		// Make sure market id saved market name
		assertTrue( marketId1.getValue().equals( marketName1 ) );
		
		// Instantiate an order side
		OrderSide orderSide1 = OrderSide.BUY;
		
		// Instantiate an order quantity
		Long orderQuantityValue1 = 200L;
		Quantity quantity1 = new Quantity( orderQuantityValue1 );
		
		// Make sure order quantity save order quantity value
		assertTrue( quantity1.getValue().equals( orderQuantityValue1 ) );
		
		// Instantiate a limit price
		Double limitPriceValue1 = new Double( 100D );
		LimitPrice limitPrice1 = new LimitPrice( limitPriceValue1 );
		
		// Make sure that limit price saved the limit price value
		assertEquals( limitPrice1.getValue(), limitPriceValue1, 0.001 );
		GTCOrder gtcOrder1 = new GTCOrder(
			clientId1, 
			clientMessageId1, 
			OrderMessageType.GTC, 
			marketId1, 
			orderSide1, 
			quantity1, 
			limitPrice1
		);
		
		//Send message to exchange
		exchangeConnection.sendMessage( gtcOrder1 );
		
		//Make sure that data is stored in the appropriate place in the book 
		assertTrue( exchange.getOrderBooks().get(marketId1).getBidBookSet().size() > 0 );
		assertTrue( exchange.getOrderBooks().get(marketId1).getOfferBookSet().size() == 0 );
		
		//Make sure that exchange received the message we sent to it
		GTCOrder receivedOrder = (GTCOrder) exchange.getMessagesReceived().removeFirst();
		assertTrue( receivedOrder == gtcOrder1 );
		assertTrue( ExchangeMessageType.ORDER_ACCEPTED == client1.getMessagesFromExchange().removeFirst().getMessageType() );
			
	}
	
	/**
	 * Test accurate implementation of the sort functionality
	 * Bid Book Orders are sorted first by price (highest to lowest) and then by date (earliest to latest)
	 * Offer Book Orders are sorted first by price (highest to lowest) and then by date (earliest to latest)
	 * 
	 * @throws Exception
	 */
	public void testOrderBookSort() throws Exception {
		String marketName1 = "DELL";
		MarketId marketId1 = new MarketId( marketName1 );
		
		OrderBook orderBook = new OrderBook();
		orderBook.buyOrderSide(new BidBook(10L,10.0,10L,11L,1L), marketId1);
		orderBook.buyOrderSide(new BidBook(100L,20.0,10L,11L,2L), marketId1); 
		orderBook.buyOrderSide(new BidBook(11L,9.0,10L,11L,3L), marketId1); 
		orderBook.buyOrderSide(new BidBook(20L,2.0,10L,11L,4L), marketId1); 
		orderBook.buyOrderSide(new BidBook(130L,9.0,10L,11L,5L), marketId1); 
		orderBook.buyOrderSide(new BidBook(20L,9.0,10L,11L,6L), marketId1); 
		orderBook.buyOrderSide(new BidBook(50L,89.0,10L,11L,7L), marketId1); 
		orderBook.buyOrderSide(new BidBook(30L,90.0,10L,11L,8L), marketId1); 
		orderBook.buyOrderSide(new BidBook(12L,494.0,10L,11L,9L), marketId1); 
		orderBook.buyOrderSide(new BidBook(15L,91.0,10L,11L,10L), marketId1); 
		orderBook.buyOrderSide(new BidBook(10L,97.0,10L,11L,11L), marketId1); 
		
		for(BidBook bid : orderBook.getBidBookSet()) {
			assertTrue(bid.getLimitPrice() == 494.0);
			assertTrue(bid.getQuantity() == 12L);
			assertTrue(bid.getOrderDate() != null);
			assertTrue(bid.getExchangeOrderId() != null);
			break;
		}
		
		orderBook.sellOrderSide(new OfferBook(100L,10.0,10L,11L,1L), marketId1); 
		orderBook.sellOrderSide(new OfferBook(20L,30.0,10L,11L,2L), marketId1); 
		orderBook.sellOrderSide(new OfferBook(11L,9.0,10L,11L,3L), marketId1);
		orderBook.sellOrderSide(new OfferBook(2310L,10.0,10L,11L,4L), marketId1); 
		orderBook.sellOrderSide(new OfferBook(12L,20.0,10L,11L,5L), marketId1); 
		orderBook.sellOrderSide(new OfferBook(123L,9.0,10L,11L,6L), marketId1);
		orderBook.sellOrderSide(new OfferBook(110L,12.0,10L,11L,7L), marketId1); 
		orderBook.sellOrderSide(new OfferBook(120L,20.0,10L,11L,8L), marketId1); 
		orderBook.sellOrderSide(new OfferBook(14L,4.0,10L,11L,9L), marketId1);
		orderBook.sellOrderSide(new OfferBook(194L,10.0,10L,11L,10L), marketId1); 
		orderBook.sellOrderSide(new OfferBook(210L,20.0,10L,11L,11L), marketId1); 
		orderBook.sellOrderSide(new OfferBook(410L,9.0,10L,11L,12L), marketId1);
		
		for(OfferBook bid : orderBook.getOfferBookSet()) {
			assertTrue(bid.getLimitPrice() == 4.0);
			assertTrue(bid.getQuantity() == 14L);
			assertTrue(bid.getOrderDate() != null);
			assertTrue(bid.getExchangeOrderId() != null);
			break;
		}
	}
	
	/**
	 * Test if the exchange rejects the order outright when the order has a market ID that CAN'T be found 
	 * Example: Ticker is created for Microsoft which is NOT an available ticker
	 * 
	 * @throws Exception
	 */
	public void testMarketID() throws Exception {
		//Instantiate client connection, which is used by exchange to communicate with clients
		ClientConnection clientConnection = new ClientConnection();
		
		//Instantiate exchange using client connection
		Exchange exchange = new Exchange( clientConnection );
		
		//Make sure exchange saves client connection
		assertTrue( exchange.getClientConnection() == clientConnection );
		
		//Instantiate exchange connection, which is used by clients to communicate with exchange
		ExchangeConnection exchangeConnection = new ExchangeConnection(exchange);
		
		//Instantiate one client
		ClientId clientId1 = new ClientId( 13472L );
		Client client1 = new Client( clientId1, exchangeConnection );
		
		//Make sure client saves client id
		assertTrue( client1.getClientId().equals( clientId1 ) );
		
		//Make sure client saved echange connection
		assertTrue( client1.getExchangeConnection() == exchangeConnection );
		
		//Add client to client connection
		clientConnection.addClient( client1 );
		
		//Make sure client connection saved client
		Client retrievedClient = clientConnection.getClient( clientId1 );
		assertTrue( retrievedClient == client1 );
		
		//Instantiate a good till cancelled order to send to exchange
		//Instantiate client message id - This is the id that clients will use
		//to identify their message when the exchange sends them some
		//response regarding their message
		Long clientMessageIdValue1 = new Long( 999L );
		ClientMessageId clientMessageId1 = new ClientMessageId( clientMessageIdValue1 );
		
		// Make sure client message id saved the id value
		assertTrue( clientMessageId1.getValue().equals( clientMessageIdValue1 ) );
		
		// Instantiate a market id
		String marketName1 = "MSFT";
		MarketId marketId1 = new MarketId( marketName1 );
		
		// Make sure market id saved market name
		assertTrue( marketId1.getValue().equals( marketName1 ) );
		
		// Instantiate an order side
		OrderSide orderSide1 = OrderSide.BUY;
		
		// Instantiate an order quantity
		Long orderQuantityValue1 = 200L;
		Quantity quantity1 = new Quantity( orderQuantityValue1 );
		
		// Make sure order quantity save order quantity value
		assertTrue( quantity1.getValue().equals( orderQuantityValue1 ) );
		
		// Instantiate a limit price
		Double limitPriceValue1 = new Double( 100D );
		LimitPrice limitPrice1 = new LimitPrice( limitPriceValue1 );
		
		// Make sure that limit price saved the limit price value
		assertEquals( limitPrice1.getValue(), limitPriceValue1, 0.001 );
		GTCOrder gtcOrder1 = new GTCOrder(
			clientId1, 
			clientMessageId1, 
			OrderMessageType.GTC, 
			marketId1, 
			orderSide1, 
			quantity1, 
			limitPrice1
		);
	
		//Send message to exchange
		exchangeConnection.sendMessage( gtcOrder1 );
		
		//Make sure that exchange received the message we sent to it
		GTCOrder receivedOrder = (GTCOrder) exchange.getMessagesReceived().removeFirst();
		assertTrue( receivedOrder == gtcOrder1 );
		assertTrue( ExchangeMessageType.ORDER_REJECTED == client1.getMessagesFromExchange().removeFirst().getMessageType() );
	}
	
	/**
	 * Received tests as part of the assignment
	 * 
	 * @throws Exception
	 */
	public void test1() throws Exception {
		// Instantiate client connection, which is used by exchange to communicate
		// with clients
			ClientConnection clientConnection = new ClientConnection();
		// Instantiate exchange using client connection
			// For debugging purposes, we want an exchange that
			// saves all all of the messages it receives so in our
			// unit test, we can check what it received
			Exchange exchange = new Exchange( clientConnection );
		// Make sure exchange saves client connection
			assertTrue( exchange.getClientConnection() == clientConnection );
		// Instantiate exchange connection, which is used by clients to communicate
		// with exchange
			ExchangeConnection exchangeConnection = new ExchangeConnection(exchange);
		// Instantiate one client
			ClientId clientId1 = new ClientId( 13472L );
			Client client1 = new Client( clientId1, exchangeConnection );
		// Make sure client saves client id
			assertTrue( client1.getClientId().equals( clientId1 ) );
		// Make sure client saved echange connection
			assertTrue( client1.getExchangeConnection() == exchangeConnection );
		// Add client to client connection
			clientConnection.addClient( client1 );
		// Make sure client connection saved client
			Client retrievedClient = clientConnection.getClient( clientId1 );
			assertTrue( retrievedClient == client1 );
		// Instantiate a good till cancelled order to send to exchange
			// Instantiate client message id - This is the id that clients will use
			// to identify their message when the exchange sends them some
			// response regarding their message
				Long clientMessageIdValue1 = new Long( 999L );
				ClientMessageId clientMessageId1 = new ClientMessageId( clientMessageIdValue1 );
			// Make sure client message id saved the id value
				assertTrue( clientMessageId1.getValue().equals( clientMessageIdValue1 ) );
			// Instantiate a market id
				String marketName1 = "IBM";
				MarketId marketId1 = new MarketId( marketName1 );
			// Make sure market id saved market name
				assertTrue( marketId1.getValue().equals( marketName1 ) );
			// Instantiate an order side
				OrderSide orderSide1 = OrderSide.BUY;
			// Instantiate an order quantity
				Long orderQuantityValue1 = 200L;
				Quantity quantity1 = new Quantity( orderQuantityValue1 );
			// Make sure order quantity save order quantity value
				assertTrue( quantity1.getValue().equals( orderQuantityValue1 ) );
			// Instantiate a limit price
				Double limitPriceValue1 = new Double( 100D );
				LimitPrice limitPrice1 = new LimitPrice( limitPriceValue1 );
			// Make sure that limit price saved the limit price value
				assertEquals( limitPrice1.getValue(), limitPriceValue1, 0.001 );
			GTCOrder gtcOrder1 = new GTCOrder(
				clientId1, 
				clientMessageId1, 
				OrderMessageType.GTC, 
				marketId1, 
				orderSide1, 
				quantity1, 
				limitPrice1
			);
		// Send message to exchange
			exchangeConnection.sendMessage( gtcOrder1 );
		// Make sure that exchange received the message we sent to it
			GTCOrder receivedOrder = (GTCOrder) exchange.getMessagesReceived().removeFirst();
			assertTrue( receivedOrder == gtcOrder1 );

		// We now test sending a cancel order to the exchange.
		// First we instantiate a cancel order.
			// We can re-use the clientId from the previous example
			// We can re-use the clientMessageId from the previous example
			// Make a message type
				OrderMessageType messageType2 = OrderMessageType.CANCEL;
			// Make an exchange order id
				Long exchangeOrderIdValue1 = 334455L;
				ExchangeOrderId exchangeOrderId1 = new ExchangeOrderId( exchangeOrderIdValue1 );
			// Make sure the exchange order id saved the exchange order id value
				assertTrue( exchangeOrderId1.getValue().equals( exchangeOrderIdValue1 ) );
			CancelOrder cancelOrder1 = new CancelOrder( clientId1, clientMessageId1, messageType2, exchangeOrderId1 );
		// We send the cancel order to the exchange
			exchangeConnection.sendMessage( cancelOrder1 );
		// Did the exchange receive and save this cancel order?
			assertTrue( exchange.getMessagesReceived().removeFirst() == cancelOrder1 );
		// We will now try sending messages to the client just
		// as the exchange would. The client is already in the
		// client connection's map of clients so
		// First, we instantiate a CancelRejected message
			// We can re-use the clientId
			// We can re-use the clientMessageId
			// We instantiate a new message type
				ExchangeMessageType exchangeMessageType1 = ExchangeMessageType.CANCEL_REJECTED;
			// We instantiate a new explanation for why the error happened
				String explanationText1 = "No such exchange order id";
				Explanation explanation1 = new Explanation( explanationText1 );
			// We make sure that the explanation text was saved
				assertTrue( explanation1.getValue().equals( explanationText1 ) );
			CancelRejected cancelRejected1 = new CancelRejected( clientId1, clientMessageId1, exchangeMessageType1, explanation1 );
		// We try sending this message as if it were being sent by the exchange
			clientConnection.sendMessage( cancelRejected1 );
		// We confirm that the client received the message we sent
			assertTrue( client1.getMessagesFromExchange().removeLast() == cancelRejected1 );
		// Now, we instantiate a Cancelled message
			// We can re-use the client id
			// We can re-use the client message id
			// We instantiate an exchange message type for this order
				ExchangeMessageType exchangeMessageType2 = ExchangeMessageType.CANCELLED;
			// We can re-use the exchange order id
			Cancelled cancelled1 = new Cancelled( clientId1, clientMessageId1, exchangeMessageType2, exchangeOrderId1 );
		// We try sending this message to the client
			clientConnection.sendMessage( cancelled1 );
		// We confirm that the client received the message we sent
			assertTrue( client1.getMessagesFromExchange().removeLast() == cancelled1 );
		// Now, we instantiate a Fill message
			// We can re-use client id
			// We can re-use client message id
			// We instantiate a new message type
				ExchangeMessageType exchangeMessageType3 = ExchangeMessageType.FILL;
			// We instantiate a quantity field
				Quantity quantity2 = new Quantity( 50L );
			// We instantiate a fill price field
				Double fillPriceValue = 89.95D;
				FillPrice fillPrice1 = new FillPrice( fillPriceValue );
			// We make sure fill price saved the fill price value
				assertEquals( fillPrice1.getValue(), fillPriceValue, 0.0001 );
			Fill fill1 = new Fill( clientId1, clientMessageId1, exchangeMessageType3, exchangeOrderId1, quantity2, fillPrice1 );
		// We try sending this message to the client
			clientConnection.sendMessage( fill1 );
		// We confirm that the client received the message
			assertTrue( client1.getMessagesFromExchange().removeLast() == fill1 );
		// Now, we instantiate an order rejected message
			// We can re-use the client id
			// We can re-use the client message id
			// We instantiate a new message type
				ExchangeMessageType exchangeMessageType4 = ExchangeMessageType.ORDER_REJECTED;
			// We instantiate a new explanation
				String explanationText2 = "No such market";
				Explanation explanation2 = new Explanation( explanationText2 );
			OrderRejected orderRejected1 = new OrderRejected( clientId1, clientMessageId1, exchangeMessageType4, explanation2 );
		// We try sending this message to the client
			clientConnection.sendMessage( orderRejected1 );
		// We confirm that the client received the message
			assertTrue( client1.getMessagesFromExchange().removeLast() == orderRejected1 );
		// Now, we instantiate an order accepted message
			// We can reuse client id
			// We can reuse client message id
			// We need a new exchange message type
				ExchangeMessageType exchangeMessageType5 = ExchangeMessageType.ORDER_ACCEPTED;
			// We need a new quantity
				Quantity quantity3 = new Quantity( 100L );
			// We need a new exchange order id
				ExchangeOrderId exchangeOrderId2 = new ExchangeOrderId( 9987L );
			OrderAccepted orderAccepted = new OrderAccepted(
					clientId1, 
					clientMessageId1,
					exchangeMessageType5,
					quantity3, // The quantity that was actually made into a resting order
					exchangeOrderId2
			);
		// We try sending the order to the client
			clientConnection.sendMessage( orderAccepted );
		// We confirm that the client received the order
			assertTrue( client1.getMessagesFromExchange().removeLast() == orderAccepted );
	} // test1
	
}
