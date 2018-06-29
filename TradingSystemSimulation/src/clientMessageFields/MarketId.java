package clientMessageFields;

import abstractMessageFields.AbstractNonBlankStringField;

public class MarketId extends AbstractNonBlankStringField {

	public MarketId( String fieldToParse ) throws Exception {
		super(fieldToParse);
	}

	@Override
	public String getFieldName() { return "marketId"; }
	
	@Override
	public String toString() {
		return this.getClass().getName() + 
		       "{" + super.getValue().toString() + "}";
	}
	
	@Override
	public boolean equals( Object object ) {
		if( ! ( object instanceof MarketId ) )
			return false;
		if( object == this )
			return true;
		MarketId temp = (MarketId) object;
		return this.getValue().equals( temp.getValue() );
	}
	
	@Override
	public int hashCode() { return this.getValue().hashCode(); }

}
