package com.wondertek.mam.util.backupUtils.util22;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

public class Path
{
  private static String getPathFromClass(Class cls)
    throws IOException
  {
    String path = null;

    if (cls == null)
    {
      throw new NullPointerException();
    }

    URL url = getClassLocationURL(cls);

    if (url != null)
    {
      path = url.getPath();

      if ("jar".equalsIgnoreCase(url.getProtocol()))
      {
        try
        {
          path = new URL(path).getPath();
        }
        catch (MalformedURLException localMalformedURLException)
        {
        }

        int location = path.indexOf("!/");

        if (location != -1)
        {
          path = path.substring(0, location);
        }
      }
      File file = new File(path);

      path = file.getCanonicalPath();
    }
    return path;
  }

  public static String getFullPathRelateClass(String relatedPath, Class cls)
  {
    String path = null;

    if (relatedPath == null)
    {
      return path;
    }

    try
    {
      String clsPath = getPathFromClass(cls);
      File clsFile = new File(clsPath);
      String tempPath = clsFile.getParent() + File.separator + 
        relatedPath;
      File file = new File(tempPath);
      path = file.getCanonicalPath();
    }
    catch (IOException localIOException)
    {
    }

    return path;
  }

  private static URL getClassLocationURL(Class cls)
  {
    if (cls == null)
    {
      throw new IllegalArgumentException("null input: cls");
    }

    URL result = null;
    String clsAsResource = cls.getName().replace('.', '/').concat(
      ".class");

    ProtectionDomain pd = cls.getProtectionDomain();

    if (pd != null)
    {
      CodeSource cs = pd.getCodeSource();
      if (cs != null)
      {
        result = cs.getLocation();
      }

      if ((result != null) && 
        ("file".equals(result.getProtocol())))
      {
        try
        {
          if ((result.toExternalForm().endsWith(".jar")) || 
            (result.toExternalForm().endsWith(".zip")))
          {
            result = new URL(
              "jar:".concat(result.toExternalForm()).concat("!/")
              .concat(clsAsResource));
          }
          else if (new File(result.getFile()).isDirectory())
          {
            result = new URL(result, clsAsResource);
          }
        }
        catch (MalformedURLException localMalformedURLException)
        {
        }
      }

    }

    if (result == null)
    {
      ClassLoader clsLoader = cls.getClassLoader();
      result = (clsLoader != null) ? clsLoader.getResource(clsAsResource) : 
        ClassLoader.getSystemResource(clsAsResource);
    }
    return result;
  }

  public static String getAbsoluteClassesPath(Class clazz)
  {
    ProtectionDomain pd = clazz.getProtectionDomain();

    if (pd == null)
    {
      return null;
    }

    CodeSource cs = pd.getCodeSource();

    if (cs == null)
    {
      return null;
    }
    URL url = cs.getLocation();

    if (url == null)
    {
      return null;
    }
    return url.getPath();
  }

  public static String getRootPath()
  {
    String url = getAbsoluteClassesPath(Path.class);

    if (url == null)
    {
      return null;
    }

    int idx = url.lastIndexOf("WEB-INF");

    if (idx < 0)
    {
      return null;
    }

    return url.substring(0, idx);
  }

  public static String getContextClassesPath()
  {
    String tmp = getRootPath();
    if (tmp == null)
    {
      return null;
    }
    return tmp + "WEB-INF/classes/";
  }

  public static String getWebInfPath()
  {
    String tmp = getRootPath();
    if (tmp == null)
    {
      return null;
    }
    return tmp + "WEB-INF/";
  }

  public static String getConfPath()
  {
    return getContextClassesPath();
  }

  public static URL getURLFromClasses(String fileName)
  {
    Class cls = Path.class;

    ClassLoader clsLoader = cls.getClassLoader();

    return (clsLoader != null) ? clsLoader.getResource(fileName) : 
      ClassLoader.getSystemResource(fileName);
  }

  public static String getTomcatRoot()
  {
    String tmp = getRootPath();

    if (tmp == null)
    {
      return null;
    }

    File file = new File(tmp);

    return file.getParentFile().getParentFile().getAbsolutePath();
  }
}