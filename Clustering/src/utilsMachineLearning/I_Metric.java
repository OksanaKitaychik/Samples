package utilsMachineLearning;

/**
 * Represents a general idea of the metric, specific implementation of a metric (i.e., Euclidean distance) is implemented in a Point class 
 * @author Oksana Kitaychik
 *
 * @param <ValType>
 */
public interface I_Metric<ValType> {
	double distance(ValType v1, ValType v2);
}



