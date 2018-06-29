package exchange;

import clientMessageFields.MarketId;
import java.util.Set;
import java.util.TreeSet;

public class OrderBook {
	
	protected BidBook _bidBook;
	protected OfferBook _offerBook;
	//Do we need other data structures here? Up to you
	//Oksana: adding TreeSet data structure to keep the elements sorted all the time
	private Set<BidBook> _bidBookSet;
	private Set<OfferBook> _offerBookSet;
	
	protected MarketId _marketId;

	public MarketId getMarketId() {
		return _marketId;
	}
	
	public Set<BidBook> getBidBookSet() {
		return _bidBookSet;
	}
	
	public Set<OfferBook> getOfferBookSet() {
		return _offerBookSet;
	}
	
	public void setBidBookSet(Set<BidBook> bidBookSet) {
		_bidBookSet = bidBookSet;
	}
	
	public void setOfferBookSet(Set<OfferBook> offerBookSet) {
		_offerBookSet = offerBookSet;
	}
	
	public OrderBook(MarketId marketId) throws Exception {
		if( marketId == null )
			throw new Exception( "Argument is null" );
		_marketId = marketId;
	}
	
	public OrderBook() {
		_bidBookSet = new TreeSet<BidBook>(new BidBookComparator<BidBook>());
		_offerBookSet = new TreeSet<OfferBook>(new OfferBookComparator<OfferBook>());
	}
	
	public OrderBook buyOrderSide(BidBook bidBook, MarketId marketId) throws Exception {
		_bidBookSet.add(bidBook);
		return this;
	}
	
	public OrderBook sellOrderSide(OfferBook offerBook, MarketId marketId) throws Exception {
		_offerBookSet.add(offerBook);
		return this;
	}
	
	/**
	 * If OrderBook is NOT null, there is a record for the current MarketID and we add new entry
	 * New instance of OrderBook will be created when MarketID in the GTC Order doesn't exist in our records
	 * @param bidBook Order information
	 * @param book OrderBook reference
	 * @return
	 * @throws Exception
	 */
	public OrderBook buyOrderSide(BidBook bidBook, OrderBook book) throws Exception {
		if (book == null) {
			book = new OrderBook();
			_bidBookSet.add(bidBook);
			book.setBidBookSet(_bidBookSet);
		}
		else {
			Set<BidBook> bidBookSet = book.getBidBookSet();
			bidBookSet.add(bidBook);
			book.setBidBookSet(bidBookSet);
		}
		return book;
	}
	
	/**
	 * If OrderBook is NOT null, there is a record for the current MarketID and we add new entry
	 * New instance of OrderBook will be created when MarketID in the GTC Order doesn't exist in our records
	 * @param offerBook Order information
	 * @param book OrderBook reference
	 * @return
	 * @throws Exception
	 */
	public OrderBook sellOrderSide(OfferBook offerBook, OrderBook book) throws Exception {
		if (book == null) {
			book = new OrderBook();
			_offerBookSet.add(offerBook);
			book.setOfferBookSet(_offerBookSet);
		}
		else {
			Set<OfferBook> offerBookSet = book.getOfferBookSet();
			offerBookSet.add(offerBook);
			book.setOfferBookSet(offerBookSet);
		}
		return book;
	}
}
