package org.ihtsdo.otf.ts.test.helpers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.SearchResult;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.services.ContentService;

public class PfsParameterForConceptTest {

  public static boolean testSort(SearchResultList results, String fieldName)
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
        field = clazz.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
        clazz = clazz.getSuperclass();
      }
    } while (clazz != null && field == null);

    if (field == null)
      throw new Exception("Could not retrieve field " + fieldName
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

    for (Concept c : concepts) {

      thisValue = field.get(c);
      if (prevValue != null) {

        System.out.println("Comparing " + thisValue + " to " + prevValue);

        switch (field.getType().getSimpleName()) {
          case "int":
            if (((Integer) thisValue).compareTo((Integer) prevValue) < 0) {
              System.out.println("  int comparison -> false");
              return false;
            }
            break;
          case "Long":
            if (((Long) thisValue).compareTo((Long) prevValue) < 0) {
              System.out.println("  Long comparison -> false");
              return false;
            }
            break;
          case "String":
            if (((String) thisValue).compareTo((String) prevValue) < 0) {
              System.out.println("  String comparison -> false");
              return false;
            }

            break;
          default:
            Logger.getLogger(PfsParameterForConceptTest.class).info(
                "  PfsParameterForConceptTest does not support testing sorting on field type "
                    + field.getType().getSimpleName());
            return false;
        }
      }

      prevValue = thisValue;

    }
    return true;
  }

  public static boolean testPaging(SearchResultList results,
    SearchResultList fullResults, int page, int pageSize) {
    // check results size, must be less than or equal to page size
    
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

  public static boolean testQuery(SearchResultList results, String query) {

    // another interesting case, would need to extract indexed fields and
    // perform
    // checks on them
    return true;
  }

  public static boolean testQueryRestriction(SearchResultList results,
    String queryRestriction) {
    return true;
  }

}
