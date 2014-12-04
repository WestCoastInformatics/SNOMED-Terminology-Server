package org.ihtsdo.otf.ts.helpers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

// TODO: Auto-generated Javadoc
/**
 * Container for key value pairs.
 */
@XmlRootElement(name = "keyValuePair")
public class KeyValuesMap {

  /** The map. */
  private Map<String,Set<String>> map;

  /**
   * Instantiates an empty {@link KeyValuesMap}.
   */
  public KeyValuesMap() {
    map = new HashMap<>();
  }

  /**
   * Instantiates a {@link KeyValuesMap} from the specified parameters.
   *
   * @param map the map
   */
  public KeyValuesMap(KeyValuesMap map) {
    this.map = map.getMap();
  }

  /**
   * Gets the map.
   *
   * @return the map
   */
  public Map<String, Set<String>> getMap() {
    return map;
  }

  /**
   * Sets the map.
   *
   * @param map the map
   */
  public void setMap(Map<String, Set<String>> map) {
    this.map = map;
  }

  /**
   * Put key and value into the map.
   *
   * @param key the key
   * @param value the value
   */
  public void put(String key, String value) {
    if (!map.containsKey(key)) {
      Set<String> values = new HashSet<>();
      map.put(key, values);
    }
    map.get(key).add(value);
  }

}
