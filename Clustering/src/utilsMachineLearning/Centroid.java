package utilsMachineLearning;

import java.util.ArrayList;
import java.util.List;

public class Centroid<ValType> {

	private int _totalCentroids;
	
	public int getTotalCentroids() { return _totalCentroids; }
	
	/**
	 * Constructor sets total number of K to be created
	 * 
	 * @param totalCentroids Total number of K to be created
	 * @throws Exception
	 */
	public Centroid(int totalCentroids) throws Exception {
		
		if(totalCentroids < 1)
			throw new Exception ( "Total number of Centroids can't be set to less than 1  (Count: " + totalCentroids + ")");
		
		this.setTotalCentroids(totalCentroids);
	}
	
	/**
	 * Returns all K randomly generate K points (X,Y)
	 * 
	 * @param clusters Cluster for point to be created
	 * @return
	 * @throws Exception
	 */
	public List<Point> getCentroids(List<ValType> clusters) throws Exception {
		List<Point> centroids = new ArrayList<Point>(getTotalCentroids());
    	for(ValType cluster : clusters) {
    		Point point = new Point(((Cluster) cluster).getPoint().getX(),((Cluster) cluster).getPoint().getY());
    		centroids.add(point);
    	}
    	return centroids;
	}
	
	/**
	 * Set total number of K to be created
	 * 
	 * @param _totalCentroids Total number of K
	 */
	public void setTotalCentroids(int _totalCentroids) {
		this._totalCentroids = _totalCentroids;
	}
	
	/**
	 * Randomly creates K points by utilizing Cluster class and predefined dimensions
	 * 
	 * @param cluster Specific cluster for K
	 * @return Cluster with K
	 * @throws Exception
	 */
	public ValType setCentroid(ValType cluster) throws Exception {
		((Cluster) cluster).setPoint(Point.createRandomPoint(Dimensions.MINIMUM,Dimensions.MAXIMUM));
		return cluster; 
	}
	
	
}
