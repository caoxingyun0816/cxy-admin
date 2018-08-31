//package com.imooc.Intercepter;
//
//import com.opensymphony.xwork2.ActionInvocation;
//import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
//import com.wondertek.mam.util.StringUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.apache.struts2.ServletActionContext;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
///***
// * 拦截器 对每个请求进行拦截，统计 请求 来自哪个IP，请求url ，请求参数，请求返回等，并输出
// */
//public class RequestInterceptor extends AbstractInterceptor {
//
//	private static final long serialVersionUID = 3244973830196015811L;
//	private Log log = LogFactory.getLog(getClass());
//
//	public String intercept(ActionInvocation invocation) throws Exception {
//		HttpServletRequest request = ServletActionContext.getRequest();
//		String remoteIp = request.getRemoteAddr();
//		Map<String, Object> session = invocation.getInvocationContext().getSession();
//		String userName = StringUtils.trimToEmpty((String) session.get("_sso_username"));
//		Map<String, Object> parameters = invocation.getInvocationContext().getParameters();
//		if (null == parameters)
//			parameters = new HashMap<String, Object>();
//		String paramsStr = "";
//		Iterator<String> it = parameters.keySet().iterator();
//		while (it.hasNext()) {
//			String key = it.next();
//			paramsStr = paramsStr + key + "=";
//			try {
//				paramsStr = paramsStr + StringUtil.object2String(((Object[])parameters.get(key))[0]);
//			} catch (Exception e) {
//			}
//			paramsStr = paramsStr + "&";
//		}
//		String actionName = "";
//		long startTime = System.currentTimeMillis();
//		String result = invocation.invoke();
//		long executionTime = System.currentTimeMillis() - startTime;
//
//		StringBuilder message = new StringBuilder(200);
//		message.append("login user [" + userName + "] from IP [" + remoteIp + "] Executed action [");
//		String namespace = invocation.getProxy().getNamespace();
//		if ((namespace != null) && (namespace.trim().length() > 0)) {
//			actionName = namespace + "/";
//		}
//		actionName = namespace + "/" + invocation.getProxy().getActionName() + "!" + invocation.getProxy().getMethod();
//		message.append(actionName);
//		message.append("] took ").append(executionTime).append(" ms. get return [" + result + "]; request params [" + paramsStr + "] ");
//
//		if (actionName.indexOf("checkSession") >= 0) {
//			log.info(message);
//		}
//		return result;
//	}
//}