# Oksana Kitaychik's Sample Class (Courant) Projects / Homeworks

### Project: Local Stochastic Volatility Model 

Source code: Python (provided as .ipynb and print-out/PDF copy)

Project files: https://github.com/OksanaKitaychik/Samples/tree/master/LocalStochasticVolModel

Description: Includes a detailed model and model validation description ("Local Stochastic Vol Model - Oksana_Kitaychik_HMW7_Model_Validation"). This model is an extension of both local volatility and stochastic volatility models. I implemented it using ‘particle method’ which builds on the chaos propagation theory and McKean non-linear stochastic differential equations. The code/description also contain a number of model validation steps (sensitivity checks, stress tests, model calibration examples, comparison to benchmark models etc.) 

### Project: K-Means and Modified Clustering

Source code: Java

Project files: https://github.com/OksanaKitaychik/Samples/tree/master/Clustering

Description: Groups data into clusters. Data is examined in sequence and allocated to the closest cluster, in terms of the Euclidean distance to the cluster mean. The mean vector is recalculated each time a new member/data point is added. Three variations of this algorithm are included in the code including an implementation of the 'classical' K-Means algorithm (https://en.wikipedia.org/wiki/K-means_clustering) and two variations that attempt to ensure that an equal number of points are assigned to each cluster. Such variations are not the exact solutions (as the exact solution does not exist); an error optimization / measure of solution quality is included as well.  

### Project: Trading System Simulation

Source code: Java

Project files: https://github.com/OksanaKitaychik/Samples/tree/master/TradingSystemSimulation

Description: I modelled / simulated a stock exchange. One of the goals of the assignment was to choose data structures that represent an order book and make adding resting orders, sweeping the book, and cancelling orders computationally efficient. To follow steps of the program, please refer to the “Test_Exchange” junit class (i.e., https://github.com/OksanaKitaychik/Samples/blob/master/TradingSystemSimulation/junit/exchange/Test_Exchange.java). Below are some of the steps described in the “Test_Exchange” class:
1.	Instantiate client
2.	Instantiate exchange
3.	Create a client
4.	Add client to connection
5.	Instantiate market id
6.	Instantiate order side (Buy, Sell), order quantity, limit price
7.	Send message to exchange
8.	Place GTC Order (Good till cancelled)


 
