package utilsMachineLearning;

import java.util.List;

public class Test_Point extends junit.framework.TestCase {
	
	/**
	 * Test creation of random points
	 * 
	 * @throws Exception
	 */
	public void testCreateRandomPoint() throws Exception {
		Point point1 = Point.createRandomPoint(Dimensions.MINIMUM, Dimensions.MAXIMUM);
		assertTrue(point1.getX() <= Dimensions.MAXIMUM.getValue() 
				&& point1.getX() >= Dimensions.MINIMUM.getValue() 
				&& point1.getY() <= Dimensions.MAXIMUM.getValue() 
				&& point1.getY() >= Dimensions.MINIMUM.getValue() 
				);
		
		Point point2 = Point.createRandomPoint(Dimensions.MINIMUM, Dimensions.MAXIMUM);
		assertTrue(point2.getX() <= Dimensions.MAXIMUM.getValue() 
				&& point2.getX() >= Dimensions.MINIMUM.getValue() 
				&& point2.getY() <= Dimensions.MAXIMUM.getValue() 
				&& point2.getY() >= Dimensions.MINIMUM.getValue() 
				);
		
		Point point3 = Point.createRandomPoint(Dimensions.MINIMUM, Dimensions.MAXIMUM);
		assertTrue(point3.getX() <= Dimensions.MAXIMUM.getValue() 
				&& point3.getX() >= Dimensions.MINIMUM.getValue() 
				&& point3.getY() <= Dimensions.MAXIMUM.getValue() 
				&& point3.getY() >= Dimensions.MINIMUM.getValue() 
				);
	}
	
	/**
	 * Test creation of points
	 * 
	 * @throws Exception
	 */
	public void testCreatePoints() throws Exception {
		List<Point> points = Point.createPoints(Dimensions.MINIMUM, Dimensions.MAXIMUM);
		assertTrue(points.get(0).getX() == 0 && points.get(0).getY() == 0);
		assertTrue(points.get(1).getX() == 0 && points.get(1).getY() == 1);
		assertTrue(points.get(2).getX() == 0 && points.get(2).getY() == 2);
		assertTrue(points.get(3).getX() == 0 && points.get(3).getY() == 3);
		assertTrue(points.get(4).getX() == 0 && points.get(4).getY() == 4);
		assertTrue(points.get(5).getX() == 0 && points.get(5).getY() == 5);
		assertTrue(points.get(6).getX() == 0 && points.get(6).getY() == 6);
		assertTrue(points.get(7).getX() == 0 && points.get(7).getY() == 7);
	}
	
	/**
	 * Test distance formula
	 * 
	 * @throws Exception
	 */
	public void testDistance() throws Exception {
		Point pointDistance = new Point();
		assertTrue(4.00 == pointDistance.distance(new Point(0.00,5.00), new Point(0.00,9.00)));
		assertTrue(3.00 == pointDistance.distance(new Point(0.00,6.00), new Point(0.00,9.00)));
		assertTrue(2.00 == pointDistance.distance(new Point(0.00,7.00), new Point(0.00,9.00)));
		assertTrue(1.00 == pointDistance.distance(new Point(0.00,8.00), new Point(0.00,9.00)));
		assertTrue(0.00 == pointDistance.distance(new Point(0.00,9.00), new Point(0.00,9.00)));
		assertTrue(1.00 == pointDistance.distance(new Point(0.00,10.00), new Point(0.00,9.00)));
		assertTrue(2.00 == pointDistance.distance(new Point(0.00,11.00), new Point(0.00,9.00)));
		assertTrue(3.00 == pointDistance.distance(new Point(0.00,12.00), new Point(0.00,9.00)));
		assertTrue(4.00 == pointDistance.distance(new Point(0.00,13.00), new Point(0.00,9.00)));
		assertTrue(5.00 == pointDistance.distance(new Point(0.00,14.00), new Point(0.00,9.00)));
	}
}
