package utilsMachineLearning;

/**
 * Defines specific dimensions for X,Y coordinates
 * @author Oksana Kitaychik
 *
 */
public class Dimensions {
	public static final Dimensions MINIMUM = new Dimensions( "Minimum", 0 );
	public static final Dimensions MAXIMUM = new Dimensions( "Maximum", 99 );
	
	private String _coordinates;
	private int    _coordinateDimension;
	
	private Dimensions (String coordinates, int coordinateValue) { 
		_coordinates = coordinates; 
		_coordinateDimension = coordinateValue;
	}
	
	public int getValue() { return _coordinateDimension; }
	
	public String toString(){ return _coordinates; };
}


