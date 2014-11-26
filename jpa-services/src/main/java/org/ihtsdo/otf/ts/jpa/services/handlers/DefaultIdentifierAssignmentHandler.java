package org.ihtsdo.otf.ts.jpa.services.handlers;

import java.util.Properties;

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
import org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler;

/**
 * Default implementation of {@link IdentifierAssignmentHandler}. This supports
 * "application-managed" identifier assignment.
 */
public class DefaultIdentifierAssignmentHandler implements
    IdentifierAssignmentHandler {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.helpers.Configurable#setProperties(java.util.Properties)
   */
  @Override
  public void setProperties(Properties p) {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#assign(
   * org.ihtsdo.otf.ts.rf2.Concept)
   */
  @Override
  public String getTerminologyId(Concept concept) {
    // no assignment
    return concept.getTerminologyId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#assign(
   * org.ihtsdo.otf.ts.rf2.Description)
   */
  @Override
  public String getTerminologyId(Description description) {
    // no assignment
    return description.getTerminologyId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#assign(
   * org.ihtsdo.otf.ts.rf2.Relationship)
   */
  @Override
  public String getTerminologyId(Relationship relationship) {
    // no assignment
    return relationship.getTerminologyId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#assign(
   * org.ihtsdo.otf.ts.rf2.AssociationReferenceRefSetMember)
   */
  @Override
  public String getTerminologyId(
    AssociationReferenceRefSetMember<? extends Component> member) {
    // no assignment
    return member.getTerminologyId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#assign(
   * org.ihtsdo.otf.ts.rf2.AttributeValueRefSetMember)
   */
  @Override
  public String getTerminologyId(AttributeValueRefSetMember<? extends Component> member) {
    // no assignment
    return member.getTerminologyId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#assign(
   * org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember)
   */
  @Override
  public String getTerminologyId(ComplexMapRefSetMember member) {
    // no assignment
    return member.getTerminologyId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#assign(
   * org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember)
   */
  @Override
  public String getTerminologyId(DescriptionTypeRefSetMember member) {
    // no assignment
    return member.getTerminologyId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#assign(
   * org.ihtsdo.otf.ts.rf2.LanguageRefSetMember)
   */
  @Override
  public String getTerminologyId(LanguageRefSetMember member) {
    // no assignment
    return member.getTerminologyId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#assign(
   * org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember)
   */
  @Override
  public String getTerminologyId(ModuleDependencyRefSetMember member) {
    // no assignment
    return member.getTerminologyId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#assign(
   * org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember)
   */
  @Override
  public String getTerminologyId(RefsetDescriptorRefSetMember member) {
    // no assignment
    return member.getTerminologyId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#assign(
   * org.ihtsdo.otf.ts.rf2.SimpleMapRefSetMember)
   */
  @Override
  public String getTerminologyId(SimpleMapRefSetMember member) {
    // no assignment
    return member.getTerminologyId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#assign(
   * org.ihtsdo.otf.ts.rf2.SimpleRefSetMember)
   */
  @Override
  public String getTerminologyId(SimpleRefSetMember member) {
    // no assignment
    return member.getTerminologyId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#assign(
   * org.ihtsdo.otf.ts.rf2.TransitiveRelationship)
   */
  @Override
  public String getTerminologyId(TransitiveRelationship relationship) {
    // no assignment
    return relationship.getTerminologyId();
  }

}
