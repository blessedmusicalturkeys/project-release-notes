package com.blessedmusicalturkeys.projectreleasenotes.utilities;

import java.util.Base64;
import org.apache.commons.lang3.ObjectUtils.Null;

/**
 * Simple Environment Vars getter
 *
 * @author Timothy Stratton
 */
public class EnvironmentVariables {
  public static String getString(String variableName) {
    try {
      return System.getenv(variableName);
    } catch (Exception e) {
      return null;
    }
  }

  public static String getBase64EncodedString(String variableName) {
    String encodedVariable = System.getenv(variableName);
    try {
      return new String(Base64.getDecoder().decode(encodedVariable));
    } catch (NullPointerException e) {
      return null;
    }
  }

  public static Integer getInt(String variableName) {
    return Integer.parseInt(System.getenv(variableName));
  }

  public static Long getLong(String variableName) {
    return Long.parseLong(System.getenv(variableName));
  }

  public static Double getDouble(String variableName) {
    return Double.parseDouble(System.getenv(variableName));
  }

  public static Boolean getBoolean(String variableName) {
    return Boolean.parseBoolean(System.getenv(variableName));
  }
}
