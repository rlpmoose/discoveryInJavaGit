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
    	Scanner in = new Scanner(System.in);
    	String keyword;
    	System.out.println("Please enter what you are searching for:");
    	keyword = in.nextLine();
    	in.close();
    	   	
    	Discovery discovery = new Discovery("2017-11-07");
    	discovery.setEndPoint("https://gateway.watsonplatform.net/discovery/api/");
    	discovery.setUsernameAndPassword("85ab1b77-932c-4260-b095-2f58d6845d1c", "wUhiQzEYUqM5");
    	
    	
    	String environmentName = "Preparedness Files";
    	String environmentDesc = "Preparedness Files";

    	String environmentId = "67fe2745-24f3-426e-9536-c02db4819770";

    	GetEnvironmentRequest getRequest = new GetEnvironmentRequest.Builder(environmentId).build();
    	GetEnvironmentResponse getResponse = discovery.getEnvironment(getRequest).execute();
    	
    	String collectionId = "5a8979c1-e508-4da1-a420-fecdbd327620";

    	GetCollectionRequest getCollRequest = new GetCollectionRequest.Builder(environmentId, collectionId).build();
    	GetCollectionResponse getCollResponse = discovery.getCollection(getCollRequest).execute();
    

    	QueryRequest.Builder queryBuilder = new QueryRequest.Builder(environmentId, collectionId); //can change query type here
    	
    	
    	queryBuilder.query(keyword);
    	QueryResponse queryResponse = discovery.query(queryBuilder.build()).execute();
    	System.out.println(queryResponse.getResults().toString());
    	
    	
    	
    	/*CreateEnvironmentRequest.Builder createRequestBuilder = new CreateEnvironmentRequest.Builder(environmentName, CreateEnvironmentRequest.Size.ONE);
    	createRequestBuilder.description(environmentDesc);
    	CreateEnvironmentResponse createResponse = discovery.createEnvironment(createRequestBuilder.build()).execute();
    	*/
       }
}
