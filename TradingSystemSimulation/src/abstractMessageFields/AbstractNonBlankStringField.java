package abstractMessageFields;

public abstract class AbstractNonBlankStringField {
	
	protected static String ERROR_MSG = "Field %s should be a non blank String";
	
	protected String _value;

	public String getValue() { return _value; }
	
	public AbstractNonBlankStringField( String fieldToParse ) throws Exception {
		if( fieldToParse == null )
			throw new Exception( String.format( ERROR_MSG, getFieldName() ) );
		if( fieldToParse.equals( "" ) )
			throw new Exception( String.format( ERROR_MSG, getFieldName() ) );
		_value = fieldToParse;
	}
	
	public abstract String getFieldName();
	
}