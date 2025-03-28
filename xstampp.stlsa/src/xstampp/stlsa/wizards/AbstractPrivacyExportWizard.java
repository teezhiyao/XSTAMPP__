package xstampp.stlsa.wizards;

import xstampp.astpa.wizards.AbstractExportWizard;
import xstampp.stlsa.model.StlsaController;

public abstract class AbstractPrivacyExportWizard extends AbstractExportWizard {

	public AbstractPrivacyExportWizard() {
		super();
	}

	public AbstractPrivacyExportWizard(String viewId) {
		super(viewId);
	}

	public AbstractPrivacyExportWizard(String[] viewId) {
		super(viewId);
	}

	@Override
	protected Class<?> getExportModel() {
		return StlsaController.class;
	}

}
