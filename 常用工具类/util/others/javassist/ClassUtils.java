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

import com.wondertek.mam.util.superutil.thirdparty.excel.Excel;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import org.jsoup.helper.StringUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 运行时动态修改注解值
 * @author c_zhangzengmin002
 *
 */
public class ClassUtils {
    //缓存加载器加载的calss对象
    private static final Map<String, Class<?>> classPoolMap=new ConcurrentHashMap<String, Class<?>>();
    private ClassUtils(){}
    /**
     * 运行时动态修改注解值
     * @param entityClassName
     *
     * @param entityClassName
     *            待映射的实体全限定类名
     * //@param  annotation 需要修改的注解全限定类名
     * @param annotationAttrName 注解属性名称
     * @param annotationVal 注解属性值
     *
     * @return 映射后的类对象
     */
    public static  Class<?> updateAnnotationForClass(String entityClassName,String annotationClassName, String annotationAttrName,String annotationVal)throws Exception {
        Class<?> c = null;
        if(StringUtil.isBlank(entityClassName) || StringUtil.isBlank(annotationClassName)
                || StringUtil.isBlank(annotationAttrName) || StringUtil.isBlank(annotationVal)){
            return c;
        }
        c=classPoolMap.get(annotationVal);
        if(null != c){
            return c;
        }
        try {
            ClassPool classPool = ClassPool.getDefault();
            classPool.appendClassPath(new ClassClassPath(ClassUtils.class));
            //classPool.importPackage("javax.persistence");
            CtClass clazz = classPool.get(entityClassName);
            clazz.defrost();
            ClassFile classFile = clazz.getClassFile();

            ConstPool constPool = classFile.getConstPool();
            Annotation tableAnnotation = new Annotation(
                    annotationClassName, constPool);
            tableAnnotation.addMemberValue(annotationAttrName, new StringMemberValue(
                    annotationVal, constPool));
            // 获取运行时注解属性
            AnnotationsAttribute attribute = (AnnotationsAttribute) classFile
                    .getAttribute(AnnotationsAttribute.visibleTag);
            attribute.addAnnotation(tableAnnotation);
            classFile.addAttribute(attribute);
            classFile.setVersionToJava5();
            // clazz.writeFile();
            AnnotationClassLoader loader = new AnnotationClassLoader(
                    ClassUtils.class.getClassLoader());
            //加载器加载class文件，一个加载器只会加载一次路径名称相同的calss
            //这里因为只改了注解，类路径是没有改变的。
            c = clazz.toClass(loader, null);
            if(null != c){
                classPoolMap.put(annotationVal, c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    /**
     * 运行时动态修改注解值
     * @param entityClassName
     *
     * @param entityClassName
     *            待映射的实体全限定类名
     * //@param  annotation 需要修改的注解全限定类名
     * @param attributeName 具体的属性的名称
     * @param annotationAttrName 注解属性名称
     * @param annotationVal 注解属性值
     *
     * @return 映射后的类对象
     */
    public static  Class<?> updateSpecifiedAnnotationForClass(String entityClassName,String annotationClassName,String attributeName, String annotationAttrName,String annotationVal)throws Exception {
        Class<?> c = null;
        if(StringUtil.isBlank(entityClassName) || StringUtil.isBlank(annotationClassName)
                || StringUtil.isBlank(annotationAttrName) || StringUtil.isBlank(annotationVal)){
            return c;
        }
        c=classPoolMap.get(annotationVal);
        if(null != c){
            return c;
        }
        try {
            ClassPool classPool = ClassPool.getDefault();
            classPool.appendClassPath(new ClassClassPath(ClassUtils.class));
            CtClass clazz = classPool.get(entityClassName);
            clazz.defrost();
            ClassFile classFile = clazz.getClassFile();
            ConstPool constPool = classFile.getConstPool();
            Annotation tableAnnotation = new Annotation(
                    annotationClassName, constPool);
            tableAnnotation.addMemberValue(annotationAttrName, new StringMemberValue(
                    annotationVal, constPool));
            CtField ctField = clazz.getField(attributeName);
            FieldInfo fieldInfo = ctField.getFieldInfo();
            AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute)fieldInfo.getAttribute(AnnotationsAttribute.visibleTag);
            //log.info("~~~~~~~~~~~~ annotationsAttribute:" + annotationsAttribute);

            // 获取运行时注解属性
//            AnnotationsAttribute attribute = (AnnotationsAttribute) classFile
//                    .getAttribute(AnnotationsAttribute.invisibleTag);
//            List<AnnotationsAttribute> annotationsAttributeList = classFile.getAttributes();
            annotationsAttribute.addAnnotation(tableAnnotation);
            classFile.addAttribute(annotationsAttribute);
            classFile.setVersionToJava5();
            AnnotationClassLoader loader = new AnnotationClassLoader(
                    ClassUtils.class.getClassLoader());
            //加载器加载class文件，一个加载器只会加载一次路径名称相同的calss
            //这里因为只改了注解，类路径是没有改变的。
            c = clazz.toClass(loader, null);
            if(null != c){
                classPoolMap.put(annotationVal, c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public static void main(String[] args)throws Exception {
//        Class<?> clazz3 = ClassUtils.updateAnnotationForClass(
//            "com.wondertek.mam.util.others.javassist.DataFile","javax.xml.bind.annotation.XmlRootElement","name", "AUTO_IDS");
//        System.out.println("修改后的@XmlRootElement: " + clazz3.getAnnotation(XmlRootElement.class).name());

//        Class<?> clazz3 = ClassUtils.updateAnnotationForClass(
//                "com.wondertek.mam.util.others.javassist.DataFile","javax.xml.bind.annotation.XmlElement","name", "false");
//        System.out.println("修改后的@XmlElement: " + clazz3.getAnnotation(XmlElement.class).name());

        Class<?> clazz3 = ClassUtils.updateSpecifiedAnnotationForClass(
                "com.wondertek.mam.vo.exportVo.CPVo","com.wondertek.mam.util.superutil.thirdparty.excel.Excel","defaultCopyrightId","name", "false");
        System.out.println("修改后的@XmlElement: " + clazz3.getAnnotation(Excel.class).name());

        //System.out.println("修改后的@Excel: " + clazz3.());
        //System.out.println("修改后的@XmlElement: " + clazz3.getField(""));


    }


    /**
     * <p>
     * Description:根据class类型，将文件流转换成对象。在这个过程中会对文件进行编码处理
     * </p>
     * <p>
     * Create Time:2012-9-31
     * </p>
     *
     * @author zzm
     * @param c
     *            <?>
     * @param in
     *            文件流
     * @throws Exception
     */
//    public static Object unmarshalClaim(Class<?> c, BufferedReader in)
//            throws Exception {
//        StringBuilder buffer = null;
//        JAXBContext jaxbContext;
//        try {
//
//            buffer = new StringBuilder();
//            String line = "";
//            if (in != null) {
//                while ((line = in.readLine()) != null) {
//                    buffer.append(line);
//                }
//
//            }
//
//            //所取头结点名称
//            String dataFileName=StringUtil.getXMlHandleNodeString(buffer);
//            //获取动态class对象
//            Class<?> calss=ClassUtils.updateAnnotationForClass(Constant.DATAFILE_CLASS_NAME, Constant.XMLROOTELEMENT_CLASS_NAME,
//                    Constant.NAME_STR, dataFileName);
//            if(null!=calss){
//                c=calss;
//            }
//            String xmlString = StringUtil.getXMlString(buffer);
//
//            StreamSource streamSource = new StreamSource(new StringReader(
//                    xmlString));
//
//            // 加载映射bean类
//            jaxbContext = JAXBContext.newInstance(c);
//            // 创建解析
//            Unmarshaller um = jaxbContext.createUnmarshaller();
//            return unmarshalClaim(um,streamSource);
//        } catch (Exception e) {
//            log.error(e);
//            e.printStackTrace();
//            throw new BasicException(e.getMessage());
//        }finally{
//            in.close();
//        }
//    }



}

//http://blog.csdn.net/mousebaby808/article/details/37696371