/*******************************************************************************
 * Copyright (c) 2013, 2017 A-STPA Stupro Team Uni Stuttgart (Lukas Balzer, Adam
 * Grahovac, Jarkko Heidenwag, Benedikt Markt, Jaqueline Patzek, Sebastian
 * Sieber, Fabian Toth, Patrick Wickenhäuser, Aliaksei Babkovich, Aleksander
 * Zotov).
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/

package xstampp.stlsa.ui.unsecurecontrolaction;

import java.util.List;
import java.util.UUID;

import xstampp.stlsa.messages.StlsaMessages;
import xstampp.astpa.model.interfaces.ITableModel;
import xstampp.astpa.model.interfaces.IUnsafeControlActionDataModel;
import xstampp.ui.common.contentassist.ITableContentProvider;

/**
 * 
 * @author Benedikt Markt
 * 
 */
public class UcaContentProvider  implements ITableContentProvider<ITableModel> {
  private static final String HAZARD_ID_PREFIX = "H-"; //$NON-NLS-1$
	private final transient IUnsafeControlActionDataModel ucaInterface;

	/**
	 * 
	 * @author Benedikt Markt
	 * 
	 * @param ucaInterface
	 *            the interface to the datamodel
	 * 
	 */
	public UcaContentProvider(final IUnsafeControlActionDataModel ucaInterface) {
		this.ucaInterface = ucaInterface;
	}

	@Override
	public List<ITableModel> getAllItems() {
		return this.ucaInterface.getAllHazards();
	}


	public List<ITableModel> getLinkedItems(final UUID itemId) {
//	   return this.ucaInterface.getControlActionController().getAllControlActions();
//	  UnsafeControlAction uca = (UnsafeControlAction) this.ucaInterface.getControlActionController().getUnsafeControlAction(itemId);
//	  System.out.println("PRINTTT");
//	  System.out.println(this.ucaInterface.getClass());
//	  this.ucaInterface.getControlActionController().getLinksOfUCA(itemId);
	  return this.ucaInterface.getLinkedHazardsOfUCA(itemId);
	}

	@Override
	public void addLink(final UUID item1, final UUID item2) {
		this.ucaInterface.addUCAHazardLink(item1, item2);

	}

	@Override
	public void removeLink(final UUID item, final UUID removeItem) {
		this.ucaInterface.removeUCAHazardLink(item, removeItem);
	}

  @Override
  public String getPrefix() {
    return HAZARD_ID_PREFIX;
  }

  @Override
  public String getEmptyMessage() {
    return StlsaMessages.NotVulnerable;
  }

  @Override
  public List<ITableModel> getAllLinkedItems() {
    // TODO Auto-generated method stub
    return null;
  }
}
