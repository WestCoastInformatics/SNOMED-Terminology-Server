package org.ihtsdo.otf.mapping.rf2;

/**
 * Represents a reference set member
 */
public interface RefSetMember extends Component {

  /**
   * returns the refSetId
   * @return the id
   */
  public String getRefSetId();

  /**
   * sets the refSetId
   * 
   * @param refSetId the reference set id
   */
  public void setRefSetId(String refSetId);

}