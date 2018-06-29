package utilsMachineLearning;

import java.io.File;

public class Test_CreateReport extends junit.framework.TestCase {
	
	/**
	 * Test saving of file data
	 * 
	 * @throws Exception
	 */
	public void testSaveFileData() throws Exception {
		CreateReport reportData = new CreateReport();
		String fileData = "Line 1\n";
		fileData += "Line 1\n";
		fileData += "Line 2\n";
		fileData += "Line 3\n";
		fileData += "Line 4\n";
		String resultsFileName = "TestKMeansResults.html";
		String dirPath = "C:\\CompFinance2015_Resources_Output\\";
	    
		reportData.saveFileData(fileData,(dirPath + resultsFileName));
		
		File fileName = new File(dirPath + resultsFileName);
		assertTrue(fileName.exists() == true);
		
		if (fileName.exists()){
            fileName.delete();
        } 
	}
}
