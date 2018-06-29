package KMeans;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import utilsMachineLearning.AssignedKPoints;
import utilsMachineLearning.Centroid;
import utilsMachineLearning.Cluster;
import utilsMachineLearning.CreateReport;
import utilsMachineLearning.Dimensions;
import utilsMachineLearning.KMeansVariations;
import utilsMachineLearning.Point;

public class KMeans {
	
    private int                   _totalKCount; 
    private String                _fileData;
    private final String          _resultsFileName = "KMeansResults.html";
	private final String          _dirPath = "C:\\Resources_Output\\";
    private List<Point>           _points;
    private List<Cluster>         _clusters;
    private List<AssignedKPoints> _pointsSorted;
    private List<AssignedKPoints> _pointAlreadyAdded;

    public String getResultsFileName() { return _resultsFileName; }
    public String getDirPath()         { return _dirPath;	      }
	public List<Point> getPoints()     { return _points;	      }
	public int getKTotal()             { return _totalKCount;	  }
	public String getFileData()        { return _fileData;	      }
	public List<Cluster> getClusters() { return _clusters;        }


	/**
	 * Constructor instantiates class data (e.g., Total Number of K points to be created)
	 * 
	 * Perform some error checking
	 * 
	 * @param totalKCount
	 */
    public KMeans(int totalKCount) throws Exception {
    	
    	if(totalKCount == 0)
			throw new Exception("Total number of K (i.e., Centroid) points to be created can't be 0.");
    	
    	setKTotal(totalKCount);
    	setPoints(new ArrayList<Point>());
    	_clusters = new ArrayList<Cluster>(); 
    	_pointAlreadyAdded = new ArrayList<AssignedKPoints>();
    	_pointsSorted = new ArrayList<AssignedKPoints>();
    }
    
    /**
     * Utilized to created points - e.g., 10,000 points where each point represents a person 
     * 
     * @param points
     */
    public void setPoints(List<Point> points) {
		this._points = points;
	}

    /**
     * Utilized to set total number of K (i.e., Centroid) points to be created
     * 
     * @param totalKCount Total K to be created
     */
	public void setKTotal(int totalKCount) {
		_totalKCount = totalKCount;
	}

	/**
	 * File data is used to save report data for the KMeans algorithms and variations
	 * 
	 * @param fileData Data to be saved in the report
	 */
	public void setFileData(String fileData) {
		if (_fileData != null)
			_fileData += fileData;
		else
			_fileData = fileData;
	}
    
	public static void main(String[] args) throws Exception {
    	
    	//Instantiate to total number of K to be created
    	KMeans kmeans = new KMeans(100);
    	
    	//Create points - these are assigned designated location 
    	kmeans.createPoints();
    	
    	//Create random Centroids within our dimension 
    	kmeans.createCentroids();
    	
    	//Calculate all K data via KMeans and variation of the algorithm
    	kmeans.executeAlgorithms(KMeansVariations.ORIGINAL);
    }
    
    /**
     * Create 10,000 points - each point represents a person standing on a corner of a city block
     * 
     * @throws Exception
     */
    public void createPoints() throws Exception {
    	setPoints(Point.createPoints(Dimensions.MINIMUM, Dimensions.MAXIMUM));
    }
    
    public void createPoints(int min, int max) throws Exception {
    	setPoints(Point.createPoints(min, max));
    }
    
    /**
     * Create random Centroids within the dimension specified
     * 
     * @throws Exception
     */
    public void createCentroids() throws Exception {
    	Centroid<Cluster> centroids = new Centroid<Cluster>(getKTotal());
    	for (int i = 0; i < centroids.getTotalCentroids(); i++) {
    		Cluster cluster = new Cluster(i);
    		centroids.setCentroid(cluster);
    		_clusters.add(cluster);
    	}
    }
    
    /**
     * The process to calculate KMeans and various variations of the KMeans algorithm with iterating method.
     *  
     * @throws Exception
     */
    public void executeAlgorithms(KMeansVariations variation) throws Exception {
        boolean finish = false;
        int iteration = 0;
        double distanceCurrent = 0;
        File fileName = new File(getDirPath() + getResultsFileName());
		FileWriter fw = new FileWriter(fileName);
		List<Double> distancePrevious = new ArrayList<Double>();
		Point pointDistance = new Point();
		CreateReport reportData = new CreateReport();
		setFileData(reportData.setCentroidData("Init", getKTotal(), _clusters));
		
		while(!finish) {
        	//Clear cluster state or erase all points assigned to a Cluster object, but NOT centroid X,Y and id
        	clearClusters();
        	
        	//This returns Centroid points (X,Y) created (i.e., randomly generated) 
        	List<Point> previousCentroids = getCentroids();
        	
        	if(KMeansVariations.ORIGINAL == variation)
        		assignCentroidToPoint(); //This is an original KMeans algorithm	
        	else if(KMeansVariations.ASSIGNALLPOINTS == variation)
        		assignAllPointsToCentroid(); //This algorithm assigns ALL available points to the closest K (i.e., Centroid), which leads to many overlaps and very little convergence 
        	else if (KMeansVariations.ASSIGNONEPOINT == variation)
        		assignOnePointToCentroid(); //This algorithm assigns 1 point at a time to each cluster to reach X number of points per cluster, which leads to fairly good convergence
        	
        	//Calculate average of all points in the specific Cluster/Centroid and set new coordinates (i.e., X,Y) for the Centroid
        	calculateCentroids();
        	
        	iteration++;
        	
        	List<Point> currentCentroids = getCentroids();
        	
        	//Calculates total distance between new and old Centroids
        	//Since we continually move the coordinates of our Centroids, we need to check the distance until it is 0 / or hasn't been moved
        	distanceCurrent = 0;
        	for(int i = 0; i < previousCentroids.size(); i++) {
        		distanceCurrent += pointDistance.distance(previousCentroids.get(i),currentCentroids.get(i));
        	}
        	
        	System.out.println("--------------");
        	System.out.println("Iteration: " + iteration);
        	System.out.println("Centroid distances: " + distanceCurrent);
        	
        	//If distance is 0 or the same as the previous (i.e., points are traded), program is terminated        	
        	if(distanceCurrent == 0 || (isDistancePattern(distancePrevious, distanceCurrent))) {
            
        		//Set K data
        		setFileData(reportData.setCentroidData("Final", getKTotal(), _clusters));
        		
        		//Set Point data
        		setFileData(reportData.savePointData(getKTotal(), _clusters));
        		
        		//Save file data to a designated file
        		reportData.saveFileData(getFileData(), getDirPath() + getResultsFileName());
        		
            	System.out.println("--------------");
            	System.out.println("Iteration: " + iteration);
            	System.out.println("Centroid distances: " + distanceCurrent);
            	
        		finish = true;
        	}
        	distancePrevious.add(distanceCurrent);
        }
        fw.flush();
        fw.close();
    }
    
    /**
     * Determine if there is a pattern in the distance between previous and current K
     * 
     * @param distancePrevious Previous distance of K
     * @param distanceCurrent Current Distance of K
     * @return Boolean value indicates if distance is the same
     */
    private boolean isDistancePattern(List<Double> distancePrevious, double distanceCurrent) {
    	for(Double distance : distancePrevious) {
    		if (distance == distanceCurrent)
    			return true;
    	}
    	return false;
    }
    
    /**
     * Clear List (i.e., List<Cluster>) of cluster objects 
     */
    public void clearClusters() {
    	for(Cluster cluster : _clusters) {
    		cluster.clear();
    	}
    }
    
    /**
     * This returns Centroid points (X,Y) created or randomly generated 
     * @return
     * @throws Exception
     */
    public List<Point> getCentroids() throws Exception {
    	Centroid<Cluster> centroids = new Centroid<Cluster>(getKTotal());
    	return centroids.getCentroids(_clusters);
    }
    
    /**
     * This is an original KMeans algorithm, assigns points to the closest cluster
     * 
     * @throws Exception
     */
    public void assignCentroidToPoint() throws Exception {
        double max = Double.MAX_VALUE;
        double min = max; 
        int cluster = 0;                 
        double distance = 0.0; 
        Point pointDistance = new Point();
        for(Point point : _points) {
        	min = max;
            for(int i = 0; i < getKTotal(); i++) {
            	Cluster c = _clusters.get(i);
                distance = pointDistance.distance(point, c.getPoint()); 
                if(distance < min){ 
                    min = distance;
                    cluster = i;
                }
            }
            point.setCluster(cluster);
            _clusters.get(cluster).addPoint(point);
        }
    }
    
    public void assignOnePointToCentroid() throws Exception {
    	double distance = 0.0; 
    	createPoints(); 
        //Matrix dimensions are divided by total number of K (e.g., 10,000/500 = 20)
        int maxCount = ((Dimensions.MAXIMUM.getValue() + 1) * (Dimensions.MAXIMUM.getValue() + 1)) / getKTotal(); 
        int i = 0;
        Point pointDistance = new Point();
        while(!isTotalPointsAssigned(maxCount)) {
        	Cluster c = _clusters.get(i);
        	_pointsSorted = new ArrayList<AssignedKPoints>();
        	
        	//Add all points to a List/X, and calculate distance for each, sort this List/X from lowest distance to highest, and then grab top X
        	for(Point point : _points) {
        		if(!point.getIsAssigned()) {
        			distance = pointDistance.distance(point, c.getPoint());
        			_pointsSorted.add(new AssignedKPoints (point, distance));
        		}
    		}
        	
        	//Sort from lowest to highest based on distance from current K to a Point
    		Collections.sort(_pointsSorted);
        	
        	//Add X (i.e., specific number of) closest points only
        	for (AssignedKPoints point : _pointsSorted) {
        		//Check if the point has been added to ANY cluster already 
        		if (!isPointAssignedToCluster(point.getPoint())) {
        			//Check the size of the cluster
        			if (c.getPoints().size() < maxCount) {
        				point.getPoint().setCluster(i);
        				point.getPoint().setIsAssigned(true);
        				c.addPoint(point.getPoint()); i++; //totalPointsAdded++;
        				if (i == getKTotal()) i = 0; 
        				break; //We add 1 point at a time
        			}
        		}
        	}
        }
    }
    
    /**
     * Determines if total points assigned to a specific K has reached 0, not a very efficient method 
     * It has been replaced in another algorithm in this program by a flag (i.e., _isAssigned) in the Point class
     * 
     * @param maxCount Maximum number of points to be assigned
     * @return
     */
    public boolean isTotalPointsAssigned(int maxCount) {
    	for(int i = 0; i < getKTotal(); i++) {
    		Cluster c = _clusters.get(i);
    		if (c.getPoints().size() < maxCount) {
    			return false;
    		}
    	}
    	return true;
    }
    
    /**
     * This algorithm assigns ALL available points to the closest K (i.e., Centroid), which leads to many overlaps and very little convergence
     * 
     * @throws Exception
     */
    public void assignAllPointsToCentroid() throws Exception {
    	double distance = 0.0; 
        int pointCount = 1;
        Point pointDistance = new Point();
        _pointAlreadyAdded = new ArrayList<AssignedKPoints>();
        
        //Matrix dimensions are divided by total number of K (e.g., 10,000/500 = 20)
        int maxCount = ((Dimensions.MAXIMUM.getValue() + 1) * (Dimensions.MAXIMUM.getValue() + 1)) / getKTotal(); 
        
    	for(int i = 0; i < getKTotal(); i++) {
    		pointCount = 1;
    		Cluster c = _clusters.get(i);
    		
    		//Add all points to a List/X, and calculate the distance for each, sort this List/X from lowest distance to highest, and then grab top X 
    		for(Point point : _points) {
    			distance = pointDistance.distance(point, c.getPoint());
    			_pointAlreadyAdded.add(new AssignedKPoints (point, distance));
    		}
    		
    		//Sort from lowest to highest based on distance from current K to a Point
    		Collections.sort(_pointAlreadyAdded);
    		
    		//Add X (i.e., specific number of) closest points only
    		for (AssignedKPoints point : _pointAlreadyAdded) {
    			//Check if the point has been added to ANY cluster already 
    			if (!isPointAssignedToCluster(point.getPoint())) {
	    			if (pointCount <= maxCount) {
	    				point.getPoint().setCluster(i); //i is a cluster ID
	    				c.addPoint(point.getPoint());
	    			}
	    			else {
	    				_pointAlreadyAdded.clear();
	    				break;
	    			}
	    			pointCount++;
    			}
    		} //end of inner for loops
    		//_kBreak++;
    		//break;
    	} //end of outer (K) for loop
    }
    
    /**
     * Determines if a point has actually been assigned to a specific K (i.e., Centroid)
     * 
     * @param pointCompare Point to compare to all points in a specific K (i.e., Centroid)
     * @return
     */
    private boolean isPointAssignedToCluster(Point pointCompare)
    {
    	List<Point> clusterPoints;
    	for(int i = 0; i < getKTotal(); i++) {
    		Cluster cluster = _clusters.get(i);
    		clusterPoints = cluster.getPoints();
    		for(Point pointAdded : clusterPoints)
        	{
    			if (pointAdded.getX() == pointCompare.getX() && 
        				pointAdded.getY() == pointCompare.getY()) {
        			return true;
        		}
        	}
    	}
    	return false;
    }
    
    /**
     * Calculates new X,Y coordinates for each K (i.e., Centroid) in the KMeans algorithm and its variations 
     */
    private void calculateCentroids() {
        for(Cluster cluster : _clusters) {
            double sumX = 0;
            double sumY = 0;
            List<Point> list = cluster.getPoints();
            int n_points = list.size();
            
            for(Point point : list) {
            	sumX += point.getX();
                sumY += point.getY();
            }
            
            Point centroid = cluster.getPoint(); 
            if(n_points > 0) {
            	double newX = sumX / n_points;
            	double newY = sumY / n_points;
                centroid.setX(newX);
                centroid.setY(newY);
            }
        }
    }
}
