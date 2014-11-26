package org.ihtsdo.otf.ts.rf2;

/**
 * Represents a refset descriptor reference set member.
 */
public interface RefsetDescriptorRefSetMember extends ConceptRefSetMember {

  /**
   * Returns the attribute description.
   *
   * @return the attribute description
   */
  public String getAttributeDescription();
  
  /**
   * Sets the attribute description.
   *
   * @param attributeDescription the attribute description
   */
  public void setAttributeDescription(String attributeDescription);
  
  /**
   * Returns the attribute type.
   *
   * @return the attribute type
   */
  public String getAttributeType();
  
  /**
   * Sets the attribute type.
   *
   * @param attributeType the attribute type
   */
  public void setAttributeType(String attributeType);
  
  /**
   * Returns the attribute order.
   *
   * @return the attribute order
   */
  public int getAttributeOrder();
  
  /**
   * Sets the attribute order.
   *
   * @param attributeOrder the attribute order
   */
  public void setAttributeOrder(int attributeOrder);
}