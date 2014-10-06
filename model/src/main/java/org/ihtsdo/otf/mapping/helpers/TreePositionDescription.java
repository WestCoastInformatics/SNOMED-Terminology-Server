package org.ihtsdo.otf.mapping.helpers;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Interface TreePositionDescription.
 */
public interface TreePositionDescription {

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
   * Returns the referenced concepts.
   *
   * @return the referenced concepts
   */
  public List<TreePositionReferencedConcept> getReferencedConcepts();

  /**
   * Sets the referenced concepts.
   *
   * @param referencedConcepts the referenced concepts
   */
  public void setReferencedConcepts(
    List<TreePositionReferencedConcept> referencedConcepts);

  /**
   * Adds the referenced concept.
   *
   * @param referencedConcept the referenced concept
   */
  public void addReferencedConcept(
    TreePositionReferencedConcept referencedConcept);

  /**
   * Removes the referenced concept.
   *
   * @param referencedConcept the referenced concept
   */
  public void removeReferencedConcept(
    TreePositionReferencedConcept referencedConcept);
}
