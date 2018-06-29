package KMeans;

import java.util.ArrayList;
import java.util.List;

import utilsMachineLearning.Cluster;
import utilsMachineLearning.Dimensions;
import utilsMachineLearning.KMeansVariations;
import utilsMachineLearning.Point;

public class Test_KMeans extends junit.framework.TestCase {

	private double[] _data;
    private int _size;   
    private List<Double> _testKVariance0 = new ArrayList<Double>();
    private List<Double> _testKStd0 = new ArrayList<Double>();;
    private List<Double> _testKVariance1 = new ArrayList<Double>();;
    private List<Double> _testKStd1 = new ArrayList<Double>();;
    private List<Double> _testKVariance2 = new ArrayList<Double>();;
    private List<Double> _testKStd2 = new ArrayList<Double>();;
    private double       _dataKMeansOriginalVariance;
    private double       _dataKMeansOriginalStd;
    private double       _dataKMeansAllPointsVariance;
    private double       _dataKMeansAllPointsStd;
    private double       _dataKMeansOnePointVariance;
    private double       _dataKMeansOnePointStd;
    
	/**
	 * Test fitness of the data for the KMeans and variation algorithms:
	 * Specifically, find minimum distance between Centroids and average them out for KMeans and two variations of KMeans
	 * This will be performed 10 times for each algorithm. The expectation is for KMeans variation of X points assigned is
	 * to have a lower standard deviation and variance for 10 executions on average as distribution of points is not controlled, 
	 * leading to different sizes of clusters as well as difference in distance between Centroids.
	 * 
	 * @throws Exception
	 */
    public void testFitness() throws Exception {
    	getKMeansOriginal();
    	getKMeansAllPoints();
    	//getKMeansOnePoint(); //slow hence commented out
    	
    	//Comparing variance of assigned X points algorithm to original KMeans and another variation
    	//Test passes because distance is smaller on average between Centroids when specific number of points is assigned
    	assertTrue((_dataKMeansOnePointVariance < _dataKMeansOriginalVariance) ? true : false);
    	assertTrue((_dataKMeansOnePointVariance < _dataKMeansAllPointsVariance) ? true : false);
    	
    	//Comparing standard deviation of assigned X points algorithm to original KMeans and another variation
    	//Test passes because distance is smaller on average between Centroids when specific number of points is assigned
    	assertTrue((_dataKMeansOnePointStd < _dataKMeansOriginalStd) ? true : false);
    	assertTrue((_dataKMeansOnePointStd < _dataKMeansAllPointsStd) ? true : false);
    }
    
	private void getKMeansOriginal() throws Exception {
		int dataSize;
		double distance = 0.0; 
		double max = Double.MAX_VALUE;
		double min = max;
        int i = 0;   
        KMeans kmeans0;
        dataSize = 5;
        double[] data = new double[dataSize];
		for(int run = 1; run <= 10; run++) {
			kmeans0 = new KMeans(5);
			kmeans0.createPoints();
			kmeans0.createCentroids();
			kmeans0.executeAlgorithms(KMeansVariations.ORIGINAL);
			List<Cluster> clusters0 = kmeans0.getClusters();
			Point pointDistance = new Point();
			distance = 0.0; 
			max = Double.MAX_VALUE;
			min = max;
	        i = 0;   
	        Cluster c2 = null;
	        //Calculate minimum distance from K to all other K
	        for(int j = 0; j < dataSize; j++) {
	        	min = max;
	        	Cluster c1 = clusters0.get(j);
	        	for(int k = 0; k < dataSize; k++) {
	        		if(j != k) {
	        			c2 = clusters0.get(k);
	        			distance = pointDistance.distance(c1.getPoint(), c2.getPoint());
	        			if(distance < min){ 
	        				min = distance;
	        			}
	        		}
	            }
	        	data[i] = min;
	        	i++;
	        }
	        _data = data;
	        _size = _data.length;
	        _testKVariance0.add(getVariance());
	        _testKStd0.add(getStdDev());
		}
		
		i = 0;
    	dataSize = 10;
    	data = new double[dataSize];
    	for (Double o : _testKVariance0) {
    		data[i] = o;
    		i++;
    	}
    	_data = data;
        _size = _data.length;
        _dataKMeansOriginalVariance = getMean();
        
        i = 0;
    	dataSize = 10;
    	data = new double[dataSize];
    	for (Double o : _testKStd0) {
    		data[i] = o;
    		i++;
    	}
    	_data = data;
        _size = _data.length;
        _dataKMeansOriginalStd = getMean();
	}
	
	private void getKMeansAllPoints() throws Exception {
		int dataSize;
		double distance = 0.0; 
		double max = Double.MAX_VALUE;
		double min = max;
        int i = 0;   
        KMeans kmeans0;
        dataSize = 5;
        double[] data = new double[dataSize];
		for(int run = 1; run <= 10; run++) {
			kmeans0 = new KMeans(5);
			kmeans0.createPoints();
			kmeans0.createCentroids();
			kmeans0.executeAlgorithms(KMeansVariations.ASSIGNALLPOINTS);
			List<Cluster> clusters0 = kmeans0.getClusters();
			Point pointDistance = new Point();
			distance = 0.0; 
			max = Double.MAX_VALUE;
			min = max;
	        i = 0;   
	        Cluster c2 = null;
	        //Calculate minimum distance from K to all other K
	        for(int j = 0; j < dataSize; j++) {
	        	min = max;
	        	Cluster c1 = clusters0.get(j);
	        	for(int k = 0; k < dataSize; k++) {
	        		if(j != k) {
	        			c2 = clusters0.get(k);
	        			distance = pointDistance.distance(c1.getPoint(), c2.getPoint());
	        			if(distance < min){ 
	        				min = distance;
	        			}
	        		}
	            }
	        	data[i] = min;
	        	i++;
	        }
	        _data = data;
	        _size = _data.length;
	        _testKVariance1.add(getVariance());
	        _testKStd1.add(getStdDev());
		}
		
		i = 0;
    	dataSize = 10;
    	data = new double[dataSize];
    	for (Double o : _testKVariance1) {
    		data[i] = o;
    		i++;
    	}
    	_data = data;
        _size = _data.length;
        _dataKMeansAllPointsVariance = getMean();
        
        i = 0;
    	dataSize = 10;
    	data = new double[dataSize];
    	for (Double o : _testKStd1) {
    		data[i] = o;
    		i++;
    	}
    	_data = data;
        _size = _data.length;
        _dataKMeansAllPointsStd = getMean();
	}
	
	private void getKMeansOnePoint() throws Exception {
		int dataSize;
		double distance = 0.0; 
		double max = Double.MAX_VALUE;
		double min = max;
        int i = 0;   
        KMeans kmeans0;
        dataSize = 5;
        double[] data = new double[dataSize];
		for(int run = 1; run <= 10; run++) {
			kmeans0 = new KMeans(5);
			kmeans0.createPoints(0,9);
			kmeans0.createCentroids();
			kmeans0.executeAlgorithms(KMeansVariations.ASSIGNONEPOINT);
			List<Cluster> clusters0 = kmeans0.getClusters();
			Point pointDistance = new Point();
			distance = 0.0; 
			max = Double.MAX_VALUE;
			min = max;
	        i = 0;   
	        Cluster c2 = null;
	        //Calculate minimum distance from K to all other K
	        for(int j = 0; j < dataSize; j++) {
	        	min = max;
	        	Cluster c1 = clusters0.get(j);
	        	for(int k = 0; k < dataSize; k++) {
	        		if(j != k) {
	        			c2 = clusters0.get(k);
	        			distance = pointDistance.distance(c1.getPoint(), c2.getPoint());
	        			if(distance < min){ 
	        				min = distance;
	        			}
	        		}
	            }
	        	data[i] = min;
	        	i++;
	        }
	        _data = data;
	        _size = _data.length;
	        _testKVariance2.add(getVariance());
	        _testKStd2.add(getStdDev());
		}
		
		i = 0;
    	dataSize = 10;
    	data = new double[dataSize];
    	for (Double o : _testKVariance2) {
    		data[i] = o;
    		i++;
    	}
    	_data = data;
        _size = _data.length;
        _dataKMeansOnePointVariance = getMean();
        
        i = 0;
    	dataSize = 10;
    	data = new double[dataSize];
    	for (Double o : _testKStd2) {
    		data[i] = o;
    		i++;
    	}
    	_data = data;
        _size = _data.length;
        _dataKMeansOnePointStd = getMean();
 	}
	
	/**
	 * Test how points are assigned to clusters 
	 * 
	 * @throws Exception
	 */
	public void testIsTotalPointsAssigned() throws Exception {
		KMeans kmeans0 = new KMeans(5);
		kmeans0.createCentroids();
		
		int maxPoints = 20;
		assertTrue(kmeans0.isTotalPointsAssigned(maxPoints) == false);
		maxPoints = 0;
		assertTrue(kmeans0.isTotalPointsAssigned(maxPoints) == true);
		
		maxPoints = ((Dimensions.MAXIMUM.getValue() + 1) * (Dimensions.MAXIMUM.getValue() + 1)) / 5;
		List<Cluster> clusters0 = kmeans0.getClusters();
    	int size = clusters0.get(0).getPoints().size();
    	assertEquals(size, 0, 0.001);
    	
    	kmeans0.assignOnePointToCentroid();
    	size = clusters0.get(0).getPoints().size();
    	assertEquals(size, maxPoints, 0.001);
    	
    	KMeans kmeans1 = new KMeans(4);
		kmeans1.createCentroids();
		kmeans1.assignOnePointToCentroid();
		size = clusters0.get(0).getPoints().size();
		maxPoints = ((Dimensions.MAXIMUM.getValue() + 1) * (Dimensions.MAXIMUM.getValue() + 1)) / 5;
		assertEquals(size, maxPoints, 0.001);
	}
	
	/**
	 * Test clear of cluster data
	 * 
	 * @throws Exception
	 */
	public void testClearClusters() throws Exception {
		KMeans kmeans0 = new KMeans(5);
		kmeans0.createPoints();
    	kmeans0.createCentroids();
    	kmeans0.assignOnePointToCentroid();
    	List<Cluster> clusters0 = kmeans0.getClusters();
    	int size = clusters0.get(0).getPoints().size();
    	kmeans0.clearClusters();
    	size = clusters0.get(0).getPoints().size();
    	assertEquals(size, 0, 0.001);
	}
	
	private double getMean()
    {
        double sum = 0.0;
        for(double d : _data)
            sum += d;
        return sum/_size;
    }
	
	private double getVariance()
    {
        double mean = getMean();
        double temp = 0;
        for(double d : _data)
            temp += (mean-d)*(mean-d);
        return temp/_size;
    }
	
	private double getStdDev()
    {
        return Math.sqrt(getVariance());
    }
    
}
