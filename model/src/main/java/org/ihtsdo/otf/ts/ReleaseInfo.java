package org.ihtsdo.otf.ts;

import java.util.Date;

/**
 * Represents information about a release.
 */
public interface ReleaseInfo {

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
   * Returns the name.
   *
   * @return the name
   */
  public String getName();

  /**
   * Sets the name.
   *
   * @param name the name
   */
  public void setName(String name);

  /**
   * Returns the description.
   *
   * @return the description
   */
  public String getDescription();

  /**
   * Sets the description.
   *
   * @param description the description
   */
  public void setDescription(String description);

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
   * Returns the release begin date.
   *
   * @return the release begin date
   */
  public Date getReleaseBeginDate();

  /**
   * Sets the release begin date.
   *
   * @param releaseBeginDate the release begin date
   */
  public void setReleaseBeginDate(Date releaseBeginDate);

  /**
   * Returns the release finish date.
   *
   * @return the release finish date
   */
  public Date getReleaseFinishDate();

  /**
   * Sets the release finish date.
   *
   * @param releaseFinishDate the release finish date
   */
  public void setReleaseFinishDate(Date releaseFinishDate);

  /**
   * Indicates whether or not the release is planned.
   *
   * @return the is planned flag
   */
  public boolean isPlanned();

  /**
   * Sets the planned flag.
   *
   * @param planned the planned flag
   */
  public void setPlanned(boolean planned);

  /**
   * Indicates whether or not the release published.
   *
   * @return the published flag
   */
  public boolean isPublished();

  /**
   * Sets the published flag.
   *
   * @param published the published flag
   */
  public void setPublished(boolean published);

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
   * Returns the terminology version.
   *
   * @return the terminology version
   */
  public String getTerminologyVersion();

  /**
   * Sets the terminology version.
   *
   * @param terminologyVersion the terminology version
   */
  public void setTerminologyVersion(String terminologyVersion);
}
