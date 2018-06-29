package utilsMachineLearning;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Point implements I_Metric<Point> {
	
	private double  _x = 0;
    private double  _y = 0;
    private int     _clusterNumber = 0;
    private boolean _isAssigned;
    
    public double  getX()          { return _x;             }
    public double  getY()          { return _y;             }
    public int     getCluster()    { return _clusterNumber; }
    public boolean getIsAssigned() { return _isAssigned;    }
    
    /**
     * Constructor with an additional parameter to determine if point has been assigned
     * 
     * @param x Coordinate X for a point 
     * @param y Coordinate Y for a point
     * @param isAssigned Flag to determine if point has been assigned
     * @throws Exception 
     */
	public Point(double x, double y, boolean isAssigned) throws Exception {
		setX(x);
        setY(y);
        setIsAssigned(isAssigned);
    }
	
	/**
	 * Constructor to create a point object with X,Y coordinate
	 * 
	 * @param x Coordinate X for a point 
     * @param y Coordinate Y for a point
	 * @throws Exception
	 */
	public Point(double x, double y) throws Exception {
		setX(x);
        setY(y);
    }
	
	/**
	 * Constructor
	 * 
	 * @throws Exception
	 */
	public Point() throws Exception {}
	
	/**
	 * Sets X Coordinates
	 * 
	 * @param x Coordinate X for a point 
	 */
	public void setX(double x) {
        _x = x;
    }
     
	/**
	 * Sets X Coordinates
	 * 
	 * @param y Coordinate Y for a point 
	 */
    public void setY(double y) {
        _y = y;
    }
    
    /**
     * Sets a flag which determines if the point has been assigned to a specific K (i.e., Centroid)
     * 
     * @param isAssigned Parameter identifies if point has been assigned 
     */
    public void setIsAssigned(boolean isAssigned) {
        _isAssigned = isAssigned;
    }
        
    /**
     * Sets cluster number
     * 
     * @param n identifies cluster number
     */
    public void setCluster(int n) {
        _clusterNumber = n;
    }
    
    /**
     * Calculates distance between two points
     * 
     * @param p Object of class Point
     * @param centroid Object of class Centroid
     * @return Euclidean distance between two points 
     */
    public double distance(Point p, Point centroid) {
        return Math.sqrt(Math.pow((centroid.getY() - p.getY()), 2) + Math.pow((centroid.getX() - p.getX()), 2));
    }
    
    /**
     * Creates random points within the specified dimensions
     * 
     * @param minCoordinate Minimum coordinate for the dimension
     * @param maxCoordinate Maximum coordinate for the dimension
     * @return One point
     * @throws Exception
     */
    public static Point createRandomPoint(Dimensions minCoordinate, Dimensions maxCoordinate) throws Exception {
    	Random r = new Random();
    	double x = minCoordinate.getValue() + (maxCoordinate.getValue() - minCoordinate.getValue()) * r.nextDouble();
    	double y = minCoordinate.getValue() + (maxCoordinate.getValue() - minCoordinate.getValue()) * r.nextDouble();
    	return new Point(x,y);
    }
    
    /**
     * Create static X (e.g., 10,000) points - each point represents a person standing on a corner of a city block
     * 
     * @param minCoordinate Minimum coordinate for the dimension
     * @param maxCoordinate Maximum coordinate for the dimension
     * @return A list of points
     * @throws Exception
     */
    public static List<Point> createPoints(Dimensions minCoordinate, Dimensions maxCoordinate) throws Exception {
    	List<Point> points = new ArrayList<Point>();
    	for(int x = minCoordinate.getValue(); x <= maxCoordinate.getValue(); x++) {
    		for(int y = minCoordinate.getValue(); y <= maxCoordinate.getValue(); y++) {
    			points.add(new Point(x,y,false));
        	}
    	}
    	return points;
    }
    
    /**
     * Create static X (e.g., 10,000) points - each point represents a person standing on a corner of a city block
     * 
     * @param minCoordinate Minimum coordinate for the dimension
     * @param maxCoordinate Maximum coordinate for the dimension
     * @return A list of points
     * @throws Exception
     */
    public static List<Point> createPoints(int min, int max) throws Exception {
    	List<Point> points = new ArrayList<Point>();
    	for(int x = min; x <= max; x++) {
    		for(int y = min; y <= max; y++) {
    			points.add(new Point(x,y,false));
        	}
    	}
    	return points;
    }
    
    /**
     * Format string to print one trailing number after "." for all double types
     */
    public String toString() { return String.format("(%.1f,%.1f)", _x,_y); }
   
}
