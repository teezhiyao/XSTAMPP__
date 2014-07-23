package astpa.export.stepImages;

import messages.Messages;
import astpa.export.AbstractExportWizard;
import astpa.export.pages.SystemDescriptionExportPage;
import astpa.preferences.IPreferenceConstants;
import astpa.ui.systemdescription.SystemDescriptionView;

/**
 *
 * @author Lukas Balzer
 *
 */
public class SystemDecriptionWizard  extends AbstractExportWizard{
			
		/**
		 *
		 * @author Lukas Balzer
		 *
		 */
		public SystemDecriptionWizard() {
			super(SystemDescriptionView.ID);
			String[] filters= new String[] {"*.png","*.pdf"}; //$NON-NLS-1$ //$NON-NLS-2$
			setExportPage(new SystemDescriptionExportPage(filters,Messages.ExportPreferences,
											this.getStore().getString(IPreferenceConstants.PROJECT_NAME)));
		}
		

		@Override
		public boolean performFinish() {
			return this.performXSLExport("/fopDesc.xsl",Messages.ExportingPdf); //$NON-NLS-1$
		}
}
