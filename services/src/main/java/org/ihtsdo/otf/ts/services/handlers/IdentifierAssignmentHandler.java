package org.ihtsdo.otf.ts.services.handlers;

import org.ihtsdo.otf.ts.helpers.Configurable;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueRefSetMember;
import org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.Component;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember;
import org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.SimpleMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;
import org.ihtsdo.otf.ts.rf2.TransitiveRelationship;

/**
 * Generically represents an algorithm for assigning identifiers.
 */
public interface IdentifierAssignmentHandler extends Configurable {

  /**
   * Returns the terminology id.
   *
   * @param concept the concept
   * @return the string
   * @throws Exception the exception
   */
  public String getTerminologyId(Concept concept) throws Exception;

  /**
   * Returns the terminology id.
   *
   * @param description the description
   * @return the string
   * @throws Exception the exception
   */
  public String getTerminologyId(Description description) throws Exception;

  /**
   * Returns the terminology id.
   *
   * @param relationship the relationship
   * @return the string
   * @throws Exception the exception
   */
  public String getTerminologyId(Relationship relationship) throws Exception;

  /**
   * Returns the terminology id.
   *
   * @param member the member
   * @return the string
   * @throws Exception the exception
   */
  public String getTerminologyId(
    AssociationReferenceRefSetMember<? extends Component> member)
    throws Exception;

  /**
   * Returns the terminology id.
   *
   * @param member the member
   * @return the string
   * @throws Exception the exception
   */
  public String getTerminologyId(AttributeValueRefSetMember<? extends Component> member)
    throws Exception;

  /**
   * Returns the terminology id.
   *
   * @param member the member
   * @return the string
   * @throws Exception the exception
   */
  public String getTerminologyId(ComplexMapRefSetMember member) throws Exception;

  /**
   * Returns the terminology id.
   *
   * @param member the member
   * @return the string
   * @throws Exception the exception
   */
  public String getTerminologyId(DescriptionTypeRefSetMember member) throws Exception;

  /**
   * Returns the terminology id.
   *
   * @param member the member
   * @return the string
   * @throws Exception the exception
   */
  public String getTerminologyId(LanguageRefSetMember member) throws Exception;

  /**
   * Returns the terminology id.
   *
   * @param member the member
   * @return the string
   * @throws Exception the exception
   */
  public String getTerminologyId(ModuleDependencyRefSetMember member) throws Exception;

  /**
   * Returns the terminology id.
   *
   * @param member the member
   * @return the string
   * @throws Exception the exception
   */
  public String getTerminologyId(RefsetDescriptorRefSetMember member) throws Exception;

  /**
   * Returns the terminology id.
   *
   * @param member the member
   * @return the string
   * @throws Exception the exception
   */
  public String getTerminologyId(SimpleMapRefSetMember member) throws Exception;

  /**
   * Returns the terminology id.
   *
   * @param member the member
   * @return the string
   * @throws Exception the exception
   */
  public String getTerminologyId(SimpleRefSetMember member) throws Exception;

  /**
   * Returns the terminology id.
   *
   * @param relationship the relationship
   * @return the string
   * @throws Exception the exception
   */
  public String getTerminologyId(TransitiveRelationship relationship) throws Exception;

}
