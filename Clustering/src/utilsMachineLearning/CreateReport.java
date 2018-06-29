package utilsMachineLearning;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * This class is utilized to generate the report data. In this case we are using plain HTML tags to generate the report data. 
 * Ideally, this would be stored in the database and/or XML. Furthermore, much more comprehensive API would be used to generate the report data. 
 * @author Oksana Kitaychik
 *
 */
public class CreateReport {

	/**
	 * Saves file data to a specific location
	 * 
	 * @param fileData Data to be saved
	 * @param filePathName Path to a file and file name
	 * @throws Exception
	 */
	public void saveFileData(String fileData, String filePathName) throws Exception {
	   	if(fileData == null)
	   		throw new Exception("File data can't be an empty string.");
	   	File fileName = new File(filePathName);
	   	FileWriter fw = null;
        if (fileName.exists()){
            fileName.delete();
        } 
        fw = new FileWriter(fileName); //"true" indicates the data is being appended to the file
        fw.append(fileData);
        fw.flush();
        fw.close();
   }
   
	/**
	 * Sets Centroid data to be saved later
	 * 
	 * @param columnHeader Our column headers
	 * @param kTotal Total number of K
	 * @param _clusters Cluster Data
	 * @return String of Centroid data to be saved
	 * @throws Exception
	 */
   public String setCentroidData(String columnHeader, int kTotal, List<Cluster> _clusters) throws Exception {
		String kData = new String();
		String kHeader = new String();
		kHeader = "<table border=\"1\"><tr>";
		kData = "<tr>";
			for (int i = 0; i < kTotal; i++) {
			Cluster c = _clusters.get(i);
			kHeader += "<td><b>" + columnHeader + " K (X) " + i + "</b></td><td><b>" + columnHeader + " K (Y) " + i + "</b></td>";
			kData += "<td>" + String.format("%.2f", c.getPoint().getX()) + "</td><td>" + String.format("%.2f", c.getPoint().getY()) + "</td>";
		}
			kHeader += "</tr>";
			kData += "</tr>";
			return (kHeader + kData + "</table>");
   }
   
   /**
    * Sets Point data to be saved later
    * 
    * @param kTotal Total number of K
    * @param _clusters Cluster Data
    * @return String of Point data to be saved
    * @throws Exception
    */
   public String savePointData(int kTotal, List<Cluster> _clusters) throws Exception {
	   	String pData = new String();
	   	pData = "<table border=\"1\"><tr>";
	   	for (int j = 0; j < kTotal; j++) 
	   		pData += "<td><b>Points (X) " + j + "</b></td><td><b>Points (Y) " + j + "</b></td>";
	   	pData+= "</tr></table>";
	   	
	   	pData+= "<table><tr>";
	   	for (int i = 0; i < kTotal; i++) {
				Cluster c = _clusters.get(i);
				List<Point> kPoints = c.getPoints();
				pData += "<td><table border=\"1\">";
				for(Point p : kPoints) {
	   			pData += "<tr><td>" + p.getX() + "</td><td>" + p.getY() + "</td></tr>";
				}
				pData += "</table></td>";
	   	}
			pData+= "</tr></table>";
			
	   	return pData;
   }
	
}
