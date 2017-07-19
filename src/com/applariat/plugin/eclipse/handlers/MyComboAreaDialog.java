package com.applariat.plugin.eclipse.handlers;

// Written by: Mazda Marvasti, AppLariat Corp.

import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MyComboAreaDialog extends TitleAreaDialog {

    private Combo comboArtifactName;
    private Combo comboReleaseName;
    private Combo comboDeployLocationName;
    private Combo comboArtifactLocationName;    
    private Text txtRepositoryOwner;
    private Text txtRepositoryName;
    private Text txtRepositoryBranch;

    private InitialReleaseData initReleaseData = new InitialReleaseData();
    private boolean cancelPressed = false;

    private List<ReleaseData> comboData;
    private List<String> artifactLocationNameList;
    private List<DeployLocationData> deployLocationList;

    public MyComboAreaDialog(Shell parentShell, List<ReleaseData> releaseData, List<String> artLocName, List<DeployLocationData> dll) {
        super(parentShell);
        this.comboData = releaseData;
        this.artifactLocationNameList = artLocName;
        this.deployLocationList=dll;
    }
    
    public void create() {
        super.create();
        setTitle("appLariat Application Configuration");
        setMessage("Configure your app for continous deployment...", IMessageProvider.INFORMATION);
    }

    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        createReleaseName(container);
        createArtifactName(container);
        createDeployLocationName(container);        
        createArtifactLocName(container);
        
        createRepositoryOwner(container);
        createRepositoryName(container);
        createRepositoryBranch(container);
        
        return area;
    }

    private void createRepositoryOwner(Composite container) {
        Label lbtRepOwnerName = new Label(container, SWT.NONE);
        lbtRepOwnerName.setText("Repository Owner");

        GridData dataRepOwner = new GridData();
        dataRepOwner.grabExcessHorizontalSpace = true;
        dataRepOwner.horizontalAlignment = GridData.FILL;

        txtRepositoryOwner = new Text(container, SWT.BORDER);
        txtRepositoryOwner.setLayoutData(dataRepOwner);
    }
    
    private void createRepositoryName(Composite container) {
        Label lbtRepName = new Label(container, SWT.NONE);
        lbtRepName.setText("Repository Name");

        GridData dataRepName = new GridData();
        dataRepName.grabExcessHorizontalSpace = true;
        dataRepName.horizontalAlignment = GridData.FILL;

        txtRepositoryName = new Text(container, SWT.BORDER);
        txtRepositoryName.setLayoutData(dataRepName);
    }
    
    private void createRepositoryBranch(Composite container) {
        Label lbtRepBranchName = new Label(container, SWT.NONE);
        lbtRepBranchName.setText("Repository Branch");

        GridData dataRepBranch = new GridData();
        dataRepBranch.grabExcessHorizontalSpace = true;
        dataRepBranch.horizontalAlignment = GridData.FILL;

        txtRepositoryBranch = new Text(container, SWT.BORDER);
        txtRepositoryBranch.setLayoutData(dataRepBranch);
    }
    
    private void createReleaseName(Composite container) {
        Label lbtDeployName = new Label(container, SWT.NONE);
        lbtDeployName.setText("App. Release Name");

        GridData dataDeployName = new GridData();
        dataDeployName.grabExcessHorizontalSpace = true;
        dataDeployName.horizontalAlignment = GridData.FILL;

        comboReleaseName = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
        comboReleaseName.setLayoutData(dataDeployName);
        // at some point I should sort these values first before displaying them
        for (ReleaseData dd: comboData) {
        	comboReleaseName.add(dd.getName());
        }
        comboReleaseName.select(0);
        
        comboReleaseName.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent event) {
        		String firstSelection = comboReleaseName.getText();
        		ReleaseData ddSelected = null;
        	        
        	    for (ReleaseData st : comboData) {
        	    	if (st.getName().equals(firstSelection)) {
        	    		ddSelected = st;
        	     	break;
        	       	}
        	    }
                comboArtifactName.removeAll();
                for (ArtifactData ad: ddSelected.getArtifacts()) {
                	comboArtifactName.add(ad.getComponentName()+" ; "+ad.getName());
                }
                comboArtifactName.select(0);
        	}
        });
    }
    private void createArtifactName(Composite container) {
        Label lbtArtifactName = new Label(container, SWT.NONE);
        lbtArtifactName.setText("Artifact to Replace");

        GridData dataArtifactName = new GridData();
        dataArtifactName.grabExcessHorizontalSpace = true;
        dataArtifactName.horizontalAlignment = GridData.FILL;
        comboArtifactName = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
        comboArtifactName.setLayoutData(dataArtifactName);
        
        List<ArtifactData> artifacts=null;
        String firstSelection = comboReleaseName.getText();
        for (ReleaseData dd : comboData) {
        	if (dd.getName().equals(firstSelection)) {
        		artifacts = dd.getArtifacts();
        		break;
        	}
        }
        for (ArtifactData ad: artifacts) {
        	comboArtifactName.add(ad.getComponentName()+" ; "+ad.getName());
        }
        comboArtifactName.select(0);
    }

    private void createDeployLocationName(Composite container) {
        Label lbtDeployLocName = new Label(container, SWT.NONE);
        lbtDeployLocName.setText("Deploy Location");

        GridData dataDeployLocName = new GridData();
        dataDeployLocName.grabExcessHorizontalSpace = true;
        dataDeployLocName.horizontalAlignment = GridData.FILL;
        comboDeployLocationName = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
        comboDeployLocationName.setLayoutData(dataDeployLocName);
        
        // at some point I should sort these values first before displaying them
        for (DeployLocationData dld: deployLocationList) {
        	comboDeployLocationName.add(dld.getName());
        }
        comboDeployLocationName.select(0);        
    }
    
    private void createArtifactLocName(Composite container) {
        Label lbtArtifactLocName = new Label(container, SWT.NONE);
        lbtArtifactLocName.setText("Location of New Code:");

        GridData dataArtifactLocName = new GridData();
        dataArtifactLocName.grabExcessHorizontalSpace = true;
        dataArtifactLocName.horizontalAlignment = GridData.FILL;
        comboArtifactLocationName = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
        comboArtifactLocationName.setLayoutData(dataArtifactLocName);
        
        for (String locTuple: artifactLocationNameList) {
        	comboArtifactLocationName.add(locTuple);
        }
        comboArtifactLocationName.select(0);
    }
    
    protected boolean isResizable() {
        return true;
    }

    private void saveInput() {
        String tmpReleaseName = comboReleaseName.getText();
        String tmpComponentName = comboArtifactName.getText().split(";")[0].trim();
        String tmpArtifactName = comboArtifactName.getText().split(";")[1].trim();
        String tmpArtifactLocName = comboArtifactLocationName.getText(); 
        String tmpDeployLocName = comboDeployLocationName.getText();  
        
        for (ReleaseData dd: comboData) {
        	if (dd.getName().equals(tmpReleaseName)) {
        		for (ArtifactData ad: dd.getArtifacts()) {
        			if (ad.getComponentName().equals(tmpComponentName) && ad.getName().equals(tmpArtifactName)) {
        				initReleaseData.setRelease(dd);        
        				initReleaseData.setArtifact(ad);
        				break;
        			}
        		}
        		break;
        	}
        }
        for(String st: artifactLocationNameList) {
        	if (st.equals(tmpArtifactLocName)) {
        		initReleaseData.setNewArtifactLocation(st);
        		break;
        	}
        }
        
        for(DeployLocationData dld: deployLocationList) {
        	if (dld.getName().equals(tmpDeployLocName)) {
        		initReleaseData.setDeployLoc(dld);
        		break;
        	}
        }
        
        initReleaseData.setRepositoryOwner(txtRepositoryOwner.getText());
        initReleaseData.setRepositoryName(txtRepositoryName.getText());
        initReleaseData.setRepositoryBranch(txtRepositoryBranch.getText());
    }

    protected void okPressed() {
        saveInput();
        super.okPressed();
    }
    protected void cancelPressed() {
        cancelPressed=true;
        super.cancelPressed();
    }

	public InitialReleaseData getInitReleaseData() {
		return initReleaseData;
	}

	public boolean isCancelPressed() {
		return cancelPressed;
	}

}
