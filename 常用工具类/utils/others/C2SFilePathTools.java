package com.wondertek.mam.util.others;

import com.wondertek.mam.commons.MamConstants;
import com.wondertek.mam.util.ST;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class C2SFilePathTools {
    protected final Log log = LogFactory.getLog(this.getClass());

    //人物文件临时目录
    private String a_starFileTemp = MamConstants.REAL_PATH + File.separator + "temp" + File.separator + MamConstants.DIR_STAR_IMG;
    private String r_starFileTemp = File.separator + "temp" + File.separator + MamConstants.DIR_STAR_IMG;
    //人物图片上传临时路径
    private String a_starUploadTemp = MamConstants.REAL_PATH + File.separator + "temp" + File.separator + MamConstants.DIR_STAR_IMG + File.separator + "imgs";
    private String r_starUploadTemp = File.separator + "temp" + File.separator + MamConstants.DIR_STAR_IMG + File.separator + "imgs";
    //人物图片剪切临时路径
    private String a_starStore = MamConstants.REAL_PATH + File.separator + "temp" + File.separator + MamConstants.DIR_STAR_IMG + File.separator + "img" + File.separator + "cut";
    private String r_starStore = File.separator + "temp" + File.separator + MamConstants.DIR_STAR_IMG + File.separator + "img" + File.separator + "cut";

    //人物图片 保存路径
    private String a_starImageStore = MamConstants.STORE_PATH + File.separator + MamConstants.DIR_STAR_IMG + File.separator + "images";
    private String r_starImageStore = File.separator + MamConstants.STORE + File.separator + MamConstants.DIR_STAR_IMG + File.separator + "images";

    //人物发布预览xml（txt）临时路径
    private String a_starPublishTemp = MamConstants.REAL_PATH + File.separator + "temp" + File.separator + MamConstants.DIR_STAR_IMG + File.separator + "publish";
    private String r_starPublishTemp = File.separator + "temp" + File.separator + MamConstants.DIR_STAR_IMG + File.separator + "publish";

    //人物发布 xml保存路径
    private String a_starInfoPublish = MamConstants.DEPOSITORY_PATH + File.separator + MamConstants.DIR_STAR_IMG + File.separator + "info" ; //+ "publish" + File.separator + "star";
    private String r_starInfoPublish = File.separator + MamConstants.DEPOSITORY + File.separator +MamConstants.DIR_STAR_IMG + File.separator + "info" ;//+ "publish" + File.separator + "star";

    //人物发布 image保存路径
    private String a_starImgPublish = MamConstants.DEPOSITORY_PATH + File.separator + MamConstants.DIR_STAR_IMG + File.separator + "images" ; //+ File.separator + "star";
    private String r_starImgPublish = File.separator + MamConstants.DEPOSITORY + File.separator +MamConstants.DIR_STAR_IMG + File.separator + "images" ;//+ File.separator + "star";

    //媒资批量导入 数据文件上传临时目录/temp/import/asset/  /*暂未使用*/
    private String a_assetImportTemp = MamConstants.REAL_PATH + File.separator + "temp" + File.separator + "import" + File.separator + "asset";
    private String r_assetImportTemp = File.separator + "temp" + File.separator + "import" + File.separator + "asset" ;

    //奖项图片剪切临时路径
    private String a_awardsUploadTemp = MamConstants.TMP_PATH+File.separator+MamConstants.DIR_AWARD+File.separator+MamConstants.DIR_AWARD_IMG;
    private String r_awardsUploadTemp = File.separator + MamConstants.TMP+File.separator+MamConstants.DIR_AWARD+File.separator+MamConstants.DIR_AWARD_IMG;
    //奖项图片保存路径
    private String a_awardsStore = MamConstants.TMP_PATH+File.separator+MamConstants.DIR_AWARD+File.separator+MamConstants.DIR_AWARD_IMG+File.separator+"cut";
    private String r_awardsStore = File.separator + MamConstants.TMP + File.separator + MamConstants.DIR_AWARD + File.separator + MamConstants.DIR_AWARD_IMG + File.separator + "cut";

    public Map<String, String> getPaths(String pathName) {
        if (ST.isNull(pathName)) {
            return null;
        }
        Map<String, String> res = new HashMap<String, String>();
        Class<?> cls = this.getClass();
        try {
            Field[] allFields = cls.getDeclaredFields();
            for (Field f : allFields) {
                if (("a_" + pathName).equalsIgnoreCase(f.getName())) {
                    Method aMethod = cls.getDeclaredMethod("getA_" + pathName);
                    String aPath = (String) aMethod.invoke(this);
                    res.put("a", aPath);
                }
                if (("r_" + pathName).equalsIgnoreCase(f.getName())) {
                    Method rMethod = cls.getDeclaredMethod("getR_" + pathName);
                    String rPath = (String) rMethod.invoke(this);
                    res.put("r", rPath);
                }
            }
        } catch (NoSuchMethodException e) {
            this.log.error(e);
        } catch (InvocationTargetException e) {
            this.log.error(e);
        } catch (IllegalAccessException e) {
            this.log.error(e);
        }
        return res;
    }

    /**
     * setter/getter
     */
    public String getA_starUploadTemp() {
        return a_starUploadTemp;
    }

    public void setA_starUploadTemp(String a_starUploadTemp) {
        this.a_starUploadTemp = a_starUploadTemp;
    }

    public String getR_starUploadTemp() {
        return r_starUploadTemp;
    }

    public void setR_starUploadTemp(String r_starUploadTemp) {
        this.r_starUploadTemp = r_starUploadTemp;
    }

    public String getA_starStore() {
        return a_starStore;
    }

    public void setA_starStore(String a_starStore) {
        this.a_starStore = a_starStore;
    }

    public String getR_starStore() {
        return r_starStore;
    }

    public void setR_starStore(String r_starStore) {
        this.r_starStore = r_starStore;
    }

    public String getA_awardsUploadTemp() {
        return a_awardsUploadTemp;
    }

    public void setA_awardsUploadTemp(String a_awardsUploadTemp) {
        this.a_awardsUploadTemp = a_awardsUploadTemp;
    }

    public String getR_awardsUploadTemp() {
        return r_awardsUploadTemp;
    }

    public void setR_awardsUploadTemp(String r_awardsUploadTemp) {
        this.r_awardsUploadTemp = r_awardsUploadTemp;
    }

    public String getA_awardsStore() {
        return a_awardsStore;
    }

    public void setA_awardsStore(String a_awardsStore) {
        this.a_awardsStore = a_awardsStore;
    }

    public String getR_awardsStore() {
        return r_awardsStore;
    }

    public void setR_awardsStore(String r_awardsStore) {
        this.r_awardsStore = r_awardsStore;
    }

    public String getA_starPublishTemp() {
        return a_starPublishTemp;
    }

    public void setA_starPublishTemp(String a_starPublishTemp) {
        this.a_starPublishTemp = a_starPublishTemp;
    }

    public String getR_starPublishTemp() {
        return r_starPublishTemp;
    }

    public void setR_starPublishTemp(String r_starPublishTemp) {
        this.r_starPublishTemp = r_starPublishTemp;
    }

    public String getA_starInfoPublish() {
        return a_starInfoPublish;
    }

    public void setA_starInfoPublish(String a_starInfoPublish) {
        this.a_starInfoPublish = a_starInfoPublish;
    }

    public String getR_starInfoPublish() {
        return r_starInfoPublish;
    }

    public void setR_starInfoPublish(String r_starInfoPublish) {
        this.r_starInfoPublish = r_starInfoPublish;
    }

    public String getA_starImgPublish() {
        return a_starImgPublish;
    }

    public void setA_starImgPublish(String a_starImgPublish) {
        this.a_starImgPublish = a_starImgPublish;
    }

    public String getR_starImgPublish() {
        return r_starImgPublish;
    }

    public void setR_starImgPublish(String r_starImgPublish) {
        this.r_starImgPublish = r_starImgPublish;
    }

    public String getA_starImageStore() {
        return a_starImageStore;
    }

    public void setA_starImageStore(String a_starImageStore) {
        this.a_starImageStore = a_starImageStore;
    }

    public String getR_starImageStore() {
        return r_starImageStore;
    }

    public void setR_starImageStore(String r_starImageStore) {
        this.r_starImageStore = r_starImageStore;
    }

    public String getA_starFileTemp() {
        return a_starFileTemp;
    }

    public void setA_starFileTemp(String a_starFileTemp) {
        this.a_starFileTemp = a_starFileTemp;
    }

    public String getR_starFileTemp() {
        return r_starFileTemp;
    }

    public void setR_starFileTemp(String r_starFileTemp) {
        this.r_starFileTemp = r_starFileTemp;
    }

    public String getA_assetImportTemp() {
        return a_assetImportTemp;
    }

    public void setA_assetImportTemp(String a_assetImportTemp) {
        this.a_assetImportTemp = a_assetImportTemp;
    }

    public String getR_assetImportTemp() {
        return r_assetImportTemp;
    }

    public void setR_assetImportTemp(String r_assetImportTemp) {
        this.r_assetImportTemp = r_assetImportTemp;
    }
}
