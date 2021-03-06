/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.rf2;

/**
 * Represents a complex map reference set member
 */
public interface ComplexMapRefSetMember extends ConceptRefSetMember {

  /**
   * returns the mapGroup
   * @return the mapGroup
   * 
   */
  public int getMapGroup();

  /**
   * sets the mapGroup
   * @param mapGroup the mapGroup
   */
  public void setMapGroup(int mapGroup);

  /**
   * returns the mapPriority
   * @return the mapPriority
   * 
   */
  public int getMapPriority();

  /**
   * sets the mapPriority
   * @param mapPriority the mapPriority
   */
  public void setMapPriority(int mapPriority);

  /**
   * returns the mapRule
   * @return the mapRule
   * 
   */
  public String getMapRule();

  /**
   * sets the mapRule
   * @param mapRule the mapRule
   */
  public void setMapRule(String mapRule);

  /**
   * returns the mapAdvice
   * @return the mapAdvice
   * 
   */
  public String getMapAdvice();

  /**
   * sets the mapAdvice
   * @param mapAdvice the mapAdvice
   */

  public void setMapAdvice(String mapAdvice);

  /**
   * returns the mapTarget
   * @return the mapTarget
   * 
   */

  public String getMapTarget();

  /**
   * sets the mapTarget
   * @param mapTarget the mapTarget
   */

  public void setMapTarget(String mapTarget);

  /**
   * returns the mapRelationId
   * @return the mapRelationId
   * 
   */
  public String getMapRelationId();

  /**
   * sets the mapRelationId
   * @param mapRelationId the mapRelationId
   */
  public void setMapRelationId(String mapRelationId);

}