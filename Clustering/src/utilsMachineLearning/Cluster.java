package utilsMachineLearning;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

	private List<Point> _points;
	private Point _point;
	private int _id;
	
	public Point       getPoint()  { return _point;  }
	public int         getId()     { return _id;  	 }
	public List<Point> getPoints() { return _points; }
	
	/**
	 * Constructor instantiates cluster ID and Point(s)
	 *  
	 * @param id Sets specific ID for each cluster
	 */
	public Cluster(int id) {
		_id = id;
		_points = new ArrayList<Point>();
		_point = null;
	}
 
	/**
	 * Adds one Point 
	 * 
	 * @param point Specific Point to be added
	 */
	public void addPoint(Point point) {
		_points.add(point);
	}
 
	/**
	 * Set many Points
	 * 
	 * @param points Specific points to be set
	 */
	public void setPoints(List<Point> points) {
		this._points = points;
	}
 
	/**
	 * Set one Point
	 * 
	 * @param point Specific Point to be set
	 */
	public void setPoint(Point point) {
		_point = point;
	}
 	
	/**
	 * Clears all points
	 */
	public void clear() {
		_points.clear();
	}
}
