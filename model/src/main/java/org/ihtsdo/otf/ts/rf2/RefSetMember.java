package org.ihtsdo.otf.ts.rf2;

/**
 * Represents a reference set member
 * @param <T> a {@link Component}
 */
public interface RefSetMember<T extends Component> extends Component {

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