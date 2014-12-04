package org.ihtsdo.otf.ts.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

// TODO: Auto-generated Javadoc
/**
 * JAXB enabled implementation of {@link StringList}.
 */
@XmlRootElement(name = "StringList")
public class StringList extends AbstractResultList<String> implements
    ResultList<String> {

  /** The map Strings. */
  private List<String> strings = new ArrayList<>();

  /**
   * Instantiates a new map String list.
   */
  public StringList() {
    // do nothing
  }

  /**
   * Adds the string.
   *
   * @param string the string
   */
  public void addString(String string) {
    strings.add(string);
  }

  /**
   * Removes the string.
   *
   * @param string the string
   */
  public void removeString(String string) {
    strings.remove(string);
  }

  /**
   * Sets the strings.
   *
   * @param strings the new strings
   */
  public void setStrings(List<String> strings) {
    this.strings = new ArrayList<>();
    if (strings != null) {
      this.strings.addAll(strings);
    }
  }

  /**
   * Gets the strings.
   *
   * @return the strings
   */
  @XmlElement(type = String.class, name = "string")
  public List<String> getStrings() {
    return strings;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.ResultList#getCount()
   */
  public int getCount() {
    return strings.size();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.ResultList#sortBy(java.util.Comparator)
   */
  public void sortBy(Comparator<String> comparator) {
    Collections.sort(strings, comparator);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.ResultList#contains(java.lang.Object)
   */
  public boolean contains(String element) {
    return strings.contains(element);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.ResultList#getIterable()
   */
  @XmlTransient
  public Iterable<String> getIterable() {
    return strings;
  }

}
