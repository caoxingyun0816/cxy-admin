package com.wondertek.mam.util.others.JAXB;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD) // @XmlAccessorType(XmlAccessType.PROPERTY) 该注解需要为属性添加get和set方法才会有用

public class Boy{
    String name="CY";
}