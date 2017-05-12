/*******************************************************************************
 * Copyright (c) 2013, 2017 Lukas Balzer, Asim Abdulkhaleq, Stefan Wagner Institute of Software
 * Technology, Software Engineering Group University of Stuttgart, Germany
 * 
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package xstampp.usermanagement.roles;

import org.apache.commons.lang3.SerializationUtils;

import xstampp.usermanagement.api.IUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * An abstract class which defines the basis af all users in thre system.
 * 
 * @author Lukas Balzer - initial implementation
 *
 */
public abstract class AbstractUser implements IUser {

  @XmlAttribute(name = "userId", required = true)
  private UUID userId;

  @XmlAttribute(name = "username", required = true)
  private byte[] username;

  @XmlAttribute(name = "password", required = true)
  private byte[] password;

  @XmlElementWrapper(name = "responsibilities")
  @XmlElement(name = "responsibility")
  private List<UUID> responsibilities;

  /**
   * Creates a user with a username and a password and an id which can and should not be changed
   * later.
   */
  public AbstractUser(String username, String password) {
    this.username = SerializationUtils.serialize(username);
    this.password = SerializationUtils.serialize(password);
    this.userId = UUID.randomUUID();
    this.responsibilities = new ArrayList<>();
  }

  public AbstractUser() {
    this("", ""); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Override
  public UUID getUserId() {
    return userId;
  }

  @Override
  public String getUsername() {
    return SerializationUtils.deserialize(username).toString();
  }

  @Override
  public boolean verifyPassword(String password) {
    return getPassword().equals(password);
  }

  private String getPassword() {
    return SerializationUtils.deserialize(password).toString();
  }

  @Override
  public boolean setUsername(String password, String username) {
    if (password.equals(this.password)) {
      this.username = SerializationUtils.serialize(username);
      return true;
    }
    return false;
  }

  @Override
  public boolean setPassword(String oldPassword, String newPassword) {
    if (getPassword().equals(oldPassword)) {
      setPassword(newPassword);
      return true;
    }
    return false;
  }

  public boolean setPassword(String newPassword) {
    this.password = SerializationUtils.serialize(newPassword);
    return true;
  }

  /**
   * @param responsibility
   *          the responsibilities to set.
   */
  public void addResponsibility(UUID responsibility) {
    this.responsibilities.add(responsibility);
  }
  
  public List<UUID> getResponsibilities() {
    return responsibilities;
  }

  @Override
  public boolean isResponibleFor(UUID responsibility) {
    return getResponsibilities().contains(responsibility);
  }
}
