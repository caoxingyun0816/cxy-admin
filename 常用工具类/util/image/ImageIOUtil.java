/*
package com.wondertek.mam.util.image;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.imageio.plugins.bmp.BMPImageReader;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.jpeg.JPEGImageReader;
import com.sun.imageio.plugins.png.PNGImageReader;
import com.sun.imageio.plugins.wbmp.WBMPImageReader;
//import magick.MagickImage;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.Method;
import java.util.Iterator;

*/
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
 *//*

public class ImageIOUtil {

    */
/**
     * 是否是合法图片
     *
     * @param imageContent
     *            图片内容
     * @return
     *//*

    public static boolean isImage(byte[] imageContent) {
        return isImage(null, imageContent);
    }

    */
/**
     * 是否是合法图片
     *
     * @param imageContent
     *            图片内容
     * @return
     *//*

    public static boolean isImage(String suffix, byte[] imageContent) {
        if (imageContent == null || imageContent.length == 0) {
            return false;
        }
        Image img = null;
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(imageContent);
            img = ImageIO.read(is);
            if (img == null || img.getWidth(null) <= 0
                    || img.getHeight(null) <= 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    */
/**
     * 是否是合法图片
     *
     * @param localImagePath
     *            图片本地绝对路径
     * @return
     *//*

    public static boolean isImage(String localImagePath) {
        File imgFile = new File(localImagePath);
        if (!imgFile.isFile()) {
            return false;
        }
        Image img;
        try {
            img = ImageIO.read(imgFile);
            if (imgFile.length() == 0 || img == null || img.getWidth(null) <= 0
                    || img.getHeight(null) <= 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    */
/**
     * 根据要求的坐标截取图片
     *
     * @param imageFullPath
     * @param x
     * @param y
     * @param width
     * @param height
     *//*

    public static void cropPart(String imageFullPath, int x, int y, int width,
                                int height) throws RuntimeException {
        Image img = null;
        ImageFilter cropFilter = null;
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(new File(imageFullPath));
            if (bi == null) {
                throw new RuntimeException("ImageIO.read return null");
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("read image fail, src=",
                    imageFullPath));
        }
        int srcW = bi.getWidth();
        int srcH = bi.getHeight();
        if (srcW <= 0 || srcH <= 0) {
            throw new RuntimeException(String.format("invalid image, src=",
                    imageFullPath));
        }
        // 异常的图片参数
        if (x >= srcW || y >= srcH) {
            throw new RuntimeException(
                    String
                            .format(
                                    "cropPart fail, point (x=%s,y=%s) not in the image(width=%s,height=%s)",
                                    x, y, srcW, srcH));
        }
        width = Math.min(width, srcW - x);
        height = Math.min(height, srcH - y);
        try {
            Image image = bi.getScaledInstance(srcW, srcH, Image.SCALE_DEFAULT);
            cropFilter = new CropImageFilter(x, y, width, height);
            img = Toolkit.getDefaultToolkit().createImage(
                    new FilteredImageSource(image.getSource(), cropFilter));
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = (Graphics2D) tag.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(img, 0, 0, null);
            g.dispose();
            ImageIO.write(tag, "jpg", new File(imageFullPath));
        } catch (Exception e) {
            throw new RuntimeException("process image error, src="
                    + imageFullPath, e);
        }
    }

    */
/**
     * 将imageFullPath指定的图片进行等比缩放，最长的边为<t>maxEdgeLength</t>
     *
     * @param imageFullPath
     *            ：需要裁剪的图片绝对路径
     * @param maxEdgeLength
     *            : 边长
     * @return
     *//*

    public static boolean resizeImage(String imageFullPath, int maxEdgeLength)
            throws Exception {
        File file = new File(imageFullPath);
        if (!file.exists()) {
            return false;
        }
        Image img = ImageIO.read(file);
        // 判断图片格式是否正确
        if (img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0) {
            return false;
        }
        int width = img.getWidth(null);
        int height = img.getHeight(null);

        boolean isWidthLonger = width > height ? true : false;

        // 得到调整后的尺寸及缩小的比例,如果{width,height}都小于等于maxEdgeLength，直接返回
        if (width > maxEdgeLength || height > maxEdgeLength) {
            double ratio;

            if (isWidthLonger) {
                ratio = ((double) maxEdgeLength) / width;
                width = maxEdgeLength;
                height = ((Double) Math.floor(ratio * height)).intValue();
            } else {
                ratio = ((double) maxEdgeLength) / height;
                width = ((Double) Math.floor(ratio * width)).intValue();
                height = maxEdgeLength;
            }
        } else {
            return true;
        }
        FileOutputStream out = null;
        try {
            BufferedImage tag = new BufferedImage((int) width, (int) height,
                    BufferedImage.TYPE_INT_RGB);
            tag.getGraphics().drawImage(
                    img.getScaledInstance(width, height, Image.SCALE_SMOOTH),
                    0, 0, null);
            out = new FileOutputStream(imageFullPath);
            ImageIO.write(tag, "jpg", out);
        } catch (Exception e) {
            throw new RuntimeException("resize image error, img="
                    + imageFullPath, e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return true;
    }

    */
/**
     * 对imageFullPath 指定的文件按要求的质量quality进行压缩(gif将不会进行压缩)。quality的范围是(0-100)
     *
     * @param imageFullPath
     *            文件的绝对路径
     * @param quality
     *            压缩的质量,范围是(0-100)
     * @param maxFileSize
     *            文件超过该大小才进行质量有损压缩,单位:byte
     * @return 文件大小,单位:byte
     *//*

    public static int compressImage(String imageFullPath, int quality,
                                    long maxFileSize) {
        // 1. entry validation
        if (StringUtils.isEmpty(imageFullPath) || quality < 0 || quality > 100) {
            throw new RuntimeException("invalid parameters, src="
                    + imageFullPath + ",quality=" + quality);
        }
        File img = new File(imageFullPath);
        if (!img.isFile()) {
            throw new RuntimeException("file not exists, src=" + imageFullPath);
        }
        if (img.length() <= maxFileSize) {
            return (int) img.length();
        }

        // 2. compress
        FileOutputStream out = null;
        try {
            // Retrieve jpg image to be compressed
            RenderedImage rendImage = ImageIO.read(new File(imageFullPath));
            if (rendImage == null || rendImage.getWidth() <= 0
                    || rendImage.getHeight() <= 0) {
                throw new RuntimeException("file is not an image, src="
                        + imageFullPath);
            }
            out = new FileOutputStream(img);
            BufferedImage tag = new BufferedImage(rendImage.getWidth(),
                    rendImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            tag.getGraphics().drawImage((Image) rendImage, 0, 0,
                    rendImage.getWidth(), rendImage.getHeight(), null);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
            jep.setQuality(quality / 100f, true);
            encoder.encode(tag, jep);
        } catch (Exception e) {
            throw new RuntimeException("compressImage fail, src="
                    + imageFullPath, e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return (int) new File(imageFullPath).length();
    }

    */
/**
     * 获取图片信息,包括宽／高／大小／类型，如果取不到会抛异常
     *
     * @param imageFullPath
     * @return
     * @throws Exception
     *//*

    public static ImageFile getImageInfo(String imageFullPath) throws Exception {
        return getImageInfo(imageFullPath, false);
    }

    */
/**
     * (本方法暂不支持)获取图片信息+EXIF信息,如果非图片格式会抛异常
     *
     * @param localImagePath
     *            本地图片路径
     * @param isReadExif
     *            是否需要读取exif信息
     * @return
     * @throws Exception
     *//*

    @SuppressWarnings("unchecked")
    public static ImageFile getImageInfo(String localImagePath,
                                         boolean isReadExif) {
        File imgFile = new File(localImagePath);
        if (!imgFile.isFile()) {
            throw new RuntimeException("file not exists or not a file, file="
                    + localImagePath);
        }
        Image img;
        try {
            img = ImageIO.read(imgFile);
            if (imgFile.length() == 0 || img == null || img.getWidth(null) <= 0
                    || img.getHeight(null) <= 0) {
                throw new RuntimeException(
                        "get image's size/width/height error, file="
                                + localImagePath);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "get image's size/width/height error, file="
                            + localImagePath);
        }
        ImageFile imageFile = new ImageFile();
        imageFile.setWidth(img.getWidth(null));
        imageFile.setHeight(img.getHeight(null));
        imageFile.setSize(imgFile.length());
        imageFile.setFile(imgFile);

        FileInputStream fis = null;
        BufferedInputStream buff = null;
        int leng;
        byte[] mapObj;
        try {
            fis = new FileInputStream(imgFile);
            leng = fis.available();
            buff = new BufferedInputStream(fis);
            mapObj = new byte[leng];
            buff.read(mapObj, 0, leng);
        } catch (IOException e) {
            throw new RuntimeException("read image file stream error, file="
                    + localImagePath);
        }

        String type = null;
        ByteArrayInputStream bais = null;
        MemoryCacheImageInputStream mcis = null;
        try {
            bais = new ByteArrayInputStream(mapObj);
            mcis = new MemoryCacheImageInputStream(bais);
            Iterator itr = ImageIO.getImageReaders(mcis);
            while (itr.hasNext()) {
                ImageReader reader = (ImageReader) itr.next();
                if (reader instanceof GIFImageReader) {
                    type = "gif";
                } else if (reader instanceof JPEGImageReader) {
                    type = "jpg";
                } else if (reader instanceof PNGImageReader) {
                    type = "png";
                } else if (reader instanceof BMPImageReader
                        || reader instanceof WBMPImageReader) {
                    type = "bmp";
                }
            }
            if (type != null) {
                imageFile.setType(ImageType.valueOf(type));
                if (isReadExif) {
                    // TODO read exif information
                }
                return imageFile;
            }
        } finally {
            if (bais != null) {
                try {
                    bais.close();
                } catch (IOException ioe) {
                }
            }
            if (mcis != null) {
                try {
                    mcis.close();
                } catch (IOException ioe) {
                }
            }
            if (buff != null) {
                try {
                    buff.close();
                } catch (IOException ioe) {
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ioe) {
                }
            }
        }
        return null;
    }

    private static Method[] exifMethods = ImageExif.class.getMethods();

*/
/*    private static ImageExif readImageExif(MagickImage image) {
        ImageExif exif = new ImageExif();
        if (image == null) {
            return exif;
        }
        try {
            for (Method method : exifMethods) {
                if ("set".equals(method.getName().substring(0, 3))) {
                    if (method.getName().length() > 3) {
                        method.invoke(exif, image.getImageAttribute("EXIF:"
                                + method.getName().substring(3)));
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("read image exif error", e);
        }
        return exif;
    }*//*


    */
/**
     * 给图片打水印(本地图片会被替换成有水印的图片)，如果图片宽高要小于水印宽高+x或y，则不会打水印
     *
     * @param localImage
     *            源图的本地绝对路径
     * @param markImage
     *            水印图片的绝对路径
     * @param maskType
     *            0-右下角, 1-左下角, 2-正中间, 3-左上角, 4-右上角, 5-自定义
     * @param x
     *            离横向边间隔距离,如左对齐则左边距,右对齐则是右边距,居中传0,自定义则为左边距,单位:px
     * @param y
     *            离纵向边距离,如上对齐则上边距,下对齐则是下边距,居中传0,自定义则为上边距,单位:px
     * @return
     * @throws Exception
     *//*

    public static boolean mask(String localImage, String markImage,
                               int maskType, int x, int y) throws Exception {
        return mask(localImage, localImage, markImage, maskType, x, y);
    }

    */
/**
     * 给图片打水印(生成目标图片会带水印)，如果图片宽高要小于水印宽高+endX或endY，则不会打水印
     *
     * @param imageBytes
     *            源图的byte数组
     * @param markImage
     *            水印图片的绝对路径
     * @param maskType
     *            0-右下角, 1-左下角, 2-正中间, 3-左上角, 4-右上角, 5-自定义
     * @param x
     *            离横向边间隔距离,如左对齐则左边距,右对齐则是右边距,居中传0,自定义则为左边距,单位:px
     * @param y
     *            离纵向边距离,如上对齐则上边距,下对齐则是下边距,居中传0,自定义则为上边距,单位:px
     * @return 处理后图片的byte数组
     * @throws Exception
     *//*

    public static byte[] mask(byte[] imageBytes, String markImage,
                              int maskType, int x, int y) throws Exception {
        File srcTmp = File.createTempFile("ImageIOUtil", null);
        Image src = ImageIO.read(new ByteArrayInputStream(imageBytes));
        Image logo = ImageIO.read(new File(markImage));
        maskCore(src, srcTmp, logo, maskType, x, y);
        InputStream input = null;
        ByteArrayOutputStream out = null;
        try {
            input = new FileInputStream(srcTmp);
            out = new ByteArrayOutputStream(4096);
            byte[] b = new byte[4096];
            int n;
            while ((n = input.read(b)) != -1) {
                out.write(b, 0, n);
                out.flush();
            }
            return out.toByteArray();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                }
            }
        }
    }

    */
/**
     * 给图片打水印(生成目标图片会带水印)，如果图片宽高要小于水印宽高+x或y，则不会打水印
     *
     * @param localImage
     *            源图的本地绝对路径
     * @param destImage
     *            目标图片的本地绝对路径
     * @param markImage
     *            水印图片的绝对路径
     * @param maskType
     *            0-右下角, 1-左下角, 2-正中间, 3-左上角, 4-右上角, 5-自定义
     * @param x
     *            离横向边间隔距离,如左对齐则左边距,右对齐则是右边距,居中传0,自定义则为左边距,单位:px
     * @param y
     *            离纵向边距离,如上对齐则上边距,下对齐则是下边距,居中传0,自定义则为上边距,单位:px
     * @return
     * @throws Exception
     *//*

    public static boolean mask(String localImage, String destImage,
                               String markImage, int maskType, int x, int y) throws Exception {
        Image src = ImageIO.read(new File(localImage));
        Image logo = ImageIO.read(new File(markImage));
        File dest = new File(destImage);
        return maskCore(src, dest, logo, maskType, x, y);
    }

    */
/**
     * 打水印主逻辑
     *
     * @param src
     *            源图
     * @param dest
     *            目标输出文件
     * @param logo
     *            水印
     * @param maskType
     *            0-右下角, 1-左下角, 2-正中间, 3-左上角, 4-右上角, 5-自定义
     * @param marginX
     *            离横向边间隔距离,如左对齐则左边距,右对齐则是右边距,居中传0,自定义则为左边距,单位:px
     * @param marginY
     *            离纵向边距离,如上对齐则上边距,下对齐则是下边距,居中传0,自定义则为上边距,单位:px
     * @return
     * @throws Exception
     *//*

    private static boolean maskCore(Image src, File dest, Image logo,
                                    int maskType, int marginX, int marginY) throws Exception {
        // 校验图片合法性
        if (src == null || src.getWidth(null) <= 0 || src.getHeight(null) <= 0
                || dest == null || logo == null || src.getWidth(null) <= 0
                || src.getHeight(null) <= 0) {
            return false;
        }
        int srcW = src.getWidth(null);
        int srcH = src.getHeight(null);
        int logoW = logo.getWidth(null);
        int logoH = logo.getHeight(null);

        int x = 0, y = 0;
        switch (maskType) {
            // 左下角
            case 1:
                x = marginX;
                y = (int) (srcH - logoH - marginY);
                break;
            // 正中间
            case 2:
                x = (int) ((srcW - logoW) / 2);
                y = (int) ((srcH - logoH) / 2);
                break;
            // 左上角
            case 3:
                x = marginX;
                y = marginY;
                break;
            // 右上角
            case 4:
                x = (int) (srcW - logoW - marginX);
                y = marginY;
                break;
            // 自定义
            case 5:
                x = marginX;
                y = marginY;
                break;
            // 右下角
            case 0:
                // 其它值默认右下角
            default:
                x = (int) (srcW - logoW - marginX);
                y = (int) (srcH - logoH - marginY);
        }

        // 校验水印是否全部落在图片中
        if (x <= 0 || y <= 0 || x > srcW - logoW || y > srcH - logoH) {
            return false;
        }

        FileOutputStream out = null;
        try {
            BufferedImage tag = new BufferedImage((int) srcW, (int) srcH,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(src, 0, 0, srcW, srcH, null);
            g.drawImage(logo, x, y, logoW, logoH, null);
            out = new FileOutputStream(dest);
            ImageIO.write(tag, "jpg", out);
            g.dispose();
        } catch (Exception e) {
            throw new RuntimeException("resize image error", e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return true;
    }

}
*/
