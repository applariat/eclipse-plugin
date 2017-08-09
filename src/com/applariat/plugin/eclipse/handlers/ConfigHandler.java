package com.applariat.plugin.eclipse.handlers;

// Written by: Mazda Marvasti, AppLariat Corp.

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class ConfigHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);		
		
		RedeployData rdd = RedeployData.readRedeployDataFromFileForAuth();
		boolean needCredentials = false;
		if (rdd!=null && rdd.getAuthToken()!=null) { // now get the token for this auth
			RedeployData.initToken(rdd);
			if (rdd.getJwtToken()==null) { // username/password is invalid
				needCredentials = true;										
			}
		} else { // need to ask for username and password
			needCredentials=true;
		}

		if (needCredentials) {
			do {
				MyLoginAreaDialog lDialog = new MyLoginAreaDialog(window.getShell());
				lDialog.create();
				lDialog.open();
				if (lDialog.isCancelPressed()) { return null; }
				rdd = lDialog.getRedeployData();
			} while(rdd.getJwtToken()==null);
		}
		
		RetrieveConfigData rcd = new RetrieveConfigData(rdd);
	    ProgressMonitorDialog pmd = new ProgressMonitorDialog(window.getShell());
	    try {
	    	pmd.run(true, true, rcd);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		if (!rcd.isError()) {
			MyComboAreaDialog dialog = new MyComboAreaDialog(window.getShell(), rcd.getReleases(), rcd.getDeployLocList(), rcd.getArtifactLocSelectionList(), rdd.getApiUrl(), rdd.getJwtToken());
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
			
			// now do the first deploy of the application with the override
			ConfigValidateProgressBar cvpb = new ConfigValidateProgressBar(dialog.getInitReleaseData(), rdd.getApiUrl(), rdd.getJwtToken());
			ProgressMonitorDialog pmd1 = new ProgressMonitorDialog(window.getShell());
		    try {
		    	pmd1.run(true, true, cvpb);
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		    
			RedeployData redeployData = cvpb.getRedeployData();
			redeployData.setAuthToken(rdd.getAuthToken());
			redeployData.setJwtToken(rdd.getJwtToken());
			redeployData.setApiUrl(rdd.getApiUrl());
			RedeployData.writeRedeployDataToFile(redeployData, window);

			MessageDialog.openInformation(
					window.getShell(),
					"appLariat",
					"Currently deploying the application. Check it's status by going to the 'App URL' menu.");
			
		} else {
			MessageDialog.openInformation(
					window.getShell(),
					"appLariat",
					"Error retrieving info:  "+rcd.getErrorMessage());					
	    }
		
		return null;
	}	
}
