package com.wondertek.mam.util.backupUtils.util22;

import com.wondertek.mobilevideo.core.util.StringUtil;

import java.lang.reflect.Array;
import java.util.Vector;

public class ArrayOperator
{
  Class type;

  public ArrayOperator(Class class1)
  {
    this.type = class1;
  }

  public Object[] concat(Object[] aobj, Vector vector)
  {
    return concat(aobj, toArray(vector));
  }

  public Object[] concat(Object[] aobj, Object[] aobj1)
  {
    Object[] aobj2 = (Object[])Array.newInstance(this.type, aobj.length + 
      aobj1.length);

    System.arraycopy(aobj, 0, aobj2, 0, aobj.length);

    for (int i = 0; i < aobj1.length; ++i)
    {
      aobj2[(i + aobj.length)] = aobj1[i];
    }

    return aobj2;
  }

  public Object[] toArray(Vector vector)
  {
    Object[] aobj = (Object[])Array.newInstance(this.type, vector.size());
    vector.copyInto(aobj);

    return aobj;
  }

  public Vector toVector(Object[] aobj)
  {
    Vector vector = new Vector(aobj.length);

    for (int i = 0; i < aobj.length; ++i)
    {
      vector.addElement(aobj[i]);
    }

    return vector;
  }

  public static String arrayToString(Object[] array)
  {
    StringBuffer arrayBuffer = null;

    if (!StringUtil.isNullStr(array))
    {
      arrayBuffer = new StringBuffer();

      for (int i = 0; i < array.length; ++i)
      {
        arrayBuffer.append(array[i]);
        if (i >= array.length - 1)
          continue;
        arrayBuffer.append(",");
      }

    }

    return (arrayBuffer == null) ? "null" : arrayBuffer.toString();
  }
}