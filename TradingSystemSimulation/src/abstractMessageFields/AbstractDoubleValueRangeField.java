package abstractMessageFields;

public abstract class AbstractDoubleValueRangeField {

	protected static String ERROR_MSG = "%s [%s] should be a double value between %d and %d inclusive";

	protected Double _fieldValue; 

	public Double getValue() { return _fieldValue; }
	
	public AbstractDoubleValueRangeField( Double value ) throws Exception {
		if( ( value < getLowerBound() ) || ( value > getUpperBound() ) )
			throwException( value.toString() );
		_fieldValue = value;
	}
	
	public AbstractDoubleValueRangeField( String fieldDescriptionToParse ) throws Exception {
		try {
			_fieldValue = Double.parseDouble( fieldDescriptionToParse  );
		} catch( Exception e ) {
			throwException( fieldDescriptionToParse  );
		}
		if( ( _fieldValue < getLowerBound() ) || ( _fieldValue > getUpperBound() ) )
			throwException( fieldDescriptionToParse  );
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
	
	protected abstract Double getLowerBound();
	protected abstract Double getUpperBound();
	protected abstract String getFieldName();
	
}
