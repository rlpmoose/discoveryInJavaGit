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
    	in.close();
    	   	
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
        	String result = queryResponse.getResults().toString();
        	if(queryResponse.getResults().size()==0) {
        		//do nothing
        	}
        	else {
        		tryAgain = false;
        	
        		if(result.length()>500) {
        			searchResults.add(result.substring(0,  500));
        		}
        		else {
        			searchResults.add(result);
        		}
        	}
        	
    	}
    	
    	while(tryAgain) {
    			System.out.println("No results found on current situation description, please tell us more: ");
    			in = new Scanner(System.in);
    	    	keywords = in.nextLine();
    	    	in.close();
    	    	
    	    	for(int i = 0; i<searchItems.size(); i++) {
    	    	System.out.println("Querying database for ..." + searchItems.get(i));
            	queryBuilder.query("enriched_text.concepts.text:" +searchItems.get(i));
            	queryResponse = discovery.query(queryBuilder.build()).execute();
            	String result = queryResponse.getResults().toString();
            	if(queryResponse.getResults().size()==0) {
            		//do nothing
            	}
            	else {
            		tryAgain = false;
            	
            		if(result.length()>500) {
            			searchResults.add(result.substring(0,  500));
            		}
            		else {
            			searchResults.add(result);
            		}
            	}
    	    	}
    		
    	}
    	System.out.println(searchResults.toString());
       }
    
    private static ArrayList<String> parseKeywords(String keywords) {
    	ArrayList<String> keywordsArray = new ArrayList<String>();
    	System.out.println("Parsing input........");
    	int space = 0;
    	
    	while(space>=0) {
    		System.out.println("Space = " + space);
    		space = keywords.indexOf(' ',1);
    		
    		System.out.println("Space = " + space);
    		if(space < 0) {
    			keywordsArray.add(keywords);
    		}
    		else {
    			keywordsArray.add(keywords.substring(0, space));
    			
    			keywords = keywords.substring(space);
    		}
    	}
    	return keywordsArray;
    		
    }
}
