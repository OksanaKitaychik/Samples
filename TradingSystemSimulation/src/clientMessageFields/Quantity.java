package clientMessageFields;

import abstractMessageFields.AbstractLongValueRangeField;

public class Quantity extends AbstractLongValueRangeField {

	public Quantity( String fieldDescriptionToParse ) throws Exception {
		super( fieldDescriptionToParse );
	}
	
	public Quantity( Long orderQuantity ) throws Exception {
		super( orderQuantity.toString() );
	}

	@Override
	protected Long getLowerBound() { return new Long( 1 ); }

	@Override
	protected Long getUpperBound() { return new Long( 10000000 ); }

	@Override
	protected String getFieldName() { return "orderQuantity"; }

}
