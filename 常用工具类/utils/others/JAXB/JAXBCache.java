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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
public final class JAXBCache {
    private static final JAXBCache instance = new JAXBCache();
    private final ConcurrentMap<String, JAXBContext> contextCache = new ConcurrentHashMap<String, JAXBContext>();
    private JAXBCache() {
    }
    public static JAXBCache instance() {
        return instance;
    }
    public JAXBContext getJAXBContext(Class<?> clazz) throws JAXBException {
        JAXBContext context = contextCache.get(clazz.getName());
        if ( context == null )
        {
            context = JAXBContext.newInstance(clazz);
            contextCache.putIfAbsent(clazz.getName(), context);
        }
        return context;
    }
}