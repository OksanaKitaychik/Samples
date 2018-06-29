package clientMessageFields;

import abstractMessageFields.AbstractDoubleValueRangeField;

public class LimitPrice extends AbstractDoubleValueRangeField {
	
	public LimitPrice( String fieldToParse ) throws Exception {
		super( fieldToParse );
	}

	public LimitPrice( Double value ) throws Exception {
		super( value );
	}

	@Override
	protected Double getLowerBound() { return new Double( 0 ); }

	@Override
	protected Double getUpperBound() { return new Double( 200000 ); }

	@Override
	protected String getFieldName() { return "limitPrice"; }
	
}
