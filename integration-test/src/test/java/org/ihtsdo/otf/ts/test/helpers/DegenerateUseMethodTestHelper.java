/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.test.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.LocalException;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.ResultList;

/**
 * Testing class for degenerate use test helper.
 */
public class DegenerateUseMethodTestHelper {

  /**
   * The Enum ExpectedFailure.
   */
  public enum ExpectedFailure {

    /** Use where exception expected. */
    EXCEPTION,

    /** Use to skip testing a parameter */
    SKIP,

    /** Use this if no failure is expected */
    NONE,

    /** Use to test successful call, but erroneous empty result list. */
    NO_RESULTS,

    /**
     * Use when invalid long value produces null instead of throwing exception;
     * null still throws exception
     */
    LONG_INVALID_NO_RESULTS_NULL_EXCEPTION,

    /**
     * Use when empty string throws exception and null throws exception
     * (identical to EXCEPTION)
     */
    STRING_INVALID_EXCEPTION_NULL_EXCEPTION,

    /**
     * Use when empty string throws exceptions and null returns erroneous no
     * results
     */
    STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,

    /** Use when empty string throws exception and null succeeds */
    STRING_INVALID_EXCEPTION_NULL_SUCCESS,

    /**
     * Use when empty string returns erroneous no results and null throws
     * exception
     */
    STRING_INVALID_NO_RESULTS_NULL_EXCEPTION,

    /**
     * Use when empty string returns erroneous no results and null returns
     * erroneous null results
     */
    STRING_INVALID_NO_RESULTS_NULL_NO_RESULTS,

    /** Use when empty string returns erroneous no results and null succeeds */
    STRING_INVALID_NO_RESULTS_NULL_SUCCESS,

    /** Use when empty string succeeds and null throws exception */
    STRING_INVALID_SUCCESS_NULL_EXCEPTION,

    /** Use when empty string succeeds and null returns erroneous null results */
    STRING_INVALID_SUCCESS_NULL_NO_RESULTS,

    /** Use when empty string succeeds and null string succeeds */
    STRING_INVALID_SUCCESS_NULL_SUCCESS,

  }

  /**
   * Test degenerate method use with default behavior.
   *
   * @param obj the obj
   * @param method the method
   * @param validParameters the valid parameters
   * @throws Exception thrown if any unexpected behavior occurs
   * @throws LocalException the local exception
   */
  public static void testDegenerateArguments(Object obj, Method method,
    Object[] validParameters) throws Exception, LocalException {

    testDegenerateArguments(obj, method, validParameters, null, null);
  }

  /**
   * Test degenerate method use with default invalid values and specified
   * failure behavior.
   *
   * @param obj the obj
   * @param method the method
   * @param validParameters the valid parameters
   * @param expectedFailures the expected failures
   * @throws Exception the exception
   * @throws LocalException the local exception
   */
  public static void testDegenerateArguments(Object obj, Method method,
    Object[] validParameters, ExpectedFailure[] expectedFailures)
    throws Exception, LocalException {

    testDegenerateArguments(obj, method, validParameters, null,
        expectedFailures);
  }

  /**
   * Test degenerate arguments with specified (non-default) invalid parameters.
   *
   * @param obj the obj
   * @param method the method
   * @param validParameters the valid parameters
   * @param invalidParameters the invalid parameters
   * @throws Exception the exception
   * @throws LocalException the local exception
   */
  public static void testDegenerateArguments(Object obj, Method method,
    Object[] validParameters, Object[] invalidParameters) throws Exception,
    LocalException {

    testDegenerateArguments(obj, method, validParameters, invalidParameters,
        null);
  }

  /**
   * Test degenerate arguments with fully specified invalid parameters and
   * failure behavior.
   *
   * @param obj the obj
   * @param method the method
   * @param validParameters the valid parameters
   * @param invalidParameters the invalid parameters
   * @param expectedFailures the expected failures
   * @throws Exception the exception
   */
  @SuppressWarnings("null")
  public static void testDegenerateArguments(Object obj, Method method,
    Object[] validParameters, Object[] invalidParameters,
    ExpectedFailure[] expectedFailures) throws Exception {

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
    if (validParameters != null && invalidParameters != null) {
      if (validParameters.length != invalidParameters.length)
        throw new Exception(
            "Specified list of invalid parameter values does not match length of list of parameters");
    }

    Logger.getLogger(DegenerateUseMethodTestHelper.class).info(
        "Testing " + obj.getClass().getName() + ", method " + method.getName());

    // first invoke the method with correct methods to ensure properly invoked
    try {
      method.invoke(obj, validParameters);
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception(
          "Could not validate method with valid parameters, testing halted");
    }

    // construct the base valid parameter list
    List<Object> validParameterList = new ArrayList<>();
    for (int i = 0; i < validParameters.length; i++)
      validParameterList.add(validParameters[i]);

    // construct the base invalid parameter list
    List<Object> invalidParameterList = new ArrayList<>();

    // if no invalid parameters specified, construct defaults
    if (invalidParameters == null) {

      for (Object validParameter : validParameterList) {

        Class<?> parameterType = validParameter.getClass();
        Object invalidParameter = null;

        if (parameterType.equals(String.class)) {
          invalidParameter = new String("");
        } else if (parameterType.equals(Long.class)
            || parameterType.equals(long.class)) {
          invalidParameter = -5L;
        } else if (parameterType.equals(Integer.class)
            || parameterType.equals(int.class)) {
          invalidParameter = -5;
        }

        invalidParameterList.add(parameterType.cast(invalidParameter));
      }
    } else {
      for (int i = 0; i < validParameters.length; i++) {
        invalidParameterList.add(invalidParameters[i]);
      }
    }

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
      Object invalidValue = invalidParameterList.get(i);

      // the class of this parameter
      Class<? extends Object> parameterType = validParameters[i].getClass();

      Logger.getLogger(DegenerateUseMethodTestHelper.class).info(
          "Object parameter tested of type " + parameterType.toString()
              + " with expected failure mode " + expectedFailure);

      // if not a pfs parameter, test object
      if (!parameterType.equals(PfsParameterJpa.class)
          && !parameterType.equals(PfsParameter.class)) {

        // if parameter not null, replace the bad parameter
        if (invalidValue != null) {
          parameters.set(i, invalidValue);
          invoke(obj, method, parameters.toArray(), invalidValue,
              expectedFailure);
        }

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
        pfs.setQueryRestriction("-");
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
  @SuppressWarnings("null")
  private static void invoke(Object obj, Method method, Object[] parameters,
    Object parameter, ExpectedFailure expectedFailure) throws Exception {

    if (expectedFailure.equals(ExpectedFailure.SKIP)) {
      // do nothing, skip this test
    } else {
      Logger.getLogger(DegenerateUseMethodTestHelper.class).info(
          "Testing value "
              + (parameter == null ? "null" : parameter.toString()));

      try {
        Object result = method.invoke(obj, parameters);

        Logger.getLogger(DegenerateUseMethodTestHelper.class).info(
            "Call succeeded for tested value "
                + (parameter == null ? "null" : parameter.toString()));

        // switch on expected failure type -- NOTE: exception types are handled
        // below, SKIP handled above
        switch (expectedFailure) {
          case EXCEPTION:
            throw new Exception("Test did not throw expected exception");
          case LONG_INVALID_NO_RESULTS_NULL_EXCEPTION:
            // check that result returned is null
            if (parameter == null) {
              throw new Exception("Test did not throw expected exception");
            } else {
              if (!isEmptyObject(result)) {
                throw new Exception(
                    "Test expecting no results returned objects");
              }
            }
            break;
          case NONE:
            // do nothing
            break;
          case NO_RESULTS:
            // check that no results returned
            if (isEmptyObject(result)) {
              throw new Exception("Test expecting no results returned objects");
            }
            break;
          case SKIP:
            // empty code for completeness (skip tested above)
            break;
          case STRING_INVALID_EXCEPTION_NULL_EXCEPTION:
            throw new Exception("Expected exception not thrown");
          case STRING_INVALID_EXCEPTION_NULL_NO_RESULTS:
            if (parameter == null && !isEmptyObject(result)) {
              throw new Exception(
                  "Null parameter expecting no results returned objects");
            } else if (parameter != null) {
              throw new Exception(
                  "Invalid parameter did not throw expected exception");
            }
            break;
          case STRING_INVALID_EXCEPTION_NULL_SUCCESS:
            if (parameter != null) {
              throw new Exception(
                  "Invalid parameter did not throw expected exception");
            }
            break;
          case STRING_INVALID_NO_RESULTS_NULL_EXCEPTION:
            if (parameter == null) {
              throw new Exception(
                  "Null parameter did not throw expected exception");
            } else if (!isEmptyObject(result)) {
              throw new Exception(
                  "Invalid parameter expecting no results returned objects");
            }
            break;
          case STRING_INVALID_NO_RESULTS_NULL_NO_RESULTS:
            if (parameter == null && !isEmptyObject(result)) {
              throw new Exception(
                  "Null parameter expecting no results returned objects");
            } else if (parameter != null && !isEmptyObject(result)) {
              throw new Exception(
                  "Invalid parameter expecting no results returned objects");
            }
            break;
          case STRING_INVALID_NO_RESULTS_NULL_SUCCESS:
            if (parameter != null && !isEmptyObject(result)) {
              throw new Exception(
                  "Invalid parameter expecting no results returned objects");
            }
            break;
          case STRING_INVALID_SUCCESS_NULL_EXCEPTION:
            if (parameter == null) {
              throw new Exception(
                  "Null parameter did not throw expected exception");
            }
            break;
          case STRING_INVALID_SUCCESS_NULL_NO_RESULTS:
            if (parameter == null && !isEmptyObject(result)) {
              throw new Exception(
                  "Null parameter expecting no results returned objects");
            }
            break;
          case STRING_INVALID_SUCCESS_NULL_SUCCESS:
            // do nothing
            break;
          default:
            break;

        }

      } catch (IllegalAccessException | IllegalArgumentException e) {
        throw new Exception("Failed to correctly invoke method");
      } catch (InvocationTargetException e) {

        switch (expectedFailure) {
          case EXCEPTION:
            // do nothing
            break;
          case LONG_INVALID_NO_RESULTS_NULL_EXCEPTION:
            if (parameter != null) {
              throw new Exception("Parameter threw unexpected exception");
            }
            break;
          case NONE:
            throw new Exception("Parameter threw unexpected exception");

          case NO_RESULTS:
            throw new Exception("Parameter threw unexpected exception");

          case SKIP:
            // do nothing
            break;
          case STRING_INVALID_EXCEPTION_NULL_EXCEPTION:
            // do nothing
            break;
          case STRING_INVALID_EXCEPTION_NULL_NO_RESULTS:
            if (parameter == null) {
              throw new Exception("Parameter threw unexpected exception");
            }
            break;
          case STRING_INVALID_EXCEPTION_NULL_SUCCESS:
            if (parameter == null) {
              throw new Exception("Parameter threw unexpected exception");
            }
            break;
          case STRING_INVALID_NO_RESULTS_NULL_EXCEPTION:
            if (parameter != null) {
              throw new Exception("Parameter threw unexpected exception");
            }
            break;
          case STRING_INVALID_NO_RESULTS_NULL_NO_RESULTS:
            throw new Exception("Parameter threw unexpected exception");
          case STRING_INVALID_NO_RESULTS_NULL_SUCCESS:
            throw new Exception("Parameter threw unexpected exception");
          case STRING_INVALID_SUCCESS_NULL_EXCEPTION:
            if (parameter != null) {
              throw new Exception("Parameter threw unexpected exception");
            }
          case STRING_INVALID_SUCCESS_NULL_NO_RESULTS:
            throw new Exception("Parameter threw unexpected exception");
          case STRING_INVALID_SUCCESS_NULL_SUCCESS:
            throw new Exception("Parameter threw unexpected exception");
          default:
            throw new Exception("Parameter threw unexpected exception");

        }

      } catch (LocalException e) {
        throw new Exception(e.getMessage());
      }
    }
  }

  /**
   * helper function to take an object and determine if it is non-null, but
   * empty
   *
   * @param o the o
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private static boolean isEmptyObject(Object o) {

    // return false if not-null, object is not semantically empty
    if (o == null)
      return false;

    // if a collection, check size is 0
    if (o instanceof Collection<?>) {
      return ((Collection<?>) o).size() == 0;
    }

    // if a result list, check total count is 0
    if (o instanceof ResultList<?>) {
      return ((ResultList<?>) o).getTotalCount() == 0;
    }

    // if any other non-null object, not semantically empty
    return false;
  }
}
