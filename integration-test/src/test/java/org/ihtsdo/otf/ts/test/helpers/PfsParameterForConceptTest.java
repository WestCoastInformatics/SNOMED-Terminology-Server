package org.ihtsdo.otf.ts.test.helpers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.SearchResult;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.services.ContentService;

/**
 * Helper testing class for PfsParameter concept tests.
 */
public class PfsParameterForConceptTest {

  /**
   * Test sort.
   *
   * @param results the results
   * @param pfs the pfs
   * @return true, if successful
   * @throws Exception the exception
   */
  @SuppressWarnings({
      "unchecked", "rawtypes"
  })
  public static boolean testSort(SearchResultList results, PfsParameter pfs)
    throws Exception {

    System.out.println("testSort called with search results: ");
    for (SearchResult sr : results.getObjects()) {
      System.out.println("  " + sr.toString());
    }

    // instantiate content service
    ContentService contentService = new ContentServiceJpa();

    Field field = null;
    Class<?> clazz = ConceptJpa.class;

    do {
      try {
        field = clazz.getDeclaredField(pfs.getSortField());
      } catch (NoSuchFieldException e) {
        clazz = clazz.getSuperclass();
      }
    } while (clazz != null && field == null);

    if (field == null)
      throw new Exception("Could not retrieve field " + pfs.getSortField()
          + " for ConceptJpa");

    field.setAccessible(true);

    System.out.println("Found field " + field.getName() + " of type "
        + field.getType().getSimpleName());

    Object prevValue = null;
    Object thisValue = null;

    List<Concept> concepts = new ArrayList<>();

    for (SearchResult sr : results.getObjects()) {
      Concept c =
          contentService.getSingleConcept(sr.getTerminologyId(),
              sr.getTerminology(), sr.getTerminologyVersion());
      concepts.add(c);
    }

    Comparator comparator = null;

  
    switch (field.getType().getSimpleName()) {
      case "int":
        comparator = new Comparator<Integer>() {
          @Override
          public int compare(Integer u1, Integer u2) {
            return u1.compareTo(u2);
          }
        };
      case "Long":
        comparator = new Comparator<Long>() {
          @Override
          public int compare(Long u1, Long u2) {
            return u1.compareTo(u2);
          }
        };
        break;
      case "String":
        comparator = new Comparator<String>() {
          @Override
          public int compare(String u1, String u2) {
            return u1.compareTo(u2);
          }
        };

        break;
      default:
        Logger.getLogger(PfsParameterForConceptTest.class).info(
            "  Concept does not support testing sorting on field type "
                + field.getType().getSimpleName());
        return false;
    }

    for (Concept c : concepts) {

      thisValue = field.get(c);
      
      // if not the first value
      if (prevValue != null) {
        
        // test ascending case
        if (pfs.isAscending() && (comparator.compare(thisValue, prevValue) < 0)) {
          return false;
        }

        // test descending case
        else if (!pfs.isAscending()
            && comparator.compare(prevValue, thisValue) < 0) {
          return false;
        }
      }

      prevValue = thisValue;

    }
    return true;
  }

  /**
   * Test paging.
   *
   * @param results the results
   * @param fullResults the full results
   * @param pfs the pfs
   * @return true, if successful
   */
  public static boolean testPaging(SearchResultList results,
    SearchResultList fullResults, PfsParameter pfs) {
    // check results size, must be less than or equal to page size

    int page = (int) (Math.floor(pfs.getStartIndex() / pfs.getMaxResults()) + 1);
    int pageSize = pfs.getMaxResults();
    
    System.out.println("List:");
    for (SearchResult sr : results.getObjects()) {
      System.out.println("  " + sr.toString());
    }
    System.out.println("Full List:");
    for (SearchResult sr : fullResults.getObjects()) {
      System.out.println("  " + sr.toString());
    }
    if (results.getCount() > pageSize)
      return false;

    // check bounds
    if ((page - 1) * pageSize < 0)
      return false;
    if ((page - 1) * pageSize + results.getCount() > fullResults
        .getTotalCount())
      return false;

    // check paging
    for (int i = 0; i < results.getCount(); i++) {
      if (!results.getObjects().get(i)
          .equals(fullResults.getObjects().get((page - 1) * pageSize + i)))
        return false;
    }

    return true;
  }

  /**
   * Test query.
   *
   * @param results the results
   * @param query the query
   * @return true, if successful
   */
  public static boolean testQuery(SearchResultList results, String query) {

    // another interesting case, would need to extract indexed fields and
    // perform
    // checks on them
    return true;
  }

  /**
   * Test query restriction.
   *
   * @param results the results
   * @param queryRestriction the query restriction
   * @return true, if successful
   */
  public static boolean testQueryRestriction(SearchResultList results,
    String queryRestriction) {
    return true;
  }

}
