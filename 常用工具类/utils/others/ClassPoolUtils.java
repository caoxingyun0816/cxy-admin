//package com.wondertek.mam.util.others;
//
//
//import java.lang.reflect.Method;
//
//import javassist.ClassPool;
//import javassist.CtClass;
//import javassist.bytecode.AnnotationsAttribute;
//import javassist.bytecode.ClassFile;
//import javassist.bytecode.ConstPool;
//import javassist.bytecode.annotation.Annotation;
//import javassist.bytecode.annotation.ArrayMemberValue;
//import javassist.bytecode.annotation.BooleanMemberValue;
//import javassist.bytecode.annotation.IntegerMemberValue;
//import javassist.bytecode.annotation.MemberValue;
//import javassist.bytecode.annotation.StringMemberValue;
//
///**
// * @Title: ClassPoolUtils.java
// * @Package com.sgfm.scsm.util
// * @Description: 动态修改实体类注释中的值(不提倡经常使用)
// * @author TangCai
// * @date 2016年11月16日 下午3:08:56
// * @version V1.0
// */
//public class ClassPoolUtils {
//    /**
//     * 运行时动态ORM表映射
//     * @param entityClass   实体类
//     * @param annotation    注释类
//     * @param atMethodName  注释类方法
//     * @param atMethodVal   注释类方法的更新值
//     * @return              修改注释值后实体类
//     */
//    @SuppressWarnings("unchecked")
//    public static Class<?> setPool(Class<?> entityClass, Class<?> annotation,
//                                   String atMethodName, Object atMethodVal){
//        Class<?> c = null;
//        try {
//            ClassPool classPool = ClassPool.getDefault();
//
//            CtClass clazz = classPool.getCtClass(entityClass.getName());
//            ClassFile classFile = clazz.getClassFile();
//            //clazz.freeze();//冻结
//
//            ConstPool constPool = classFile.getConstPool();
//            Class<java.lang.annotation.Annotation> cl = (Class<java.lang.annotation.Annotation>)annotation;
//            Annotation newAnnotation = createJavassistAnnotation(constPool,entityClass.getAnnotation(cl),atMethodName,atMethodVal);
//
//            AnnotationsAttribute attribute = (AnnotationsAttribute)classFile.getAttribute(AnnotationsAttribute.visibleTag);
//            attribute.addAnnotation(newAnnotation);
//            classFile.addAttribute(attribute);
//            ClassLoader parent = entityClass.getClassLoader();
//            EntityClassLoader loader = new ClassPoolUtils().new EntityClassLoader(parent);
//            //clazz.toClass();//同一个ClassLoader不能多次加载同一个类
//            c = clazz.toClass(loader , null);//替换Class的时候，  加载该Class的ClassLoader也必须用新的
//            //clazz.defrost();//解除冻结
//            clazz.detach(); //注销class
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return c;
//    }
//    public static Annotation createJavassistAnnotation(ConstPool constpool,
//                                                       java.lang.annotation.Annotation annotation,String atMethodName, Object atMethodVal) throws Exception {
//        Annotation annotationCopy = new Annotation(annotation.annotationType().getName(), constpool);
//        String typeName = null;
//        MemberValue mv = null;
//        for (Method m : annotation.annotationType().getDeclaredMethods()) {
//            Object value = m.invoke(annotation);
//            typeName = m.getReturnType().getSimpleName();
//            if(atMethodName.equals(m.getName())) value = atMethodVal;
//            if(typeName.equals("int"))
//                mv = new IntegerMemberValue(constpool,(Integer)value);
//            else if(typeName.equals("String[]")){
//                String[] stres = (String[])value;
//                StringMemberValue[] elements = new StringMemberValue[stres.length];
//                for(int i=0;i<stres.length;i++)
//                    elements[i] = new StringMemberValue(stres[i],constpool);
//                ArrayMemberValue amv = new ArrayMemberValue(constpool);
//                amv.setValue(elements);
//                mv = amv;
//            }
//            else if(typeName.equals("boolean"))
//                mv = new BooleanMemberValue((Boolean)value,constpool);
//            else mv = new StringMemberValue(value.toString(),constpool);
//            annotationCopy.addMemberValue(m.getName(),mv);
//        }
//        return annotationCopy;
//    }
//    /**
//     * @Title: EntityClassLoader.java
//     * @Package com.sgfm.scsm.util
//     * @Description:  实体类加载器
//     * 该加载器主要用于运行时动态修改实体后，重新装载实体
//     * @author TangCai
//     * @date 2016年11月16日 下午3:18:55
//     * @version V1.0
//     */
//    public class EntityClassLoader extends ClassLoader {
//        private ClassLoader parent;
//
//        public EntityClassLoader(ClassLoader parent){
//            this.parent = parent;
//        }
//
//        @Override
//        public Class<?> loadClass(String name) throws ClassNotFoundException {
//            return this.loadClass(name, false);
//        }
//
//        @Override
//        protected synchronized Class<?> loadClass(String name, boolean resolve)
//                throws ClassNotFoundException {
//            Class<?> clazz = this.findLoadedClass(name);
//            if(null != parent)
//                clazz = parent.loadClass(name);
//            if(null == clazz)
//                this.findSystemClass(name);
//            if(null == clazz)
//                throw new ClassNotFoundException();
//            if(null != clazz && resolve)
//                this.resolveClass(clazz);
//            return clazz;
//        }
//    }
//}