package org.ihtsdo.otf.ts.helpers;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

// TODO: Auto-generated Javadoc
/**
 * JAXB enabled implementation of a {@link ReleaseInfo}.
 */
@XmlRootElement(name = "searchCriteria")
public class SearchCriteriaJpa implements SearchCriteria {

  /** The id. */
  @Id
  @GeneratedValue
  private Long id;

  /** The active only. */
  private boolean activeOnly;

  /** The destination id. */
  private String destinationId;

  /** The module id. */
  private String moduleId;

  /** The relationship descendants. */
  private boolean relationshipDescendants;

  /** The relationship type id. */
  private String relationshipTypeId;

  /** The source id. */
  private String sourceId;

  /** The descendants. */
  private boolean descendants;

  /** The defined only. */
  private boolean definedOnly;

  /** The inactive only. */
  private boolean inactiveOnly;

  /** The primitive only. */
  private boolean primitiveOnly;

  /** The self. */
  private boolean self;

  /**
   * Instantiates an empty {@link SearchCriteriaJpa}.
   */
  public SearchCriteriaJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link SearchCriteriaJpa} from the specified parameters.
   *
   * @param searchCriteria the search criteria
   */
  public SearchCriteriaJpa(SearchCriteria searchCriteria) {
    activeOnly = searchCriteria.getFindActiveOnly();
    destinationId = searchCriteria.getFindByDestinationId();
    moduleId = searchCriteria.getFindByModuleId();
    relationshipDescendants = searchCriteria.getFindByRelationshipDescendants();
    relationshipTypeId = searchCriteria.getFindByRelationshipTypeId();
    sourceId = searchCriteria.getFindBySourceId();
    definedOnly = searchCriteria.getFindDefinedOnly();
    descendants = searchCriteria.getFindDescendants();
    inactiveOnly = searchCriteria.getFindInactiveOnly();
    primitiveOnly = searchCriteria.getFindPrimitiveOnly();
    self = searchCriteria.getFindSelf();
  }

  /**
   * ID for XML serialization.
   *
   * @return the object id
   */
  @XmlID
  public String getObjectId() {
    return this.id.toString();
  }

  /**
   * Sets the object id.
   *
   * @param id the object id
   */
  public void setObjectId(String id) {
    this.id = Long.valueOf(id);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#getId()
   */
  @Override
  public Long getId() {
    return id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#setId(java.lang.Long)
   */
  @Override
  public void setId(Long id) {
    this.id = id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#getFindActiveOnly()
   */
  @Override
  public boolean getFindActiveOnly() {
    return activeOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#setFindActiveOnly(boolean)
   */
  @Override
  public void setFindActiveOnly(boolean activeOnly) {
    this.activeOnly = activeOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#getFindInactiveOnly()
   */
  @Override
  public boolean getFindInactiveOnly() {
    return inactiveOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#setFindInactiveOnly(boolean)
   */
  @Override
  public void setFindInactiveOnly(boolean inactiveOnly) {
    this.inactiveOnly = inactiveOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#getFindByModuleId()
   */
  @Override
  public String getFindByModuleId() {
    return moduleId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.helpers.SearchCriteria#setFindByModuleId(java.lang.String
   * )
   */
  @Override
  public void setFindByModuleId(String moduleId) {
    this.moduleId = moduleId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#getFindDescendants()
   */
  @Override
  public boolean getFindDescendants() {
    return descendants;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#setFindDescendants(boolean)
   */
  @Override
  public void setFindDescendants(boolean descendants) {
    this.descendants = descendants;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#getFindSelf()
   */
  @Override
  public boolean getFindSelf() {
    return self;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#setFindSelf(boolean)
   */
  @Override
  public void setFindSelf(boolean self) {
    this.self = self;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#getFindPrimitiveOnly()
   */
  @Override
  public boolean getFindPrimitiveOnly() {
    return primitiveOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#setFindPrimitiveOnly(boolean)
   */
  @Override
  public void setFindPrimitiveOnly(boolean primitiveOnly) {
    this.primitiveOnly = primitiveOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#getFindDefinedOnly()
   */
  @Override
  public boolean getFindDefinedOnly() {
    return definedOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#setFindDefinedOnly(boolean)
   */
  @Override
  public void setFindDefinedOnly(boolean definedOnly) {
    this.definedOnly = definedOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#getFindBySourceId()
   */
  @Override
  public String getFindBySourceId() {
    return sourceId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#getFindByRelationshipTypeId()
   */
  @Override
  public String getFindByRelationshipTypeId() {
    return relationshipTypeId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#getFindByDestinationId()
   */
  @Override
  public String getFindByDestinationId() {
    return destinationId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.helpers.SearchCriteria#getFindByRelationshipDescendants()
   */
  @Override
  public boolean getFindByRelationshipDescendants() {
    return relationshipDescendants;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.helpers.SearchCriteria#setFindSourceOfRelationship(java
   * .lang.String, java.lang.String, boolean)
   */
  @Override
  public void setFindSourceOfRelationship(String typeId, String destinationId,
    boolean descendants) {
    this.relationshipTypeId = typeId;
    this.destinationId = destinationId;
    this.relationshipDescendants = descendants;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.helpers.SearchCriteria#setFindDestinationOfRelationship
   * (java.lang.String, java.lang.String, boolean)
   */
  @Override
  public void setFindDestinationOfRelationship(String typeId, String sourceId,
    boolean descendants) {
    this.relationshipTypeId = typeId;
    this.sourceId = sourceId;
    this.relationshipDescendants = descendants;
  }

}
