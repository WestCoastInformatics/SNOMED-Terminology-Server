package org.ihtsdo.otf.ts.rf2.jpa;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;
import org.ihtsdo.otf.ts.rf2.Component;

/**
 * Abstract implementation of {@link Component} for use with JPA.
 */
@Audited
@MappedSuperclass
public abstract class AbstractComponent implements Component {

  /** The id. */
  @Id
  @GeneratedValue
  private Long id;

  /** The effective time. e.g. publication time. */
  @Temporal(TemporalType.TIMESTAMP)
  private Date effectiveTime;

  /** The last modified. */
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  /** The last modified. */
  @Column(nullable = false)
  private String lastModifiedBy;

  /** The active. */
  @Column(nullable = false)
  private boolean active;

  /** The module id. */
  @Column(nullable = false)
  private String moduleId;

  /** The terminology. */
  @Column(nullable = false)
  private String terminology;

  /** The terminology id. */
  @Column(nullable = false)
  private String terminologyId;

  /** The terminology version. */
  @Column(nullable = false)
  private String terminologyVersion;

  /**
   * Instantiates an empty {@link AbstractComponent}.
   */
  protected AbstractComponent() {
    // do nothing
  }

  /**
   * Instantiates a {@link AbstractComponent} from the specified parameters.
   *
   * @param component the component
   */
  protected AbstractComponent(Component component) {
    active = component.isActive();
    effectiveTime = component.getEffectiveTime();
    id = component.getId();
    label = component.getLabel();
    lastModified = component.getLastModified();
    lastModifiedBy = component.getLastModifiedBy();
    moduleId = component.getModuleId();
    terminology = component.getTerminology();
    terminologyId = component.getTerminologyId();
    terminologyVersion = component.getTerminologyVersion();
  }

  /**
   * Generalized field for any additional value that needs to be attached to a
   * component.
   */
  @Column(nullable = true, length = 4000)
  private String label;

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#getId()
   */
  @Override
  @XmlTransient
  public Long getId() {
    return this.id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#setId(java.lang.Long)
   */
  @Override
  public void setId(Long id) {
    this.id = id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#getObjectId()
   */
  @Override
  @XmlID
  public String getObjectId() {
    return this.id.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#setId(java.lang.Long)
   */
  @Override
  public void setObjectId(String id) {
    this.id = Long.valueOf(id);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#getEffectiveTime()
   */
  @Override
  public Date getEffectiveTime() {
    return effectiveTime;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#setEffectiveTime(java.util.Date)
   */
  @Override
  public void setEffectiveTime(Date effectiveTime) {
    this.effectiveTime = effectiveTime;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#getLastModified()
   */
  @Override
  public Date getLastModified() {
    return lastModified;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#setLastModified(java.util.Date)
   */
  @Override
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#getLastModifiedBy()
   */
  @Override
  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#setLastModifiedBy(java.lang.String)
   */
  @Override
  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#isActive()
   */
  @Override
  public boolean isActive() {
    return active;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#setActive(boolean)
   */
  @Override
  public void setActive(boolean active) {
    this.active = active;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#getModuleId()
   */
  @Override
  public String getModuleId() {
    return moduleId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#setModuleId(java.lang.String)
   */
  @Override
  public void setModuleId(String moduleId) {
    this.moduleId = moduleId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (active ? 1231 : 1237);
    result = prime * result + ((moduleId == null) ? 0 : moduleId.hashCode());
    result =
        prime * result + ((terminology == null) ? 0 : terminology.hashCode());
    result =
        prime * result
            + ((terminologyId == null) ? 0 : terminologyId.hashCode());
    result =
        prime
            * result
            + ((terminologyVersion == null) ? 0 : terminologyVersion.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AbstractComponent other = (AbstractComponent) obj;
    if (active != other.active)
      return false;
    if (moduleId == null) {
      if (other.moduleId != null)
        return false;
    } else if (!moduleId.equals(other.moduleId))
      return false;
    if (terminology == null) {
      if (other.terminology != null)
        return false;
    } else if (!terminology.equals(other.terminology))
      return false;
    if (terminologyId == null) {
      if (other.terminologyId != null)
        return false;
    } else if (!terminologyId.equals(other.terminologyId))
      return false;
    if (terminologyVersion == null) {
      if (other.terminologyVersion != null)
        return false;
    } else if (!terminologyVersion.equals(other.terminologyVersion))
      return false;
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#getTerminologyVersion()
   */
  @Override
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  public String getTerminologyVersion() {
    return terminologyVersion;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Component#setTerminologyVersion(java.lang.String)
   */
  @Override
  public void setTerminologyVersion(String terminologyVersion) {
    this.terminologyVersion = terminologyVersion;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#getTerminology()
   */
  @Override
  public String getTerminology() {
    return terminology;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#setTerminology(java.lang.String)
   */
  @Override
  public void setTerminology(String terminology) {
    this.terminology = terminology;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#getTerminologyId()
   */
  @Override
  @XmlID
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  public String getTerminologyId() {
    return terminologyId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#setTerminologyId(java.lang.String)
   */
  @Override
  public void setTerminologyId(String terminologyId) {
    this.terminologyId = terminologyId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    return this.getId() + "," + this.getTerminology() + ","
        + this.getTerminologyId() + "," + this.getTerminologyVersion() + ","
        + this.getEffectiveTime() + "," + this.isActive() + ","
        + this.getModuleId(); // end of basic component fields
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#getLabel()
   */
  @Override
  public String getLabel() {
    return label;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Component#setLabel(java.lang.String)
   */
  @Override
  public void setLabel(String label) {
    this.label = label;
  }

}
