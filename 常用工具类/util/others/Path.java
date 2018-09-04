package com.wondertek.mam.util.others;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 * 系统路径类
 * 
 * @author chenlijian
 * 
 */
public class Path
{
	/**
	 * 方法描述：获取一个类的class文件所在的绝对路径。 这个类可以是JDK自身的类， 也可以是用户自定义的类，或者是第三方开发包里的类。
	 * 只要是在本程序中可以被加载的类， 都可以定位到它的class文件的绝对路径。
	 * 
	 * @param cls
	 *            一个对象的Class属性
	 * @return String 这个类的class文件位置的绝对路径。 如果没有这个类的定义，则返回null。
	 */
	@SuppressWarnings("unchecked")
	private static String getPathFromClass(Class cls) throws IOException
	{
		String path = null;

		if (null == cls)
		{
			throw new NullPointerException();
		}

		// 根据class取得定位对应的URL
		URL url = getClassLocationURL(cls);

		if (url != null)
		{
			path = url.getPath();
			// 此URL的协议名称不区分大小等于jar
			if ("jar".equalsIgnoreCase(url.getProtocol()))
			{
				try
				{
					// 获得此URL的路径部分
					path = new URL(path).getPath();
				} catch (MalformedURLException e)
				{
				}

				int location = path.indexOf("!/");

				if (location != -1)
				{
					path = path.substring(0, location);
				}
			}
			File file = new File(path);
			// 返回抽象路径名的规范路径名字符串
			path = file.getCanonicalPath();
		}
		return path;
	}

	@SuppressWarnings("unchecked")
	public static String getClassPathFromClass(Class cls)
	{
		String classPath = null;
		try
		{
			classPath = getPathFromClass(cls);
			String pkg = cls.getName().replace(".", File.separator) + ".class";
			if (classPath.endsWith(pkg))
				classPath = classPath.substring(0, classPath.indexOf(pkg) - 1);
			else
			{
				File clsFile = new File(classPath);
				String tempPath = clsFile.getParent() + File.separator + "../classes";
				File file = new File(tempPath);
				classPath = file.getCanonicalPath();
			}
		} catch (Exception e)
		{
		}
		return classPath;
	}

	/**
	 * 方法描述：这个方法可以通过与某个类的class文件的相对路径来获取文件或目录的绝对路径。 通常在程序中很难定位某个相对路径，特别是在B/S应用中。
	 * 通过这个方法， 我们可以根据我们程序自身的类文件的位置来定位某个相对路径。
	 * 比如：某个txt文件相对于程序的Test类文件的路径是../../resource/test.txt，
	 * 那么使用本方法Path.getFullPathRelateClass("../../resource/test.txt",Test.class)
	 * 得到的结果是txt文件的在系统中的绝对路径。
	 * 
	 * @param relatedPath
	 *            相对路径
	 * @param cls
	 *            用来定位的类
	 * @return String 相对路径所对应的绝对路径
	 * @throws IOException
	 *             因为本方法将查询文件系统，所以可能抛出IO异常
	 */
	@SuppressWarnings("unchecked")
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
			String tempPath = clsFile.getParent() + File.separator + relatedPath;
			File file = new File(tempPath);
			path = file.getCanonicalPath();
		} catch (IOException e)
		{
		}

		return path;
	}

	/**
	 * 方法描述：获取类的class文件位置的URL。这个方法是本类最基础的方法，供其它方法调用。
	 * 
	 * @param cls
	 *            用来定位的类
	 * @return URL 返回定位对应的URL
	 */
	@SuppressWarnings("unchecked")
	private static URL getClassLocationURL(final Class cls)
	{
		if (cls == null)
		{
			throw new IllegalArgumentException("null input: cls");
		}

		URL result = null;
		final String clsAsResource = cls.getName().replace('.', '/').concat(".class");
		// 返回该类的 ProtectionDomain
		final ProtectionDomain pd = cls.getProtectionDomain();

		if (pd != null)
		{
			// 返回此域的 CodeSource
			final CodeSource cs = pd.getCodeSource();
			if (cs != null)
			{
				// 返回与此 CodeSource 关联的位置
				result = cs.getLocation();
			}

			if (result != null)
			{
				// 如果获得此URL的协议名称等于file
				if ("file".equals(result.getProtocol()))
				{
					try
					{
						// 如果此URL的字符串表示形式以'.jar'或者'zip'结束
						if (result.toExternalForm().endsWith(".jar") || result.toExternalForm().endsWith(".zip"))
						{
							result = new URL("jar:".concat(result.toExternalForm()).concat("!/").concat(clsAsResource));
						} else if (new File(result.getFile()).isDirectory())
						{
							result = new URL(result, clsAsResource);
						}
					} catch (MalformedURLException ignore)
					{
					}
				}
			}
		}

		if (result == null)
		{
			// 返回该类的类加载器
			final ClassLoader clsLoader = cls.getClassLoader();
			result = clsLoader != null ? clsLoader.getResource(clsAsResource) : ClassLoader.getSystemResource(clsAsResource);
		}
		return result;
	}

	/**
	 * 方法描述：获取classes文件的绝对路径,可能是一个jar包
	 * 
	 * @return String 绝对路径
	 */
	@SuppressWarnings("unchecked")
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
}