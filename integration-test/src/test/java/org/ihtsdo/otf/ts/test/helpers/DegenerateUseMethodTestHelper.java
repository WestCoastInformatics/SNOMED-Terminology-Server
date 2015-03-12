package org.ihtsdo.otf.ts.test.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.ihtsdo.otf.ts.helpers.AbstractResultList;
import org.ihtsdo.otf.ts.helpers.LocalException;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;

public class DegenerateUseMethodTestHelper {

  /**
   * Test a object's method for invalid and null values Assumes invalid values
   * are -1 (as String, Long, int) Also tests PfsParameter
   * @param obj
   * @param method
   * @param validParameters
   * @throws Exception thrown if any unexpected behavior occurs
   */
  public static void testDegenerateArguments(Object obj, Method method,
    Object[] validParameters) throws Exception, LocalException {
    System.out.println("Testing " + obj.getClass().getName() + ", method "
        + method.getName());
    testDegenerateArguments(obj, method, validParameters, null);
  }

  public static void testDegenerateArguments(Object obj, Method method,
    Object[] validParameters, boolean[] isFieldValueTested) throws Exception {

    // check assumptions
    if (obj == null)
      throw new Exception("Class to test method for not specified");
    if (method == null)
      throw new Exception("Method to test not specified");
    if (validParameters != null && isFieldValueTested != null) {
      if (validParameters.length != isFieldValueTested.length)
        throw new Exception(
            "Specified list of whether to test field values does not match length of list of parameters");
    }

    // first invoke the method with correct methods to ensure properly invoked
    try {
      method.invoke(obj, validParameters);
    } catch (Exception e) {
      throw new Exception("Could not validate method with valid parameters, error thrown");
    }

    // construct the base valid parameter list
    List<Object> validParameterList = new ArrayList<>();
    for (int i = 0; i < validParameters.length; i++)
      validParameterList.add(validParameters[i]);

    // cycle over parameters
    for (int i = 0; i < validParameters.length; i++) {

      if (isFieldValueTested != null && isFieldValueTested[i] == false) {
        System.out.println("  Skipping parameter " + i);
      } else {
        System.out.println("  Testing parameter " + i);

        // instantiate parameters list from base valid parameter list
        List<Object> parameters = new ArrayList<>(validParameterList);

        System.out.println("  Valid parameters: "
            + validParameterList.toString());

        // the invalid value to test with
        Object invalidValue = null;

        // construct the bad parameter based on type (supports String, Long,
        // Integer)
        // PfsParameter handled below
        Class<? extends Object> parameterType = validParameters[i].getClass();
        if (parameterType.equals(String.class)) {
          invalidValue = parameterType.cast(" ");
        } else if (parameterType.equals(Long.class)
            || parameterType.equals(long.class)) {
          invalidValue = parameterType.cast(-5L);
        } else if (parameterType.equals(Integer.class)
            || parameterType.equals(int.class)) {
          invalidValue = parameterType.cast(-5);
        }

        if (!parameterType.equals(PfsParameterJpa.class)
            && !parameterType.equals(PfsParameter.class)) {

          if (isFieldValueTested != null) {
            System.out.println("Field value tested = " + isFieldValueTested[i]);
          }

          // replace the bad parameter
          parameters.set(i, invalidValue);
          invoke(obj, method, parameters.toArray());

          // if not primitive, test null
          if (!parameterType.isPrimitive()) {
            parameters.set(i, null);
            invoke(obj, method, parameters.toArray());
          }

          // pfs parameter testing
        } else {

          PfsParameter pfs;

          pfs = new PfsParameterJpa((PfsParameter) validParameters[i]);

          // test invalid sort field (does not exist)
          pfs.setSortField("-");
          parameters.set(i, pfs);
          System.out.println("  Testing with invalid pfs: " + pfs.toString());

          invoke(obj, method, parameters.toArray());

          // test invalid start index (< -1)
          pfs = new PfsParameterJpa((PfsParameter) validParameters[i]);
          pfs.setStartIndex(-5);
          pfs.setMaxResults(10);
          parameters.set(i, pfs);
          System.out.println("  Testing with invalid pfs: " + pfs.toString());

          invoke(obj, method, parameters.toArray());

          // test bad query restriction (bad lucene syntax)
          pfs = new PfsParameterJpa((PfsParameter) validParameters[i]);
          pfs.setQueryRestriction("BAD_SYNTAX*:*!~bad");
          parameters.set(i, pfs);
          System.out.println("  Testing with invalid pfs: " + pfs.toString());

          invoke(obj, method, parameters.toArray());
        }
      }
    }
  }

  private static void invoke(Object obj, Method method, Object[] parameters)
    throws Exception {
    String debugStr = "";
    for (Object o : parameters) {
      debugStr += " " + (o == null ? "null" : o.toString());

    }
    System.out.println("    Testing with parameters: " + debugStr);

    try {
      method.invoke(obj, parameters);

      // if successful, throw new LocalException, caught and rethrown below
      throw new Exception(
          "Method invocation with invalid parameters did not throw expected exception");
    } catch (IllegalAccessException | IllegalArgumentException e) {
      throw new Exception("Failed to correctly invoke method");
    } catch (InvocationTargetException e) {
      System.out.println("      Failed (expected)");
    } catch (LocalException e) {
      throw new Exception(e.getMessage());
    }
  }

}
