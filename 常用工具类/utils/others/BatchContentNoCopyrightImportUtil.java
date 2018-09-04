package com.wondertek.mam.util.others;

import com.wondertek.mam.commons.MamConstants;
import com.wondertek.mam.util.SourceUtil;
import com.wondertek.mam.util.file.FileHelper;
import com.wondertek.mam.util.image.ImageUtils;
import com.wondertek.mobilevideo.core.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 批量内容无版权注入接口的工具类
 */
public class BatchContentNoCopyrightImportUtil {
    public static final Log log = LogFactory.getLog(BatchContentNoCopyrightImportUtil.class);

    public static void main(String[] args) throws Exception {
		String filePath = "9000005514_9000005514_20160907_Req.xml";
		String cpid = "800033";
		String onCommandContentXMLStr = readFile2String(1, filePath, cpid);
		parseOnCommandContentNoCopyrightXml(onCommandContentXMLStr);
	}
    
    //实时响应XML格式
    public static String responseXML(Map<String,String> paramsMap){
        StringBuffer sb = new StringBuffer();
        sb.append("<Response>");
        sb.append("<ResCode>");
        sb.append(paramsMap.get("resCode"));
        sb.append("</ResCode>");
        sb.append("<ResMessage>");
        sb.append(paramsMap.get("resMessage"));
        sb.append("</ResMessage>");
        sb.append("</Response>");
        return sb.toString();
    }

    /**
     *
     * @param path FTP上保存的文件相对路径
     * @param cpid CPID
     * @param type 用于判断不同的类别的文件的名称命名要求 1:点播内容注入接口 、2：直播内容注入接口、 3：直播节目单更新接口, null:不做文件名称认证
     * @return 将文件内容转为字符串形式
     */
    public static String readFile2String(Integer type, String path, String cpid) throws Exception{
        if(StringUtil.isNullStr(path) || StringUtil.isNullStr(cpid)){
            return null;
        }
        //source/upload/cpid/...     FilePathHelper.getContentThreeDir(contentId)
        String filePath = MamConstants.SOURCE_PATH + File.separator +"upload" + File.separator + cpid + File.separator
                + path;
        log.info("______________the filePath of readFile2String is " + filePath);
        InputStream is = null;
        try {
            File file = new File(filePath);
            switch (type){
                case 1:
                    //文件以_yyyyMMdd_Req.xml为后缀
                    String eL1 = "^(\\d+)_(\\d+)_(\\d{4})(0\\d{1}|1[0-2])(0\\d{1}|[12]\\d{1}|3[01])_(Req{1})\\.xml$";
                    if(!Pattern.compile(eL1).matcher(file.getName()).matches()){
                        log.info("*********** the file（" + file.getName() + "） named is not correct! ");
                        return null;
                    }
                    break;
                case 2:
                    //文件以_yyyyMMdd_Req.xml为后缀
                    String eL2 = "^(\\d+)_(\\d+)_(\\d{4})(0\\d{1}|1[0-2])(0\\d{1}|[12]\\d{1}|3[01])_(Req{1})\\.xml$";
                    if(!Pattern.compile(eL2).matcher(file.getName()).matches()){
                        log.info("*********** the file（" + file.getName() + "） named is not correct! ");
                        return null;
                    }
                    break;
                case 3:
                    //playbill_assetId.xml
                    String eL3 = "^playbill_(\\d+)\\.xml$";
                    if(!Pattern.compile(eL3).matcher(file.getName()).matches()){
                        log.info("*********** the file（" + file.getName() + "） named is not correct! ");
                        return null;
                    }
                    break;
                default://do nothing

            }
            if(!file.exists()){//文件不存在
                log.info("~~~~~~~&&&&&& XML is not exist in FTP Server!");
                return null;
            }
            is = new FileInputStream(file);
            return  FileHelper.readFromStream(is);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (AgentEngineException e) {
            throw e;
        } finally {
			if(is != null) {
				is.close();
			}
		}
    }

    public static Vector<Object> parsePlaybillXml(String xmlStr) throws DocumentException {
        Vector<Object> playbillVector = new Vector<Object>();
        try {
            StringReader reader = new StringReader(xmlStr);
            //创建一个新的SAXReader
            SAXReader sb = new SAXReader();
            Document doc = sb.read(reader);
            Element root = doc.getRootElement();
            List<Element> childElements = root.elements();
            for(Element ele : childElements){
                Map<String, Object> rootMap = new ConcurrentHashMap<String, Object>();
                String playDay = ele.elementText("PlayDay");
                Vector<Object> playListsVector = new Vector<Object>();
                Element playListsEle = ele.element("PlayLists");
                List<Element> playLists = playListsEle.elements();
                for(int j=0; j < playLists.size(); j++){
                    Element playListEle = playLists.get(j);
                    String startTime = playListEle.elementText("StartTime");
                    String endTime = playListEle.elementText("EndTime");
                    String playName = playListEle.elementText("PlayName");
                    Map<String, String> playMap = new ConcurrentHashMap<String, String>();
                    playMap.put("StartTime",startTime);
                    playMap.put("EndTime",endTime);
                    playMap.put("PlayName",playName);
                    playListsVector.add(playMap);
                }
                rootMap.put("PlayDay",playDay);
                rootMap.put("PlayLists",playListsVector);
                playbillVector.add(rootMap);
            }
        } catch (DocumentException e) {
            throw e;
        } finally {
        }
        return playbillVector;
    }


    /**
     * 报错时调用的方法，直接将错误结果返回给用户
     * @param paramsMap
     */
    public static void returnMethod(Map<String,Object> paramsMap){
        try {
            Map<String,String> params = new HashMap<String, String>();
            params.put("resCode",String.valueOf(paramsMap.get("resCode")));
            params.put("resMessage",String.valueOf(paramsMap.get("resMessage")));
            String returnStr = BatchContentNoCopyrightImportUtil.responseXML(params);
            HttpServletResponse response = (HttpServletResponse)paramsMap.get("response");
            PrintWriter out = response.getWriter();
            out.write(returnStr);
            out.flush();
            if(out != null){
                out.close();
            }
            log.info("!!!!!!!!!!!!!!!!!!!!!!!! reponse xml String is " + returnStr);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("^^^^^^IOException:" + e.toString());
        }
    }

    public static void returnMethod(String returnStr,HttpServletResponse response){
        PrintWriter out = null;
        try{
            out = response.getWriter();
            out.print(returnStr);
            out.flush();
        }catch (Exception e){
            log.error("getWriter exception :" + e);
        }finally {
            if(out != null) {
                out.close();
            }
        }
    }

    public static Map<String,String> parseRequestXml(String xmlStr) throws DocumentException{
        Map<String, String> map = new HashMap<String, String>();
        try {
            StringReader read = new StringReader(xmlStr);
            //创建一个新的SAXReader
            SAXReader sb = new SAXReader();
            Document doc = sb.read(read);
            Element root = doc.getRootElement();

            String version = root.elementText("Version");
            String filePath  = root.elementText("FilePath");
            Element auth = root.element("Auth");
            String accessId = auth.elementText("AccessId");
            String securityKey = auth.elementText("SecurityKey");
            String cpid = auth.elementText("CPID");
            String currentTime = auth.elementText("CurrentTime");
            map.put("Version", version);
            map.put("AccessId", accessId);
            map.put("SecurityKey", securityKey);
            map.put("CPID", cpid);
            map.put("CurrentTime", currentTime);
            map.put("FilePath", filePath);
            log.info("RequestInfo [Version=" + version + ", AccessId=" + accessId + ", SecurityKey=" + securityKey
                    + ", CPID=" + cpid + ", CurrentTime=" + currentTime + ", FilePath=" + filePath  + "]");

        } catch (DocumentException e) {
            throw e;
        } finally {
        }
        return map;
    }


    //解析直播内容无版权的XML文件
    public static List<Object> parseLivingContentNoCopyrightXml(String xmlStr) throws DocumentException {
        List<Object> list = new ArrayList<Object>();

        try {
            StringReader read = new StringReader(xmlStr);
            //创建一个新的SAXReader
            SAXReader sb = new SAXReader();
            Document doc = sb.read(read);
            Element root = doc.getRootElement();
            List<Element> childElements = root.elements();
            for(Element ele : childElements){
                Map<String, Object> rootMap = new ConcurrentHashMap<String, Object>();
                String version = ele.elementText("Version");
                String operCode = ele.elementText("OperCode");
                String assetId = ele.elementText("AssetId");
                String type = ele.elementText("Type");
                String isPrivate = ele.elementText("IsPrivate");
                String sequence = ele.elementText("Sequence");
                String name = ele.elementText("Name");
                String shortName = ele.elementText("ShortName");
                String keywords = ele.elementText("Keywords");
                String description = ele.elementText("Description");
                String displayType = ele.elementText("DisplayType");
                String assist = ele.elementText("Assist");
                String category = ele.elementText("Category");
                String channelType = ele.elementText("ChannelType");
                String radioType = ele.elementText("RadioType");
                String tvType = ele.elementText("TvType");
                String directRecFlag = ele.elementText("DirectRecFlag");
                String onlineTime = ele.elementText("OnlineTime");
                String directRecDuration = ele.elementText("DirectRecDuration");

                String playbillFilePath = ele.elementText("PlaybillFilePath");

                rootMap.put("Version",version);
                rootMap.put("OperCode",operCode);
                rootMap.put("AssetId",assetId);
                rootMap.put("Type",type);
                rootMap.put("Sequence",sequence);
                if(!StringUtil.isNullStr(isPrivate)){
                    rootMap.put("IsPrivate",isPrivate);
                }
                rootMap.put("Name",name);
                rootMap.put("ShortName",shortName);
                rootMap.put("Keywords",keywords);
                rootMap.put("Description",description);
                rootMap.put("DisplayType",displayType);
                rootMap.put("Assist",assist);
                rootMap.put("Category",category);
                rootMap.put("ChannelType",channelType);
                if(!StringUtil.isNullStr(radioType)){
                    rootMap.put("RadioType",radioType);
                }
                if(!StringUtil.isNullStr(tvType)){
                    rootMap.put("TvType",tvType);
                }
                rootMap.put("DirectRecFlag",directRecFlag);
                rootMap.put("OnlineTime",onlineTime);
                rootMap.put("DirectRecDuration",directRecDuration);
                if(!StringUtil.isNullStr(playbillFilePath)){
                    rootMap.put("PlaybillFilePath",playbillFilePath);
                }

                /**
                 * 媒体文件信息
                 */
                Vector<Object> mediaList = new Vector<Object>();
                Element mediaFileListEle = ele.element("MediaFileLists");
                List<Element> MediaFileLists = mediaFileListEle.elements();
                for(int j=0; j < MediaFileLists.size(); j++){
                    Element mediaFileEle = MediaFileLists.get(j);
                    String mediaFilePath = mediaFileEle.elementText("MediaFilePath");
                    String mediaUsageCode = mediaFileEle.elementText("MediaUsageCode");
                    Map<String, String> mediaFileMap = new ConcurrentHashMap<String, String>();
                    if(!StringUtil.isNullStr(mediaFilePath)){
                        mediaFileMap.put("MediaFilePath",mediaFilePath);
                    }
                    if(!StringUtil.isNullStr(mediaUsageCode)){
                        mediaFileMap.put("MediaUsageCode",mediaUsageCode);
                    }
                    if(!mediaFileMap.isEmpty()){
                        mediaList.add(mediaFileMap);
                    }
                }
                rootMap.put("MediaFileLists",mediaList);
                //内容图片信息
                Vector<String> imageList = new Vector<String>();
                Element DisPlayFileListEle = ele.element("DisPlayFileLists");
                List<Element> DisPlayFileLists = DisPlayFileListEle.elements();
                for(Element DisPlayFileEle : DisPlayFileLists){
                    String dPFilePath = DisPlayFileEle.elementText("DPFilePath");
                    if(!StringUtil.isNullStr(dPFilePath)){
                        imageList.add(dPFilePath);
                    }
                }
                rootMap.put("DisPlayFileLists",imageList);
                list.add(rootMap);
            }

        } catch (DocumentException e) {
            throw e;
        } finally {
        }
        return list;
    }
    
    //解析点播内容的无版权XML文件
    public static List<Object> parseOnCommandContentNoCopyrightXml(String xmlStr) throws DocumentException {
        List<Object> list = new ArrayList<Object>();
        try {
            StringReader read = new StringReader(xmlStr);
            //创建一个新的SAXReader
            SAXReader sb = new SAXReader();
            Document doc = sb.read(read);
            Element root = doc.getRootElement();
            List<Element> childElements = root.elements();
            for(Element ele : childElements){
                Map<String, Object> rootMap = new ConcurrentHashMap<String, Object>();
                String version = ele.elementText("Version");
                String operCode = ele.elementText("OperCode");
                String assetId = ele.elementText("AssetId");
                String type = ele.elementText("Type");
                String sequence = ele.elementText("Sequence");
                String formType = ele.elementText("FormType");
                String isPrivate = ele.elementText("IsPrivate");
                String name = ele.elementText("Name");
                String shortName = ele.elementText("ShortName");
                String keywords = ele.elementText("Keywords");
                String assetType = ele.elementText("AssetType");
                String description = ele.elementText("Description");
                String displayType = ele.elementText("DisplayType");
                String assist = ele.elementText("Assist");
                String country = ele.elementText("Country");
                String captionLang = ele.elementText("CaptionLang");
                String lang = ele.elementText("Lang");
                String platform = ele.elementText("Platform");
                String serialCount = ele.elementText("SerialCount");
                String serialSequence = ele.elementText("SerialSequence");
                String category = ele.elementText("Category");
                String mediaLevel = ele.elementText("MediaLevel");
                String cDuration = ele.elementText("CDuration");
                String serialAssetId = ele.elementText("SerialAssetId");
                String serialTrailingSequence = ele.elementText("SerialTrailingSequence");
                String onlineTime = ele.elementText("OnlineTime");
                String mpid = ele.elementText("Mpid");
                String isUrgency = ele.elementText("IsUrgency");
                String strategyId = ele.elementText("StrategyId");
                String occurred = ele.elementText("Occurred");
                String recommendation = ele.elementText("Recommendation");
                String assetSource = ele.elementText("AssetSource");

                if(!StringUtil.isNullStr(occurred)) {
                	rootMap.put("Occurred", occurred);
                }
				if(!StringUtil.isNullStr(recommendation)) {
					rootMap.put("Recommendation", recommendation);
                }
				if(!StringUtil.isNullStr(assetSource)) {
					rootMap.put("AssetSource", assetSource);
				}
                rootMap.put("Version",version);
                rootMap.put("OperCode",operCode);
                if(!StringUtil.isNullStr(assetId)){
                    rootMap.put("AssetId",assetId);
                }
                rootMap.put("Type",type);
                rootMap.put("Sequence",sequence);
                rootMap.put("FormType",formType);
                if(!StringUtil.isNullStr(isPrivate)){
                    rootMap.put("IsPrivate",isPrivate);
                }
                rootMap.put("Name",name);
                rootMap.put("ShortName",shortName);
                rootMap.put("Keywords",keywords);
                rootMap.put("AssetType",assetType);
                rootMap.put("Description",description);
                rootMap.put("DisplayType",displayType);
                rootMap.put("Assist",assist);
                rootMap.put("Country",country);
                rootMap.put("CaptionLang",captionLang);
                rootMap.put("Lang",lang);
                rootMap.put("Platform",platform);
                rootMap.put("SerialCount",serialCount);
                rootMap.put("SerialSequence",serialSequence);
                rootMap.put("Category",category);
                rootMap.put("MediaLevel",mediaLevel);
                rootMap.put("CDuration",cDuration);
                rootMap.put("SerialAssetId",serialAssetId);
                rootMap.put("SerialTrailingSequence",serialTrailingSequence);
                rootMap.put("OnlineTime",onlineTime);
                rootMap.put("Mpid",StringUtil.null2Str(mpid));//新添加的内容
                rootMap.put("IsUrgency",isUrgency);
                rootMap.put("StrategyId",strategyId);
                /**
                 * 拓展属性
                 */
                Map<String, String> extendsMap = new ConcurrentHashMap<String, String>();
                Element propertyFileList = ele.element("PropertyFileLists");
                List<Element> propertyFileLists = propertyFileList.elements();
                for(Element eee : propertyFileLists){
                    String key = eee.elementText("PropertyFileKey");
                    String value = eee.elementText("PropertyFileValue");
                    if(!StringUtil.isNullStr(key) && !StringUtil.isNullStr(value)){
                        extendsMap.put(key,value);
                    }
                }
                rootMap.put("PropertyFileLists",extendsMap);
               
                /**
                 * 媒体文件信息
                 */
                Vector<Object> mediaList = new Vector<Object>();
                Element mediaFileListEle = ele.element("MediaFileLists");
                List<Element> MediaFileLists = mediaFileListEle.elements();
                for(int j=0; j < MediaFileLists.size(); j++){
                    Element mediaFileEle = MediaFileLists.get(j);
                    String mediaFileName = mediaFileEle.elementText("MediaFileName");
                    String mediaFilePath = mediaFileEle.elementText("MediaFilePath");
                    String mediaUsageCode = mediaFileEle.elementText("MediaUsageCode");
                    String source = mediaFileEle.elementText("Source");
                    Map<String, String> mediaFileMap = new ConcurrentHashMap<String, String>();
                    if(!StringUtil.isNullStr(mediaFileName)){
                        mediaFileMap.put("MediaFileName",mediaFileName);
                    }
                    if(!StringUtil.isNullStr(mediaFilePath)){
                        mediaFileMap.put("MediaFilePath",mediaFilePath);
                    }
                    if(!StringUtil.isNullStr(mediaUsageCode)){
                        mediaFileMap.put("MediaUsageCode",mediaUsageCode);
                    }
                    if(!StringUtil.isNullStr(source)){
                        mediaFileMap.put("Source",source);
                    }
                    if(!mediaFileMap.isEmpty()){
                        mediaList.add(mediaFileMap);
                    }
                }
                rootMap.put("MediaFileLists",mediaList);
                //内容图片信息
                Vector<String> imageList = new Vector<String>();
                Element DisPlayFileListEle = ele.element("DisPlayFileLists");
                List<Element> DisPlayFileLists = DisPlayFileListEle.elements();
                for(Element DisPlayFileEle : DisPlayFileLists){
                    String dPFilePath = DisPlayFileEle.elementText("DPFilePath");
                    if(!StringUtil.isNullStr(dPFilePath)){
                        imageList.add(dPFilePath);
                    }
                }
                rootMap.put("DisPlayFileLists",imageList);
                list.add(rootMap);
            }


           /* log.info("the XML file of onDemand content in FTP server is  [Version=" + version + ", AccessId=" + accessId + ", SecurityKey=" + securityKey
                    + ", CPID=" + cpid + ", CurrentTime=" + currentTime + ", OperType="
                    + operType + ", FilePath=" + filePath  + "]");*/

        } catch (DocumentException e) {
            throw e;
        } finally {
        }
        return list;
    }

    public static String geneCorrXMLFile(String resCode, String resMessage, String sequence, String contentId, String assetId){
        StringBuilder sb = new StringBuilder("<Content>");
        if(!StringUtil.isNullStr(resCode)){
            sb.append("<ResCode>" + resCode + "</ResCode>");
        }else{
            sb.append("<ResCode>" + "</ResCode>");
        }
        if(!StringUtil.isNullStr(resMessage)){
            sb.append("<ResMessage>" + resMessage + "</ResMessage>");
        }else{
            sb.append("<ResMessage>" + "</ResMessage>");
        }
        if(!StringUtil.isNullStr(sequence)){
            sb.append("<Sequence>" + sequence + "</Sequence>");
        }else{
            sb.append("<Sequence>" + "</Sequence>");
        }
        if(!StringUtil.isNullStr(contentId)){
            sb.append("<ContentId>" + contentId + "</ContentId>");
        }else {
            sb.append("<ContentId>" + "</ContentId>");
        }
        if(!StringUtil.isNullStr(assetId)){
            sb.append("<AssetId>" + assetId + "</AssetId>");
        }else {
            sb.append("<AssetId>" + "</AssetId>");
        }
      
        sb.append("</Content>");
        return sb.toString();
    }

    public static String geneCorrXMLFile1(String resCode, String resMessage, String isAssetExist,String sequence, String contentId, String assetId){
        StringBuilder sb = new StringBuilder("<Content>");
        if(!StringUtil.isNullStr(resCode)){
            sb.append("<ResCode>" + resCode + "</ResCode>");
        }else{
            sb.append("<ResCode>" + "</ResCode>");
        }
        if(!StringUtil.isNullStr(resMessage)){
            sb.append("<ResMessage>" + resMessage + "</ResMessage>");
        }else{
            sb.append("<ResMessage>" + "</ResMessage>");
        }
        if(!StringUtil.isNullStr(isAssetExist)){
            sb.append("<IsAssetExist>" + isAssetExist + "</IsAssetExist>");
        }else{
            sb.append("<IsAssetExist>" + MamConstants.IS_ASSET_EXIST_NEW + "</IsAssetExist>");
        }
        if(!StringUtil.isNullStr(sequence)){
            sb.append("<Sequence>" + sequence + "</Sequence>");
        }else{
            sb.append("<Sequence>" + "</Sequence>");
        }
        if(!StringUtil.isNullStr(contentId)){
            sb.append("<ContentId>" + contentId + "</ContentId>");
        }else {
            sb.append("<ContentId>" + "</ContentId>");
        }
        if(!StringUtil.isNullStr(assetId)){
            sb.append("<AssetId>" + assetId + "</AssetId>");
        }else {
            sb.append("<AssetId>" + "</AssetId>");
        }
       
        sb.append("</Content>");
        return sb.toString();
    }
    
    /**
     * 验证点播媒资无版权基础信息的方法
     */
    public static boolean validateAssetNoCopyrightBaseInfo(Map<String,Object> paramsMap){
        Map<String,Object> returnMap = (Map<String,Object>)paramsMap.get("ReturnMap");
        Map<String,Object> contentXmlMap = (Map<String,Object>)paramsMap.get("ContentXmlMap");
        /**********************************************************************************/
        String operCode = (String)contentXmlMap.get("OperCode");
        String formType = (String)contentXmlMap.get("FormType");
        String isPrivate = (String)contentXmlMap.get("IsPrivate");
        String assetId = (String)contentXmlMap.get("AssetId");
        String type = (String)contentXmlMap.get("Type");
        String assetType = (String)contentXmlMap.get("AssetType");
        String sequence = (String)contentXmlMap.get("Sequence");
        String assetName = (String)contentXmlMap.get("Name");
        String shortName = (String)contentXmlMap.get("ShortName");
        String keywords = (String)contentXmlMap.get("Keywords");
        String country = (String)contentXmlMap.get("Country");
        String captionLang = (String)contentXmlMap.get("CaptionLang");
        String description = (String)contentXmlMap.get("Description");
        String lang = (String)contentXmlMap.get("Lang");
        String platform = (String)contentXmlMap.get("Platform");
        String serialSequence = (String)contentXmlMap.get("SerialSequence");
        String serialAssetId = (String)contentXmlMap.get("SerialAssetId");
        String category = (String)contentXmlMap.get("Category");
        String mediaLevel = (String)contentXmlMap.get("MediaLevel");
        String onlineTime = (String)contentXmlMap.get("OnlineTime");
        String isUrgency = (String)contentXmlMap.get("IsUrgency");

        if(StringUtil.isNullStr(operCode) || "1,2".indexOf(operCode) == -1){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","格式校验失败，操作编码格式不正确,操作编码只能为1：新增 2：变更！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }
        if(MamConstants.IMPORTED_CONTENT_OPERCODE_MODIFY.equals(operCode)){
            //变更的情况
            if(StringUtil.isNullStr(assetId) || !StringUtil.isNumber(assetId)){
                returnMap.put("resCode","1003");
                returnMap.put("resMessage","变更的情况下，AssetId不允许为空，且必须为数字类型！");
                BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                return Boolean.FALSE;
            }

        }else if(MamConstants.IMPORTED_CONTENT_OPERCODE_ADD.equals(operCode)){
            //新增的情况
            if(!StringUtil.isNullStr(assetId)){
                returnMap.put("resCode","1003");
                returnMap.put("resMessage","新增的情况下，AssetId必须为空！");
                BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                return Boolean.FALSE;
            }
        }
        if(StringUtil.isNullStr(isPrivate) || "true,false".indexOf(isPrivate) == -1){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","是否为私有媒资不允许为空且必须填写true或者false！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }
        if(!StringUtil.isNumber(assetType)){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容形态不允许为空且必须为数字类型！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }
        if(!"7".equals(type)){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的服务类型必须为7(精简编码)！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }
        if(StringUtil.isNullStr(formType) || "6,7,8,10".indexOf(formType) == -1){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的媒资类型必须为6：剧集7：子集 8：单片10：内容集的其中之一！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }
        if(StringUtil.isNullStr(sequence)){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的格式校验失败，Sequence字段不允许为空！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }

        if(StringUtil.isNullStr(assetName)){//
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的格式校验失败，Name字段不允许为空！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }
        if(StringUtil.isNullStr(keywords)){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的格式校验失败，Keywords字段不允许为空！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }
        if(StringUtil.isNullStr(description)){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的格式校验失败，Description字段不允许为空！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }
        String displayType = (String)contentXmlMap.get("DisplayType");
        if(StringUtil.isNullStr(displayType) || !StringUtil.isNumber(displayType)){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的格式校验失败，DisplayType字段不允许为空且必须为数字类型！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }
        if(!MamConstants.IS_SIMPLE_CLASS.equals("1")){
            if(StringUtil.isNullStr(shortName)){
                returnMap.put("resCode","1003");
                returnMap.put("resMessage","内容的格式校验失败，ShortName字段不允许为空！");
                BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                return Boolean.FALSE;
            }
        }

        //String assist = (String)contentXmlMap.get("Assist");
        if(StringUtil.isNullStr(country)){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的格式校验失败，Country字段不允许为空！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }
        if(StringUtil.isNullStr(captionLang)){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的格式校验失败，CaptionLang字段不允许为空！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }
        if(StringUtil.isNullStr(lang)){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的格式校验失败，Lang字段不允许为空！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }
        if(StringUtil.isNullStr(platform)){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的格式校验失败，Platform字段不允许为空！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }
        if("6".indexOf(formType) > -1){
            String serialCount = (String)contentXmlMap.get("SerialCount");
            if(StringUtil.isNullStr(serialCount) || !StringUtil.isNumber(serialCount)){
                returnMap.put("resCode","1003");
                returnMap.put("resMessage","内容的格式校验失败，当媒资类型为剧集时，SerialCount字段不允许为空且必须为数字类型！");
                BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                return Boolean.FALSE;
            }
        }
        if("7".equals(formType)){
            if(StringUtil.isNullStr(serialAssetId) || !StringUtil.isNumber(serialAssetId)){
                returnMap.put("resCode","1003");
                returnMap.put("resMessage","内容的格式校验失败，媒资类型为子集时，SerialAssetId字段不允许为空且必须为数字类型！");
                BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                return Boolean.FALSE;
            }
         
        }
        if(StringUtil.isNullStr(category) || "1,2,3".indexOf(category) == -1){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的格式校验失败，Category字段必须为1,2,3中之一！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }
        if(!StringUtil.isNullStr(mediaLevel)){
            int mediaLevelInt = 0;
            try {
                mediaLevelInt = Integer.valueOf(mediaLevel);
            } catch (NumberFormatException e) {
                returnMap.put("resCode","1003");
                returnMap.put("resMessage","内容的格式校验失败，MediaLevel字段必须为1,2,3中之一！");
                BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                return Boolean.FALSE;
            }
            switch(mediaLevelInt){
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                default:
                    returnMap.put("resCode","1003");
                    returnMap.put("resMessage","内容的格式校验失败，MediaLevel字段必须为1,2,3中之一！");
                    BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                    return Boolean.FALSE;
            }

        }else{
            //子集和单片的MediaLevel字段不允许为空
            if("7,8".indexOf(formType) > -1 && !"2".equals(category)){
                returnMap.put("resCode","1003");
                returnMap.put("resMessage","内容的格式校验失败，MediaLevel字段不允许为空！");
                BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                return Boolean.FALSE;
            }
        }
        if(!StringUtil.isNullStr(onlineTime)){
            if(!DateUtil.isValidateCopyrightDate(onlineTime)){
                returnMap.put("resCode","1003");
                returnMap.put("resMessage","内容的格式校验失败，OnlineTime必须为yyyy-MM-dd HH:mm:ss的格式！");
                BatchContentImportUtil.returnMethod(returnMap);
                return Boolean.FALSE;
            }
        }
        if("0,1,2".indexOf(isUrgency) == -1){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的格式校验失败，IsUrgency字段必须为0,1,2中之一！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 验证直播媒资无版权基础信息的方法
     */
    public static boolean validateLivingAssetNoCopyrightBaseInfo(Map<String,Object> paramsMap){
        Map<String,Object> returnMap = (Map<String,Object>)paramsMap.get("ReturnMap");
        Map<String,Object> contentXmlMap = (Map<String,Object>)paramsMap.get("ContentXmlMap");
        /**********************************************************************************/
        String operCode = (String)contentXmlMap.get("OperCode");
        String formType = (String)contentXmlMap.get("FormType");
        String isPrivate = (String)contentXmlMap.get("IsPrivate");
        String assetId = (String)contentXmlMap.get("AssetId");
        String type = (String)contentXmlMap.get("Type");
        String assetType = (String)contentXmlMap.get("AssetType");
        String sequence = (String)contentXmlMap.get("Sequence");
        String assetName = (String)contentXmlMap.get("Name");
        String shortName = (String)contentXmlMap.get("ShortName");
        String keywords = (String)contentXmlMap.get("Keywords");
        String country = (String)contentXmlMap.get("Country");
        String captionLang = (String)contentXmlMap.get("CaptionLang");
        String description = (String)contentXmlMap.get("Description");
        String lang = (String)contentXmlMap.get("Lang");
        String platform = (String)contentXmlMap.get("Platform");
        String serialSequence = (String)contentXmlMap.get("SerialSequence");
        String serialAssetId = (String)contentXmlMap.get("SerialAssetId");
        String category = (String)contentXmlMap.get("Category");
        String mediaLevel = (String)contentXmlMap.get("MediaLevel");
        String onlineTime = (String)contentXmlMap.get("OnlineTime");
        String isUrgency = (String)contentXmlMap.get("IsUrgency");

        if(StringUtil.isNullStr(operCode) || "1,2".indexOf(operCode) == -1){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","格式校验失败，操作编码格式不正确,操作编码只能为1：新增 2：变更！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }

        if(MamConstants.IMPORTED_CONTENT_OPERCODE_MODIFY.equals(operCode)){
            if(StringUtil.isNullStr(assetId) || !StringUtil.isNumber(assetId)){
                returnMap.put("resCode","1003");
                returnMap.put("resMessage","变更的情况下，AssetId不允许为空，且必须为数字类型！");
                BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                return Boolean.FALSE;
            }
        }

        log.info("operCode:" + operCode + ",type:" + type);
        if(StringUtil.isNullStr(type) || "8,12".indexOf(type) == -1){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的服务类型必须为 8：精简直播 12：互联网直播！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }

        if(StringUtil.isNullStr(isPrivate) || "true,false".indexOf(isPrivate) == -1){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","是否为私有媒资不允许为空且必须填写true或者false！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }
        if(StringUtil.isNullStr(sequence)){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的格式校验失败，Sequence字段不允许为空！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }

        if(!StringUtil.isNullStr(assetName)){//

        }else{
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的格式校验失败，Name字段不允许为空！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }

        if(StringUtil.isNullStr(keywords)){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的格式校验失败，Keywords字段不允许为空！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }

        if(!StringUtil.isNullStr(description)){//不包含特殊字符，字数长度范围[20~500]

        }else{
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的格式校验失败，Description字段不允许为空！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }

        String displayType = (String)contentXmlMap.get("DisplayType");
        if(StringUtil.isNullStr(displayType)){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的格式校验失败，DisplayType字段不允许为空！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }
        if(!StringUtil.isNullStr(shortName)){

        }else{
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的格式校验失败，ShortName字段不允许为空！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }
        String assist = (String)contentXmlMap.get("Assist");
        if(StringUtil.isNullStr(assist)){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的格式校验失败，Assist字段不允许为空！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }

        if(StringUtil.isNullStr(category) || "1,2,3".indexOf(category) == -1){
            returnMap.put("resCode","1003");
            returnMap.put("resMessage","内容的格式校验失败，Category字段必须为1,2,3中之一！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    /**
     * 验证点播媒资图片信息的方法
     */
    public static boolean validateAssetImgInfo(Map<String,Object> paramsMap){
        Map<String,Object> returnMap = (Map<String,Object>)paramsMap.get("ReturnMap");
        Map<String,Object> contentXmlMap = (Map<String,Object>)paramsMap.get("ContentXmlMap");
        Vector<String> imgList = (Vector<String>)contentXmlMap.get("DisPlayFileLists");
        String cpid = (String)paramsMap.get("CPID");
        String WORK_DIR = (String)paramsMap.get("RootDir");
        String formType = (String)contentXmlMap.get("FormType");
        String ASSET_TYPE_FULL = (String)paramsMap.get("AssetTypeFull");
        String assetType = (String)contentXmlMap.get("AssetType");
        //内容的图片是否已经存于FTP服务器上
        if(imgList.size() < 1){
            //FTP无内容的图片
            returnMap.put("resCode","2002");
            returnMap.put("resMessage","内容的图片文件不存在！");
            log.info("内容的图片文件不存在！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }else{
            //当内容的图片存在时，判断图片的数量和格式是否满足要求
            boolean h32 = false;
            boolean v34 = false;
            boolean v23 = false;
            boolean h53 = false;
            boolean h43 = false;
            boolean s11 = false;
            boolean h16x9 = false;
            for(int i=0; i <  imgList.size(); i++){
                String imgRelativePath = imgList.get(i);
                String imgFullPath = WORK_DIR + cpid + File.separator + imgRelativePath;
                log.info("*******imgFullPath:" + imgFullPath);
                File imgFile = new File(imgFullPath);
                if(imgFile.exists()){
                    //FILENAME为图片文件名称，文件名称中只能包含英文字母、数字和下划线，不能出现空格和特殊字符，后缀名必须小写。
                    String imgName = imgFile.getName();
                    log.info("**********imgName:"+imgName);
                    String eL = "[\\w\\W]*\\.jpg";
                    // String eL = "[0-9a-zA_Z_]+\\.jpg";//匹配只能包含英文字母、数字和下划线，后缀名必须小写且为.jpg
                    if(Pattern.compile(eL).matcher(imgName).matches()){
                        //点播剧集壳、内容集壳和单片中内容形态为”全片“(20180125单片不在区分全片非全片ASSET_TYPE_FULL.equals(assetType))
                        if("6,10".indexOf(formType) > -1 || ("8".equals(formType) ) ){
                            //if(imgList.size() >= 6){
                            if(imgName.indexOf("_H32_sc")>-1) {
                                h32 = true;
                            }
                            if(imgName.indexOf("_V34_sc")>-1) {
                                v34 = true;
                            }
                            if(imgName.indexOf("_V23_sc")>-1) {
                                v23 = true;
                            }
                            if(imgName.indexOf("_H53_sc")>-1) {
                                h53 = true;
                            }
                            if(imgName.indexOf("_H43_sc")>-1) {
                                h43 = true;
                            }
                            if(imgName.indexOf("_S11_sc")>-1) {
                                s11 = true;
                            }
                            if(imgName.indexOf("_H169_sc")>-1) {
    							h16x9 = true;
    						}
                            if(i == (imgList.size() - 1)){
                            	if("6,10".indexOf(formType) > -1||(!"1".equals(MamConstants.IS_SIMPLE_CLASS)&&"8".equals(formType))) {
                                	if((!h32 || !v34 || !v23 || !h16x9)&&imgList.size() >= 4) {
                                        returnMap.put("resCode","1003");
                                        returnMap.put("resMessage","内容的图片文件名称命名不正确,必须要满足名称命名规范！");
                                        BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                                        return Boolean.FALSE;
                                    } else if(imgList.size() < 4) {
                                    	returnMap.put("resCode","2002");
                                        returnMap.put("resMessage","内容的图片文件不存在,至少需要四张图片！");
                                        BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                                        return Boolean.FALSE;
                                    }
                                } else if("1".equals(MamConstants.IS_SIMPLE_CLASS)&&"8".equals(formType)) {
                                	if((!h32 || !h16x9)&&imgList.size() >= 2) {
                                        returnMap.put("resCode","1003");
                                        returnMap.put("resMessage","内容的图片文件名称命名不正确,必须要满足规范！");
                                        BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                                        return Boolean.FALSE;
                                    } else if(imgList.size() < 2) {
                                    	returnMap.put("resCode","2002");
                                        returnMap.put("resMessage","内容的图片文件不存在,至少需要两张图片！");
                                        BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                                        return Boolean.FALSE;
                                    }
                                } 
                            }
                        }
                        /*//点播单片中内容形态非全片的
                        if("8".equals(formType) && !ASSET_TYPE_FULL.equals(assetType)){
                            if(imgList.size() >= 2){
                                if(imgName.indexOf("_H32_sc")>-1) {
                                    h32 = true;
                                }
                                if(imgName.indexOf("_V34_sc")>-1) {
                                    v34 = true;
                                }

                                if(i == (imgList.size() - 1)){
                                    if(!h32 || !v34) {
                                        returnMap.put("resCode","1003");
                                        returnMap.put("resMessage","内容的图片文件名称命名不正确,必须要满足名称命名规范！");
                                        BatchContentImportUtil.returnMethod(returnMap);
                                        return Boolean.FALSE;
                                    }
                                }
                            }else{
                                returnMap.put("resCode","2002");
                                returnMap.put("resMessage","内容的图片文件不存在,至少需要两张图片！");
                                BatchContentImportUtil.returnMethod(returnMap);
                                return Boolean.FALSE;
                            }
                        }*/
                        //子集的图片要求
                        if("7".equals(formType)){
                            if(imgName.indexOf("_H32_sc")>-1) {
                                h32 = true;
                            }
                            if(imgName.indexOf("_H169_sc")>-1) {
                            	h16x9 = true;
                            }
                            if(i == (imgList.size() - 1)){
                            	if((!h32 || !h16x9)&&imgList.size() >= 2) {
                                    returnMap.put("resCode","1003");
                                    returnMap.put("resMessage","内容的图片文件名称命名不正确,必须要满足规范！");
                                    BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                                    return Boolean.FALSE;
                                } else if(imgList.size() < 2) {
                                	returnMap.put("resCode","2002");
                                    returnMap.put("resMessage","内容的图片文件缺少,至少需要两张图片！");
                                    BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                                    return Boolean.FALSE;
                                }
                            }
                        }
                        //图片的命名格式校验完成后校验文件的尺寸大小比例和最低尺寸要求
                        if(!validateImageRatio(imgFullPath)){
                            returnMap.put("resCode","1003");
                            returnMap.put("resMessage","内容的图片" + imgName + " 文件大小和比例不正确,必须要满足图片的制作规范！");
                            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                            return Boolean.FALSE;
                        }
                    }else{
                        returnMap.put("resCode","1003");
                        returnMap.put("resMessage","内容的图片名称命名格式不正确，不能包含除英文字母、数字和下划线以外的字符，且后缀为.jpg！");
                        BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                        return Boolean.FALSE;
                    }
                }else{
                    returnMap.put("resCode","2002");
                    returnMap.put("resMessage","路径为"+ imgRelativePath +"的内容的图片文件不存在！");
                    BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                    return Boolean.FALSE;
                }
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 验证直播媒资图片信息的方法
     */
    public static boolean validateLivingAssetImgInfo(Map<String,Object> paramsMap){
        Map<String,Object> returnMap = (Map<String,Object>)paramsMap.get("ReturnMap");
        Map<String,Object> contentXmlMap = (Map<String,Object>)paramsMap.get("ContentXmlMap");
        Vector<String> imgList = (Vector<String>)contentXmlMap.get("DisPlayFileLists");
        String cpid = (String)paramsMap.get("CPID");
        String WORK_DIR = (String)paramsMap.get("RootDir");

        //内容的图片是否已经存于FTP服务器上
        if(imgList.size() < 3){
            //FTP无内容的图片
            returnMap.put("resCode","2002");
            returnMap.put("resMessage","内容的图片文件不满足至少三张的要求！");
            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
            return Boolean.FALSE;
        }else{
            //当内容的图片存在时，判断图片的数量和格式是否满足要求
            boolean h32 = false;
            boolean v34 = false;
            boolean tb = false;
            for(int i=0; i <  imgList.size(); i++){
                String imgRelativePath = imgList.get(i);
                String imgFullPath = WORK_DIR + cpid + File.separator + imgRelativePath;
                log.info("*******imgFullPath:" + imgFullPath);
                File imgFile = new File(imgFullPath);
                if(imgFile.exists()){
                    //FILENAME为图片文件名称，文件名称中只能包含英文字母、数字和下划线，不能出现空格和特殊字符，后缀名必须小写。
                    String imgName = imgFile.getName();
                    log.info("**********imgName:"+imgName);
                    if(imgName.indexOf("_H32_sc.jpg")>-1) {
                        h32 = true;
                    }
                    if(imgName.indexOf("_V34_sc.jpg")>-1) {
                        v34 = true;
                    }
                    if(imgName.indexOf("_TB_sc.jpg")>-1) {
                        tb = true;
                    }
                    if(i == (imgList.size() - 1)){
                        if(!h32 || !v34 || !tb) {
                            returnMap.put("resCode","1003");
                            returnMap.put("resMessage","内容的图片文件名称命名不正确,必须要满足名称命名规范！");
                            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                            return Boolean.FALSE;
                        }
                    }
                    //图片的命名格式校验完成后校验文件的尺寸大小比例和最低尺寸要求
                    if(!validateImageRatio(imgFullPath)){
                        returnMap.put("resCode","1003");
                        returnMap.put("resMessage","内容的图片" + imgName + " 文件大小和比例不正确,必须要满足图片的制作规范！");
                        BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                        return Boolean.FALSE;
                    }
                }else{
                    returnMap.put("resCode","2002");
                    returnMap.put("resMessage","路径为"+ imgRelativePath +"的内容的图片文件不存在！");
                    BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                    return Boolean.FALSE;
                }
            }

        }
        return Boolean.TRUE;
    }


    /**
     * 验证点播媒资媒体文件信息的方法
     */
    public static boolean validateAssetMeidaFile(Map<String,Object> paramsMap){
        Map<String,Object> returnMap = (Map<String,Object>)paramsMap.get("ReturnMap");
        Map<String,Object> contentXmlMap = (Map<String,Object>)paramsMap.get("ContentXmlMap");
        String cpid = (String)paramsMap.get("CPID");
        String WORK_DIR = (String)paramsMap.get("RootDir");
        String formType = (String)contentXmlMap.get("FormType");
        List<Object> mediaFileList = (List<Object>)contentXmlMap.get("MediaFileLists");
        if("6,10".indexOf(formType) > -1){
            //内容集和剧集壳都是没有媒体文件的
            if(mediaFileList.size() > 0){
                returnMap.put("resCode","1008");
                returnMap.put("resMessage","剧集或者内容集的媒体文件必须为空！");
                BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                return Boolean.FALSE;
            }
        }else{
            String strategyId = (String)contentXmlMap.get("StrategyId");
            if(mediaFileList.size() > 0){
                //如果存在媒体文件，必须要填写转码策略
                if(StringUtil.isNullStr(strategyId) || !StringUtil.isNumber(strategyId)){
                    returnMap.put("resCode","1003");
                    returnMap.put("resMessage","内容的格式校验失败，StrategyId字段不允许为空且必须为数字类型！");
                    BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                    return Boolean.FALSE;
                }
                int sourceCount = 0;//用于判断源片是否只有一个
                int sourceFormatCount = 0;//用于判断源片的命名格式是否满足源文件的要求
                for(int i=0; i < mediaFileList.size(); i++){
                    Map<String,String> mediaFileMap = (Map<String,String>)mediaFileList.get(i);
                    String mediaFileName = mediaFileMap.get("MediaFileName");
                    String mediaFilePath = mediaFileMap.get("MediaFilePath");
                    String mediaUsageCode = mediaFileMap.get("MediaUsageCode");
                    String source = mediaFileMap.get("Source");
                    if(StringUtil.isNullStr(mediaFilePath)){
                        returnMap.put("resCode","1003");
                        returnMap.put("resMessage","内容的格式校验失败，媒体文件MediaFilePath不允许为空！");
                        BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                        return Boolean.FALSE;
                    }
                    if(StringUtil.isNullStr(mediaUsageCode) || !StringUtil.isNumber(mediaUsageCode)){
                        returnMap.put("resCode","1003");
                        returnMap.put("resMessage","内容的格式校验失败，媒体文件MediaUsageCode不允许为空且必须为数字类型！");
                        BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                        return Boolean.FALSE;
                    }
                    if(StringUtil.isNullStr(source) || "true,false".indexOf(source) == -1){
                        returnMap.put("resCode","1003");
                        returnMap.put("resMessage","内容的格式校验失败，媒体文件Source不允许为空且必须为true或者false！ ");
                        BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                        return Boolean.FALSE;
                    }
                    if("true".indexOf(source) > -1){
                        sourceCount++;
                    }
                    String mediaFileFullPath = WORK_DIR + cpid + File.separator + mediaFilePath;
                    File mediaFile = new File(mediaFileFullPath);
                    if(!StringUtil.isNullStr(mediaFileName)){
                        String eL = "[\\w\\W]*\\.(ts|ps|mpg|m2t|3gp|mp4|mov|f4v|mkv|avi|wmv|asf|rm|rmvb|flv|wav|mp3|m3u8)";//匹配所有的视频后缀格式
                        if(Pattern.compile(eL).matcher(mediaFileName).matches() && !StringUtil.isNullStr(SourceUtil.getUsageCode(mediaFile.getName()))){
                            if(mediaFile.exists()){
                                if(SourceUtil.isSource(mediaFile.getName())){
                                    sourceFormatCount++;
                                }
                            }else{
                                returnMap.put("resCode","2003");
                                returnMap.put("resMessage","内容的媒体文件不存在！");
                                BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                                return Boolean.FALSE;
                            }

                        }else{
                            returnMap.put("resCode","1003");
                            returnMap.put("resMessage","内容的格式校验失败，媒体文件名称MediaFileName只能包括后缀为.ts|ps|mpg|m2t|3gp|mp4|mov|f4v|mkv|avi|wmv|asf|rm|rmvb|flv|wav|mp3|m3u8的格式且必须满足命名要求！");
                            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                            return Boolean.FALSE;
                        }

                    }else{
                        returnMap.put("resCode","1003");
                        returnMap.put("resMessage","内容的格式校验失败，mediaFileName不允许为空！");
                        BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                        return Boolean.FALSE;
                    }
                }
                if(sourceCount > 1){
                    returnMap.put("resCode","1003");
                    returnMap.put("resMessage","媒体文件源文件只允许有一个！");
                    BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                    return Boolean.FALSE;
                }
                if(sourceCount == 1){
                    contentXmlMap.put("IsSource",Boolean.TRUE);
                }
                if(sourceCount != sourceFormatCount){
                    returnMap.put("resCode","1003");
                    returnMap.put("resMessage","媒体文件源文件命名规范必须和Source节点匹配！");
                    BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                    return Boolean.FALSE;
                }
            }else {
                if(!StringUtil.isNullStr(strategyId)){
                    if(!StringUtil.isNumber(strategyId)){
                        returnMap.put("resCode","1003");
                        returnMap.put("resMessage","内容的格式校验失败，StrategyId字段必须为数字类型！");
                        BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                        return Boolean.FALSE;
                    }
                }
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 验证直播媒资媒体文件信息的方法
     */
    public static boolean validateLivingAssetMeidaFile(Map<String,Object> paramsMap){
        Map<String,Object> returnMap = (Map<String,Object>)paramsMap.get("ReturnMap");
        Map<String,Object> contentXmlMap = (Map<String,Object>)paramsMap.get("ContentXmlMap");
        String cpid = (String)paramsMap.get("CPID");
        String WORK_DIR = (String)paramsMap.get("RootDir");
        String formType = (String)contentXmlMap.get("FormType");
        List<Object> mediaFileList = (List<Object>)contentXmlMap.get("MediaFileLists");


        return Boolean.TRUE;
    }
    /**
     * 验证直播节目单文件信息的方法
     */
    public static Map<String,Object> validatePlaybillFile(Map<String,Object> paramsMap){
        //方法返回的Map
        Map<String,Object> returnMethodMap = new HashMap<String,Object>();

        Map<String,Object> returnMap = (Map<String,Object>)paramsMap.get("ReturnMap");
        Map<String,Object> contentXmlMap = (Map<String,Object>)paramsMap.get("ContentXmlMap");
        String cpid = (String)paramsMap.get("CPID");
        String WORK_DIR = (String)paramsMap.get("RootDir");
        Vector<Object> playBillVector = null;
        if(contentXmlMap.containsKey("PlaybillFilePath")){
            String playbillFilePath = (String)contentXmlMap.get("PlaybillFilePath");
            log.info("$$$$$$$$~~~~~~  playbillFilePath:" + playbillFilePath);
            File playbillFile = new File(WORK_DIR + cpid + File.separator + playbillFilePath.trim());
            if(playbillFile.exists()){
                //存在节目单的情况校验节目单的开始时间必须小于结束时间
                try {
                    String playbillXMLStr = null;
                    playbillXMLStr = BatchContentNoCopyrightImportUtil.readFile2String(4,playbillFilePath, cpid);
                    playBillVector = BatchContentNoCopyrightImportUtil.parsePlaybillXml(playbillXMLStr);
                    for(Object obj : playBillVector){
                        Map<String, Object> rootMap = (Map<String, Object>)obj;
                        String playDay = (String)rootMap.get("PlayDay");
                        if(StringUtil.isNullStr(playDay)){
                            returnMap.put("resCode","1003");
                            returnMap.put("resMessage","格式校验失败，节目单的PlayDay不允许为空！");
                            BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                            returnMethodMap.put("Flag",MamConstants.METHOD_RETURN_STATUS_FAILTURE);
                            return returnMethodMap;
                        }
                        Vector<Object> playListsVector = (Vector<Object>)rootMap.get("PlayLists");
                        //previousEndTime
                        String previousEndTime ="";
                        for(int j = 0; j < playListsVector.size(); j++){
                            Map<String,String> playbillMap = (Map<String,String>)playListsVector.get(j);
                            String startTime = playbillMap.get("StartTime");
                            String endTime = playbillMap.get("EndTime");
                            String playName = playbillMap.get("PlayName");
                            if(StringUtil.isNullStr(playName)){
                                returnMap.put("resCode","1003");
                                returnMap.put("resMessage","格式校验失败，节目单的PlayName不允许为空！");
                                BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                                returnMethodMap.put("Flag",MamConstants.METHOD_RETURN_STATUS_FAILTURE);
                                return returnMethodMap;
                            }

                            if(DateUtil.isValidateDate(startTime) && DateUtil.isValidateDate(endTime)){
                                if(j == 0){
                                    if(startTime.lastIndexOf("00:00") == -1){
                                        returnMap.put("resCode","1003");
                                        returnMap.put("resMessage","格式校验失败，节目单每天的第一条节目必须从00:00开始！");
                                        BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                                        returnMethodMap.put("Flag",MamConstants.METHOD_RETURN_STATUS_FAILTURE);
                                        return returnMethodMap;
                                    }
                                }else {
                                    //比较结束时间小于等于下一条的开始时间
                                    if(previousEndTime.compareTo(startTime) > 0){
                                        returnMap.put("resCode","1003");
                                        returnMap.put("resMessage","格式校验失败，节目单的下一条节目的开始时间必须大于等于当前节目的结束时间！");
                                        BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                                        returnMethodMap.put("Flag",MamConstants.METHOD_RETURN_STATUS_FAILTURE);
                                        return returnMethodMap;
                                    }
                                }
                                previousEndTime = endTime;
                                if(startTime.compareTo(endTime) >= 0){
                                    returnMap.put("resCode","1003");
                                    returnMap.put("resMessage","格式校验失败，节目单的开始时间必须小于结束时间！");
                                    BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                                    returnMethodMap.put("Flag",MamConstants.METHOD_RETURN_STATUS_FAILTURE);
                                    return returnMethodMap;
                                }

                            }else {
                                returnMap.put("resCode","1003");
                                returnMap.put("resMessage","格式校验失败，节目单的日期格式不正确，日期格式需要满足yyyy-MM-dd HH:mm的要求！");
                                BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                                returnMethodMap.put("Flag",MamConstants.METHOD_RETURN_STATUS_FAILTURE);
                                return returnMethodMap;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    returnMap.put("resCode","1001");
                    returnMap.put("resMessage","直播节目单XML文件解析错误！");
                    BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                    returnMethodMap.put("Flag",MamConstants.METHOD_RETURN_STATUS_FAILTURE);
                    return returnMethodMap;
                }
            }else{
                returnMap.put("resCode","2005");
                returnMap.put("resMessage","直播节目单文件不存在！");
                BatchContentNoCopyrightImportUtil.returnMethod(returnMap);
                returnMethodMap.put("Flag",MamConstants.METHOD_RETURN_STATUS_FAILTURE);
                return returnMethodMap;
            }
        }
        //return Boolean.TRUE;
        returnMethodMap.put("Flag",MamConstants.METHOD_RETURN_STATUS_SUCCESS);
        returnMethodMap.put("PlayBillVector",playBillVector);
        return returnMethodMap;
    }
    
    /**
     * 验证单张图片的长宽比例和最低长宽要求，只允许jpg后缀的图片通过
     * @param imgFullPath 图片的绝对路径路径
     * @return
     */
    public static boolean validateImageRatio(String imgFullPath){
        try {
            String imgName = new File(imgFullPath).getName();
            String imageSuffix = ImageUtils.getFileSuffix(imgName);
            if(!StringUtil.isNullStr(imageSuffix)){
                String imageType = ImageUtils.getImageType(imgFullPath);
                if(imageSuffix.equals(imageType)){
                    //只允许后缀为jpg格式的图片
                    int[] sizeInfo = ImageUtils.getSizeInfo(imgFullPath);
                    log.info("~~~~~~~  sizeInfo " + sizeInfo + ",sizeInfo[0]:" + sizeInfo[0] + ",sizeInfo[1]:" + sizeInfo[1]);
                    if(sizeInfo[1] == 0){
                        log.info("~~~~~~~  sizeInfo[1] == 0 ");
                        return Boolean.FALSE;
                    }
                    if(imgName.indexOf("_H32_sc") > -1) {
                        log.info("~~~~~~~ image type is _H32_sc ");
                        if(sizeInfo[0]/sizeInfo[1] != 3/2){
                            log.info("~~~~~~~  sizeInfo[0]/sizeInfo[1] != 3/2 ");
                            return Boolean.FALSE;
                        }
                        if(sizeInfo[0] < 600){
                            log.info("~~~~~~~  sizeInfo[0] < 600 ");
                            return Boolean.FALSE;
                        }
                    }
                    if(imgName.indexOf("_V34_sc") > -1) {
                        log.info("~~~~~~~ image type is _V34_sc ");
                        if(sizeInfo[0]/sizeInfo[1] != 3/4){
                            return Boolean.FALSE;
                        }
                        if(sizeInfo[0] < 450){
                            return Boolean.FALSE;
                        }
                    }
                    if(imgName.indexOf("_V23_sc") > -1) {
                        log.info("~~~~~~~ image type is _V23_sc ");
                        if(sizeInfo[0]/sizeInfo[1] != 2/3){
                            log.info("~~~~~~~  sizeInfo[0]/sizeInfo[1] != 2/3 ");
                            return Boolean.FALSE;
                        }
                        if(sizeInfo[0] < 400){
                            log.info("~~~~~~~  sizeInfo[0] < 400 ");
                            return Boolean.FALSE;
                        }
                    }
                    if(imgName.indexOf("_H53_sc") > -1) {
                        log.info("~~~~~~~ image type is _H53_sc ");
                        if(sizeInfo[0]/sizeInfo[1] != 5/3){
                            log.info("~~~~~~~ sizeInfo[0]/sizeInfo[1] != 5/3 ");
                            return Boolean.FALSE;
                        }
                        if(sizeInfo[0] < 650){
                            log.info("~~~~~~~ sizeInfo[0] < 650 ");
                            return Boolean.FALSE;
                        }
                    }
                    if(imgName.indexOf("_H43_sc") > -1) {
                        log.info("~~~~~~~ image type is _H43_sc ");
                        if(sizeInfo[0]/sizeInfo[1] != 4/3){
                            log.info("~~~~~~~ sizeInfo[0]/sizeInfo[1] != 4/3 ");
                            return Boolean.FALSE;
                        }
                        if(sizeInfo[0] < 600){
                            log.info("~~~~~~~ sizeInfo[0] < 600 ");
                            return Boolean.FALSE;
                        }
                    }
                    if(imgName.indexOf("_S11_sc") > -1) {
                        log.info("~~~~~~~ image type is _S11_sc ");
                        if(sizeInfo[0]/sizeInfo[1] != 1/1){
                            log.info("~~~~~~~ sizeInfo[0]/sizeInfo[1] != 1/1 ");
                            return Boolean.FALSE;
                        }
                        if(sizeInfo[0] < 400){
                            log.info("~~~~~~~ sizeInfo[0] < 400 ");
                            return Boolean.FALSE;
                        }
                    }
                    if(imgName.indexOf("_H169_sc") > -1) {
                        log.info("~~~~~~~ image type is _H169_sc ");
                        if(sizeInfo[0]/sizeInfo[1] != 16/9){
                            log.info("~~~~~~~ sizeInfo[0]/sizeInfo[1] != 16/9 ");
                            return Boolean.FALSE;
                        }
                        if(sizeInfo[0] < 640){
                            log.info("~~~~~~~ sizeInfo[0] < 640 ");
                            return Boolean.FALSE;
                        }
                    }
                    return Boolean.TRUE;
                }else{
                    return Boolean.FALSE;
                }
            }else{
                return Boolean.FALSE;
            }
        } catch (IOException e) {
            log.info("$$$$$$$$$$$$ validateImageRatio IOException!");
            e.printStackTrace();
            return Boolean.FALSE;
        } catch (Exception e) {
            log.info("$$$$$$$$$$$$ validateImageRatio Exception!");
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }
}
