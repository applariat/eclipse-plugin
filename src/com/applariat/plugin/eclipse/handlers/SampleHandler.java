package com.applariat.plugin.eclipse.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;

public class SampleHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);		

		// read config information
		RedeployData rdd=RedeployData.readRedeployDataFromFile(window);
		if (rdd==null) { return null; }
	
		// first read the data from the deploy config dialog
		MyTitleAreaDialog dialog = new MyTitleAreaDialog(window.getShell());
		dialog.create();
		dialog.open();		
		if (dialog.isCancelPressed()) { return null; }
		
		if (dialog.getErrorMessage()!=null && dialog.getErrorMessage().length()>1) {
			MessageDialog.openInformation(
					window.getShell(),
					"appLariat",
					"Error In Input Data:  "+dialog.getErrorMessage());		
			return null;
		}
		
		ProgressBar pb = new ProgressBar(rdd, dialog.isSilentDeploy());
	    ProgressMonitorDialog pmd = new ProgressMonitorDialog(window.getShell());
	    try {
	    	pmd.run(true, true, pb);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
	    if (!pb.isError()) {
	    	if (!dialog.isSilentDeploy()) {
	    		if (pb.getIps().keySet().size()>0) {
	    			StringBuffer ipMessage = new StringBuffer("");
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
						"Application is being deployed. You can get the URL later from the appLariat Menu.");			
	    	}
	    } else {
	    	MessageDialog.openInformation(
					window.getShell(),
					"appLariat",
					"Error deploying new artifact to Deployment "+rdd.getDeployData().getName()+". "+pb.getErrorMessage());					
	    }

		return null;
	}	
}
