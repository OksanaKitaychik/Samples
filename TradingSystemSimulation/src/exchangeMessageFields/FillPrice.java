package exchangeMessageFields;

import abstractMessageFields.AbstractDoubleValueRangeField;

public class FillPrice extends AbstractDoubleValueRangeField {
	
	public FillPrice( String fieldToParse ) throws Exception {
		super( fieldToParse );
	}

	public FillPrice( Double value ) throws Exception {
		super( value );
	}

	@Override
	protected Double getLowerBound() { return new Double( 0 ); }

	@Override
	protected Double getUpperBound() { return new Double( 200000 ); }

	@Override
	protected String getFieldName() { return "fillPrice"; }
	
}
