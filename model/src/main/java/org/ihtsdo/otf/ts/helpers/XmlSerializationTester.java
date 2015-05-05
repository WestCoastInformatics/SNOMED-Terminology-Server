/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.helpers;

import org.apache.log4j.Logger;

/**
 * Automates JUnit testing of equals and hashcode methods.
 */
public class XmlSerializationTester extends ProxyTester {

  /**
   * Constructs a new getter/setter tester to test objects of a particular
   * class.
   * 
   * @param obj Object to test.
   */
  public XmlSerializationTester(Object obj) {
    super(obj);
  }

  /**
   * Creates an object from the object.
   *
   * @return true, if successful
   * @throws Exception the exception
   */
  public boolean testXmlSerialization() throws Exception {
    Logger.getLogger(getClass()).debug(
        "Test xml serialization - " + clazz.getName());
    Object obj = createObject(1);
    String xml = ConfigUtility.getStringForGraph(obj);
    System.out.println(xml);
    Object obj2 =
        ConfigUtility
            .getGraphForString(xml, obj.getClass());

    String json = ConfigUtility.getJsonForGraph(obj);
    System.out.println(json);
    Object obj3 =
        ConfigUtility
            .getGraphForJson(json, obj.getClass());
    
    System.out.println(obj.toString());
    System.out.println(obj2.toString());
    System.out.println(obj3.toString());
    
    return obj.equals(obj2) && obj.equals(obj3);
  }

}
