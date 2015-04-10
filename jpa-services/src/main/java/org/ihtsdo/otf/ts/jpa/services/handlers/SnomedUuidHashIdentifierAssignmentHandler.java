/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.jpa.services.handlers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.jpa.services.helper.TerminologyUtility;
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
 * 
 * If a component already has an SCTID, it keeps it.
 */
public class SnomedUuidHashIdentifierAssignmentHandler implements
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

  /**
   * Concept ID assignment is based on sorted active parent IDs and the default
   * preferred name.
   */
  @Override
  public String getTerminologyId(Concept concept) throws Exception {
    // If the concept already has an sctid return it
    Logger.getLogger(getClass()).debug("assigning concept id");
    StringBuilder sb = new StringBuilder();
    List<Concept> concepts =
        TerminologyUtility.getActiveParentConcepts(concept);
    Collections.sort(concepts, new Comparator<Concept>() {
      @Override
      public int compare(Concept o1, Concept o2) {
        return o1.getTerminologyId().compareTo(o2.getTerminologyId());
      }
    });
    for (Concept parent : concepts) {
      // assumes parent terminology id is set
      sb.append(parent.getTerminologyId());
    }
    sb.append(concept.getDefaultPreferredName());
    String id = TerminologyUtility.getUuid(sb.toString()).toString();
    Logger.getLogger(getClass()).debug("  setting id " + id);
    return id;
  }

  /**
   * Description ID is based on concept id, typeId, term.
   */
  @Override
  public String getTerminologyId(Description description) throws Exception {

    // otherwise return the uuid - for identity comparisons
    String value =
        description.getConcept().getTerminologyId() + description.getTypeId()
            + description.getTerm();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Relationship ID is based on source/destination concept, typeId,
   * characteristicType (e.g. inferred or stated) and whether its grouped.
   */
  @Override
  public String getTerminologyId(Relationship relationship) throws Exception {

    // always return the uuid - for identity comparisons
    String value =
        relationship.getSourceConcept().getTerminologyId()
            + relationship.getDestinationConcept().getId()
            + relationship.getTypeId() + relationship.getCharacteristicTypeId()
            + (relationship.getRelationshipGroup() == 0 ? "false" : "true");
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Association reference refset member ID based on refSetId, referenced
   * component id, and the target component.
   */
  @Override
  public String getTerminologyId(
    AssociationReferenceRefSetMember<? extends Component> member)
    throws Exception {
    String value =
        member.getRefSetId() + member.getComponent().getId()
            + member.getTargetComponentId();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Attribute value refset member ID based on refSetId, referenced component
   * id.
   */
  @Override
  public String getTerminologyId(
    AttributeValueRefSetMember<? extends Component> member) throws Exception {
    String value = member.getRefSetId() + member.getComponent().getId();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Complex map refset member ID based on refset id, referenced component id,
   * map rule, and map target.
   */
  @Override
  public String getTerminologyId(ComplexMapRefSetMember member)
    throws Exception {
    String value =
        member.getRefSetId() + member.getComponent().getId()
            + member.getMapRule() + member.getMapTarget();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Description type refset member ID based on refset id, referenced component
   * id.
   */
  @Override
  public String getTerminologyId(DescriptionTypeRefSetMember member)
    throws Exception {
    String value = member.getRefSetId() + member.getComponent().getId();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Language refset member ID based on refset id, referenced component id.
   */
  @Override
  public String getTerminologyId(LanguageRefSetMember member) throws Exception {
    String value =
        member.getRefSetId() + member.getComponent().getTerminologyId();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Module dependency member ID based on refset id, referenced component id.
   */
  @Override
  public String getTerminologyId(ModuleDependencyRefSetMember member)
    throws Exception {
    String value = member.getRefSetId() + member.getComponent().getId();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Refset descriptor member ID based on refset id, referenced component id,
   * attribute description, attribute type, and attribute order.
   */
  @Override
  public String getTerminologyId(RefsetDescriptorRefSetMember member)
    throws Exception {
    String value =
        member.getRefSetId() + member.getComponent().getId()
            + member.getAttributeDescription() + member.getAttributeType()
            + member.getAttributeType();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Simple map member ID based on refset id, referenced component id, map
   * target.
   */
  @Override
  public String getTerminologyId(SimpleMapRefSetMember member) throws Exception {
    String value =
        member.getRefSetId() + member.getComponent().getId()
            + member.getMapTarget();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Simple member ID based on refset id, referenced component id.
   */
  @Override
  public String getTerminologyId(SimpleRefSetMember member) throws Exception {
    String value = member.getRefSetId() + member.getComponent().getId();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Transitive relationship id based on super/subtype concepts.
   */
  @Override
  public String getTerminologyId(TransitiveRelationship relationship)
    throws Exception {
    // always return the uuid - for identity comparisons
    String value =
        relationship.getSuperTypeConcept() + ","
            + relationship.getSubTypeConcept();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#
   * allowIdChangeOnUpdate()
   */
  @Override
  public boolean allowIdChangeOnUpdate() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#
   * allowConceptIdChangeOnUpdate()
   */
  @Override
  public boolean allowConceptIdChangeOnUpdate() {
    return true;
  }

}
