package org.ihtsdo.otf.ts.helpers;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB enabled implementation of a {@link SearchCriteria}.
 */
@XmlRootElement(name = "searchCriteria")
public class SearchCriteriaJpa implements SearchCriteria {

  /** The id. */
  @Id
  @GeneratedValue
  private Long id;

  /** The active only. */
  private boolean findActiveOnly;

  /** The destination id. */
  private String destinationId;

  /** The module id. */
  private String findByModuleId;

  /** The relationship descendants. */
  private boolean relationshipDescendants;

  /** The relationship type id. */
  private String relationshipTypeId;

  /** The source id. */
  private String sourceId;

  /** The descendants. */
  private boolean findDescendants;

  /** The defined only. */
  private boolean findDefinedOnly;

  /** The inactive only. */
  private boolean findInactiveOnly;

  /** The primitive only. */
  private boolean findPrimitiveOnly;

  /** The self. */
  private boolean findSelf;

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
    findActiveOnly = searchCriteria.getFindActiveOnly();
    destinationId = searchCriteria.getFindByDestinationId();
    findByModuleId = searchCriteria.getFindByModuleId();
    relationshipDescendants = searchCriteria.getFindByRelationshipDescendants();
    relationshipTypeId = searchCriteria.getFindByRelationshipTypeId();
    sourceId = searchCriteria.getFindBySourceId();
    findDefinedOnly = searchCriteria.getFindDefinedOnly();
    findDescendants = searchCriteria.getFindDescendants();
    findInactiveOnly = searchCriteria.getFindInactiveOnly();
    findPrimitiveOnly = searchCriteria.getFindPrimitiveOnly();
    findSelf = searchCriteria.getFindSelf();
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
    return findActiveOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#setFindActiveOnly(boolean)
   */
  @Override
  public void setFindActiveOnly(boolean activeOnly) {
    this.findActiveOnly = activeOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#getFindInactiveOnly()
   */
  @Override
  public boolean getFindInactiveOnly() {
    return findInactiveOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#setFindInactiveOnly(boolean)
   */
  @Override
  public void setFindInactiveOnly(boolean inactiveOnly) {
    this.findInactiveOnly = inactiveOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#getFindByModuleId()
   */
  @Override
  public String getFindByModuleId() {
    return findByModuleId;
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
    this.findByModuleId = moduleId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#getFindDescendants()
   */
  @Override
  public boolean getFindDescendants() {
    return findDescendants;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#setFindDescendants(boolean)
   */
  @Override
  public void setFindDescendants(boolean descendants) {
    this.findDescendants = descendants;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#getFindSelf()
   */
  @Override
  public boolean getFindSelf() {
    return findSelf;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#setFindSelf(boolean)
   */
  @Override
  public void setFindSelf(boolean self) {
    this.findSelf = self;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#getFindPrimitiveOnly()
   */
  @Override
  public boolean getFindPrimitiveOnly() {
    return findPrimitiveOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#setFindPrimitiveOnly(boolean)
   */
  @Override
  public void setFindPrimitiveOnly(boolean primitiveOnly) {
    this.findPrimitiveOnly = primitiveOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#getFindDefinedOnly()
   */
  @Override
  public boolean getFindDefinedOnly() {
    return findDefinedOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.SearchCriteria#setFindDefinedOnly(boolean)
   */
  @Override
  public void setFindDefinedOnly(boolean definedOnly) {
    this.findDefinedOnly = definedOnly;
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
    result = prime * result + (findActiveOnly ? 1231 : 1237);
    result = prime * result + (findDefinedOnly ? 1231 : 1237);
    result = prime * result + (findDescendants ? 1231 : 1237);
    result =
        prime * result
            + ((destinationId == null) ? 0 : destinationId.hashCode());
    result = prime * result + (findInactiveOnly ? 1231 : 1237);
    result = prime * result + ((findByModuleId == null) ? 0 : findByModuleId.hashCode());
    result = prime * result + (findPrimitiveOnly ? 1231 : 1237);
    result = prime * result + (relationshipDescendants ? 1231 : 1237);
    result =
        prime
            * result
            + ((relationshipTypeId == null) ? 0 : relationshipTypeId.hashCode());
    result = prime * result + (findSelf ? 1231 : 1237);
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
    if (findActiveOnly != other.findActiveOnly)
      return false;
    if (findDefinedOnly != other.findDefinedOnly)
      return false;
    if (findDescendants != other.findDescendants)
      return false;
    if (destinationId == null) {
      if (other.destinationId != null)
        return false;
    } else if (!destinationId.equals(other.destinationId))
      return false;
    if (findInactiveOnly != other.findInactiveOnly)
      return false;
    if (findByModuleId == null) {
      if (other.findByModuleId != null)
        return false;
    } else if (!findByModuleId.equals(other.findByModuleId))
      return false;
    if (findPrimitiveOnly != other.findPrimitiveOnly)
      return false;
    if (relationshipDescendants != other.relationshipDescendants)
      return false;
    if (relationshipTypeId == null) {
      if (other.relationshipTypeId != null)
        return false;
    } else if (!relationshipTypeId.equals(other.relationshipTypeId))
      return false;
    if (findSelf != other.findSelf)
      return false;
    if (sourceId == null) {
      if (other.sourceId != null)
        return false;
    } else if (!sourceId.equals(other.sourceId))
      return false;
    return true;
  }
}
