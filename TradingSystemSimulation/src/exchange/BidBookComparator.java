package exchange;

import java.util.Comparator;

/**
 * Sorted by price-time priority. Orders are sorted first by price (highest to lowest) and then by date (earliest to latest)
 * Exchange Order ID is a proxy of time in my program. The sorting is done by price and then date (time) proxied by Exchange Order ID
 * As Exchange Order ID is generated to be unique in my program (no duplicates by the sorting field combination, TreeSet will NOT delete any records and will sort correctly
 * @author Oksana Kitaychik
 *
 * @param <T>
 */
public class BidBookComparator<T> implements Comparator<BidBook> {
	@Override
	public int compare(BidBook o1, BidBook o2) {
		int limitPrice = o2.getLimitPrice().compareTo(o1.getLimitPrice()); 
		
		if (limitPrice != 0)
			return limitPrice;
		
		return o1.getExchangeOrderId().compareTo(o2.getExchangeOrderId());
	}
}
