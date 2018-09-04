package com.wondertek.mam.util.others;

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
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;

//在使用Javassist过程中，最常用的方法有CtMethod（或者CtConstructor）的insertBefore,insertAfter,addCatch，另外还有一种是injectAround（这种是需要自己来完成的）。可以参考：Spring：Aop before after afterReturn afterThrowing around 的原理
//在代码里引入了before,beforeAround,beforeAfter,beforeThrowing,beforeReturning的概念，是取材于Spring AOP配置中的叫法。
//injectInterceptor（）的实现原理：将原来的方法改名为一个delegateMethod，重新创建一个target method，方法体是织入代码，并调用delegateMethod。
//injectAround（CtConstrouctor）的实现原理：先将构造体的内容提取到一个delegateMethod中，再对delegateMethod做织入，最后设置新的构建体。在新的构造体中调用delegateMethod。

/**
 * Code Inject Tool
 *
 * @author <a href="mailto:fs1194361820@163.com">fs1194361820@163.com</a>
 *
 */
public class CodeInjector {
    public static final String METHOD_RUTURN_VALUE_VAR = "__FJN__result";
    public static final String METHOD_EXEC_EXCEPTION_VAR = "ex";
    public static final String CONSTRUCTOR_DELEGATE_PREFIX = "__FJN__DOC__";
    public static final String METHOD_DELEGATE_PREFIX = "__FJN__DOM__";
    public static final String PROCEED = "$proceed";
    public static final String CRLF = "\n";
    public static final String CRLF_TAB = "\n\t";
    public static final String CRLF_2TAB = "\n\t\t";
    public static final String CRLF_3TAB = "\n\t\t\t";
    public static final String CRLF_4TAB = "\n\t\t\t\t";

    public static final String getDelegateMethodNameOfConstructor(String constructorName) {
        return CONSTRUCTOR_DELEGATE_PREFIX + constructorName;
    }

    public static final String getDelegateMethodNameOfMethod(String methodName) {
        return METHOD_DELEGATE_PREFIX + methodName;
    }

    /**
     * Inject around code to the specified method
     *
     * @see #injectInterceptor(CtClass, CtMethod, String, String, String,
     *      String, String)
     */
    public void injectAround(CtMethod method, String beforeAround, String afterAround, String afterThrowing,
                             String afterReturning) throws Exception {
        CtClass clazz = method.getDeclaringClass();
        injectAround(clazz, method, beforeAround, afterAround, afterThrowing, afterReturning);
    }

    /**
     * Inject around code to the specified method
     *
     * @see #injectInterceptor(CtClass, CtMethod, String, String, String,
     *      String, String)
     */
    public void injectAround(CtClass clazz, CtMethod method, String beforeAround, String afterAround,
                             String afterThrowing, String afterReturning) throws Exception {
        injectInterceptor(clazz, method, null, beforeAround, afterAround, afterThrowing, afterReturning);
    }

    /**
     * Inject around code to the specified method
     *
     * <pre>
     * <code>
     * <span style="font-size:12px; color:green;">before block ... </span>
     * try{
     *     <span style="font-size:12px; color:green;">beforeAround block ... </span>
     *     $procced($$);
     *     <span style="font-size:12px; color:green;">afterAround block ... </span>
     * }catch (Throwable ex){
     *     <span style="font-size:12px; color:green;">afterThrowing block ... </span>
     * }finally{
     *     <span style="font-size:12px; color:green;">afterReturning block ... </span>
     * }
     * </code>
     * </pre>
     *
     */
    public void injectInterceptor(CtClass clazz, CtMethod method, String before, String beforeAround,
                                  String afterAround, String afterThrowing, String afterReturning) throws Exception {
        clazz.defrost();
        int modifiers = method.getModifiers();
        CtClass returnType = method.getReturnType();
        CtClass[] parameters = method.getParameterTypes();
        CtClass[] exceptions = method.getExceptionTypes();
        String methodName = method.getName();
        String delegateMethod = getDelegateMethodNameOfMethod(methodName);
        method.setName(delegateMethod);
        StringBuilder buffer = new StringBuilder(256);

        boolean hasReturnValue = (CtClass.voidType == returnType);
        buffer.append("{" + CRLF_TAB);
        {
            if (hasReturnValue) {
                String returnClass = returnType.getName();
                buffer.append(returnClass + " " + METHOD_RUTURN_VALUE_VAR + ";");
            }
            if (before != null) {
                buffer.append(before);
            }
            buffer.append(CRLF_TAB);
            buffer.append("try {" + CRLF_2TAB);
            {
                if (beforeAround != null) {
                    buffer.append(beforeAround);
                }
                buffer.append(CRLF_2TAB);
                if (hasReturnValue) {
                    buffer.append(METHOD_RUTURN_VALUE_VAR + " = ($r)" + delegateMethod + "($$);");
                } else {
                    buffer.append(delegateMethod + "($$);");
                }
                if (afterAround != null) {
                    buffer.append(CRLF_2TAB);
                    buffer.append(afterAround);
                }
                if (hasReturnValue) {
                    buffer.append(CRLF_2TAB);
                    buffer.append("return " + METHOD_RUTURN_VALUE_VAR);
                }
            }
            buffer.append(CRLF_TAB);
            buffer.append("} catch (Throwable ex) {");
            {
                buffer.append(CRLF_2TAB);
                if (afterThrowing != null) {
                    buffer.append(afterThrowing);
                }
                buffer.append(CRLF_2TAB);
                buffer.append("throw ex;");
            }
            buffer.append(CRLF_TAB);
            buffer.append("}");

            if (afterReturning != null) {
                buffer.append(CRLF_TAB);
                buffer.append("finally {");
                {
                    buffer.append(CRLF_2TAB);
                    buffer.append(afterReturning);
                }
                buffer.append(CRLF_TAB);
                buffer.append("}");
            }
        }
        buffer.append(CRLF);
        buffer.append("}");
        System.out.println(methodName + " will be modified as :\n" + buffer.toString());
        CtMethod newMethod = CtNewMethod.make(modifiers, returnType, methodName, parameters, exceptions,
                buffer.toString(), clazz);
        clazz.addMethod(newMethod);
    }

    /**
     * Inject around code to the specified constructor
     *
     * @see #injectAround(CtClass, CtConstructor, String, String, String,
     *      String)
     */
    public void injectAround(CtConstructor constructor, String beforeAround, String afterAround, String afterThrowing,
                             String afterReturning) throws Exception {
        CtClass clazz = constructor.getDeclaringClass();
        injectAround(clazz, constructor, beforeAround, afterAround, afterThrowing, afterReturning);
    }

    /**
     * Inject around code to the specified constructor
     *
     * <pre>
     * <code>
     * try{
     *     <span style="font-size:12px; color:green;">beforeAround block ... </span>
     *     $procced($$);
     *     <span style="font-size:12px; color:green;">afterAround block ... </span>
     * }catch (Throwable ex){
     *     <span style="font-size:12px; color:green;">afterThrowing block ... </span>
     * }finally{
     *     <span style="font-size:12px; color:green;">afterReturning block ... </span>
     * }
     * </code>
     * </pre>
     *
     */
    public void injectAround(CtClass clazz, CtConstructor constructor, String beforeAround, String afterAround,
                             String afterThrowing, String afterReturning) throws Exception {
        clazz.defrost();
        String delegateMethodName = getDelegateMethodNameOfConstructor(constructor.getName());
        CtMethod delegateMethod = constructor.toMethod(delegateMethodName, clazz);
        clazz.addMethod(delegateMethod);
        injectAround(clazz, delegateMethod, beforeAround, afterAround, afterThrowing, afterReturning);
        constructor.setBody("{" + PROCEED + "($$);", "this", delegateMethodName);
    }

    /**
     * Copy form the src method's body to a overrid method's body in target
     * class.
     *
     * @param targetClass
     *            overrid the method in the target class
     * @param srcMethod
     *            the overrid from will copy from the src method. If the target
     *            class has not owner overrid method , you should specified the
     *            srcMethod in the super class.
     * @param body
     *            the body of the override method
     * @param delegateObject
     *            the delegate object default value is "this".
     * @param delegateMethod
     * @throws Exception
     */
    public void overrideMethod(CtClass targetClass, CtMethod srcMethod, String body, String delegateObject,
                               String delegateMethod) throws Exception {
        targetClass.defrost();
        System.out.println(body);
        if (delegateObject == null) {
            delegateObject = "this";
        }
        // override method in a super class of the target class
        if (srcMethod.getDeclaringClass() != targetClass) {
            CtMethod destMethod = CtNewMethod.copy(srcMethod, targetClass, null);
            if (body != null && !body.isEmpty()) {
                if (delegateMethod != null && !delegateMethod.isEmpty()) {
                    destMethod.setBody(body, delegateObject, delegateMethod);
                } else {
                    destMethod.setBody(body);
                }
            }
            targetClass.addMethod(destMethod);
        }
        // override method in the target class
        else {
            if (delegateMethod != null && !delegateMethod.isEmpty()) {
                srcMethod.setBody(body, delegateObject, delegateMethod);
            } else {
                srcMethod.setBody(body);
            }
        }
    }
}