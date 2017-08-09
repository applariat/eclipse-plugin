package com.applariat.plugin.eclipse.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class LogoutHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);		
		
		// read config information
		RedeployData rdd = RedeployData.readRedeployDataFromFile(window);
		if (rdd==null) { return null; }						
		
		rdd.setAuthToken(null);
		
		RedeployData.writeRedeployDataToFile(rdd, window);
		return null;
	}	
}
