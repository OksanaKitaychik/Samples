package abstractMessageFields;

public abstract class AbstractLongValueRangeField {

	public static String ERROR_MSG = "%s [%s] should be a long value between %d and %d inclusive";

	protected Long _fieldValue; 
	
	public Long getValue() { return _fieldValue; }
	
	public AbstractLongValueRangeField( Long value ) throws Exception {
		if( ( value < getLowerBound() ) || ( value > getUpperBound() ) )
			throwException( value.toString()  );
		_fieldValue = value;
	}
	
	public AbstractLongValueRangeField( String fieldDescriptionToParse ) throws Exception {
		try {
			_fieldValue = Long.parseLong( fieldDescriptionToParse  );
		} catch( Exception e ) {
			throwException( fieldDescriptionToParse  );
		}
		if( ( _fieldValue < getLowerBound() ) || ( _fieldValue > getUpperBound() ) )
			throwException( fieldDescriptionToParse );
	}
	
	public void throwException( String fieldDescriptionToParse ) throws Exception {
		throw new Exception( 
			String.format( 
					ERROR_MSG, 
					getFieldName(),
					fieldDescriptionToParse,
					getLowerBound(),
					getUpperBound()
			)
		);
	}
	
	protected abstract Long   getLowerBound();
	protected abstract Long   getUpperBound();
	protected abstract String getFieldName();
	
}
