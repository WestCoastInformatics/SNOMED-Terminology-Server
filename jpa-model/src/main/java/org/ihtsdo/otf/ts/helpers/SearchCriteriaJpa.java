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
    return (id == null ? "" : id.toString());
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


  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (activeOnly ? 1231 : 1237);
    result = prime * result + (definedOnly ? 1231 : 1237);
    result = prime * result + (descendants ? 1231 : 1237);
    result =
        prime * result
            + ((destinationId == null) ? 0 : destinationId.hashCode());
    result = prime * result + (inactiveOnly ? 1231 : 1237);
    result = prime * result + ((moduleId == null) ? 0 : moduleId.hashCode());
    result = prime * result + (primitiveOnly ? 1231 : 1237);
    result = prime * result + (relationshipDescendants ? 1231 : 1237);
    result =
        prime
            * result
            + ((relationshipTypeId == null) ? 0 : relationshipTypeId.hashCode());
    result = prime * result + (self ? 1231 : 1237);
    result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SearchCriteriaJpa other = (SearchCriteriaJpa) obj;
    if (activeOnly != other.activeOnly)
      return false;
    if (definedOnly != other.definedOnly)
      return false;
    if (descendants != other.descendants)
      return false;
    if (destinationId == null) {
      if (other.destinationId != null)
        return false;
    } else if (!destinationId.equals(other.destinationId))
      return false;
    if (inactiveOnly != other.inactiveOnly)
      return false;
    if (moduleId == null) {
      if (other.moduleId != null)
        return false;
    } else if (!moduleId.equals(other.moduleId))
      return false;
    if (primitiveOnly != other.primitiveOnly)
      return false;
    if (relationshipDescendants != other.relationshipDescendants)
      return false;
    if (relationshipTypeId == null) {
      if (other.relationshipTypeId != null)
        return false;
    } else if (!relationshipTypeId.equals(other.relationshipTypeId))
      return false;
    if (self != other.self)
      return false;
    if (sourceId == null) {
      if (other.sourceId != null)
        return false;
    } else if (!sourceId.equals(other.sourceId))
      return false;
    return true;
  }
}
