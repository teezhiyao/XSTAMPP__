package xstpa.ui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import xstampp.astpa.model.DataModelController;
import xstampp.astpa.model.controlstructure.interfaces.IRectangleComponent;
import xstpa.model.ControlActionEntry;
import xstpa.model.ProcessModelVariables;


public class AddEntryShell {

	private Shell shell;
	private List<UUID> variables;
	private List<String> varNames;
	private DataModelController model;
	private Listener listener;
	private Map<UUID,Combo> comboToId;
	private Map<UUID,List<UUID>> idListToID;

	public AddEntryShell(List<UUID> vars,DataModelController controller) {
		this.variables = vars;
		this.model = controller;
		this.comboToId = new HashMap<>();
		this.idListToID = new HashMap<>();
	}
	
	public void open(int x, int y){

		this.shell = new Shell(Display.getCurrent().getActiveShell(),SWT.CLOSE | SWT.TITLE);
		
		this.shell.setText("Add New Entry");
		this.shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellDeactivated(ShellEvent e) {
				AddEntryShell.this.shell.close();
			}
		});
		
		this.shell.setLayout(new GridLayout(2,false));
		
		Composite combos = new Composite(this.shell, SWT.NONE);
		combos.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 2, 1));
		combos.setLayout(new GridLayout(this.variables.size(), false));
		varNames = new ArrayList<>();
		for(UUID id:this.variables){
			//for each variable a composite with the variable name and
			//a combo with all the values is added 
			Composite comp = new Composite(combos, SWT.None);
			comp.setLayoutData(new GridData());
			comp.setLayout(new GridLayout(1, false));
			IRectangleComponent variable = this.model.getComponent(id);
			Label label = new Label(comp, SWT.None);
			label.setText(variable.getText()+":");
			varNames.add(variable.getText());
			Combo varCombo = new Combo(comp, SWT.READ_ONLY);
			ArrayList<UUID> valueList = new ArrayList<>();
			for(IRectangleComponent value : variable.getChildren()){
				varCombo.add(value.getText());
				valueList.add(value.getId());
			}
			//all value ids are stored in a list mapped to the combo they are chosen from
			this.idListToID.put(id,valueList);
			this.comboToId.put(id,varCombo);
			varCombo.select(0);		
		}
		Composite btnComp = new Composite(shell, SWT.None);
		btnComp.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 2, 1));
		btnComp.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Button addEntry = new Button(btnComp, SWT.PUSH);
		addEntry.setText("Add Entry");

		addEntry.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
		    	notifyListener(getValueList());
				shell.close();
			}
		});
		
		Button cancel = new Button(btnComp, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		
		shell.pack();
		shell.setLocation(new Point(x - shell.getBounds().width, y - shell.getBounds().height));
		shell.open();
		while (!shell.isDisposed()) {
		    if (!Display.getDefault().readAndDispatch()) {
		    	Display.getDefault().sleep();
		    }
		}
	}
	
	private ProcessModelVariables getValueList(){
		ProcessModelVariables variable = new ProcessModelVariables();
		
		variable.setPmVariables(varNames);
		for(int i= 0; i<variables.size();i++){
			variable.addVariableId(variables.get(i));
			Combo tmp =comboToId.get(variables.get(i));
			variable.addValue(tmp.getText());
			variable.addValueId(idListToID.get(variables.get(i)).get(tmp.getSelectionIndex()));
		}
		return variable;
	}
	public void addApplyListener(Listener listener){
		this.listener = listener;
	}
	
	private void notifyListener(ProcessModelVariables values){
		if(listener != null && values != null){
			Event e = new Event();
			e.data = values;
			listener.handleEvent(e);
		}
	}
}
