package com.applariat.plugin.eclipse.handlers;

// Written by: Mazda Marvasti, AppLariat Corp.

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class UrlHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);		

		// read config information
		AppLariatEclipsePlugin aep = new AppLariatEclipsePlugin();
		File configFile = aep.getStateLocation().append(aep.getFilename()).toFile();
		RedeployData rdd=null;
		if (configFile.exists()) {
			try(
				InputStream file = new FileInputStream(configFile);
			    InputStream buffer = new BufferedInputStream(file);
			    ObjectInput input = new ObjectInputStream (buffer);
			    ){
			      //deserialize the List
			      rdd = (RedeployData)input.readObject();
			} catch(Exception e){
				MessageDialog.openInformation(window.getShell(),"appLariat","Error reading config information. "+e.toString());
				return null;
			}
		} else {
			MessageDialog.openInformation(window.getShell(),"appLariat","Please configure deployment first through the Config menu. ");
			return null;			
		}
		
		ProgressBarUrl pb = new ProgressBarUrl(rdd);
	    ProgressMonitorDialog pmd = new ProgressMonitorDialog(window.getShell());
	    try {
	    	pmd.run(true, true, pb);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
	    if (!pb.isError()) {
	    	if (pb.getIps().keySet().size()>0) {
	    		StringBuffer ipMessage = new StringBuffer("Application last updated "+pb.getLastUpdateTimeLocal()+" ago.\n");
	    		if (pb.getIps().keySet().size()>1) {
	    			for (String key: pb.getIps().keySet()) {
	    				ipMessage.append("Click <a href=\"http://"+pb.getIps().get(key)+"\">here</a> to access the service "+ key+".<br>");
	    			}	
	    		} else {
	    			ipMessage.append("Click <a href=\"http://"+pb.getIps().values().toArray()[0]+"\">here</a> to access your application.");
	    		}
	    		MyMessageDialog.openInformation(
	    			window.getShell(),
	    			"appLariat",
	    			ipMessage.toString());
	    	} else {
	    		MessageDialog.openInformation(
					window.getShell(),
					"appLariat",
					"Artifact is now deployed to "+rdd.getDeployData().getName()+". ");					
	    	}
	    } else {
	    	MessageDialog.openInformation(
					window.getShell(),
					"appLariat",
					"Error retrieving application URL: \n"+pb.getErrorMessage());					
	    }

		return null;
	}	
}
