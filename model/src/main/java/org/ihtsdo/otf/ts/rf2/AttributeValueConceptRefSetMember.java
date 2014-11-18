package org.ihtsdo.otf.ts.rf2;

/**
 * Represents an attribute value reference set
 */
public interface AttributeValueConceptRefSetMember extends ConceptRefSetMember {

  /**
   * returns the value id
   * @return the value id
   * 
   */
  public String getValueId();

  /**
   * sets the value id
   * @param valueId the value id
   */
  public void setValueId(String valueId);
}
