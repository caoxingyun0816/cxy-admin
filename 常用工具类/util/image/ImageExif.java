package com.wondertek.mam.util.image;

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


/**
 * JPG的EXIT信息 ,相关规范参考： 1.* EXIF.org http://www.exif.org/ 2.* Opanda
 * http://www.opanda.com/cn/iexif/exif.htm 3.* EXIF 2.1
 * 官方标准（PDF文档）http://www.exif.org/Exif2-1.PDF 4.* EXIF 2.2
 * 官方标准（PDF文档）http://www.exif.org/Exif2-2.PDF 5.* EXIF 文件格式说明
 * http://park2.wakwak.com/~tsuruzoh/Computer/Digicams/exif-e.html
 *
 * @author
 * @since 2010-5-19
 */
public class ImageExif implements Serializable {
    private static final long serialVersionUID = 4713490466591635082L;

    private String ImageDescription;// 图像描述、来源. 指生成图像的工具 　　
    private String Artist;// 作者 有些相机可以输入使用者的名字 　　
    private String Make;// 生产者 指产品生产厂家 　　
    private String Model;// 型号 指设备型号 　　
    private String Orientation;// 方向 有的相机支持，有的不支持 　　
    private String XResolution; // X方向分辨率
    private String YResolution;// Y方向分辨率
    private String ResolutionUnit;// 分辨率单位 一般为PPI
    private String Software;// 软件 显示固件
    private String Firmware;// 版本
    private String DateTime;// 日期和时间
    private String YCbCrPositioning;// 色相定位 　　
    private String ExifOffsetExif;// 信息位置，定义Exif在信息在文件中的写入，有些软件不显示。
    private String ExposureTime;// 曝光时间 即快门速度 　
    private String FNumber; // 光圈系数
    private String ExposureProgram;// 曝光程序 指程序式自动曝光的设置，各相机不同,可能是Sutter
    // Priority（快门优先）、Aperture Priority（快门优先）等等。
    private String IsoSpeedRatings;// 感光度
    private String ExifVersion;// Exif版本
    private String DateTimeOriginal;// 创建时间
    private String DateTimeDigitized;// 数字化时间
    private String ComponentsConfiguration;// 图像构造（多指色彩组合方案）
    private String CompressedBitsPerPixel;// (BPP)压缩时每像素色彩位 指压缩程度
    private String ExposureBiasValue;// 曝光补偿。
    private String MaxApertureValue;// 最大光圈
    private String MeteringMode;// 测光方式，平均式测光、中央重点测光、点测光等。
    private String Lightsource;// 光源 指白平衡设置
    private String Flash;// 是否使用闪光灯。
    private String FocalLength;// 焦距，一般显示镜头物理焦距，有些软件可以定义一个系数，从而显示相当于35mm相机的焦距
    private String MakerNote;// (User Comment)作者标记、说明、记录
    private String FlashPixVersionFlashPix;// 版本 （个别机型支持）
    private String ColorSpace;// 色域、色彩空间
    private String ExifImageWidth;// (Pixel X Dimension)图像宽度 指横向像素数
    private String ExifImageLength;// (Pixel Y Dimension)图像高度 指纵向像素数
    private String Interoperability;// IFD通用性扩展项定义指针 和TIFF文件相关，具体含义不详
    private String FileSource;// 源文件 Compression压缩比

    public String getImageDescription() {
        return ImageDescription;
    }

    public void setImageDescription(String imageDescription) {
        ImageDescription = imageDescription;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public String getMake() {
        return Make;
    }

    public void setMake(String make) {
        Make = make;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getOrientation() {
        return Orientation;
    }

    public void setOrientation(String orientation) {
        Orientation = orientation;
    }

    public String getXResolution() {
        return XResolution;
    }

    public void setXResolution(String xResolution) {
        XResolution = xResolution;
    }

    public String getYResolution() {
        return YResolution;
    }

    public void setYResolution(String yResolution) {
        YResolution = yResolution;
    }

    public String getResolutionUnit() {
        return ResolutionUnit;
    }

    public void setResolutionUnit(String resolutionUnit) {
        ResolutionUnit = resolutionUnit;
    }

    public String getSoftware() {
        return Software;
    }

    public void setSoftware(String software) {
        Software = software;
    }

    public String getFirmware() {
        return Firmware;
    }

    public void setFirmware(String firmware) {
        Firmware = firmware;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getYCbCrPositioning() {
        return YCbCrPositioning;
    }

    public void setYCbCrPositioning(String yCbCrPositioning) {
        YCbCrPositioning = yCbCrPositioning;
    }

    public String getExifOffsetExif() {
        return ExifOffsetExif;
    }

    public void setExifOffsetExif(String exifOffsetExif) {
        ExifOffsetExif = exifOffsetExif;
    }

    public String getExposureTime() {
        return ExposureTime;
    }

    public void setExposureTime(String exposureTime) {
        ExposureTime = exposureTime;
    }

    public String getFNumber() {
        return FNumber;
    }

    public void setFNumber(String fNumber) {
        FNumber = fNumber;
    }

    public String getExposureProgram() {
        return ExposureProgram;
    }

    public void setExposureProgram(String exposureProgram) {
        ExposureProgram = exposureProgram;
    }

    public String getIsoSpeedRatings() {
        return IsoSpeedRatings;
    }

    public void setIsoSpeedRatings(String isoSpeedRatings) {
        IsoSpeedRatings = isoSpeedRatings;
    }

    public String getExifVersion() {
        return ExifVersion;
    }

    public void setExifVersion(String exifVersion) {
        ExifVersion = exifVersion;
    }

    public String getDateTimeOriginal() {
        return DateTimeOriginal;
    }

    public void setDateTimeOriginal(String dateTimeOriginal) {
        DateTimeOriginal = dateTimeOriginal;
    }

    public String getDateTimeDigitized() {
        return DateTimeDigitized;
    }

    public void setDateTimeDigitized(String dateTimeDigitized) {
        DateTimeDigitized = dateTimeDigitized;
    }

    public String getComponentsConfiguration() {
        return ComponentsConfiguration;
    }

    public void setComponentsConfiguration(String componentsConfiguration) {
        ComponentsConfiguration = componentsConfiguration;
    }

    public String getCompressedBitsPerPixel() {
        return CompressedBitsPerPixel;
    }

    public void setCompressedBitsPerPixel(String compressedBitsPerPixel) {
        CompressedBitsPerPixel = compressedBitsPerPixel;
    }

    public String getExposureBiasValue() {
        return ExposureBiasValue;
    }

    public void setExposureBiasValue(String exposureBiasValue) {
        ExposureBiasValue = exposureBiasValue;
    }

    public String getMaxApertureValue() {
        return MaxApertureValue;
    }

    public void setMaxApertureValue(String maxApertureValue) {
        MaxApertureValue = maxApertureValue;
    }

    public String getMeteringMode() {
        return MeteringMode;
    }

    public void setMeteringMode(String meteringMode) {
        MeteringMode = meteringMode;
    }

    public String getLightsource() {
        return Lightsource;
    }

    public void setLightsource(String lightsource) {
        Lightsource = lightsource;
    }

    public String getFlash() {
        return Flash;
    }

    public void setFlash(String flash) {
        Flash = flash;
    }

    public String getFocalLength() {
        return FocalLength;
    }

    public void setFocalLength(String focalLength) {
        FocalLength = focalLength;
    }

    public String getMakerNote() {
        return MakerNote;
    }

    public void setMakerNote(String makerNote) {
        MakerNote = makerNote;
    }

    public String getFlashPixVersionFlashPix() {
        return FlashPixVersionFlashPix;
    }

    public void setFlashPixVersionFlashPix(String flashPixVersionFlashPix) {
        FlashPixVersionFlashPix = flashPixVersionFlashPix;
    }

    public String getColorSpace() {
        return ColorSpace;
    }

    public void setColorSpace(String colorSpace) {
        ColorSpace = colorSpace;
    }

    public String getExifImageWidth() {
        return ExifImageWidth;
    }

    public void setExifImageWidth(String exifImageWidth) {
        ExifImageWidth = exifImageWidth;
    }

    public String getExifImageLength() {
        return ExifImageLength;
    }

    public void setExifImageLength(String exifImageLength) {
        ExifImageLength = exifImageLength;
    }

    public String getInteroperability() {
        return Interoperability;
    }

    public void setInteroperability(String interoperability) {
        Interoperability = interoperability;
    }

    public String getFileSource() {
        return FileSource;
    }

    public void setFileSource(String fileSource) {
        FileSource = fileSource;
    }


}
