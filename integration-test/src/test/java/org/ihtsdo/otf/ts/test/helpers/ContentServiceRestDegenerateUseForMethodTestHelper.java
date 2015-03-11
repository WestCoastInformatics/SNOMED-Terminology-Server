package org.ihtsdo.otf.ts.test.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.ihtsdo.otf.ts.jpa.client.ContentClientRest;
import org.ihtsdo.otf.ts.jpa.client.MetadataClientRest;
import org.ihtsdo.otf.ts.jpa.client.SecurityClientRest;
import org.ihtsdo.otf.ts.rest.ContentServiceRest;

public class ContentServiceRestDegenerateUseForMethodTestHelper {

  public static void testDegenerateArgumentsForServiceMethod(
    ContentServiceRest service, String methodName, Object[] validParameters,
    Object[] invalidParameters) throws Exception {
    
    System.out.println(validParameters.toString());

    Class<?>[] parameterTypes = new Class<?>[validParameters.length];
    for (int i = 0; i < validParameters.length; i++) {
      parameterTypes[i] = validParameters[i].getClass();
    }
    Method m = service.getClass().getMethod(methodName, parameterTypes);
    System.out.println("Found method " + m.getName());

    for (int i = 0; i < parameterTypes.length; i++) {

      // set the valid parameters
      Object[] parameters = validParameters;

      // set the invalid parameter
      parameters[i] = invalidParameters[i];

      // invoke with invalid parameter
      System.out.println("  Parameters: " + parameters.toString());
      m.invoke(service, (Object) parameters);

      // if not primitive argument, call with null
      if (!validParameters[i].getClass().isPrimitive()) {
        parameters[i] = null;
        m.invoke(service, parameters);

      }
    }

  }

}
