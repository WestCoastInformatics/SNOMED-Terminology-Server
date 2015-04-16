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
	 * org.ihtsdo.otf.ts.helpers.Configurable#setProperties(java.util.Properties
	 * )
	 */
	@Override
	public void setProperties(Properties p) {
		// do nothing
	}

	/**
	 * Concept ID assignment is based on sorted active parent IDs and the
	 * default preferred name.
	 */
	@Override
	public String getTerminologyId(Concept concept) throws Exception {
		// If the concept already has an sctid return it
		Logger.getLogger(getClass()).debug("assigning concept id");
		StringBuilder sb = new StringBuilder();
		List<Concept> concepts = TerminologyUtility
				.getActiveParentConcepts(concept);
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
		if (description != null && description.getConcept() != null) {
			String value = description.getConcept().getTerminologyId()
					+ description.getTypeId() + description.getTerm();
			String id = TerminologyUtility.getUuid(value).toString();
			return id;
		} else if (description == null) {
			throw new Exception("Cannot resolve a null description.");
		}
		return null;
	}

	/**
	 * Relationship ID is based on source/destination concept, typeId,
	 * characteristicType (e.g. inferred or stated) and whether its grouped.
	 */
	@Override
	public String getTerminologyId(Relationship relationship) throws Exception {

		if (relationship != null && relationship.getSourceConcept() != null
				&& relationship.getDestinationConcept() != null) {
			// always return the uuid - for identity comparisons
			String value = relationship.getSourceConcept().getTerminologyId()
					+ relationship.getDestinationConcept().getId()
					+ relationship.getTypeId()
					+ relationship.getCharacteristicTypeId()
					+ (relationship.getRelationshipGroup() == 0 ? "false"
							: "true");
			String id = TerminologyUtility.getUuid(value).toString();
			return id;
		} else if (relationship == null) {
			throw new Exception("Cannot resolve a null relationship.");
		}
		return null;
	}

	/**
	 * Association reference refset member ID based on refSetId, referenced
	 * component id, and the target component.
	 */
	@Override
	public String getTerminologyId(
			AssociationReferenceRefSetMember<? extends Component> member)
			throws Exception {
		if (member != null && member.getComponent() != null) {
			String value = member.getRefSetId() + member.getComponent().getId()
					+ member.getTargetComponentId();
			String id = TerminologyUtility.getUuid(value).toString();
			return id;
		} else if (member == null) {
			throw new Exception(
					"Cannot resolve a null association reference refset member.");
		}
		return null;
	}

	/**
	 * Attribute value refset member ID based on refSetId, referenced component
	 * id.
	 */
	@Override
	public String getTerminologyId(
			AttributeValueRefSetMember<? extends Component> member)
			throws Exception {

		if (member != null && member.getComponent() != null) {
			String value = member.getRefSetId() + member.getComponent().getId();
			String id = TerminologyUtility.getUuid(value).toString();
			return id;
		} else if (member == null) {
			throw new Exception(
					"Cannot resolve a null attribute value refset member.");
		}
		return null;
	}

	/**
	 * Complex map refset member ID based on refset id, referenced component id,
	 * map rule, and map target.
	 */
	@Override
	public String getTerminologyId(ComplexMapRefSetMember member)
			throws Exception {

		if (member != null && member.getComponent() != null) {
			String value = member.getRefSetId() + member.getComponent().getId()
					+ member.getMapRule() + member.getMapTarget();
			String id = TerminologyUtility.getUuid(value).toString();
			return id;
		} else if (member == null) {
			throw new Exception(
					"Cannot resolve a null complex map refset member.");
		}
		return null;
	}

	/**
	 * Description type refset member ID based on refset id, referenced
	 * component id.
	 */
	@Override
	public String getTerminologyId(DescriptionTypeRefSetMember member)
			throws Exception {

		if (member != null && member.getComponent() != null) {
			String value = member.getRefSetId() + member.getComponent().getId();
			String id = TerminologyUtility.getUuid(value).toString();
			return id;
		} else if (member == null) {
			throw new Exception(
					"Cannot resolve a null description type refset member.");
		}
		return null;
	}

	/**
	 * Language refset member ID based on refset id, referenced component id.
	 */
	@Override
	public String getTerminologyId(LanguageRefSetMember member)
			throws Exception {
		if (member != null && member.getComponent() != null) {
			String value = member.getRefSetId()
					+ member.getComponent().getTerminologyId();
			String id = TerminologyUtility.getUuid(value).toString();
			return id;
		} else if (member == null) {
			throw new Exception("Cannot resolve a null language refset member.");
		}
		return null;
	}

	/**
	 * Module dependency member ID based on refset id, referenced component id.
	 */
	@Override
	public String getTerminologyId(ModuleDependencyRefSetMember member)
			throws Exception {
		if (member != null && member.getComponent() != null) {
			String value = member.getRefSetId() + member.getComponent().getId();
			String id = TerminologyUtility.getUuid(value).toString();
			return id;
		} else if (member == null) {
			throw new Exception(
					"Cannot resolve a null module dependency refset member.");
		}
		return null;
	}

	/**
	 * Refset descriptor member ID based on refset id, referenced component id,
	 * attribute description, attribute type, and attribute order.
	 */
	@Override
	public String getTerminologyId(RefsetDescriptorRefSetMember member)
			throws Exception {
		if (member != null && member.getComponent() != null) {
			String value = member.getRefSetId() + member.getComponent().getId()
					+ member.getAttributeDescription()
					+ member.getAttributeType() + member.getAttributeType();
			String id = TerminologyUtility.getUuid(value).toString();
			return id;
		} else if (member == null) {
			throw new Exception(
					"Cannot resolve a null refset descriptor refset member.");
		}
		return null;
	}

	/**
	 * Simple map member ID based on refset id, referenced component id, map
	 * target.
	 */
	@Override
	public String getTerminologyId(SimpleMapRefSetMember member)
			throws Exception {
		if (member != null && member.getComponent() != null) {
			String value = member.getRefSetId() + member.getComponent().getId()
					+ member.getMapTarget();
			String id = TerminologyUtility.getUuid(value).toString();
			return id;
		} else if (member == null) {
			throw new Exception(
					"Cannot resolve a null simple map refset member.");
		}
		return null;
	}

	/**
	 * Simple member ID based on refset id, referenced component id.
	 */
	@Override
	public String getTerminologyId(SimpleRefSetMember member) throws Exception {
		if (member != null && member.getComponent() != null) {
			String value = member.getRefSetId() + member.getComponent().getId();
			String id = TerminologyUtility.getUuid(value).toString();
			return id;
		} else if (member == null) {
			throw new Exception("Cannot resolve a null simple refset member.");
		}
		return null;
	}

	/**
	 * Transitive relationship id based on super/subtype concepts.
	 */
	@Override
	public String getTerminologyId(TransitiveRelationship relationship)
			throws Exception {
		// always return the uuid - for identity comparisons
		if (relationship != null) {
			String value = relationship.getSuperTypeConcept() + ","
					+ relationship.getSubTypeConcept();
			String id = TerminologyUtility.getUuid(value).toString();
			return id;
		} else {
			throw new Exception(
					"Cannot resolve a null transitive relationship.");
		}
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
