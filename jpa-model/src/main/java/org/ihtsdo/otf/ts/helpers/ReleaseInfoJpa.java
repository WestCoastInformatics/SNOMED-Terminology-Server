package org.ihtsdo.otf.ts.helpers;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;

/**
 * JPA enabled implementation of a {@link ReleaseInfo}.
 */
@Entity
@Table(name = "release_infos")
@Audited
@XmlRootElement(name = "releaseInfo")
public class ReleaseInfoJpa implements ReleaseInfo {

  /** The id. */
  @Id
  @GeneratedValue
  private Long id;

  /** The name. */
  @Column(nullable = false, length = 256)
  private String name;

  /** The description. */
  @Column(nullable = false, length = 4000)
  private String description;

  /** The effective time. */
  @Temporal(TemporalType.TIMESTAMP)
  private Date effectiveTime;

  /** The release begin date. */
  @Temporal(TemporalType.TIMESTAMP)
  private Date releaseBeginDate;

  /** The release finish date. */
  @Temporal(TemporalType.TIMESTAMP)
  private Date releaseFinishDate;

  /**
   * Instantiates an empty {@link ReleaseInfoJpa}.
   */
  public ReleaseInfoJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link ReleaseInfoJpa} from the specified parameters.
   *
   * @param releaseInfo the release info
   */
  public ReleaseInfoJpa(ReleaseInfo releaseInfo) {
    id = releaseInfo.getId();
    name = releaseInfo.getName();
    description = releaseInfo.getDescription();
    effectiveTime = releaseInfo.getEffectiveTime();
    releaseBeginDate = releaseInfo.getReleaseBeginDate();
    releaseFinishDate = releaseInfo.getReleaseFinishDate();
  }

  /**
   * ID for XML serialization
   *
   * @return the object id
   */
  @XmlID
  public String getObjectId() {
    return this.id.toString();
  }

  /**
   * Sets the object id.
   *
   * @param id the object id
   */
  public void setObjectId(String id) {
    this.id = Long.valueOf(id);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.ReleaseInfo#getId()
   */
  @Override
  @XmlTransient
  public Long getId() {
    return id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.ReleaseInfo#setId(java.lang.Long)
   */
  @Override
  public void setId(Long id) {
    this.id = id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.ReleaseInfo#getName()
   */
  @Override
  public String getName() {
    return name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.ReleaseInfo#setName(java.lang.String)
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.ReleaseInfo#getDescription()
   */
  @Override
  public String getDescription() {
    return description;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.ReleaseInfo#setDescription(java.lang.String)
   */
  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.ReleaseInfo#getEffectiveTime()
   */
  @Override
  public Date getEffectiveTime() {
    return effectiveTime;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.ReleaseInfo#setEffectiveTime(java.util.Date)
   */
  @Override
  public void setEffectiveTime(Date effectiveTime) {
    this.effectiveTime = effectiveTime;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.ReleaseInfo#getReleaseBeginDate()
   */
  @Override
  public Date getReleaseBeginDate() {
    return releaseBeginDate;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.helpers.ReleaseInfo#setReleaseBeginDate(java.util.Date)
   */
  @Override
  public void setReleaseBeginDate(Date releaseBeginDate) {
    this.releaseBeginDate = releaseBeginDate;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.ReleaseInfo#getReleaseFinishDate()
   */
  @Override
  public Date getReleaseFinishDate() {
    return releaseFinishDate;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.helpers.ReleaseInfo#setReleaseFinishDate(java.util.Date)
   */
  @Override
  public void setReleaseFinishDate(Date releaseFinishDate) {
    this.releaseFinishDate = releaseFinishDate;
  }


  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + ((description == null) ? 0 : description.hashCode());
    result =
        prime * result
            + ((effectiveTime == null) ? 0 : effectiveTime.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result =
        prime * result
            + ((releaseBeginDate == null) ? 0 : releaseBeginDate.hashCode());
    result =
        prime * result
            + ((releaseFinishDate == null) ? 0 : releaseFinishDate.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ReleaseInfoJpa other = (ReleaseInfoJpa) obj;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (effectiveTime == null) {
      if (other.effectiveTime != null)
        return false;
    } else if (!effectiveTime.equals(other.effectiveTime))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (releaseBeginDate == null) {
      if (other.releaseBeginDate != null)
        return false;
    } else if (!releaseBeginDate.equals(other.releaseBeginDate))
      return false;
    if (releaseFinishDate == null) {
      if (other.releaseFinishDate != null)
        return false;
    } else if (!releaseFinishDate.equals(other.releaseFinishDate))
      return false;
    return true;
  }
}
