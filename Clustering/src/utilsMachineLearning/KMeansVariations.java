package utilsMachineLearning;

/**
 * Defines specific variations for the KMeans algorithm to be executed
 * @author Oksana Kitaychik
 *
 */
public class KMeansVariations {
	public static final KMeansVariations ORIGINAL = new KMeansVariations( "Original" );
	public static final KMeansVariations ASSIGNALLPOINTS = new KMeansVariations( "AssignAllPoints" ); //This assigns all closet points to cluster X and moves to the next one
	public static final KMeansVariations ASSIGNONEPOINT = new KMeansVariations( "AssignOnePoint" ); //This assigns one point at a time to each cluster, results in better convergence
	
	private String _variations;
	
	private KMeansVariations (String variations) { 
		_variations = variations; 
	}
	
	public String toString(){ return _variations; };
}
