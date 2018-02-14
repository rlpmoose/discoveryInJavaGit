package edu.osu.cse.trialProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.ibm.watson.developer_cloud.discovery.v1.Discovery;
import com.ibm.watson.developer_cloud.discovery.v1.model.collection.GetCollectionRequest;
import com.ibm.watson.developer_cloud.discovery.v1.model.collection.GetCollectionResponse;
import com.ibm.watson.developer_cloud.discovery.v1.model.environment.CreateEnvironmentRequest;
import com.ibm.watson.developer_cloud.discovery.v1.model.environment.CreateEnvironmentResponse;
import com.ibm.watson.developer_cloud.discovery.v1.model.environment.GetEnvironmentRequest;
import com.ibm.watson.developer_cloud.discovery.v1.model.environment.GetEnvironmentResponse;
import com.ibm.watson.developer_cloud.discovery.v1.model.query.QueryRequest;
import com.ibm.watson.developer_cloud.discovery.v1.model.query.QueryResponse;
import com.ibm.watson.developer_cloud.http.ServiceCall;
import com.ibm.watson.developer_cloud.personality_insights.v3.PersonalityInsights;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.Profile;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.ProfileOptions;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	boolean tryAgain = true;
    	
    	
    	String keywords;
    	System.out.println("Please describe your emergency:");
    	Scanner in = new Scanner(System.in);
    	keywords = in.nextLine();
    	ArrayList<String> searchItems = parseKeywords(keywords);
    	ArrayList<String> searchResults = new ArrayList<String>();
    	//in.close();  don't close here if you want to use input ever again
    	   	
    	Discovery discovery = new Discovery("2017-11-07");
    	discovery.setEndPoint("https://gateway.watsonplatform.net/discovery/api/");
    	discovery.setUsernameAndPassword("85ab1b77-932c-4260-b095-2f58d6845d1c", "wUhiQzEYUqM5");
    	
    	
    	String environmentName = "Preparedness Files";
    	String environmentDesc = "Preparedness Files";

    	String environmentId = "67fe2745-24f3-426e-9536-c02db4819770";

    	GetEnvironmentRequest getRequest = new GetEnvironmentRequest.Builder(environmentId).build();
    	GetEnvironmentResponse getResponse = discovery.getEnvironment(getRequest).execute();
    	
    	String collectionId = "3842edf9-d906-444d-92e5-96dadb04f8e5";

    	GetCollectionRequest getCollRequest = new GetCollectionRequest.Builder(environmentId, collectionId).build();
    	GetCollectionResponse getCollResponse = discovery.getCollection(getCollRequest).execute();
    

    	QueryRequest.Builder queryBuilder = new QueryRequest.Builder(environmentId, collectionId); //can change query type here
    	
    	QueryResponse queryResponse;
    	
    	for(int i = 0; i<searchItems.size(); i++) {
    		System.out.println("Querying database for ..." + searchItems.get(i));
        	queryBuilder.query("enriched_text.concepts.text:" +searchItems.get(i));
        	queryResponse = discovery.query(queryBuilder.build()).execute();
        	String result = queryResponse.toString();
        	if(queryResponse.getResults().size()==0) {
        		//do nothing
        	}
        	else {
        		tryAgain = false;
        	
        		if(result.length()>500) {
        			searchResults.add(result);
        			//searchResults.add(result.substring(0, 500));
        		}
        		else {
        			searchResults.add(result);
        		}
        	}
        	
    	}
    	
    	System.out.println("Parsing results........");
    	ArrayList<ArrayList<String>> combined = parseFileName(searchResults.toString());
    	ArrayList<String> filenames = (ArrayList) combined.get(0);
    	ArrayList<String> fileIndices = (ArrayList) combined.get(1);
    	
    	if(filenames.size()==0) {
    		tryAgain = true;
    	}
    	else {
    		System.out.println("Observations match results for the following emergencies: ");
    	for(int i = 0; i<filenames.size(); i++) {
    		System.out.println((i+1) + "." + filenames.get(i));
    	}
    	System.out.print("Enter number of emergency you would like to get further help with, or choose 0 to try another entry: ");
    	
    	
    
    	int numberChoice;
    	if(in.hasNextLine()) {
    		numberChoice = Integer.parseInt(in.nextLine());
    	}
    	else {
    		System.out.println("Automatically choosing the first one because I can't get it to read the integer...");
    		numberChoice = 1;
    	}
    
    	
    	
    	if((numberChoice > filenames.size()) || (numberChoice<0)) {
    		System.out.println("You failed to follow basic instructions. Natural selection for you.");
    		System.exit(0);
    	}
    	else if(numberChoice ==0) {
    		tryAgain = true;
    	}
    	else {
    		int why1 = Integer.parseInt(fileIndices.get(numberChoice -1));
    		int why2 = Integer.parseInt(fileIndices.get(numberChoice));
    		//System.out.println("Passing start point " + why1 + "and end point" + why2);
    		ArrayList<String> finalResults = parseSentenceNotes(searchResults.toString(), why1, why2);
    		System.out.println("Printing results........");
        	System.out.println(finalResults.toString());
    	}
    }
    	
    	while(tryAgain) {
     	
			System.out.println("No acceptable results found; Please tell us more: ");
	    	keywords = in.nextLine();
	    	searchItems = parseKeywords(keywords);
        	searchResults = new ArrayList<String>();
	    	for(int i = 0; i<searchItems.size(); i++) {
	    		System.out.println("Querying database for ..." + searchItems.get(i));
	        	queryBuilder.query("enriched_text.concepts.text:" +searchItems.get(i));
	        	queryResponse = discovery.query(queryBuilder.build()).execute();
	        	String result = queryResponse.toString();
	        	if(queryResponse.getResults().size()==0) {
	        		//do nothing
	        	}
	        	else {
	        		tryAgain = false;
	        	
	        		if(result.length()>500) {
	        			searchResults.add(result);
	        			//searchResults.add(result.substring(0, 500));
	        		}
	        		else {
	        			searchResults.add(result);
	        		}
	        	}
	        	
	    	}
	    	
	    	System.out.println("Parsing results........");
	    	combined = parseFileName(searchResults.toString());
	    	filenames = (ArrayList) combined.get(0);
	    	fileIndices = (ArrayList) combined.get(1);
	    	if(filenames.size()==0) {
	    		tryAgain = true;
	    	}
	    	else {
	    	System.out.println("Observations match results for the following emergencies: ");
	    	for(int i = 0; i<filenames.size(); i++) {
	    		System.out.println((i+1) + "." + filenames.get(i));
	    	}
	    	System.out.print("Enter number of emergency you would like to get further help with, or choose 0 to try another entry: ");
	    	
	    	
	    
	    	int numberChoice2;
	    	if(in.hasNextLine()) {
	    		numberChoice2 = Integer.parseInt(in.nextLine());
	    	}
	    	else {
	    		System.out.println("Automatically choosing the first one because I can't get it to read the integer...");
	    		numberChoice2 = 1;
	    	}
	    	
	    	
	    	
	    	if((numberChoice2 > filenames.size()) || (numberChoice2<0)) {
	    		System.out.println("You failed to follow basic instructions. Natural selection for you.");
	    		System.exit(0);
	    	}
	    	else if(numberChoice2 ==0) {
	    		tryAgain = true;
	    	}
	    	else {
	    		int why1 = Integer.parseInt(fileIndices.get(numberChoice2 -1));
	    		int why2 = Integer.parseInt(fileIndices.get(numberChoice2));
	    		//System.out.println("Passing start point " + why1 + "and end point" + why2);
	    		ArrayList<String> finalResults = parseSentenceNotes(searchResults.toString(), why1, why2);
	    		System.out.println("Printing results........");
	        	System.out.println(finalResults.toString());
	    	}
	    	}
    	}
   in.close();
       }
    
    private static ArrayList<String> parseKeywords(String keywords) {
    	ArrayList<String> keywordsArray = new ArrayList<String>();
    	System.out.println("Parsing input........");
    	int space = 0;
    	
    	while(space>=0) {
    		//System.out.println("Space = " + space);
    		space = keywords.indexOf(' ',1);
    		
    		//System.out.println("Space = " + space);
    		if(space < 0) {
    			keywordsArray.add(keywords);
    		}
    		else {
    			String newSentence = keywords.substring(0, space);
    			keywordsArray.add(newSentence);
    			
    			
    			keywords = keywords.substring(space);
    		}
    	}
    	return keywordsArray;
    		
    }
    
    private static ArrayList<String> parseSentenceNotes(String results, int startPoint, int endPoint) {
    	ArrayList<String> sentences = new ArrayList<String>();
    	System.out.println("Parsing results........");
    	int indexSentence = 0;
    	//System.out.println("Space = " + indexSentence);
		indexSentence = results.indexOf("\"sentence\":", startPoint);
		
		//System.out.println("Space = " + indexSentence);
		if(indexSentence < 0) {
			//do nothing
		}
		else {
			int newLine = results.indexOf("\n", indexSentence);
			String newSentence = results.substring(indexSentence+13, newLine-3);
			newSentence = '\n' + newSentence;
			sentences.add(newSentence);
			//System.out.println("Added "+ newSentence + " to results.");
			
			
			results = results.substring(newLine);
			endPoint = endPoint - newLine;
			
		}
    	
    	while(indexSentence>=0) {
    		//System.out.println("Space = " + indexSentence);
    		indexSentence = results.indexOf("\"sentence\":", 1);
    		
    		//System.out.println("Space = " + indexSentence);
    		if(indexSentence < 0 || indexSentence > endPoint) {
    			indexSentence = -1;
    		}
    		else {
    			int newLine = results.indexOf("\n", indexSentence);
    			String newSentence = results.substring(indexSentence+13, newLine-3);
    			newSentence = '\n' + newSentence;
    			if(sentences.contains(newSentence)) {
    				//do not add duplicate
    			}
    			else {
    				sentences.add(newSentence);
    			}
    			//System.out.println("Added "+ newSentence + " to results.");
    			
    			
    			results = results.substring(newLine);
    			endPoint = endPoint - newLine;
    			
    		}
    	}
    	return sentences;
    		
    }
    
    private static ArrayList<ArrayList<String>> parseFileName(String results) {
    	ArrayList<String> fileNames = new ArrayList<String>();
    	ArrayList<String> fileIndices = new ArrayList<String>();
    	int originalSize = results.length();
    	int elapsedLength = 0;
    	System.out.println("Parsing results........");
    	int index = 0;
    	
    	while(index>=0) {
    		//System.out.println("Space = " + index);
    		index = results.indexOf("\"filename\": ", 1);
    		
    		//System.out.println("Space = " + index);
    		if(index < 0) {
    			//do nothing
    		}
    		else {
    			int newLine = results.indexOf("\n", index);
    			String fileName = results.substring(index+13, newLine-7);
    			fileNames.add(fileName);
    			int hold = index+ elapsedLength;
    			fileIndices.add(""+ hold);
    			//System.out.println("Added "+ fileName + " to possible files and index " + index);
    			
    			elapsedLength = elapsedLength + newLine;
    			results = results.substring(newLine);
    			
    		}
    	}
    	fileIndices.add("" +originalSize);
    	ArrayList<ArrayList<String>> returnList = new ArrayList<ArrayList<String>>();
    	returnList.add(fileNames);
    	returnList.add(fileIndices);
    	return returnList;
    		
    }
}
