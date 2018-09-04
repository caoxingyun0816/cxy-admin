package com.wondertek.mam.util.image;

import java.io.File;
import java.io.Serializable;

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
public class ImageFile implements Serializable {

    private static final long serialVersionUID = -337911125594555523L;

    /**
     * 图片宽，单位:px
     */
    private double width;

    /**
     * 图片高，单位:px
     */
    private double height;

    /**
     * 图片大小，单位:byte
     */
    private double size;

    /**
     * 图片类型
     */
    private ImageType type;

    /**
     * 图片文件
     */
    private File file;

    /**
     * 图片EXIF信息
     */
    private ImageExif exif;

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public ImageType getType() {
        return type;
    }

    public void setType(ImageType type) {
        this.type = type;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ImageExif getExif() {
        return exif;
    }

    public void setExif(ImageExif exif) {
        this.exif = exif;
    }


}
