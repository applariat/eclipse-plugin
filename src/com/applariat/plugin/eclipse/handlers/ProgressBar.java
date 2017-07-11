package com.applariat.plugin.eclipse.handlers;

// Written by: Mazda Marvasti, AppLariat Corp.

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor; 
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser; 

public class ProgressBar implements IRunnableWithProgress { 
    
	private RedeployData rdd;
	private boolean silentDeploy;
	
	public ProgressBar(RedeployData rdd, boolean silentDeploy) {
		this.rdd=rdd;
		this.silentDeploy=silentDeploy;
	} 
	
	private HashMap<String,String> ips = new HashMap<String,String>(); // key = service name, value=url
	boolean error = false;
	String errorMessage = null;
	
	
	public HashMap<String, String> getIps() {
		return ips;
	}

	public void setIps(HashMap<String, String> ips) {
		this.ips = ips;
	}

	public void addIp(String serviceName, String url) {
		this.ips.put(serviceName,  url);
	}
	
	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
	public RedeployData getRdd() {
		return rdd;
	}

	public void setRdd(RedeployData rdd) {
		this.rdd = rdd;
	}

	public boolean isSilentDeploy() {
		return silentDeploy;
	}

	public void setSilentDeploy(boolean silentDeploy) {
		this.silentDeploy = silentDeploy;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
	    String baseUrl="$APL_API_BASE_URL";
        try { 
        	JSONParser parser = new JSONParser();
        	
        	monitor.beginTask("Deploying Application", 100); 
        
        	monitor.subTask("Connecting to appLariat API Service ...");
        	baseUrl = UrlCalls.replaceApiHostPlaceholder(baseUrl);
        	String jwtToken = UrlCalls.urlConnectRequestToken();
        	if (jwtToken==null || jwtToken.length()<2) {
        		setError(true);
        		setErrorMessage("Unable to aquire authorization to access the API. Please check your credentials or connection.");
        		monitor.done(); 
        		return;
        	}
        	monitor.worked(5); 
 
        	monitor.subTask("Deploying application ...");
          	String query = "{\"data\": {\"command\": \"override\",\"components\": [{\"stack_component_id\": \""+rdd.getArtifactData().getStackComponentId()+"\",\"services\": [{\"component_service_id\": \""+rdd.getArtifactData().getComponentServiceId()+"\",\"build\": {\"buildvars\": [{\"key\": \"REBUILD_NUM\", \"value\":\""+rdd.getNewBuildHash()+"\"}]}}]}]}}";
          	String deployData = UrlCalls.urlConnectPut(baseUrl+"/deployments/"+rdd.getDeployData().getId(), query, jwtToken);
          	if (deployData==null) {
          		setError(true);
          		setErrorMessage("Unable to find existing deployment.");
          		monitor.done(); 
      			return;
          	}
          	JSONObject jo1 = (JSONObject)parser.parse(deployData);
          	if (jo1==null || jo1.get("data")==null) {
          		setError(true);
          		setErrorMessage("New artifact deployment failed. Please check the application at http://www.applariat.io");
          		monitor.done(); 
      			return;
          	}

      		monitor.worked(20);
      		
      		if (!isSilentDeploy()) {
      			// now wait until you get confirmation that the app was redeployed
      			monitor.subTask("Waiting to get validation (can take some time)..."); 
      			boolean finished = false;
      			int count=0;
      			do {      	
      				Thread.sleep(20000);
      				count++;
      				deployData = UrlCalls.urlConnectGet(baseUrl+"/deployments/"+rdd.getDeployData().getId(),"", jwtToken);
      				if (deployData!=null) {
      					jo1 = (JSONObject)parser.parse(deployData);
      					if(((JSONObject)jo1.get("data")).toJSONString().length()>5) {
      						JSONObject jo2 = (JSONObject)jo1.get("data");
      						String state = (String)((JSONObject)jo2.get("status")).get("state");

      						if (state.equalsIgnoreCase("running")) {
      							finished=true;
      						} else if (state.equalsIgnoreCase("failed")) {
      							setError(true);
      							setErrorMessage("The new artifact deployment failed.");
      							monitor.done(); 
      							return;
      						}
      					}
      				}
      			
      			} while (!finished && count<25);
      		
      			if (!finished) {
      				setError(true);
      				setErrorMessage("Unable to validate new artifact deployment. Please check https://www.applariat.io");
      				monitor.done(); 
      				return;
      			}
      			monitor.worked(20);
      		
      			monitor.subTask("Retrieving application URL ..."); 
      			JSONArray components = (JSONArray)((JSONObject)((JSONObject)jo1.get("data")).get("status")).get("components");
      			for (int i=0; i<components.size(); i++) {
      				JSONArray services = (JSONArray)((JSONObject)components.get(i)).get("services");
      				for (int j=0; j<services.size(); j++) {
      					if (((JSONObject)services.get(j)).get("ips")!=null) {
      						JSONArray ipArray = (JSONArray)((JSONObject)services.get(j)).get("ips");
      						String serviceName = (String)((JSONObject)services.get(j)).get("name");
      						if (ipArray.size()>1) {
      							for (int k=0; k<ipArray.size(); k++) {
      								addIp(serviceName+"-"+k, (String)(ipArray.get(k)));
      							}
      						} else {
      							addIp(serviceName, (String)(ipArray.get(0)));
      						}
      					}
      				}
      			}
      		}
          	
      		monitor.worked(15); 
      		
      		monitor.done(); 
      		
        } catch (Exception e) { 
        	setError(true);
        	setErrorMessage("An error occured during deployment: "+e.toString());
        	throw new InvocationTargetException(e); 
        } 
  }   
}