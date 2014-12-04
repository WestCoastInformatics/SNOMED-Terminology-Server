package org.ihtsdo.otf.ts.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * JAXB-enabled implementation of {@link SearchResultList}.
 */
@XmlRootElement(name = "searchResultList")
public class SearchResultListJpa extends AbstractResultList<SearchResult>
    implements SearchResultList {


  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getObjects()
   */
  @Override
  @XmlElement(type = SearchResultJpa.class, name = "searchResult")
  public List<SearchResult> getObjects() {
    return super.getObjects();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SearchResultListJpa [searchResults=" + getObjects()
        + ", getCount()=" + getCount() + "]";
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getIterable()
   */
  @XmlTransient
  @Override
  public Iterable<SearchResult> getIterable() {
    return super.getIterable();
  }

}
