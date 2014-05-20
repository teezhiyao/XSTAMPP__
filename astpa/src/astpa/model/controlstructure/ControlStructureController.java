/*******************************************************************************
 * Copyright (c) 2013 A-STPA Stupro Team Uni Stuttgart (Lukas Balzer, Adam
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

package astpa.model.controlstructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.eclipse.draw2d.geometry.Rectangle;

import astpa.model.causalfactor.ICausalComponent;
import astpa.model.causalfactor.ICausalFactor;
import astpa.model.controlstructure.components.Anchor;
import astpa.model.controlstructure.components.CSConnection;
import astpa.model.controlstructure.components.Component;
import astpa.model.controlstructure.components.ComponentType;
import astpa.model.controlstructure.components.ConnectionType;
import astpa.model.controlstructure.interfaces.IConnection;
import astpa.model.controlstructure.interfaces.IRectangleComponent;

/**
 * Controller-class for working with the control structure diagram
 * 
 * @author Fabian Toth
 * 
 */
public class ControlStructureController {
	
	@XmlElement(name = "component")
	private Component root;
	
	@XmlElementWrapper(name = "connections")
	@XmlElement(name = "connection")
	private List<CSConnection> connections;
	private final Map<UUID,IRectangleComponent> componentTrash;
	private final Map<UUID,IConnection> connectionTrash;
	private final Map<UUID,List<UUID>> removedLinks;
	
	/**
	 * Constructor of the control structure controller
	 * 
	 * @author Fabian Toth
	 */
	public ControlStructureController() {
		this.connections = new ArrayList<>();
		this.componentTrash= new HashMap<>();
		this.connectionTrash= new HashMap<>();
		this.removedLinks= new HashMap<>();
	}
	
	/**
	 * Adds a new component to a parent with the given values.
	 * 
	 * @param parentId the id of the parent
	 * @param layout the layout of the new component
	 * @param text the text of the new component
	 * @param type the type of the new component
	 * @return the id of the created component. Null if the component could not
	 *         be added
	 * 
	 * @author Fabian Toth
	 */
	public UUID addComponent(UUID parentId, Rectangle layout, String text, ComponentType type) {
		Component newComp = new Component(text, layout, type);
		Component parent = this.getInternalComponent(parentId);
		parent.addChild(newComp);
		return newComp.getId();
	}
	
	/**
	 * Creates a new root with the given values.
	 * 
	 * @param layout the layout of the new component
	 * @param text the text of the new component
	 * @return the id of the created component. Null if the component could not
	 *         be added
	 * 
	 * @author Fabian Toth
	 */
	public UUID setRoot(Rectangle layout, String text) {
		Component newComp = new Component(text, layout, ComponentType.ROOT);
		this.root = newComp;
		return newComp.getId();
	}
	
	/**
	 * Searches for the component with the given id and changes the layout of it
	 * 
	 * @param componentId the id of the component
	 * @param layout the new text
	 * @param step1 if the layout of step 1 should be changed
	 * @return true if the text could be changed
	 * 
	 * @author Fabian Toth
	 * 
	 */
	public boolean changeComponentLayout(UUID componentId, Rectangle layout, boolean step1) {
		Component component = this.getInternalComponent(componentId);
		if (component != null) {
			component.setLayout(layout, step1);
			return true;
		}
		return false;
	}
	
	/**
	 * Searches for the component with the given id and changes the text of it
	 * 
	 * @param componentId the id of the component
	 * @param text the new text
	 * @return true if the text could be changed
	 * 
	 * @author Fabian Toth
	 */
	public boolean changeComponentText(UUID componentId, String text) {
		Component component = this.getInternalComponent(componentId);
		if (component != null) {
			component.setText(text);
			return true;
		}
		return false;
	}
	
	/**
	 * Searches recursively for the component with the given id and removes it
	 * 
	 * @param componentId the id of the component to delete
	 * @return true if this controller contained the specified element
	 * 
	 * @author Fabian Toth
	 */
	public boolean removeComponent(UUID componentId) {
		Component component = this.getInternalComponent(componentId);
		this.removeAllLinks(componentId);
		this.componentTrash.put(componentId, component);
		return this.root.removeChild(componentId);
	}
	
	/**
	 *This methode recovers a Component which was deleted before,
	 *from the componentTrash
	 *
	 * @author Lukas Balzer
	 *
	 * @param parentId the id of the parent
	 * @param componentId the id of the component to recover
	 * @return
	 * 		whether the component could be recoverd or not
	 */
	public boolean recoverComponent(UUID parentId,UUID componentId){
		if(this.componentTrash.containsKey(componentId)){
			Component parent = this.getInternalComponent(parentId);
			boolean success = parent.addChild((Component)this.componentTrash.get(componentId));
			this.componentTrash.remove(componentId);
			if(this.removedLinks.containsKey(componentId)){
				for(UUID connectionId: this.removedLinks.get(componentId)){
					this.recoverConnection(connectionId);
				}
			}
			return success;
		}
		
		return false;
		
	}
	
	/**
	 * Searches recursively for the component with the given id
	 * 
	 * @param componentId the id of the child
	 * @return the component with the given id
	 * 
	 * @author Fabian Toth
	 */
	public IRectangleComponent getComponent(UUID componentId) {
		if (this.root == null) {
			return null;
		}
		return this.root.getChild(componentId);
	}
	
	/**
	 * Gets all components of the root level
	 * 
	 * @return the the components
	 * 
	 * @author Fabian Toth
	 */
	public IRectangleComponent getRoot() {
		return this.root;
	}
	
	/**
	 * Adds a new connection with the given values
	 * 
	 * @param sourceAnchor the anchor at the source component
	 * @param targetAnchor the anchor at the target component
	 * @param connectionType the type of the connection
	 * @return the id of the new connection
	 * 
	 * @author Fabian Toth
	 */
	public UUID addConnection(Anchor sourceAnchor, Anchor targetAnchor, ConnectionType connectionType) {
		CSConnection newConn = new CSConnection(sourceAnchor, targetAnchor, connectionType);
		this.connections.add(newConn);
		return newConn.getId();
	}
	
	/**
	 * Searches for the connection with the given id and changes the connection
	 * type to the new value
	 * 
	 * @param connectionId the id of the connection to change
	 * @param connectionType the new connection type
	 * @return true if the connection type could be changed
	 * 
	 * @author Fabian Toth
	 */
	public boolean changeConnectionType(UUID connectionId, ConnectionType connectionType) {
		IConnection connection = this.getConnection(connectionId);
		if (connection != null) {
			((CSConnection) connection).setConnectionType(connectionType);
			return true;
		}
		return false;
	}
	
	/**
	 * Searches for the connection with the given id and changes the targetId to
	 * the new value
	 * 
	 * @param connectionId the id of the connection to change
	 * @param targetAnchor the new source anchor
	 * @return true if the targetId could be changed
	 * 
	 * @author Fabian Toth
	 */
	public boolean changeConnectionTarget(UUID connectionId, Anchor targetAnchor) {
		IConnection connection = this.getConnection(connectionId);
		if (connection != null) {
			((CSConnection) connection).setTargetAnchor(targetAnchor);
			return true;
		}
		return false;
	}
	
	/**
	 * Searches for the connection with the given id and changes the sourceId to
	 * the new value
	 * 
	 * @param connectionId the id of the connection to change
	 * @param sourceAnchor the new source anchor
	 * @return true if the sourceId could be changed
	 * 
	 * @author Fabian Toth
	 */
	public boolean changeConnectionSource(UUID connectionId, Anchor sourceAnchor) {
		IConnection connection = this.getConnection(connectionId);
		if (connection != null) {
			((CSConnection) connection).setSourceFigureId(sourceAnchor);
			return true;
		}
		return false;
	}
	
	/**
	 * Deletes the connection with the given id
	 * 
	 * @param connectionId the id of the connection
	 * @return true if this component contained the specified element
	 * 
	 * @author Fabian Toth
	 */
	public boolean removeConnection(UUID connectionId) {
		IConnection connection = this.getConnection(connectionId);
		if(this.connections.remove(connection)){
			this.connectionTrash.put(connectionId, connection);
			return true;
		}
		return false;
	}
	
	/**
	 *This methode recovers a Connection which was deleted before,
	 *from the connectionTrash
	 *
	 * @author Lukas Balzer
	 *
	 * @param connectionId the id of the component to recover
	 * @return
	 * 		whether the Connection could be recovered or not
	 */
	public boolean recoverConnection(UUID connectionId){
		if(this.connectionTrash.containsKey(connectionId)){
			boolean success = this.connections.add((CSConnection)this.connectionTrash.get(connectionId));
			this.connectionTrash.remove(connectionId);
			return success;
		}
		return false;
		
	}
	/**
	 * Gets the connection with the given id
	 * 
	 * @param connectionId the id of the connection
	 * @return the connection with the given id
	 * 
	 * @author Fabian Toth
	 */
	public IConnection getConnection(UUID connectionId) {
		for (IConnection connection : this.connections) {
			if (connection.getId().equals(connectionId)) {
				return connection;
			}
		}
		return null;
	}
	
	/**
	 * Searches recursively for the internal component with the given id
	 * 
	 * @param componentId the id of the child
	 * @return the component with the given id
	 * 
	 * @author Fabian Toth
	 */
	private Component getInternalComponent(UUID componentId) {
		if (this.root == null) {
			return null;
		}
		return this.root.getChild(componentId);
	}
	
	/**
	 * Removes all links that are connected to the component with the given id
	 * 
	 * @author Fabian Toth,Lukas Balzer
	 * 
	 * @param componentId the id of the component
	 * @return true if the connections have been deleted
	 */
	private boolean removeAllLinks(UUID componentId) {
		List<IConnection> connectionList = new ArrayList<>();
		this.removedLinks.put(componentId, new ArrayList<UUID>());
		for (CSConnection connection : this.connections) {
			if (connection.connectsComponent(componentId)) {
					UUID tmpID=connection.getId();
					connectionList.add(connection);
					this.connectionTrash.put(tmpID, connection);
					this.removedLinks.get(componentId).add(tmpID);
			}
		}
		return this.connections.removeAll(connectionList);
	}
	
	/**
	 * Gets all connections of the control structure diagram
	 * 
	 * @author Fabian Toth
	 * 
	 * @return all connections
	 */
	public List<IConnection> getConnections() {
		List<IConnection> result = new ArrayList<>();
		for (CSConnection connection : this.connections) {
			result.add(connection);
		}
		return result;
	}
	
	/**
	 * Get all causal components
	 * 
	 * @author Fabian Toth
	 * 
	 * @return all causal components
	 */
	public List<ICausalComponent> getCausalComponents() {
		List<ICausalComponent> result = new ArrayList<>();
		if (this.root == null) {
			return result;
		}
		
		for (Component component : this.root.getInternalChildren()) {
			if (!component.getComponentType().equals(ComponentType.TEXTFIELD)) {
				result.add(component);
			}
		}
		return result;
	}
	
	/**
	 * Gets all components of an internal type. Do not use outside the data
	 * model.
	 * 
	 * @author Fabian Toth
	 * 
	 * @return all components
	 */
	public List<Component> getInternalComponents() {
		if (this.root == null) {
			return new ArrayList<Component>();
		}
		return this.root.getInternalChildren();
	}
	
	/**
	 * Removes a causal factor
	 * 
	 * @author Fabian Toth
	 * 
	 * @param causalFactorId the id of the causal factor to remove
	 * @return true, if the causal factor has been removed
	 */
	public boolean removeCausalFactor(UUID causalFactorId) {
		for (ICausalComponent comp : this.getCausalComponents()) {
			for (ICausalFactor causalFactor : comp.getCausalFactors()) {
				if (causalFactor.getId().equals(causalFactorId)) {
					return this.getInternalComponent(comp.getId()).getInternalCausalFactors().remove(causalFactor);
				}
			}
		}
		return false;
	}
	
	/**
	 * Overwrites the layout of step3 with the layout of step1
	 * 
	 * @author Lukas Balzer
	 * 
	 * @param id the id of the component
	 * @return true, if the layout has been synchronized
	 */
	public boolean sychronizeLayout(UUID id) {
		return this.root.getChild(id).sychronizeLayout();
	}
}
