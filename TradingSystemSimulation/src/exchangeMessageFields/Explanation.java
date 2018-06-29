package exchangeMessageFields;

import abstractMessageFields.AbstractNonBlankStringField;

public class Explanation extends AbstractNonBlankStringField {

	public Explanation( String fieldToParse ) throws Exception {
		super( fieldToParse );
	}

	@Override
	public String getFieldName() { return "explanation"; }

}
