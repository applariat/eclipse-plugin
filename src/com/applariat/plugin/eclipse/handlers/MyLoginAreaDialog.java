package com.applariat.plugin.eclipse.handlers;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MyLoginAreaDialog extends TitleAreaDialog {

    private Text userNameType;
    private Text passwordType;
    
    private String username;
    private String password;
    
    public MyLoginAreaDialog(Shell parentShell) {
        super(parentShell);             
    }
    
    public void create() {
        super.create();
        setTitle("appLariat Login Information");
        setMessage("Enter your appLariat username and password...", IMessageProvider.INFORMATION);
    }

    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);
        
        createUserNameType(container);
        createPasswordType(container);

        return area;
    }
   
    private void createUserNameType(Composite container) {
        
        Label lbtRepUserName = new Label(container, SWT.NONE);
        lbtRepUserName.setText("Username");

        GridData dataRepUserName = new GridData();
        dataRepUserName.grabExcessHorizontalSpace = true;
        dataRepUserName.horizontalAlignment = GridData.FILL;

        userNameType= new Text (container, SWT.SINGLE | SWT.BORDER);
        userNameType.setLayoutData(dataRepUserName);
    }
    
 private void createPasswordType(Composite container) {
        
        Label lbtRepPassword = new Label(container, SWT.NONE);
        lbtRepPassword.setText("Username");

        GridData dataRepPassword = new GridData();
        dataRepPassword.grabExcessHorizontalSpace = true;
        dataRepPassword.horizontalAlignment = GridData.FILL;

        passwordType= new Text (container, SWT.SINGLE | SWT.BORDER);
        passwordType.setEchoChar('*');
        passwordType.setLayoutData(dataRepPassword);
    }
 
    protected boolean isResizable() {
        return true;
    }

    private void saveInput() {
        setUsername(userNameType.getText());
        setPassword(passwordType.getText());
    }

    protected void okPressed() {
        saveInput();
        super.okPressed();
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
