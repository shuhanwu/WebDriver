package PrintMeetingsPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PrintMeetingsPerPerson 
{
	static TreeMap<String, List<String>> personSchedule = new TreeMap<>();
	
	
	//Check is input string in the array
	public static boolean checkStringInArray(String[] arr, String value)
	{
		for (String element : arr) {	
		    if (element.trim().equals(value.trim()) ) {
		        return true;
		    }
		}
		
		return false;
	}
	
	//Verify Text Compare the input strings (expected versus actual) and display the output message
	public static void verifyText(String expectedStr, String actualStr)
	{
	    /*
	     * compare the actual title of the page with the expected one and print
	     * the result as "Passed" or "Failed"
	     */
	    if (actualStr.contentEquals(expectedStr)){
	        System.out.println("Test Step To Verify Actual Text: '" + actualStr + "' match Expected: '" + expectedStr +   "' Step Passed!");
	    } else {
	        System.out.println("Test Step To Verify Actual Text: '" + actualStr + "' does not match Expected: '" + expectedStr +   "' Step Failed!");
	    }
	   
	}
	
	public static void waitForPageLoad(WebDriver wDriver, String xPath, Integer waitTime)
	{
	    WebDriverWait wait = new WebDriverWait(wDriver, waitTime);
	    By addItem = By.xpath(xPath);
	    wait.until(ExpectedConditions.presenceOfElementLocated(addItem));
	}
	
	/*
	 * print the meeting occurrences for each person in designed format 
	 * Person A meeting frequency 
	 * Person B meeting frequency
	 */

    public static void printMeetings(String dayOfWeek) 
    { 
  	  for (Entry<String, List<String>> entry : personSchedule.entrySet())  
  	  {	  
           
            if (entry.getKey().contains(dayOfWeek.toUpperCase() + ":Person"))
            {
              // Hashmap to store the frequency of element 
              Map<String, Integer> hm = new HashMap<String, Integer>(); 
         
               for (String i : entry.getValue()) { 
                   Integer j = hm.get(i); 
                   hm.put(i, (j == null) ? 1 : j + 1); 
               } 
         
               // display the occurrence of person with meetings in Arraylist 
               for (Map.Entry<String, Integer> val : hm.entrySet()) { 
                   System.out.println("Person " + val.getKey() + " " + " " + val.getValue() ); 
               } 
            }
            
  	  }   
  	  

    } 
	
	/**
	 * @param args
	 */
	//@SuppressWarnings("null")
	
	public static void main(String[] args) 
	{

		//use Chrome driver to run test with Chrome browser
		System.setProperty("webdriver.chrome.driver","C:\\chromedriver\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();
	
		String baseUrl = "https://www.weightwatchers.com/us/";
		String expectedTitle = "WW (Weight Watchers): Weight Loss & Wellness Help";
		String actualTitle = "";

		// launch chrome and direct it to the Base URL
		driver.get(baseUrl);

		// get the actual value of the title
		actualTitle = driver.getTitle();
    
		//click to find a workshop
		driver.findElement(By.partialLinkText("Find a Workshop")).click();  

    
		//Verify title ¡°Find WW Studios & Meetings Near You | WW USA¡±
		expectedTitle = "Find WW Studios & Meetings Near You | WW USA";
		actualTitle = driver.getTitle();
    
		verifyText(expectedTitle, actualTitle);
   
		waitForPageLoad(driver, "//*[@id=\"location-search\"]", 30);
    
		//In the search field, search for meetings for zip code: 10011 
		driver.findElement(By.id("location-search")).click();
		//wait.until(ExpectedConditions.stalenessOf(element));
    
		driver.findElement(By.id("location-search")).sendKeys("10011");
		driver.findElement(By.id("location-search-cta")).click();
    
		waitForPageLoad(driver, "//*[@id=\"search-results\"]", 30);

		driver.findElement(By.xpath("/html/body/div[1]/div[3]/div[2]/div[2]/div/div[1]/div[1]/a/div[1]/div/a")).click();
    
		//WebDriverWait waitFirstSearchItems = new WebDriverWait(driver, 30);
		//By firstSearchItems = By.xpath("html/body/div[1]/div[3]/div[1]/div[2]/div[3]/div[1]/span");
		//waitFirstSearchItems.until(ExpectedConditions.presenceOfElementLocated(firstSearchItems));
    
		waitForPageLoad(driver, "html/body/div[1]/div[3]/div[1]/div[2]/div[3]/div[1]/span", 30);

		///html/body/div[1]/div[3]/div[1]/div[2]/div[3]
		String spanText = driver.findElement(By.xpath("/html/body/div[1]/div[3]/div[1]/div[2]/div[3]")).getText();


		String[] dayOfWeek= {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
	  
	  
		String[] scheduleStr = spanText.split("\n");
	  

		ArrayList<String> dateS = new ArrayList<>();
	  
		int i =0; 
	  
		for (String s : scheduleStr) 
		{ 
			String str = s.trim();
	  	  
			if (checkStringInArray(dayOfWeek, str)) 
			{			  
				dateS.add(str);
				i+=1;
				
				//System.out.println("dayOfWeek str: " + str);
			}
			else
			{
				String skey = dateS.get(i-1);
				
				if (str.contains("AM") || str.contains("PM"))
				{  
				  personSchedule.computeIfAbsent(skey + ":Time", k -> new ArrayList<>()).add(str);
				}
				else if (str.substring(str.length()-1).equals("."))
				{
				  personSchedule.computeIfAbsent(skey + ":Person", k -> new ArrayList<>()).add(str);
				}
			  
				//System.out.println("str: " + str);

			}

		}
	  
		System.out.println("personSchedule: " + personSchedule);
	  

		for (String dayW : dayOfWeek)
		{
			System.out.println("Print Meeting for Day of Week : " + dayW );
			printMeetings(dayW);
		}
	  
		//close chrome
		driver.close();
	}

}
