package org.ihtsdo.otf.ts.test.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.LocalException;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.ResultList;

// TODO: Auto-generated Javadoc
/**
 * The Class DegenerateUseMethodTestHelper.
 *
 * @author ${author}
 */
public class DegenerateUseMethodTestHelper {

  /**
   * The Enum ExpectedFailure.
   *
   * @author ${author}
   */
  public enum ExpectedFailure {

    /** Use where exception expected. */
    EXCEPTION,

    /** Use to skip testing a parameter */
    SKIP,

    /** Use this if no failure is expected */
    NONE,

    /** Use when empty string throws exception and null throws exception (identical to EXCEPTION) */
    STRING_EMPTY_EXCEPTION_NULL_EXCEPTION,

    /** Use when empty string throws exceptions and null returns erroneous no results */
    STRING_EMPTY_EXCEPTION_NULL_NO_RESULTS, 
    
    /** Use when empty string throws exception and null succeeds */
    STRING_EMPTY_EXCEPTION_NULL_SUCCESS, 
    
    /** Use when empty string returns erroneous no results and null throws exception */
    STRING_EMPTY_NO_RESULTS_NULL_EXCEPTION, 
    
    /** Use when empty string returns erroneous no results and null returns erroneous null results*/
    STRING_EMPTY_NO_RESULTS_NULL_NO_RESULTS, 
    
    /** Use when empty string returns erroneous no results and null succeeds */
    STRING_EMPTY_NO_RESULTS_NULL_SUCCESS, 

    /** Use when empty string succeeds and null throws exception */
    STRING_EMPTY_SUCCESS_NULL_EXCEPTION,
    
    /** Use when empty string succeeds and null returns erroneous null results*/
    STRING_EMPTY_SUCCESS_NULL_NO_RESULTS,
    
    /** Use when empty string succeeds and null string succeeds */
    STRING_EMPTY_SUCCESS_NULL_SUCCESS, 

    /** Use to test successful call, but erroneous empty result list. */
    NO_RESULTS,
  };

  /**
   * Test a object's method for invalid and null values Assumes invalid values
   * are -1 (as String, Long, int) Also tests PfsParameter.
   *
   * @param obj the obj
   * @param method the method
   * @param validParameters the valid parameters
   * @throws Exception thrown if any unexpected behavior occurs
   * @throws LocalException the local exception
   */
  public static void testDegenerateArguments(Object obj, Method method,
    Object[] validParameters) throws Exception, LocalException {

    testDegenerateArguments(obj, method, validParameters, null);
  }

  /**
   * Test degenerate arguments.
   *
   * @param obj the obj
   * @param method the method
   * @param validParameters the valid parameters
   * @param expectedFailures the expected failures
   * @throws Exception the exception
   */
  public static void testDegenerateArguments(Object obj, Method method,
    Object[] validParameters, ExpectedFailure[] expectedFailures)
    throws Exception {

    // check assumptions
    if (obj == null)
      throw new Exception("Class to test method for not specified");
    if (method == null)
      throw new Exception("Method to test not specified");
    if (validParameters != null && expectedFailures != null) {
      if (validParameters.length != expectedFailures.length)
        throw new Exception(
            "Specified list of whether to test field values does not match length of list of parameters");
    }

    Logger.getLogger(DegenerateUseMethodTestHelper.class).info(
        "Testing " + obj.getClass().getName() + ", method " + method.getName());

    // first invoke the method with correct methods to ensure properly invoked
    try {
      method.invoke(obj, validParameters);
    } catch (Exception e) {
      throw new Exception(
          "Could not validate method with valid parameters, error thrown");
    }

    // construct the base valid parameter list
    List<Object> validParameterList = new ArrayList<>();
    for (int i = 0; i < validParameters.length; i++)
      validParameterList.add(validParameters[i]);

    // cycle over parameters
    for (int i = 0; i < validParameters.length; i++) {

      // if expected failures array null, or this value null, expect an
      // Exception
      ExpectedFailure expectedFailure;
      if (expectedFailures == null || expectedFailures[i] == null)
        expectedFailure = ExpectedFailure.EXCEPTION;
      else
        expectedFailure = expectedFailures[i];

      // System.out.println("  Testing parameter " + i);

      // instantiate parameters list from base valid parameter list
      List<Object> parameters = new ArrayList<>(validParameterList);

      // System.out
      // .println("  Valid parameters: " + validParameterList.toString());

      // the invalid value to test with
      Object invalidValue = null;

      // construct the bad parameter based on type (supports String, Long,
      // Integer)
      // PfsParameter handled below
      Class<? extends Object> parameterType = validParameters[i].getClass();
      if (parameterType.equals(String.class)) {
        invalidValue = parameterType.cast("");
      } else if (parameterType.equals(Long.class)
          || parameterType.equals(long.class)) {
        invalidValue = parameterType.cast(-5L);
      } else if (parameterType.equals(Integer.class)
          || parameterType.equals(int.class)) {
        invalidValue = parameterType.cast(-5);
      }

      Logger.getLogger(DegenerateUseMethodTestHelper.class).info(
          "Object parameter tested of type " + parameterType.toString()
              + " with expected failure mode " + expectedFailure);

      // if not a pfs parameter, test object
      if (!parameterType.equals(PfsParameterJpa.class)
          && !parameterType.equals(PfsParameter.class)) {

        // replace the bad parameter
        parameters.set(i, invalidValue);
        invoke(obj, method, parameters.toArray(), invalidValue, expectedFailure);

        // if not primitive, test null
        if (!parameterType.isPrimitive()) {
          parameters.set(i, null);
          invoke(obj, method, parameters.toArray(), null, expectedFailure);
        }

      }

      // pfs parameter testing
      else {

        PfsParameter pfs =
            new PfsParameterJpa((PfsParameter) validParameters[i]);

        // test invalid sort field (does not exist)
        pfs.setSortField("-");
        parameters.set(i, pfs);

        invoke(obj, method, parameters.toArray(), pfs, expectedFailure);

        // test invalid start index (< -1)
        pfs = new PfsParameterJpa((PfsParameter) validParameters[i]);
        pfs.setStartIndex(-5);
        pfs.setMaxResults(10);
        parameters.set(i, pfs);

        invoke(obj, method, parameters.toArray(), pfs, expectedFailure);

        // test bad query restriction (bad lucene syntax)
        pfs = new PfsParameterJpa((PfsParameter) validParameters[i]);
        pfs.setQueryRestriction("BAD_SYNTAX*:*!~bad");
        parameters.set(i, pfs);

        invoke(obj, method, parameters.toArray(), pfs, expectedFailure);

      }
    }
  }

  /**
   * Invoke.
   *
   * @param obj the obj
   * @param method the method
   * @param parameters the parameters
   * @param parameter the parameter
   * @param expectedFailure the expected failure
   * @throws Exception the exception
   */
  private static void invoke(Object obj, Method method, Object[] parameters,
    Object parameter, ExpectedFailure expectedFailure) throws Exception {

    /*
     * String debugStr = ""; for (Object o : parameters) { debugStr += " " + (o
     * == null ? "null" : o.toString());
     * 
     * } System.out.println("  Testing with parameters: " + debugStr); if
     * (parameter == null) System.out.println("    NULL PARAMETER"); else {
     * System.out.println("    class = " + parameter.getClass().toString()); if
     * (parameter.getClass().equals(String.class)) {
     * System.out.println("    empty = " + ((String) parameter).isEmpty()); } }
     */

    if (expectedFailure.equals(ExpectedFailure.SKIP)) {
      // do nothing, skip this test
    } else {
      Logger.getLogger(DegenerateUseMethodTestHelper.class).info(
          "Testing value "
              + (parameter == null ? "null" : parameter.toString()));

      try {
        ResultList<?> results = (ResultList<?>) method.invoke(obj, parameters);

        Logger.getLogger(DegenerateUseMethodTestHelper.class).info(
            "Call succeeded for tested value "
                + (parameter == null ? "null" : parameter.toString()));

        // switch on expected failure type -- NOTE: exception types are handled
        // below, SKIP handled above
        switch (expectedFailure) {

          case NONE:
            // do nothing, expect success
            break;
          case NO_RESULTS:
            // check that no results returned
            if (results.getCount() != 0) {
              throw new Exception("Test expecting no results returned objects");
            }
            break;
          case STRING_EMPTY_NO_RESULTS_NULL_NO_RESULTS:
            // if parameter is string or null
            if (parameter == null || parameter.getClass().equals(String.class)) {

              // check assumption: empty string
              if (((String) parameter).isEmpty() == false) {
                throw new Exception(
                    "Empty string test used non-empty string as parameter");
              }

              // check no results
              if (results.getCount() != 0) {
                throw new Exception(
                    "Test expecting no results returned objects");
              }
            }

            // if parameter non-null and is not string
            else {
              throw new Exception(
                  "Test of String parameter used non-String object");
            }

            break;
          case STRING_EMPTY_NO_RESULTS_NULL_SUCCESS:

            // if parameter is null, do nothing
            if (parameter == null) {
              // do nothing
            }

            // if parameter is non-null and a string
            else if (parameter.getClass().equals(String.class)) {

              // check assumption: empty string
              if (((String) parameter).isEmpty() == false) {
                throw new Exception(
                    "Empty string test used non-empty string as parameter");
              }

              // check no results
              if (results.getCount() != 0) {
                throw new Exception(
                    "Test expecting no results returned objects");
              }
            }

            // if parameter non-null but is not string
            else {
              throw new Exception(
                  "Test of String parameter used non-String object");
            }
          case STRING_EMPTY_SUCCESS_NULL_NO_RESULTS:
            // if parameter null
            if (parameter == null) {

              // check no results
              if (results.getCount() != 0) {
                throw new Exception(
                    "Test expecting no results returned objects");
              }
            }

            // if parameter non-null but is not string
            else if (!parameter.getClass().equals(String.class)) {
              throw new Exception(
                  "Test of String parameter used non-String object");
            }

            // otherwise, empty string succeeds,
            else {
              // check assumption: empty string
              if (((String) parameter).isEmpty() == false) {
                throw new Exception(
                    "Empty string test used non-empty string as parameter");
              }
            }

            break;
          case STRING_EMPTY_SUCCESS_NULL_SUCCESS:
            if (parameter == null) {
              // do nothing
            } else {
              // check assumption: empty string
              if (((String) parameter).isEmpty() == false) {
                throw new Exception(
                    "Empty string test used non-empty string as parameter");

                // do nothing
              }

            }
            break;
          default:
            throw new Exception(
                "Unexpected failure type detected, could not validate results");

        }

        Logger.getLogger(DegenerateUseMethodTestHelper.class).info(
            "  Successful call expected");

      } catch (IllegalAccessException | IllegalArgumentException e) {
        throw new Exception("Failed to correctly invoke method");
      } catch (InvocationTargetException e) {

        switch (expectedFailure) {
          case EXCEPTION:
            // do nothing, expected exception
            break;

          case STRING_EMPTY_EXCEPTION_NULL_EXCEPTION:
            if (parameter == null) {
              // do nothing, expected exception
            }

            else if (parameter.getClass().equals(String.class)) {
              // check assumption: empty string
              if (((String) parameter).isEmpty() == false) {
                throw new Exception(
                    "Empty string test used non-empty string as parameter");
              }

              // do nothing
            } else {
              throw new Exception(
                  "Test expecting String value given non-String parameter");
            }

            break;
          case STRING_EMPTY_EXCEPTION_NULL_NO_RESULTS:
            if (parameter == null) {
              throw new Exception(
                  "Testing string parameter with null value threw an unexpected exception");

            } else if (parameter.getClass().equals(String.class)) {
              // check assumption: empty string
              if (((String) parameter).isEmpty() == false) {
                throw new Exception(
                    "Empty string test used non-empty string as parameter");
              }

              // do nothing
            } else {
              throw new Exception(
                  "Test expecting String value given non-String parameter");
            }
            break;
          case STRING_EMPTY_EXCEPTION_NULL_SUCCESS:
            if (parameter == null) {
              throw new Exception(
                  "Testing string parameter with null value threw an unexpected exception");

            } else if (parameter.getClass().equals(String.class)) {
              // check assumption: empty string
              if (((String) parameter).isEmpty() == false) {
                throw new Exception(
                    "Empty string test used non-empty string as parameter");
              }

              // do nothing
            } else {
              throw new Exception(
                  "Test expecting String value given non-String parameter");
            }
            break;
          case STRING_EMPTY_NO_RESULTS_NULL_EXCEPTION:
            if (parameter == null) {
              // do nothing, expected exception

            } else if (parameter.getClass().equals(String.class)) {
              throw new Exception(
                  "Test expecting no results for empty string threw unexpected exception");
            } else {
              throw new Exception(
                  "Test expecting String value given non-String parameter");
            }
            break;
          case STRING_EMPTY_SUCCESS_NULL_EXCEPTION:
            if (parameter == null) {
              // do nothing, expected exception

            } else if (parameter.getClass().equals(String.class)) {
              throw new Exception(
                  "Test expecting success for empty string threw unexpected exception");
            } else {
              throw new Exception(
                  "Test expecting String value given non-String parameter");
            }
            break;
          default:
            throw new Exception("Call failed (not expected) with value "
                + (parameter == null ? "null" : parameter.toString()));
        }

      } catch (LocalException e) {
        throw new Exception(e.getMessage());
      }
    }
  }
}
