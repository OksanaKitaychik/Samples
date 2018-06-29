package exchangeMessageFields;

import abstractMessageFields.AbstractLongValueRangeField;

public class ExchangeOrderId extends AbstractLongValueRangeField {

	public ExchangeOrderId( String fieldDescriptionToParse ) throws Exception {
		super( fieldDescriptionToParse );
	}
	
	public ExchangeOrderId( Long value ) throws Exception {
		super( value );
	}

	@Override
	protected Long getLowerBound() { return new Long( 1 ); }

	@Override
	protected Long getUpperBound() {return Long.MAX_VALUE; }

	@Override
	protected String getFieldName() { return "exchangeOrderId"; }
	
	@Override
	public String toString() {
		return this.getClass().getName() + 
		       "{" + super.getValue().toString() + "}";
	}
	
	@Override
	public boolean equals( Object object ) {
		if( ! ( object instanceof ExchangeOrderId ) )
			return false;
		if( object == this )
			return true;
		ExchangeOrderId temp = (ExchangeOrderId) object;
		return this.getValue().equals( temp.getValue() );
	}
	
	@Override
	public int hashCode() { return this.getValue().hashCode(); }

}
