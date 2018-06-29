package utilsMachineLearning;

/**
 * Utilized to sort points from lowest to highest based on distance from current K to a Point or find the closest point
 * @author Oksana Kitaychik
 *
 */
public class AssignedKPoints implements Comparable<AssignedKPoints> {
	
	private Point _point;
    private double _distance = 0;
    
    public Point  getPoint()          { return _point;         }
    public double getDistance()       { return _distance;      }
    
    public AssignedKPoints(Point point, double distance) {
    	setPoint(point);
        setDistance(distance);
    }
    
    public void setPoint(Point point) {
		_point = point;
	}
	
	public void setDistance(double distance) {
        _distance = distance;
    }
	
	@Override
	public int compareTo(AssignedKPoints compareDistance) {
		if(_distance == compareDistance.getDistance())
			return 0;
		else if (_distance > compareDistance.getDistance())
			return 1;
		else 
			return -1;
	}
}
