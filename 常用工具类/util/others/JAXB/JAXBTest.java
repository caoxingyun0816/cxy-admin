package com.wondertek.mam.util.others.JAXB;

import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

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
public class JAXBTest{

    public static void main(String[] args) throws Exception{
        JAXBContext context = JAXBContext.newInstance(Boy.class);
        Marshaller marsheller = context.createMarshaller();
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Boy boy = new Boy();
        marsheller.marshal(boy,System.out);
        System.out.println();
        String xml="<boy><name>David</name></boy>";
        Boy boy2 = (Boy)unmarshaller.unmarshal(new StringReader(xml));
        System.out.println(boy2.name);
    }

}