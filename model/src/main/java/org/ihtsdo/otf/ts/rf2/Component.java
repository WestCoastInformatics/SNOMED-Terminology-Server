package org.ihtsdo.otf.ts.rf2;

import java.util.Date;

/**
 * Represents a terminology component.
 */
public interface Component {

  /**
   * Returns the id.
   * 
   * @return the id
   */
  public Long getId();

  /**
   * Sets the id.
   * 
   * @param id the id
   */
  public void setId(Long id);

  /**
   * Returns the id as a string. This method is used for handling the identifier
   * for XML transport.
   * @return the id
   */
  public String getObjectId();

  /**
   * Returns the terminology.
   * 
   * @return the terminology
   */
  public String getTerminology();

  /**
   * Sets the terminology.
   * 
   * @param terminology the terminology
   */
  public void setTerminology(String terminology);

  /**
   * Returns the effective time.
   * 
   * @return the effective time
   */
  public Date getEffectiveTime();

  /**
   * Sets the effective time.
   * 
   * @param effectiveTime the effective time
   */
  public void setEffectiveTime(Date effectiveTime);

  /**
   * Returns the last modified.
   * 
   * @return the last modified
   */
  public Date getLastModified();

  /**
   * Sets the last modified.
   * 
   * @param lastModified the last modified
   */
  public void setLastModified(Date lastModified);

  /**
   * Returns the last modified by.
   * 
   * @return the last modified by
   */
  public String getLastModifiedBy();

  /**
   * Sets the last modified by.
   * 
   * @param lastModifiedBy the last modified by
   */
  public void setLastModifiedBy(String lastModifiedBy);

  /**
   * Indicates whether or not active is the case.
   * 
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  public boolean isActive();

  /**
   * Sets the active.
   * 
   * @param active the active
   */
  public void setActive(boolean active);

  /**
   * Indicates whether or not the component is published.
   *
   * @return true, if is published
   */
  public boolean isPublished();

  /**
   * Sets the published flag.
   *
   * @param published the new published
   */
  public void setPublished(boolean published);

  /**
   * Indicates whether or not the component should be published. This is a
   * mechanism to have data in the server that can be ignored by publishing
   * processes.
   * 
   * @return true, if is publishable
   */
  public boolean isPublishable();

  /**
   * Sets the publishable flag.
   *
   * @param publishable the new publishable
   */
  public void setPublishable(boolean publishable);

  /**
   * Returns the module id.
   * 
   * @return the module id
   */
  public String getModuleId();

  /**
   * Sets the module id.
   * 
   * @param moduleId the module id
   */
  public void setModuleId(String moduleId);

  /**
   * Returns the terminology version.
   * 
   * @return the terminology version
   */
  public String getTerminologyVersion();

  /**
   * Sets the terminology version.
   * 
   * @param version the terminology version
   */
  public void setTerminologyVersion(String version);

  /**
   * Returns the terminology id.
   * 
   * @return the terminology id
   */
  public String getTerminologyId();

  /**
   * Sets the terminology id.
   * 
   * @param terminologyId the terminology id
   */
  public void setTerminologyId(String terminologyId);

  /**
   * Returns the label.
   *
   * @return the label
   */
  public String getLabel();

  /**
   * Sets the label.
   *
   * @param label the label to set
   */
  public void setLabel(String label);

  /**
   * Returns a string of comma-separated fields of this object.
   * 
   * @return a string of comma-separated fields
   */
  @Override
  public String toString();

}