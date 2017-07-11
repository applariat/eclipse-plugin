package com.applariat.plugin.eclipse.handlers;

// Written by: Mazda Marvasti, AppLariat Corp.

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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
	
		RetrieveConfigData rcd = new RetrieveConfigData();
	    ProgressMonitorDialog pmd = new ProgressMonitorDialog(window.getShell());
	    try {
	    	pmd.run(true, true, rcd);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		if (!rcd.isError()) {
			List<String> artifactData = new ArrayList<String>();
			artifactData.add("GitHub");
//			artifactData.add("BitBucket");
			
			MyComboAreaDialog dialog = new MyComboAreaDialog(window.getShell(), rcd.getReleases(), artifactData, rcd.getDeployLocList());
			dialog.create();
			dialog.open();		
			
			if (dialog.getErrorMessage()!=null && dialog.getErrorMessage().length()>1) {
				MessageDialog.openInformation(
						window.getShell(),
						"appLariat",
						"Error In Input Data:  "+dialog.getErrorMessage());		
				return null;
			}
			
			// now do the first deploy of the application with the override
			ConfigValidateProgressBar cvpb = new ConfigValidateProgressBar(dialog.getInitReleaseData(), rcd.getJwtToken());
			ProgressMonitorDialog pmd1 = new ProgressMonitorDialog(window.getShell());
		    try {
		    	pmd1.run(true, true, cvpb);
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		    
			AppLariatEclipsePlugin aep = new AppLariatEclipsePlugin();
			File configFile = aep.getStateLocation().append(aep.getFilename()).toFile();
			RedeployData redeployData = cvpb.getRedeployData();
			
			try (
			    OutputStream file = new FileOutputStream(configFile);
			    OutputStream buffer = new BufferedOutputStream(file);
			    ObjectOutput output = new ObjectOutputStream(buffer);
			){
			    output.writeObject(redeployData);
			}  catch(IOException ex){
				MessageDialog.openInformation(
						window.getShell(),
						"appLariat",
						"Unable to write config data :  "+ex.toString());
				return null;
			}

			MessageDialog.openInformation(
					window.getShell(),
					"appLariat",
					"Config Data Saved:  Deploy Name = "+redeployData.getDeployData().getName()+",   Artifact to Replace = "+redeployData.getArtifactData().getName());
			
		} else {
			MessageDialog.openInformation(
					window.getShell(),
					"appLariat",
					"Error retrieving info:  "+rcd.getErrorMessage());					
	    }
		
		return null;
	}	
}
