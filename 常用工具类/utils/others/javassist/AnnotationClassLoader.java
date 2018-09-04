package com.wondertek.mam.util.others.javassist;

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
public class AnnotationClassLoader extends ClassLoader {
    public AnnotationClassLoader(ClassLoader parent) {
        super(parent);
    }

    /**
     * @param b
     * @param off
     * @param len
     * @return
     */
    @SuppressWarnings("deprecation")
    public Class<?> defineCustomClass(byte[] b, int off, int len) {
        return super.defineClass(b, off, len);
    }

}
