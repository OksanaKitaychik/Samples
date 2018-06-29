package clientMessageFields;

import abstractMessageFields.AbstractLongValueRangeField;

public class ClientMessageId extends AbstractLongValueRangeField {

	public ClientMessageId(String fieldDescriptionToParse) throws Exception {
		super(fieldDescriptionToParse);
	}
	
	public ClientMessageId( Long value ) throws Exception {
		super( value );
	}

	@Override
	protected Long getLowerBound() { return new Long( 1 ); }

	@Override
	protected Long getUpperBound() {return Long.MAX_VALUE; }

	@Override
	protected String getFieldName() { return "clientMessageId"; }

}
