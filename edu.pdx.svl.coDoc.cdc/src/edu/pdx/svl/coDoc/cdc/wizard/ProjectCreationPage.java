package edu.pdx.svl.coDoc.cdc.wizard;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

public class ProjectCreationPage extends WizardPage 
{
	private Text containerText;
	private Text fileText;
	private ISelection selection;

	public ProjectCreationPage(ISelection selection) 
	{
		super("wizardPage");
		setTitle("Create a new coDoc project");
		setDescription("Create a new coDoc project");
		setPageComplete(false);
		this.selection = selection;
		// workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) 
	{
		initializeDialogUnits(parent);

		// create container
		final Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridlayout = new GridLayout();
		gridlayout.marginWidth = 10;
		gridlayout.marginHeight = 10;
		gridlayout.numColumns = 3;
		gridlayout.verticalSpacing = 9;
		container.setLayout(gridlayout);
		Label label = new Label(container, SWT.NULL);
		label.setText("&Container:");

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		//containerText.setText("Container");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener() 
		{
			@Override
			public void modifyText(ModifyEvent e) 
			{
				//Text text = (Text) e.getSource();
				//String name = text.getText();
				dialogChanged();
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				handleBrowse();
			}
		});
		label = new Label(container, SWT.NULL);
		label.setText("&File name:");

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) 
			{
				dialogChanged();
			}
		});
		initialize();
		dialogChanged();

		/*
		Combo combo = new Combo(container, SWT.NONE);
		combo.setText("设定类别");
		combo.add("普通");
		combo.add("同事");
		combo.add("商业");
		combo.add("朋友");*/

		setControl(container);
		// Dialog.applyDialogFont(composite);
	}

	private void initialize() 
	{
		if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) 
		{
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
			{
				return;
			}
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) 
			{
				IContainer container;
				if (obj instanceof IContainer)
				{
					container = (IContainer) obj;
				}
				else
				{
					container = ((IResource) obj).getParent();
				}
				containerText.setText(container.getFullPath().toString());
			}
		}
		fileText.setText("new_file.mpe");
	}

	private void handleBrowse() 
	{
		//ContainerSelectionDialog(Shell parentShell, IContainer initialRoot, boolean allowNewContainerName, String message)
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(), false, "Select new file container");
		if (dialog.open() == Window.OK) 
		{
			Object[] result = dialog.getResult();
			if (result.length == 1) 
			{
				containerText.setText(((Path) result[0]).toString());
			}
		}
	}

	private void dialogChanged() 
	{
		IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getContainerName()));
		String fileName = getFileName();

		if (getContainerName().length() == 0) 
		{
			updateStatus("File container must be specified");
			return;
		}
		if (container == null || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) 
		{
			updateStatus("File container must exist");
			return;
		}
		if (!container.isAccessible()) 
		{
			updateStatus("Project must be writable");
			return;
		}
		if (fileName.length() == 0) 
		{
			updateStatus("File name must be specified");
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) 
		{
			updateStatus("File name must be valid");
			return;
		}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) 
		{
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("mpe") == false) 
			{
				updateStatus("File extension must be \"mpe\"");
				return;
			}
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getContainerName() 
	{
		return containerText.getText();
	}

	public String getFileName() 
	{
		return fileText.getText();
	}
}