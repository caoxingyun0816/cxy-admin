package com.wondertek.mam.util.image;

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
public enum ImageType {
    unknown(0, "unknown"),
    jpg(1, "jpg"),
    gif(2, "gif"),
    png(3, "png"),
    bmp(4, "bmp");
    private int code;
    private String name;
    ImageType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 将后缀转换成图片类型, JPEG将转成jpg
     *
     * @param suffix
     * @return
     */
    public static ImageType toImageType(String suffix) {
        if (suffix == null || "".equals(suffix)) {
            return unknown;
        }
        suffix = suffix.toLowerCase();
        if ("jpeg".equals(suffix)) {
            suffix = "jpg";
        }
        try {
            return valueOf(suffix);
        } catch (Exception e) {
            return unknown;
        }
    }

    /**
     * 判断图片类型
     *
     * @param suffix
     * @return
     */
    public static boolean isAcceptType(String suffix) {
        if (suffix == null || "".equals(suffix)) {
            return false;
        }
        if ("jpeg".equalsIgnoreCase(suffix)) {
            return true;
        }
        ImageType type = ImageType.valueOf(suffix.toLowerCase());
        if (type != null && type.getCode() > 0 && type.getCode() < 5) {
            return true;
        }
        return false;
    }

    public static boolean isAcceptType(ImageType type) {
        if (type == null) {
            return false;
        }
        return isAcceptType(type.getName());
    }

}
