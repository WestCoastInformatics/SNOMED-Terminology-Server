package org.ihtsdo.otf.ts.test.helpers;

import java.lang.reflect.Method;
import java.util.List;

import org.ihtsdo.otf.ts.helpers.IndexUtility;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.ResultList;
import org.ihtsdo.otf.ts.helpers.SearchResult;
import org.ihtsdo.otf.ts.helpers.SearchResultList;

/**
 * Helper class to automatically test paging and sorting based on a provided
 * query
 * @author Patrick
 *
 */
public class PfsParameterTestHelper {

  /**
   * Test paging and sorting.
   *
   * @param service the service containig the method
   * @param method the method returning a ResultList
   * @param parameters the parameters used by the method
   * @return true, if successful
   * @throws Exception the exception
   */
  @SuppressWarnings("unchecked")
  public static boolean testPagingAndSorting(Object service, Method method,
    Object[] parameters, Class clazz, SearchResultList fullResults)
    throws Exception {

    // CHECK: Method has a PfsParameter argument
    boolean hasPfsParameter = false;
    for (Class<?> parameterType : method.getParameterTypes()) {
      if (parameterType.equals(PfsParameterJpa.class)) {
        hasPfsParameter = true;
      }
    }
    if (!hasPfsParameter)
      throw new Exception(
          "Cannot test paging and sorting, method does not take PFS parameter");

    // CHECK: One of parameters is a PFS Parameter object
    int pfsPosition = -1;
    for (int i = 0; i < parameters.length; i++) {
      if (parameters[i].getClass().equals(PfsParameterJpa.class))
        pfsPosition = i;
    }
    if (pfsPosition == -1) {
      throw new Exception(
          "Cannot test paging and sorting, parameters supplied do not include a PFS parameter");
    }

    // CHECK: Results are supplied
    if (fullResults == null || fullResults.getCount() == 0) {
      throw new Exception("Could not test paging/sorting due to no results");
    }

    // Get the initial pfs parameter
    PfsParameter localPfs = (PfsParameter) parameters[pfsPosition];

    // force initial call to ascending with default sort field, with no paging
    // parameters
    localPfs.setAscending(true);
    localPfs.setSortField(null);
    localPfs.setStartIndex(-1);
    localPfs.setMaxResults(-1);

    // Set local parameters
    Object[] localParameters = parameters;
    localParameters[pfsPosition] = localPfs;

    // set page size to 1 for page testing
    localPfs.setMaxResults(1);

    // test paging
    for (int startIndex = 0; startIndex < fullResults.getTotalCount(); startIndex++) {

      localPfs.setStartIndex(startIndex);
      localParameters[pfsPosition] = localPfs;

      SearchResultList resultsPaged =
          (SearchResultList) method.invoke(service, localParameters);

      // check total count is correct
      if (resultsPaged.getTotalCount() != fullResults.getTotalCount()) {
        throw new Exception("Paging results in differing total count");
      }

      for (int i = 0; i < resultsPaged.getObjects().size(); i++) {
        if (!resultsPaged.getObjects().get(i)
            .equals(fullResults.getObjects().get(localPfs.getStartIndex() + i))) {
          throw new Exception("Unequal objects found after paging");
        }
      }
    }

    // test example of sorting (using terminology id)

    // call ascending sort on this field
    localPfs.setAscending(true);
    localPfs.setSortField("terminologyId");
    localPfs.setStartIndex(-1);
    localPfs.setMaxResults(-1);

    SearchResultList results =
        (SearchResultList) method.invoke(service, localParameters);

    for (int i = 1; i < results.getObjects().size(); i++) {
      if (results.getObjects().get(i).getTerminologyId()
          .compareTo(results.getObjects().get(i - 1).getTerminologyId()) < 0) {
        throw new Exception("Ascending sort failed");
      }
    }

    // check descending sort
    localPfs.setAscending(false);
    localPfs.setSortField("terminologyId");
    localPfs.setStartIndex(-1);
    localPfs.setMaxResults(-1);

    results = (SearchResultList) method.invoke(service, localParameters);

    for (int i = 1; i < results.getObjects().size(); i++) {
      if (results.getObjects().get(i).getTerminologyId()
          .compareTo(results.getObjects().get(i - 1).getTerminologyId()) > 0) {
        throw new Exception("Descending sort failed");
      }
    }

    // TODO Decide whether we want to actually test sorting on all indexed
    // fields by retrieving objects

    return true;
  }
}
