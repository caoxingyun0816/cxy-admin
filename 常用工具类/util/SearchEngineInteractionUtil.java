package com.wondertek.mam.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SearchEngineInteractionUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchEngineInteractionUtil.class);
	private SearchEngineInteractionUtil() {}
	private static boolean timing = true;
	private static final String CANNOT_GENERATE_AN_INDEXING_MESSAGE_WITHOUT_A_VALID_COLLECTION_NAME = "cannot generate an indexing message without a valid collection name";
	
//	public void sendRequest(String solrStr) {
//		Map<String, String> uriVariables = new HashMap<String, String>();
//        uriVariables.put("kafkaMsg", solrStr);
//        String param = "?kafkaMsg={kafkaMsg}";
//        String solrResponse = this.restTemplate.postForEntity(solrUrl + param, null, String.class, uriVariables).getBody();
//	}
	
	// 新增/更新索引的消息
	public static String generateAddOrUpdateMsg4Single(Object obj, String collection) throws IndexingException {
		if (obj == null) {
			throw new IndexingException("cannot generate an indexing message for object of null");
		}
		if (collection == null || collection.trim().length() == 0) {
			throw new IndexingException(CANNOT_GENERATE_AN_INDEXING_MESSAGE_WITHOUT_A_VALID_COLLECTION_NAME);
		}
		List<Object> list = new ArrayList<Object>();
		list.add(obj);
		return generateAddOrUpdateMsg4Batch(list, collection);
	}
	
	public static String generateAddOrUpdateMsg4Batch(List<?> objectList, String collection) throws IndexingException {
		long msgTimeStart = System.currentTimeMillis();
		if (objectList == null || objectList.isEmpty()) {
			throw new IndexingException("cannot generate an indexing message for list of null or an empty list");
		}
		if (collection == null || collection.trim().length() == 0) {
			throw new IndexingException(CANNOT_GENERATE_AN_INDEXING_MESSAGE_WITHOUT_A_VALID_COLLECTION_NAME);
		}
		List<Map<String, Object>> docDatas;
		try {
			docDatas = convertList2DocMapList(objectList, collection);
		} catch (Exception e) {
			throw new IndexingException(e);
		}
		if (docDatas.isEmpty()) {
			throw new IndexingException("not valid information to create or update index");
		}
		String msg;
		try {
			msg = generateAddOrUpdateMsg(collection, docDatas);
		} catch (JsonProcessingException e) {
			throw new IndexingException(e);
		}
		long msgTimeEnd = System.currentTimeMillis();
		if (timing) {
			LOGGER.info("indexMsg generated consuming {} ms for {} object(s)", msgTimeEnd - msgTimeStart, docDatas.size());
		}
		return msg;
	}
	private static String generateAddOrUpdateMsg(String collection, List<Map<String, Object>> docDatas) throws JsonProcessingException {
		Map<String, Object> importMap = new HashMap<String, Object>();
		importMap.put("collection", collection);
		importMap.put("optType", SearchEngineOperationEnum.IMPORT.getCode());
		importMap.put("docDatas", docDatas);
		importMap.put("msgTime", new SimpleDateFormat(SearchEngineConstants.TIMESTAMP_STRING_FORMAT_ALLNUM).format(new Date()));
		String indexMsg = value2String(importMap);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("this message can be sent to Kafka: {}", indexMsg);
		}
		return indexMsg;
	}
	
	// 根据搜索条件删除索引的消息
	public static String generateDeleteMsg4DeleteByQuery(String deleteQuery, String collection) throws IndexingException {
		long msgTimeStart = System.currentTimeMillis();
		if (collection == null || collection.trim().length() == 0) {
			throw new IndexingException(CANNOT_GENERATE_AN_INDEXING_MESSAGE_WITHOUT_A_VALID_COLLECTION_NAME);
		}
		Map<String, Object> importMap = new HashMap<String, Object>();
		importMap.put("collection", collection);
		importMap.put("optType", SearchEngineOperationEnum.DELETE_QUERY.getCode());
		importMap.put("deleteQuery", deleteQuery);
		importMap.put("msgTime", new SimpleDateFormat(SearchEngineConstants.TIMESTAMP_STRING_FORMAT_ALLNUM).format(new Date()));
		String indexMsg;
		try {
			indexMsg = value2String(importMap);
		} catch (JsonProcessingException e) {
			throw new IndexingException(e);
		}
		long msgTimeEnd = System.currentTimeMillis();
		if (timing) {
			LOGGER.info("indexMsg generated consuming {} ms", msgTimeEnd - msgTimeStart);
		}
		return indexMsg;
	}
	
	// 替换（先删除后创建索引）索引的消息
	public static String generateReplaceMsg(String deleteQuery, List<?> addList, String collection) throws IndexingException {
		long msgTimeStart = System.currentTimeMillis();
		if (collection == null || collection.trim().length() == 0) {
			throw new IndexingException(CANNOT_GENERATE_AN_INDEXING_MESSAGE_WITHOUT_A_VALID_COLLECTION_NAME);
		}
		List<Map<String, Object>> docDatas;
		try {
			docDatas = convertList2DocMapList(addList, collection);
		} catch (Exception e) {
			throw new IndexingException(e);
		}

		Map<String, Object> importMap = new HashMap<String, Object>();
		importMap.put("collection", collection);
		importMap.put("optType", SearchEngineOperationEnum.REPLACE.getCode());
		importMap.put("deleteQuery", deleteQuery);
		importMap.put("docDatas", docDatas);
		importMap.put("msgTime", new SimpleDateFormat(SearchEngineConstants.TIMESTAMP_STRING_FORMAT_ALLNUM).format(new Date()));
		String indexMsg;
		try {
			indexMsg = value2String(importMap);
		} catch (JsonProcessingException e) {
			throw new IndexingException(e);
		}
		long msgTimeEnd = System.currentTimeMillis();
		if (timing) {
			LOGGER.info("indexMsg generated consuming {} ms for {} object(s)", msgTimeEnd - msgTimeStart, docDatas.size());
		}
		return indexMsg;
	}	
	
	// 以下为对象转换工具方法
	private static List<Map<String, Object>> convertList2DocMapList(List<?> objectList, String collection) throws IOException {
		List<Map<String, Object>> docDatas = new ArrayList<Map<String, Object>>();
		if (objectList != null && !objectList.isEmpty()) {
			for (Object obj : objectList) {
				if (obj == null) {
					//跳过，不报异常
					continue;
				}
				docDatas.add(generateDocData(obj, null, collection));
			}
		}
		return docDatas;
	}
	private static Map<String, Object> generateDocData(Object parent, List<?> childList, String collection) throws IOException {
		String searchType = parent.getClass().getSimpleName();
		searchType = processAfterCheckMG(collection, searchType);
		Map<String, Object> parentDocumentMap = generateSingleDocument(objectConvert2Map(parent), searchType);
		Map<String, Object> docData = new HashMap<String, Object>();
		docData.put("parentDocument", parentDocumentMap);

		List<Map<String, Object>> childDocumentList = new ArrayList<Map<String, Object>>();
		if (childList != null && !childList.isEmpty()) {
			for (Object child : childList) {
				if (child == null) {
					continue;
				}
				String childSearchType = child.getClass().getSimpleName();
				childSearchType = processAfterCheckMG(collection, childSearchType);
				childDocumentList.add(generateSingleDocument(objectConvert2Map(child), childSearchType));
			}
		}
		docData.put("childDocumentList", childDocumentList);
		return docData;
	}
	private static Map<String, Object> objectConvert2Map(Object object) throws IOException {
		long timeStart = System.currentTimeMillis();
		String str = value2String(object);
		long timeMid = System.currentTimeMillis();
		Map<String, Object> map = mapper.readValue(str, Map.class);
		long timeEnd = System.currentTimeMillis();
		if (timing && ((timeMid - timeStart) >= 4 || (timeEnd - timeMid) >= 3)) {
			LOGGER.info("mapper write operation consuming " + (timeMid - timeStart) + " ms, read operation consuming " + (timeEnd - timeMid) + " ms, for length of " + str.length() + ": " + str);
		}
		return map;
	}
	private static Map<String, Object> generateSingleDocument(Map<String, Object> map, String searchType) {
		Map<String, Object> singleDocument = new HashMap<String, Object>();
		if (map != null && !map.isEmpty()) {
			singleDocument.put("searchType", searchType);
			singleDocument.put("searchId", getPrimaryKey(map, searchType));
			singleDocument.put("data", map);
		}
		return singleDocument;
	}
	private static String getPrimaryKey(Map<String, Object> map, String searchType) {
		String[] idMarks = new String[]{"id", "Id", "ID", "iD"};
		String id = null;
		for (String real_id : idMarks) {
			if (map.get(real_id) != null && String.valueOf(map.get(real_id)).trim().length() != 0) {
				id = String.valueOf(map.get(real_id));
				break;
			}
		}
		if (id == null || id.trim().length() == 0) {
			LOGGER.error("map's id is null: {}", map);
		}
		return definePrimaryKey(searchType, id);
	}
	
	// 以下为 索引设计 相关方法
	private static String getPlatformTag() {
		return null;
	}
	private static String definePrimaryKey(String searchType, String id) {
		String platformTag = getPlatformTag();
		return searchType + "_" + (platformTag == null ? "" : (platformTag + "_")) + id;
	}
	private static String processAfterCheckMG(String collection, String searchType) {
		boolean isMG = checkMG(collection);
		if (!isMG && searchType.endsWith("MG")) {
			searchType = searchType.substring(0, searchType.length() - 2);
		}
		searchType = checkSearchType(collection, searchType);
		return searchType;
	}
	private static boolean checkMG(String collection) {
		if ("cmam_video".equals(collection) || "cmam_runners".equals(collection) || "cmam_digital_media".equals(collection)) {
			return true;
		}
		return false;
	}
	private static String checkSearchType(String collection, String searchType) {
		if ("cmam_music".equals(collection) || "cmam_music_new".equals(collection)) {
			if ("Crbt".equals(searchType)) {
				searchType = "CrbtContent";
			}
		}
		return searchType;
	}
	
	private static String value2String(Object obj) throws JsonProcessingException {
		return mapper.writeValueAsString(obj);
	}
	private static ObjectMapper mapper = new ObjectMapper()
			// 在序列化时忽略值为 null 的属性
			.setSerializationInclusion(Include.NON_NULL)
			// 在序列化时日期格式默认为 yyyy-MM-dd'T'HH:mm:ss.SSSZ
			// .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
			// 在反序列化时忽略在 JSON 中存在但 Java 对象不存在的属性
			// .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			// Date 默认采用 yyyy-MM-dd HH:mm:ss 格式
			.setDateFormat(new SimpleDateFormat(SearchEngineConstants.DATETIME_STRING_FORMAT_NORMAL))
			;
}

/**
 * 索引操作枚举值
 */
enum SearchEngineOperationEnum {

	DELETE_QUERY("0", "根据搜索条件删除"),
	IMPORT("1", "创建索引"),
	DELETE_BY_ID("2", "根据索引ID删除"),
	REPLACE("3", "替换（先删除后创建索引）");

    private String code;
    private String msg;
    public String getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
    
    SearchEngineOperationEnum(final String code, final String msg) {
        this.code = code;
        this.msg = msg;
    }
}

class SearchEngineConstants {
	private SearchEngineConstants() {}
	public static final String DATETIME_STRING_FORMAT_ALLNUM = "yyyyMMddHHmmss";
	public static final String DATETIME_STRING_FORMAT_NORMAL = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_STRING_FORMAT_NORMAL = "yyyy-MM-dd";
	public static final String TIME_STRING_FORMAT_NORMAL = "HH:mm:ss";
	public static final String TIMESTAMP_STRING_FORMAT_ALLNUM = "yyyyMMddHHmmssSSS";
}
