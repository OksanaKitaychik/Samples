package exchange;

import java.util.HashMap;
import java.util.Map;

import clientMessageFields.MarketId;

/**
 * This class defines a list of available markets - stocks that can be traded
 * 
 * @author Oksana Kitaychik
 *
 */
public class AvailableMarkets {
	
	protected Map<String, String> _availableMarkets;
	
	public AvailableMarkets() throws Exception {
		_availableMarkets = new HashMap<String, String>();
		setAvailableMarkets();
	}
	
	private void setAvailableMarkets() throws Exception {
		_availableMarkets.put("IBM", "IBM Ticker");
		_availableMarkets.put("DELL", "DELL Ticker");
	}
	
	public Map<String, String> getAvailableMarkets() {
		return _availableMarkets;
	}
	
	public boolean isAvailableMarket(MarketId id) {
		return _availableMarkets.containsKey(id.getValue());
	}

}
