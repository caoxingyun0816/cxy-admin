package com.wondertek.mam.util.others.JAXB;

/**
 * Simple to Introduction
 *
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [一句话描述该类的功能]
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @UpdateUser: [${user}]
 * @UpdateDate: [${date} ${time}]
 * @UpdateRemark: [说明本次修改内容]
 * @Version: [v1.0]
 */

//http://www.360doc.com/content/14/0815/16/14416931_402177954.shtml
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.wondertek.mam.util.others.JAXB.JAXBCache;
import com.wondertek.mam.util.others.JAXB.Overinfo;
import com.wondertek.mam.util.others.JAXB.Userinfo;
import org.apache.commons.io.IOUtils;
/**
 * marshal对象和unmarshal对象都是由JAXBContext创建.所以一开始需要初始化JAXBContext.
 * @author Credo
 */
public class JAXBUtils {
    /**
     * 生成xml文件的二进制数据
     * @param obj 对象
     */
    public static byte[] marshal(Object obj) throws JAXBException {
        JAXBContext context = JAXBCache.instance().getJAXBContext(obj.getClass());
        Marshaller m = context.createMarshaller();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(obj, outputStream);
        byte[] result = outputStream.toByteArray();
        return result;
    }
    /**
     * @param data xml stream
     * @param classe 类
     * @return jaxb生成xml的java 类对象
     */
    public static Object unmarshal(byte[] data, Class<?> classe) throws JAXBException {
        JAXBContext context = JAXBCache.instance().getJAXBContext(classe);
        Unmarshaller m = context.createUnmarshaller();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        Object obj = m.unmarshal(inputStream);
        return obj;
    }
    /**
     * @param in xml stream
     * @param classe 类
     * @return jaxb生成xml的java 类对象
     */
    public static Object unmarshal(InputStream in, Class<?> classe) throws JAXBException, IOException {
        JAXBContext context = JAXBCache.instance().getJAXBContext(classe);
        byte[] data = IOUtils.toByteArray(in);
        Unmarshaller m = context.createUnmarshaller();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        Object obj = m.unmarshal(inputStream);
        return obj;
    }
    public static void main(String[] args) throws JAXBException {
        Userinfo userinfo = new Userinfo();
        userinfo.setId(Long.valueOf(11));
        List<Overinfo> list = new ArrayList<Overinfo>();
        Overinfo e = new Overinfo();
        e.setHobby("陪女友");
        list.add(e);
        Overinfo e1 = new Overinfo();
        e1.setHobby("写代码");
        list.add(e1);
        userinfo.setOverinfos(list);
        byte[] b = JAXBUtils.marshal(userinfo);
        System.out.println(new String(b));
        userinfo = (Userinfo) JAXBUtils.unmarshal(b, Userinfo.class);
        System.out.println(userinfo.getOverinfos().get(0).getHobby());
    }
}