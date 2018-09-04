package com.imooc.cache;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caoxingyun on 2018/9/3.
 */
public class SystemConfigCache {
    private static Logger log = Logger.getLogger(SystemConfigCache.class);

    /***
     * cache map
     */
    private static Map<String, String> configs = new HashMap<String, String>();

    public static String getValue(String key)
    {
        return configs.get(key);
    }

    public static String getValueDefault(String key, String defValue)
    {
        return configs.get(key) == null ? defValue : configs.get(key);
    }

    public static int getIntValueDefault(String key, int defValue)
    {
        try
        {
            return Integer.parseInt(configs.get(key));
        } catch (Exception e)
        {
            return defValue;
        }
    }

    public static int getIntValue(String key)
    {
        return getIntValueDefault(key, 0);
    }

    public static boolean containsConfig(String key)
    {
        return configs.containsKey(key);
    }

    public static void init(SystemConfigDao dao)
    {
        log.info("begin init System Config......");
        Map<String, String> temp = new HashMap<String, String>();
        List<SystemConfig> list = dao.getAll();
        for (SystemConfig config : list)
        {
            temp.put(config.getConfigKey(), config.getConfigValue());
        }
        configs = temp;
        log.info("end init System Config , totals : " + configs.size());
        //缓存都放在configs
        //在其他地方获取只需要用 SystemConfigCache.getKey("key");即可
    }

    public static Map<String, String> getAllConfigs()
    {
        return configs ;
    }

    public static void initMamConstants() {

    }
}
