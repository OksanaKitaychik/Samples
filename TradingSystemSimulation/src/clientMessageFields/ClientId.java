package clientMessageFields;

import abstractMessageFields.AbstractLongValueRangeField;


public class ClientId extends AbstractLongValueRangeField implements Comparable<ClientId> {

	public ClientId(String fieldDescriptionToParse) throws Exception {
		super(fieldDescriptionToParse);
	}
	
	public ClientId( Long value ) throws Exception {
		super( value );
	}

	@Override
	protected Long getLowerBound() { return new Long( 1 ); }

	@Override
	protected Long getUpperBound() {return Long.MAX_VALUE; }

	@Override
	protected String getFieldName() { return "clientId"; }
	
	@Override
	public String toString() {
		return this.getClass().getName() + 
		       "{" + _fieldValue.toString() + "}";
	}
	
	@Override
	public boolean equals( Object object ) {
		if( ! ( object instanceof ClientId ) )
			return false;
		if( object == this )
			return true;
		ClientId temp = (ClientId) object;
		return this.getValue().equals( temp.getValue() );
	}
	
	@Override
	public int hashCode() { return this.getValue().hashCode(); }

	@Override
	public int compareTo( ClientId otherClientId ) {
		return this.getValue().compareTo( otherClientId.getValue() );
	}

}
