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
import java.io.File;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
/**
 * JAXB 导出Schema。
 *
 * @author: Credo
 * @date: 2013-6-25
 */
public class JAXBExportSchema {
    public static void main(String[] args) {
        JAXBContext jct;
        try
        {
            jct = JAXBContext.newInstance(Userinfo.class);
            jct.generateSchema(new Resolver());
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
    }
}
class Resolver extends SchemaOutputResolver {
    @Override
    public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
        File file = new File("d:\\", suggestedFileName);
        StreamResult result = new StreamResult(file);
        result.setSystemId(file.toURI().toURL().toString());
        return result;
    }
}