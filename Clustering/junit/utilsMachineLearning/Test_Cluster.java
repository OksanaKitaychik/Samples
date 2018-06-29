package utilsMachineLearning;

import java.util.List;

public class Test_Cluster extends junit.framework.TestCase {
	
	/**
	 * Test creation of cluster data 
	 * 
	 * @throws Exception
	 */
	public void testSetPoint() throws Exception {
		Cluster cluster0 = new Cluster(0);
		cluster0.setPoint(new Point(0.00,1.00));
    	assertTrue(cluster0.getId() == 0);
    	assertTrue(cluster0.getPoints().size() == 0);
    	assertTrue(cluster0.getPoint().getX() == 0.00);
    	assertTrue(cluster0.getPoint().getY() == 1.00);
    	
    	Cluster cluster1 = new Cluster(1);
		cluster1.setPoint(new Point(1.00,2.00));
		assertEquals(cluster1.getId(), 1, 0.001);
    	assertEquals(cluster1.getPoints().size(), 0, 0.001);
    	assertEquals(cluster1.getPoint().getX(), 1.00, 0.001);
    	assertEquals(cluster1.getPoint().getY(), 2.00, 0.001);
    	
    	Cluster cluster2 = new Cluster(2);
		cluster2.setPoint(new Point(4.00,12.00));
		assertEquals(cluster2.getId(), 2, 0.001);
    	assertEquals(cluster2.getPoints().size(), 0, 0.001);
    	assertEquals(cluster2.getPoint().getX(), 4.00, 0.001);
    	assertEquals(cluster2.getPoint().getY(), 12.00, 0.001);
	}
	
	/**
	 * Test creation of cluster data
	 * 
	 * @throws Exception
	 */
	public void testSetPoints() throws Exception {
		List<Point> points = Point.createPoints(Dimensions.MINIMUM, Dimensions.MAXIMUM);
		Cluster cluster0 = new Cluster(0);
		cluster0.setPoints(points);
		assertEquals(cluster0.getId(), 0, 0.001);
		
		int totalPoints = (Dimensions.MAXIMUM.getValue() + 1) * (Dimensions.MAXIMUM.getValue() + 1);
		assertEquals(cluster0.getPoints().size(), totalPoints, 0.001);
		
		assertTrue(cluster0.getPoints().get(0).getX() == 0 && cluster0.getPoints().get(0).getY() == 0);
		assertTrue(cluster0.getPoints().get(1).getX() == 0 && cluster0.getPoints().get(1).getY() == 1);
		assertTrue(cluster0.getPoints().get(2).getX() == 0 && cluster0.getPoints().get(2).getY() == 2);
		
		Cluster cluster24 = new Cluster(24);
		cluster24.setPoints(points);
		assertEquals(cluster24.getId(), 24, 0.001);
		
		assertEquals(cluster24.getPoints().size(), totalPoints, 0.001);
		
		assertTrue(cluster24.getPoints().get(0).getX() == 0 && cluster0.getPoints().get(0).getY() == 0);
		assertTrue(cluster24.getPoints().get(3).getX() == 0 && cluster0.getPoints().get(1).getY() == 1);
		assertTrue(cluster24.getPoints().get(1).getX() == 0 && cluster0.getPoints().get(2).getY() == 2);
	}
}
