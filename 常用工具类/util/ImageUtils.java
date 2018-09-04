package com.wondertek.mam.util;

import com.sun.media.jai.codec.FileSeekableStream;
import com.wondertek.mam.util.file.FilePathHelper;
import com.wondertek.mam.util.file.FileUtil;
import com.wondertek.mam.util.others.CmsUtil;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.swing.*;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.*;
import java.awt.image.renderable.ParameterBlock;
import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;

/**
 * 纯JAVA实现的图片处理工具类
 *
 */
public class ImageUtils {
    private static Logger log = Logger.getLogger(ImageUtils.class);

    /**
     * 获取图片尺寸信息
     *
     * @param filePath
     *            a {@link java.lang.String} object.
     * @return [width, height]
     */
    public static int[] getSizeInfo(String filePath) throws Exception {
        File file = new File(filePath);
        return getSizeInfo(file);
    }

    /**
     * 获取图片尺寸信息
     *
     * @param url
     *            a {@link java.net.URL} object.
     * @return [width,height]
     */
    public static int[] getSizeInfo(URL url) throws Exception {
        InputStream input = null;
        try {
            input = url.openStream();
            return getSizeInfo(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    /**
     * 获取图片尺寸信息
     *
     * @param file
     *            a {@link java.io.File} object.
     * @return [width,height]
     */
    public static int[] getSizeInfo(File file) throws Exception {
        if (!file.exists()) {
            throw new Exception("file " + file.getAbsolutePath() + " doesn't exist.");
        }
        BufferedInputStream input = null;
        try {
            input = new BufferedInputStream(new FileInputStream(file));
            return getSizeInfo(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    /**
     * 获取图片尺寸
     *
     * @param input
     *            a {@link java.io.InputStream} object.
     * @return [width,height]
     */
    public static int[] getSizeInfo(InputStream input) throws Exception {
        try {
            BufferedImage img = ImageIO.read(input);
            int w = img.getWidth(null);
            int h = img.getHeight(null);
            return new int[] { w, h };
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    /**
     * 重调图片尺寸
     *
     * @param srcFilePath
     *            原图路径
     * @param destFile
     *            目标文件
     * @param width
     *            新的宽度，小于1则忽略，按原图比例缩放
     * @param height
     *            新的高度，小于1则忽略，按原图比例缩放
     */
    public static void resize(String srcFilePath, String destFile, int width, int height) throws Exception {
        resize(srcFilePath, destFile, width, height, -1, -1);
    }

    /**
     * 重调图片尺寸
     *
     * @param input
     *            a {@link java.io.InputStream} object.
     * @param output
     *            a {@link java.io.OutputStream} object.
     * @param width
     *            a int.
     * @param height
     *            a int.
     */
    public static void resize(InputStream input, OutputStream output, int width, int height) throws Exception {
        resize(input, output, width, height, -1, -1);
    }

    /**
     * 重调图片尺寸
     *
     * @param input
     *            a {@link java.io.InputStream} object.
     * @param output
     *            a {@link java.io.OutputStream} object.
     * @param width
     *            a int.
     * @param height
     *            a int.
     * @param maxWidth
     *            a int.
     * @param maxHeight
     *            a int.
     */
    public static void resize(InputStream input, OutputStream output,
                              int width, int height, int maxWidth, int maxHeight) throws Exception {

        if (width < 1 && height < 1 && maxWidth < 1 && maxHeight < 1) {
            try {
                IOUtils.copy(input, output);
            } catch (IOException e) {
                throw new Exception("resize error: ", e);
            }
        }
        try {
            BufferedImage img = ImageIO.read(input);
            boolean hasNotAlpha = !img.getColorModel().hasAlpha();
            double w = img.getWidth(null);
            double h = img.getHeight(null);
            int toWidth;
            int toHeight;
            double rate = w / h;

            if (width > 0 && height > 0) {
                rate = ((double) width) / ((double) height);
                toWidth = width;
                toHeight = height;
            } else if (width > 0) {
                toWidth = width;
                toHeight = (int) (toWidth / rate);
            } else if (height > 0) {
                toHeight = height;
                toWidth = (int) (toHeight * rate);
            } else {
                toWidth = ((Number) w).intValue();
                toHeight = ((Number) h).intValue();
            }

            if (maxWidth > 0 && toWidth > maxWidth) {
                toWidth = maxWidth;
                toHeight = (int) (toWidth / rate);
            }
            if (maxHeight > 0 && toHeight > maxHeight) {
                toHeight = maxHeight;
                toWidth = (int) (toHeight * rate);
            }

            BufferedImage tag = new BufferedImage(toWidth, toHeight, hasNotAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);

            // Image.SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
            tag.getGraphics().drawImage(img.getScaledInstance(toWidth, toHeight, Image.SCALE_SMOOTH), 0, 0, null);
            ImageIO.write(tag, hasNotAlpha ? "jpg" : "png", output);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }

    }

    /**
     * 重调图片尺寸
     *
     * @param srcFile
     *            原图路径
     * @param destFile
     *            目标文件
     * @param width
     *            新的宽度，小于1则忽略，按原图比例缩放
     * @param height
     *            新的高度，小于1则忽略，按原图比例缩放
     * @param maxWidth
     *            最大宽度，限制目标图片宽度，小于1则忽略此设置
     * @param maxHeight
     *            最大高度，限制目标图片高度，小于1则忽略此设置
     */
    public static void resize(String srcFile, String destFile, int width,
                              int height, int maxWidth, int maxHeight) throws Exception {
        resize(new File(srcFile), new File(destFile), width, height, maxWidth, maxHeight);
    }

    /**
     * 重调图片尺寸
     *
     * @param srcFile
     *            原图路径
     * @param destFile
     *            目标文件
     * @param width
     *            新的宽度，小于1则忽略，按原图比例缩放
     * @param height
     *            新的高度，小于1则忽略，按原图比例缩放
     */
    public static void resize(File srcFile, File destFile, int width, int height) throws Exception {
        resize(srcFile, destFile, width, height, -1, -1);
    }

    /**
     * 重调图片尺寸
     *
     * @param srcFile
     *            原图路径
     * @param destFile
     *            目标文件
     * @param width
     *            新的宽度，小于1则忽略，按原图比例缩放
     * @param height
     *            新的高度，小于1则忽略，按原图比例缩放
     * @param maxWidth
     *            最大宽度，限制目标图片宽度，小于1则忽略此设置
     * @param maxHeight
     *            最大高度，限制目标图片高度，小于1则忽略此设置
     */
    public static void resize(File srcFile, File destFile, int width,
                              int height, int maxWidth, int maxHeight) throws Exception {
        if (destFile.exists()) {
            destFile.delete();
        } else {
            FilePathHelper.mkdirs(destFile.getParent());
        }
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new BufferedInputStream(new FileInputStream(srcFile));
            output = new FileOutputStream(destFile);
            resize(input, output, width, height, maxWidth, maxHeight);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
    }

    /**
     * 裁剪图片
     *
     * @param source
     *            a {@link java.lang.String} object.
     * @param target
     *            a {@link java.lang.String} object.
     * @param x
     *            a int.
     * @param y
     *            a int.
     * @param w
     *            a int.
     * @param h
     *            a int.
     */
    public static void crop(String source, String target, int x, int y, int w, int h) throws Exception {
        crop(new File(source), new File(target), x, y, w, h);
    }

    /**
     * 裁剪图片
     *
     * @param source
     *            a {@link java.io.File} object.
     * @param target
     *            a {@link java.io.File} object.
     * @param x
     *            a int.
     * @param y
     *            a int.
     * @param w
     *            a int.
     * @param h
     *            a int.
     */
    public static void crop(File source, File target, int x, int y, int w, int h) throws Exception {
        OutputStream output = null;
        InputStream input = null;
        String ext = FilenameUtils.getExtension(target.getName());
        try {
            input = new BufferedInputStream(new FileInputStream(source));
            if (target.exists()) {
                target.delete();
            } else {
                FilePathHelper.mkdirs(target.getParent());
            }
            output = new BufferedOutputStream(new FileOutputStream(target));
        } catch (IOException e) {
            throw new Exception(e);
        }
        crop(input, output, x, y, w, h, StringUtils.equalsIgnoreCase("png", ext));
    }

    /**
     * 裁剪图片
     *
     * @param x
     *            a int.
     * @param y
     *            a int.
     * @param w
     *            a int.
     * @param h
     *            a int.
     * @param input
     *            a {@link java.io.InputStream} object.
     * @param output
     *            a {@link java.io.OutputStream} object.
     * @param isPNG
     *            a boolean.
     */
    public static void crop(InputStream input, OutputStream output, int x,
                            int y, int w, int h, boolean isPNG) throws Exception {
        try {
            BufferedImage srcImg = ImageIO.read(input);
            int tmpWidth = srcImg.getWidth();
            int tmpHeight = srcImg.getHeight();
            int xx = Math.min(tmpWidth - 1, x);
            int yy = Math.min(tmpHeight - 1, y);

            int ww = w;
            if (xx + w > tmpWidth) {
                ww = Math.max(1, tmpWidth - xx);
            }
            int hh = h;
            if (yy + h > tmpHeight) {
                hh = Math.max(1, tmpHeight - yy);
            }

            BufferedImage dest = srcImg.getSubimage(xx, yy, ww, hh);

            BufferedImage tag = new BufferedImage(w, h, isPNG ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);

            tag.getGraphics().drawImage(dest, 0, 0, null);
            ImageIO.write(tag, isPNG ? "png" : "jpg", output);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
    }

    /**
     * 压缩图片,PNG图片按JPG处理
     *
     * @param input
     *            a {@link java.io.InputStream} object.
     * @param output
     *            a {@link java.io.OutputStream} object.
     * @param quality
     *            图片质量0-1之间
     */
    public static final void optimize(InputStream input, OutputStream output, float quality) throws Exception {

        // create a BufferedImage as the result of decoding the supplied
        // InputStream
        BufferedImage image;
        ImageOutputStream ios = null;
        ImageWriter writer = null;
        try {
            image = ImageIO.read(input);

            // get all image writers for JPG format
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");

            if (!writers.hasNext())
                throw new IllegalStateException("No writers found");

            writer = (ImageWriter) writers.next();
            ios = ImageIO.createImageOutputStream(output);

            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();

            // optimize to a given quality
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);

            // appends a complete image stream containing a single image and
            // associated stream and image metadata and thumbnails to the output
            writer.write(null, new IIOImage(image, null, null), param);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            if (ios != null) {
                try {
                    ios.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new Exception(e);
                }
            }
            writer.dispose();
        }
    }

    /**
     * 压缩图片
     *
     * @param source
     *            a {@link java.lang.String} object.
     * @param target
     *            a {@link java.lang.String} object.
     * @param quality
     *            a float.
     */
    public static final void optimize(String source, String target, float quality) throws Exception {
        File fromFile = new File(source);
        File toFile = new File(target);
        optimize(fromFile, toFile, quality);
    }

    /**
     * 压缩图片
     *
     * @param source
     *            a {@link java.io.File} object.
     * @param target
     *            a {@link java.io.File} object.
     * @param quality
     *            图片质量0-1之间
     */
    public static final void optimize(File source, File target, float quality) throws Exception {
        if (target.exists()) {
            target.delete();
        } else {
            FilePathHelper.mkdirs(target.getParent());
        }
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new BufferedInputStream(new FileInputStream(source));
            os = new BufferedOutputStream(new FileOutputStream(target));
            optimize(is, os, quality);
        } catch (FileNotFoundException e) {
            throw new Exception(e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    /**
     * 制作圆角
     *
     * @param srcFile
     *            原文件
     * @param destFile
     *            目标文件
     * @param cornerRadius
     *            角度
     */
    public static void makeRoundedCorner(File srcFile, File destFile, int cornerRadius) throws Exception {
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new BufferedInputStream(new FileInputStream(srcFile));
            FilePathHelper.mkdirs(destFile.getParentFile().getAbsolutePath());
            out = new BufferedOutputStream(new FileOutputStream(destFile));
            makeRoundedCorner(in, out, cornerRadius);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
        }

    }

    /**
     * 制作圆角
     *
     * @param srcFile
     *            原文件
     * @param destFile
     *            目标文件
     * @param cornerRadius
     *            角度
     */
    public static void makeRoundedCorner(String srcFile, String destFile, int cornerRadius) throws Exception {
        makeRoundedCorner(new File(srcFile), new File(destFile), cornerRadius);
    }

    /**
     * 制作圆角
     *
     * @param inputStream
     *            原图输入流
     * @param outputStream
     *            目标输出流
     * @param radius
     *            角度
     */
    public static void makeRoundedCorner(final InputStream inputStream,
                                         final OutputStream outputStream, final int radius) throws Exception {
        BufferedImage sourceImage = null;
        BufferedImage targetImage = null;
        try {
            sourceImage = ImageIO.read(inputStream);
            int w = sourceImage.getWidth();
            int h = sourceImage.getHeight();
            System.out.println(w);

            int cornerRadius = radius < 1 ? w / 4 : radius;

            targetImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2 = targetImage.createGraphics();

            // This is what we want, but it only does hard-clipping, i.e.
            // aliasing
            // g2.setClip(new RoundRectangle2D ...)

            // so instead fake soft-clipping by first drawing the desired clip
            // shape
            // in fully opaque white with antialiasing enabled...
            g2.setComposite(AlphaComposite.Src);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

            // ... then compositing the image on top,
            // using the white shape from above as alpha source
            g2.setComposite(AlphaComposite.SrcAtop);
            g2.drawImage(sourceImage, 0, 0, null);
            g2.dispose();
            ImageIO.write(targetImage, "png", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }


    /**
     * 使用ImageReader获取图片尺寸
     *  这个方法比BufferedImage的效率高
     * @param src
     * 源图片路径
     */
     public void getImageSizeByImageReader(String src) {
                 long beginTime = new Date().getTime();
                File file = new File(src);
                 try {
                         Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("jpg");
                         ImageReader reader = (ImageReader) readers.next();
                         ImageInputStream iis = ImageIO.createImageInputStream(file);
                         reader.setInput(iis, true);
                         System.out.println("width:" + reader.getWidth(0));
                         System.out.println("height:" + reader.getHeight(0));
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 long endTime = new Date().getTime();
                 System.out.println("使用[ImageReader]获取图片尺寸耗时：[" + (endTime - beginTime)+"]ms");
             }

        /**
         * 使用BufferedImage获取图片尺寸
         * @param src
         * 源图片路径
         */
             public void getImageSizeByBufferedImage(String src) {
                 long beginTime = new Date().getTime();
                 File file = new File(src);
                 FileInputStream is = null;
                 try {
                         is = new FileInputStream(file);
                     } catch (FileNotFoundException e2) {
                         e2.printStackTrace();
                     }
                 BufferedImage sourceImg = null;
                 try {
                         sourceImg = javax.imageio.ImageIO.read(is);
                         System.out.println("width:" + sourceImg.getWidth());
                         System.out.println("height:" + sourceImg.getHeight());
                     } catch (IOException e1) {
                         e1.printStackTrace();
                     }
                 long endTime = new Date().getTime();
                 System.out.println("使用[BufferedImage]获取图片尺寸耗时：[" + (endTime - beginTime)+"]ms");
             }

    /**
     * 把图片印刷到图片上
     *
     * @param pressImg
     *            -- 水印文件
     * @param targetImg
     *            -- 目标文件
     * @param intAlign
     *            --位置类型
     */
/*    public final static void pressImage(String pressImg, String targetImg, int intAlign) {

        try {
            // 目标文件
            File _file = new File(targetImg);
            Image src = ImageIO.read(_file);
            int wideth = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, wideth, height, null);

            // 水印文件
            File _filebiao = new File(pressImg);
            Image src_biao = ImageIO.read(_filebiao);
            int wideth_biao = src_biao.getWidth(null);
            int height_biao = src_biao.getHeight(null);
            if (intAlign == 1) {
                g.drawImage(src_biao, 5, 10, wideth_biao, height_biao, null);
            } else if (intAlign == 2) {
                g.drawImage(src_biao, (wideth - wideth_biao) / 2 - 5, 10, wideth_biao, height_biao, null);
            } else if (intAlign == 3) {
                g.drawImage(src_biao, wideth - wideth_biao - 5, 10, wideth_biao, height_biao, null);
            } else if (intAlign == 4) {
                g.drawImage(src_biao, 5, (height - height_biao) / 2 - 10, wideth_biao, height_biao, null);
            } else if (intAlign == 5) {
                g.drawImage(src_biao, (wideth - wideth_biao) / 2 - 5, (height - height_biao) / 2 - 10, wideth_biao, height_biao, null);
            } else if (intAlign == 6) {
                g.drawImage(src_biao, wideth - wideth_biao - 5, (height - height_biao) / 2 - 10, wideth_biao, height_biao, null);
            } else if (intAlign == 7) {
                g.drawImage(src_biao, 5, height - height_biao - 10, wideth_biao, height_biao, null);
            } else if (intAlign == 8) {
                g.drawImage(src_biao, (wideth - wideth_biao) / 2 - 5, height - height_biao - 10, wideth_biao, height_biao, null);
            } else if (intAlign == 9) {
                g.drawImage(src_biao, wideth - wideth_biao - 5, height - height_biao - 10, wideth_biao, height_biao, null);
            }
            // 水印文件结束
            g.dispose();
            FileOutputStream out = new FileOutputStream(targetImg);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(image);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /**
     *
     * @param strImageText
     * @param fm
     * @return
     * @author googol Feb 9, 2006
     */
    public static int getStringWidth(String strImageText, FontMetrics fm) {
        int intReturn = 0;
        int intCount = strImageText.length();
        char chrImageText[] = strImageText.toCharArray();
        for (int i = 0; i < intCount; i++) {
            int charWidth = fm.charWidth(chrImageText[i]);
            intReturn += charWidth;
        }

        return intReturn += 10;
    }

    /** */
    /**
     * 打印文字水印图片
     *
     * @param pressText
     *            --文字
     * @param targetImg
     *            -- 目标图片
     * @param fontName
     *            -- 字体名
     * @param fontStyle
     *            -- 字体样式
     * @param color
     *            -- 字体颜色
     * @param fontSize
     *            -- 字体大小
     * @param intAlign
     *            --位置类型
     */
/*
    public static void pressText(String pressText, String targetImg, String fontName, int fontStyle, Color color, int fontSize, int intAlign) {
        try {
            File _file = new File(targetImg);
            Image src = ImageIO.read(_file);
            int wideth = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, wideth, height, null);
            g.setColor(color);
            g.setFont(new Font(fontName, fontStyle, fontSize));
            int intWidth = getStringWidth(pressText, g.getFontMetrics());
            if (wideth > intWidth) {
                if (intAlign == 1) {
                    g.drawString(pressText, 5, 20);
                } else if (intAlign == 2) {
                    g.drawString(pressText, (wideth - intWidth) / 2 - 5, 20);
                } else if (intAlign == 3) {
                    g.drawString(pressText, wideth - intWidth - 5, 20);
                } else if (intAlign == 4) {
                    g.drawString(pressText, 5, height / 2 - 10);
                } else if (intAlign == 5) {
                    g.drawString(pressText, (wideth - intWidth) / 2 - 5, height / 2 - 10);
                } else if (intAlign == 6) {
                    g.drawString(pressText, wideth - intWidth - 5, height / 2 - 10);
                } else if (intAlign == 7) {
                    g.drawString(pressText, 5, height - 10);
                } else if (intAlign == 8) {
                    g.drawString(pressText, (wideth - intWidth) / 2 - 5, height - 10);
                } else if (intAlign == 9) {
                    g.drawString(pressText, wideth - intWidth - 5, height - 10);
                }
            } else {
                g.drawString(pressText, 0, height - 10);
            }
            g.dispose();
            FileOutputStream out = new FileOutputStream(targetImg);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(image);
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }*/

    /**
     * 只限制宽度生成缩略图，按比例缩放，如果原图宽度比需要生成的图的宽度还小，则简单拷贝一张图
     *
     * @author Dennis
     *
     *         2009-11-17 下午04:41:55
     */
    public static void makeMiniature(String strPicturePath, String strOutPath, int intMinWidth) {
        File _file = new File(strPicturePath);
        Image src;
        try {
            src = ImageIO.read(_file);
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            if (width <= intMinWidth) {
                // 直接copy
                makeMiniature(strPicturePath, strOutPath, width, height);
            } else {
                // 得到缩放比例
                float scale = (float) intMinWidth / width;
                makeMiniature(strPicturePath, strOutPath, intMinWidth, (int) (height * scale - 1));
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * 产生一个缩略
     *
     * @param strPicturePath
     *            原图片位置
     * @param strOutPath
     *            生成图片的位置 2009-11-17 下午04:43:50
     */
    public static void makeMiniature(String strPicturePath, String strOutPath, int intMinWidth, int intMinHeight) {

        try {
            String imageFile = strPicturePath;
            File file = new File(imageFile);

            BufferedImage im = null;
            InputStream imageIn = new FileInputStream(file);

            im = ImageIO.read(imageIn);
            int minh = intMinHeight, minw = intMinWidth;

            BufferedImage imout = new BufferedImage(minw, minh, 1);
            Graphics g = imout.getGraphics();
            g.drawImage(im, 0, 0, minw, minh, null);

            imageIn.close();

            File out = new File(strOutPath);
            if (strOutPath.endsWith(".JPG") || strOutPath.endsWith(".jpg")) {
                ImageIO.write(imout, "jpg", out);

            } else if (strOutPath.endsWith(".GIF") || strOutPath.endsWith(".gif")) {
                ImageIO.write(imout, "gif", out);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 压缩图片文件<br>
     * 先保存原文件，再压缩、上传
     *
     * @param oldFile
     *            要进行压缩的文件全路径
     * @param width
     *            宽度
     * @param height
     *            高度
     * @param quality
     *            质量
     * @param smallIcon
     *            小图片的后缀
     * @return 返回压缩后的文件的全路径
     */
/*    public static String zipImageFile(String oldFile, int width, int height, float quality, String smallIcon) {
        if (oldFile == null)
            return null;
        String newImage = null;
        try {
            *//** 对服务器上的临时文件进行处理 *//*
            Image srcFile = ImageIO.read(new File(oldFile));
            double rate1 = ((double) srcFile.getWidth(null)) / (double) width + 0.1;
            double rate2 = ((double) srcFile.getHeight(null)) / (double) width + 0.1;
            double rate = rate1 > rate2 ? rate1 : rate2;
            width = (int) (((double) srcFile.getWidth(null)) / rate);
            width = (int) (((double) srcFile.getHeight(null)) / rate);

            *//** 宽,高设定 *//*
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            tag.getGraphics().drawImage(srcFile, 0, 0, width, height, null);
            String filePrex = oldFile.substring(0, oldFile.indexOf('.'));
            *//** 压缩后的文件名 *//*
            newImage = filePrex + smallIcon + oldFile.substring(filePrex.length());

            *//** 压缩之后临时存放位置 *//*
            FileOutputStream out = new FileOutputStream(newImage);

            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
            *//** 压缩质量 *//*
            jep.setQuality(quality, true);
            encoder.encode(tag, jep);
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newImage;
    }*/

    /**
     * @author Thinkpad
     * 比例缩放
     * @method resize
     * @param sourcePath 原图路径
     * @param targetPath 目标路径
     * @param height 目标图片高
     * @param width 目标图片宽
     * @param colorValue 填充颜色
     * @return void
     * @date 2013 下午01:38:34
     */
    public static void resize(String sourcePath,String targetPath, int height, int width, String colorValue) {
        try {
            double ratio = 0; //缩放比例
            File f = new File(sourcePath);
            Color color = Color.ORANGE;
            color = new Color(Integer.parseInt(colorValue, 16));
            String type = sourcePath.substring(sourcePath.indexOf(".")+1,sourcePath.length());
            File ft = new File(targetPath);
            BufferedImage bi = ImageIO.read(f);
            Image itemp = bi.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
            //计算比例
            if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
                if (bi.getHeight() > bi.getWidth()) {
                    ratio = (new Integer(height)).doubleValue() / bi.getHeight();
                } else {
                    ratio = (new Integer(width)).doubleValue() / bi.getWidth();
                }
                AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
                itemp = op.filter(bi, null);
            }
            if (color != null) {
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = image.createGraphics();
                g.setColor(color);
                g.fillRect(0, 0, width, height);
                if (width == itemp.getWidth(null))
                    g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null), itemp.getHeight(null), color, null);
                else
                    g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null), itemp.getHeight(null), color, null);
                g.dispose();
                itemp = image;
            }
            ImageIO.write((BufferedImage) itemp, type, ft);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**图片格式：JPG*/
    private static final String PICTRUE_FORMATE_JPG = "jpg";

    /**
     * @author Thinkpad
     * 添加图片水印
     * @method pressImage
     * @param targetImg 目标图片路径，如：C://myPictrue//1.jpg
     * @param waterImg  水印图片路径，如：C://myPictrue//logo.png
     * @param x 水印图片距离目标图片左侧的偏移量，如果x<0, 则在正中间
     * @param y 水印图片距离目标图片上侧的偏移量，如果y<0, 则在正中间
     * @param alpha 透明度(0.0 -- 1.0, 0.0为完全透明，1.0为完全不透明)
     * @return void
     * @date 2013 下午01:44:26
     */
    public final static void pressImage(String targetImg, String waterImg, int x, int y, float alpha) {
        try {
            File file = new File(targetImg);
            Image image = ImageIO.read(file);
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufferedImage.createGraphics();
            g.drawImage(image, 0, 0, width, height, null);

            Image waterImage = ImageIO.read(new File(waterImg));    // 水印文件
            int width_1 = waterImage.getWidth(null);
            int height_1 = waterImage.getHeight(null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

            int widthDiff = width - width_1;
            int heightDiff = height - height_1;
            if(x < 0){
                x = widthDiff / 2;
            }else if(x > widthDiff){
                x = widthDiff;
            }
            if(y < 0){
                y = heightDiff / 2;
            }else if(y > heightDiff){
                y = heightDiff;
            }
            g.drawImage(waterImage, x, y, width_1, height_1, null); // 水印文件结束
            g.dispose();
            ImageIO.write(bufferedImage, PICTRUE_FORMATE_JPG, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author Thinkpad
     * 添加文字水印
     * @method pressText
     * @param targetImg 目标图片路径，如：C://myPictrue//1.jpg
     * @param pressText 水印文字， 如：中国证券网
     * @param fontName 字体名称，    如：宋体
     * @param fontStyle 字体样式，如：粗体和斜体(Font.BOLD|Font.ITALIC)
     * @param fontSize 字体大小，单位为像素
     * @param color 字体颜色
     * @param x 水印文字距离目标图片左侧的偏移量，如果x<0, 则在正中间
     * @param y 水印文字距离目标图片上侧的偏移量，如果y<0, 则在正中间
     * @param alpha 透明度(0.0 -- 1.0, 0.0为完全透明，1.0为完全不透明)
     * @return void
     * @date 2013 下午01:43:16
     */
    public static void pressText(String targetImg, String pressText, String fontName, int fontStyle, int fontSize, Color color, int x, int y, float alpha) {
        try {
            File file = new File(targetImg);

            Image image = ImageIO.read(file);
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufferedImage.createGraphics();
            g.drawImage(image, 0, 0, width, height, null);
            g.setFont(new Font(fontName, fontStyle, fontSize));
            g.setColor(color);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

            //int width_1 = fontSize * getLength(pressText);
            int width_1 = 0;
            int height_1 = fontSize;
            int widthDiff = width - width_1;
            int heightDiff = height - height_1;
            if(x < 0){
                x = widthDiff / 2;
            }else if(x > widthDiff){
                x = widthDiff;
            }
            if(y < 0){
                y = heightDiff / 2;
            }else if(y > heightDiff){
                y = heightDiff;
            }

            g.drawString(pressText, x, y + height_1);
            g.dispose();
            ImageIO.write(bufferedImage, PICTRUE_FORMATE_JPG, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * POMS系统中采用的图像缩放的方法，缩放效果比一般的方法好
     *
     * @param srcImageFile
     * @param result
     * @param width
     * @param height
     * @param bb
     * @param fileType
     * @param destFoderPath
     * @return
     */

    public final static boolean scaleImage(String srcImageFile, String result, int width, int height, boolean bb, String fileType, String destFoderPath) {
        boolean bSuc = false;
        //1.检查目标文件夹是否存在，不存在则创建
        FileUtil.checkDirExists(destFoderPath);
        ImageOutputStream ios = null;
        try {
            //获取原图片的宽和高
            BufferedImage srcImg = ImageIO.read(new File(srcImageFile));
            int srcWidth = srcImg.getWidth(null);
            int srcHeight = srcImg.getHeight(null);
            float ratio = 1.0f;
            float widthRatio = (float)width/srcWidth;
            float heightRatio = (float)height/srcHeight;
            if(widthRatio<1.0 || heightRatio<1.0) {
                ratio = (widthRatio<=heightRatio) ? widthRatio : heightRatio;
            }
            int newWidth = (int) (ratio * srcWidth);
            int newHeight = (int) (ratio * srcHeight);
            BufferedImage targetImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = targetImg.createGraphics();
            graphics.drawImage(srcImg.getScaledInstance(newWidth, newHeight, Image.SCALE_AREA_AVERAGING), 0, 0, newWidth, newHeight, Color.white, null);
            graphics.dispose();
            //卷积核(权矩阵)
            float[] kernelData2 = {-0.125f, -0.125f, -0.125f, -0.125f, 2, -0.125f, -0.125f, -0.125f, -0.125f };
            Kernel kernel = new Kernel(3, 3, kernelData2);
            ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
            targetImg = cOp.filter(targetImg, null);
            ImageWriter imageWriter = ImageIO.getImageWritersBySuffix("jpg").next();
            ios = ImageIO.createImageOutputStream(new FileOutputStream(result));
            imageWriter.setOutput(ios);
            IIOMetadata imageMetaData = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(targetImg), null);
            JPEGImageWriteParam jpegParams  =  (JPEGImageWriteParam) imageWriter.getDefaultWriteParam();
            jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
            jpegParams.setCompressionQuality(0.75f);
            imageWriter.write(imageMetaData, new IIOImage(targetImg, null, null), null);
            imageWriter.dispose();
            bSuc = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(ios != null) {
                try {
                    ios.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        log.info("========================新的图片转码结果[" + bSuc + "]========================");
        return bSuc;
    }

    /**
     * 将原图片sourceImageFile 按照encoder格式转化为despImage目标图片
     *
     * @param sourceImage
     * @param despImage
     * @param encoder
     */
    public static void changeImgeEncode(File sourceImage, File despImage,
                                        String encoder) throws Exception{
        FileUtil.createNewFile(despImage);
        BufferedImage input = null;
        input = ImageIO.read(sourceImage);
        ImageIO.write(input, encoder, despImage);
        input.flush();
    }

    /**
     * 将彩色图转化为灰度图
     * @param srcImage
     * @param destImage
     *            图片文件路径
     * @return 一个图片对象
     * @throws IOException
     */
    public static BufferedImage gray(String srcImage,String destImage) throws IOException {
        int width;
        int height;
        int[] pixels;
        BufferedImage bi = readImageFromFile(srcImage);
        // 得到宽和高
        width = bi.getWidth(null);
        height = bi.getHeight(null);
        // 读取像素
        pixels = new int[width * height];
        bi.getRGB(0, 0, width, height, pixels, 0, width);
        int newPixels[] = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            int r = (pixels[i] >> 16) & 0xff;
            int g = (pixels[i] >> 8) & 0xff;
            int b = (pixels[i]) & 0xff;
            // 方法1，图像很奇怪
            int gray = (int) (0.229 * r + 0.587 * g + 0.114 * b);
            newPixels[i] = (gray << 16) + (gray << 8) + gray;
        }
        // 基于 newPixels 构造一个 BufferedImage
        BufferedImage bi2 = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        bi2.setRGB(0, 0, width, height, newPixels, 0, width);
        newPixels = null;
        try {
            // 写入磁盘
            writeImageToFile(destImage, bi2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bi;
    }

    /**
     * 从磁盘文件读取图片
     *
     * @param imageFile
     *            文件路径
     * @return BufferedImage对象，失败为null
     * @throws IOException
     */
    public static BufferedImage readImageFromFile(String imageFile)
            throws IOException {
        BufferedImage bi;
        // 获取某种图片格式的reader对象
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(ImageUtils.getImageType(imageFile));
        ImageReader reader = (ImageReader) readers.next();
        // 为该reader对象设置输入源
        ImageInputStream iis = ImageIO.createImageInputStream(new File(imageFile));
        reader.setInput(iis);
        // 创建图片对象
        bi = reader.read(0);
        readers = null;
        reader = null;
        iis = null;
        return bi;
    }

    /**
     * 将图片写入磁盘文件
     *
     * @param imgFile
     *            文件路径
     * @param bi
     *            BufferedImage 对象
     * @return 无
     */
    public static void writeImageToFile(String imgFile, BufferedImage bi)
            throws IOException {


        // 写图片到磁盘上
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(imgFile.substring(imgFile.lastIndexOf('.') + 1));
        ImageWriter writer = (ImageWriter) writers.next();
        // 设置输出源
        File f = new File(imgFile);
        ImageOutputStream ios;
        FileUtil.createNewFile(f);
        ios = ImageIO.createImageOutputStream(f);
        writer.setOutput(ios);
        // 写入到磁盘
        writer.write(bi);
    }

    /**
     * 获取图片文件的后缀
     * @param fileName
     * @return
     */
    public static String getFileSuffix(String fileName) {
        int point = fileName.lastIndexOf('.');
        int length = fileName.length();
        if (point == -1 || point == length - 1) {
            return "";
        }else {
            return fileName.substring(point+1, length);
        }
    }

    /**
     * 判断一个图片文件的类型。 前提是，已知该文件是图片；本函数仅读取文件头部两个字节进行判断。
     * 虽然可以多读几个字节会更精确，这里没必要，因为已知是图片了。
     *
     * @param filePath
     * @return 图片类型后缀
     * @throws IOException
     */

    /*public static String getImageType(String filePath) throws IOException {
        File f = new File(filePath);
        FileInputStream in = null;
        String type = null;
        byte[] bytes = { 0, 0 }; // 用于存放文件头两个字节
        in = new FileInputStream(f);
        in.read(bytes, 0, 2);
        if (((bytes[0] & 0xFF) == 0x47) && ((bytes[1] & 0xFF) == 0x49)) { // GIF
            type = "gif";
        } else if (((bytes[0] & 0xFF) == 0x89) && ((bytes[1] & 0xFF) == 0x50)) { // PNG
            type = "png";
        } else if (((bytes[0] & 0xFF) == 0xFF) && ((bytes[1] & 0xFF) == 0xD8)) { // JPG
            type = "jpg";
        } else if (((bytes[0] & 0xFF) == 0x42) && ((bytes[1] & 0xFF) == 0x4D)) { // BMP
            type = "bmp";
        } else { // not supported type
            // System.out.println("not supported type!");
        }
        in.close();
        return type;
    }*/
    public static String getImageType(String filePath) throws IOException {
    	String info = null;
    	try {
    		ImageInputStream iis = ImageIO.createImageInputStream(new FileInputStream(filePath));
        	Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
        	
        	if (iter.hasNext()) {
            	ImageReader reader = iter.next();
            	info = reader.getFormatName();
        	}
        	iis.close();
        	return info;
    	} catch (IOException ex) {
            ex.printStackTrace();
            log.info("----------------------reader image error");
        }
        return info;
    }

    public static BufferedImage getBufferImage(File file) {
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bi;
    }

    public static void gray2(String srcPath, String destPath) throws IOException {
		File srcFile=new File(srcPath);
	    if(!srcFile.exists())return;
	    File destFile=new File(destPath);
	    if(!destFile.exists())
			FileUtil.createNewFile(destFile);
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		BufferedImage destBi=getBufferImage(destFile);
		ImageIO.write(op.filter(getBufferImage(srcFile),destBi), getFileSuffix(srcPath),destFile);
	}

    public static boolean  grayToBit8(String srcPath, String destPath,String tmpImagePath,String type){
        boolean flag=false;
        try {
            ImageUtils.gray(srcPath,destPath);
            ImageUtils.changeImgeEncode(new File(destPath),new File(tmpImagePath),"gif");
            ImageUtils.changeImgeEncode(new File(tmpImagePath),new File(destPath),type);
            flag=true;
        } catch (Exception e) {
            log.error("gray error:"+srcPath);
        }
        return flag;
    }

    /**
     * 注：2010.02.26 duguoc将该方法由private改为public 比例缩放图片
     *
     * @param sourcePath
     * @param despPath
     * @param height
     * @param width
     * @throws Exception
     */
    private static void chageImageSize(String sourcePath, String despPath,
                                       String imgType, int height, int width) throws Exception {
        sourcePath = CmsUtil.replaceSeparator(sourcePath);
        double Ratio = 0.0;
        double hightRatio = 0.0; // 缩放比例
        double widthRatio = 0.0; // 缩放比例
        File sourceFile = new File(sourcePath);
        File despFile = new File(despPath);
        if (!despFile.isFile())
            throw new Exception(despFile
                    + " is not image file error in getFixedBoundIcon!====");
        Icon ret = new ImageIcon(sourcePath);
        BufferedImage Bi = ImageIO.read(sourceFile);
        if ((Bi.getHeight() > height) || (Bi.getWidth() > width)) {
            if (Bi.getHeight() > height) {
                Ratio = (new Integer(height)).doubleValue() / Bi.getHeight();
            } else {
                Ratio = (new Integer(width)).doubleValue() / Bi.getWidth();
            }
            File ThF = new File(despPath);
            Image Itemp = Bi.getScaledInstance(width, height, Bi.SCALE_SMOOTH);
            AffineTransformOp op = new AffineTransformOp(AffineTransform
                    .getScaleInstance(Ratio, Ratio), null);
            Itemp = op.filter(Bi, null);
            try {
                ImageIO.write((BufferedImage) Itemp, imgType, ThF);

            } catch (Exception ex) {

            }
        }
        return;
    }

    /**
     * 对图片裁剪，并把裁剪完蛋新图片保存 。
     */
    public static void operateImage(int x, int y, int width, int height,
                                    String srcpath, String despPath, String imgType) {
        FileInputStream is = null;
        ImageInputStream iis = null;
        try {
            is = new FileInputStream(srcpath);
            // 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader
            // 声称能够解码指定格式。 参数：formatName - 包含非正式格式名称 .
            // （例如 "jpeg" 或 "tiff"）等 。
            Iterator<ImageReader> it = ImageIO
                    .getImageReadersByFormatName(imgType);
            ImageReader reader = it.next();
            iis = ImageIO.createImageInputStream(is);
            reader.setInput(iis, true);
            ImageReadParam param = reader.getDefaultReadParam();
            Rectangle rect = new Rectangle(x, y, width, height);
            param.setSourceRegion(rect);
            BufferedImage bi = reader.read(0, param);
            ImageIO.write(bi, imgType, new File(despPath));
        } catch (Exception e) {
            log.error("errorMsg:"+e.getMessage());
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("errorMsg:"+e.getMessage());
                }
            if (iis != null)
                try {
                    iis.close();
                } catch (IOException e) {
                    log.error("errorMsg:"+e.getMessage());
                }
        }
    }

    /**
     * 注：2010.02.26 duguoc将该方法由private改为public 比例缩放图片
     *
     * @param sourcePath
     * @param outputFolderPath
     *            目标文件夹
     * @param despPath
     *            目标路径
     * @param height
     * @param width
     * @throws Exception
     */
    public static void chageImageSize(String sourcePath,
                                      String outputFolderPath, String despPath, String imgType,
                                      int height, int width) throws Exception {
        sourcePath = CmsUtil.replaceSeparator(sourcePath);
        double Ratio = 0.0;
        File sourceFile = new File(sourcePath);
        Icon ret = new ImageIcon(sourcePath);
        BufferedImage Bi = ImageIO.read(sourceFile);
        if ((Bi.getHeight() > height) || (Bi.getWidth() > width)) {
            if (Bi.getHeight() > height) {
                Ratio = (new Integer(height)).doubleValue() / Bi.getHeight();
            } else {
                Ratio = (new Integer(width)).doubleValue() / Bi.getWidth();
            }
            File ThF = new File(despPath);
            Image Itemp = Bi.getScaledInstance(width, height, Bi.SCALE_SMOOTH);
            AffineTransformOp op = new AffineTransformOp(AffineTransform
                    .getScaleInstance(Ratio, Ratio), null);
            Itemp = op.filter(Bi, null);
            try {
                com.wondertek.mobilevideo.core.util.FileUtil.checkDirExists(outputFolderPath);
                ImageIO.write((BufferedImage) Itemp, imgType, ThF);
            } catch (Exception ex) {

            }
        }
        return;
    }

    /**
     * 注：2010.02.26 duguoc将该方法由private改为public 比例缩放图片 如果原图片的高比目标图片的高要大，则以高来缩小
     * 如果原图片的高比目标图片的高要小，则以宽来放大
     *
     * @param sourcePath
     * @param outputFolderPath
     *            目标文件夹
     * @param despPath
     *            目标路径
     * @param height
     * @param width
     * @throws Exception
     *             0:默认情况返回 1：成功 2：太小了 3：转换失败
     */
    public static boolean changeImageSize(String sourcePath,
                                          String outputFolderPath, String despPath, String imgType,
                                          int height, int width) throws Exception {
        sourcePath = CmsUtil.replaceSeparator(sourcePath);
        double Ratio = 0.0;
        File sourceFile = new File(sourcePath);
        Icon ret = new ImageIcon(sourcePath);
        BufferedImage Bi = ImageIO.read(sourceFile);
        if (Bi.getHeight() > height) {
            Ratio = (new Integer(height)).doubleValue() / Bi.getHeight();
        } else {
            Ratio = (new Integer(width)).doubleValue() / Bi.getWidth();
        }
        File ThF = new File(despPath);
        Image Itemp = Bi.getScaledInstance(width, height, Bi.SCALE_REPLICATE  );
        AffineTransformOp op = new AffineTransformOp(AffineTransform
                .getScaleInstance(Ratio, Ratio), null);
        Itemp = op.filter(Bi, null);
        try {
            com.wondertek.mobilevideo.core.util.FileUtil.checkDirExists(outputFolderPath);
            boolean isSuc = ImageIO.write((BufferedImage) Itemp, imgType, ThF);
            return isSuc;
        } catch (Exception ex) {
            log.error("errorMsg:"+ex.getMessage());
            return false;
        }
    }

    public static BufferedImage gray(BufferedImage bi) {
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp op = new ColorConvertOp(cs, null);
        bi = op.filter(bi, null);
        return bi;
    }

    public static  void rotateImage(String src,String dest, String imageType, int rotateAngle){
        FileSeekableStream stream = null;
        try {
            stream = new FileSeekableStream(src);
        } catch (IOException e) {
            log.error("errorMsg:"+e.getMessage());
            System.exit(0);
        }

		/* Create an operator to decode the image file. */
        RenderedOp image = JAI.create("stream", stream);
		/*
		 * Create a standard bilinear interpolation object to be used with the
		 * "scale " operator.
		 */
        Interpolation interp = Interpolation
                .getInstance(Interpolation.INTERP_BILINEAR);

        int value = rotateAngle;
        float angle = (float) (value * (Math.PI / 180.0F));

        // Create a ParameterBlock and specify the source and
        // parameters
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(image); // The source image//******************replace im
        // with image****************
        pb.add(0.0F); // The x origin
        pb.add(0.0F); // The y origin
        pb.add(angle); // The rotation angle
        pb.add(new InterpolationNearest()); // The interpolation

        // Create the scale operation
        RenderedOp im = JAI.create("Rotate", pb, null);// **************************************************create
        // an instance im of
        // type
        // RenderedOp********
        try {
            ImageIO.write(im.getAsBufferedImage(), imageType, new File(dest));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            log.error("errorMsg:"+e.getMessage());
        }

    }

    /**
     *  根据原图生成缩略图
     * @param originalFile
     * @param thumbnailFile
     * @param thumbWidth
     * @param thumbHeight
     * @param quality
     * @throws Exception
     */
    public void transform(String originalFile, String thumbnailFile, int thumbWidth, int thumbHeight, int quality) throws Exception
    {
        Image image = javax.imageio.ImageIO.read(new File(originalFile));

        double thumbRatio = (double)thumbWidth / (double)thumbHeight;
        int imageWidth    = image.getWidth(null);
        int imageHeight   = image.getHeight(null);
        double imageRatio = (double)imageWidth / (double)imageHeight;
        if (thumbRatio < imageRatio)
        {
            thumbHeight = (int)(thumbWidth / imageRatio);
        }
        else
        {
            thumbWidth = (int)(thumbHeight * imageRatio);
        }

        if(imageWidth < thumbWidth && imageHeight < thumbHeight)
        {
            thumbWidth = imageWidth;
            thumbHeight = imageHeight;
        }
        else if(imageWidth < thumbWidth)
            thumbWidth = imageWidth;
        else if(imageHeight < thumbHeight)
            thumbHeight = imageHeight;

        BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = thumbImage.createGraphics();
        graphics2D.setBackground(Color.WHITE);
        graphics2D.setPaint(Color.WHITE);
        graphics2D.fillRect(0, 0, thumbWidth, thumbHeight);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);

        javax.imageio.ImageIO.write(thumbImage, "JPG", new File(thumbnailFile));
    }

//    public static void main(String[] args) {
//        try {
//            File f = new File("C:/data/16xw0216ljzb06_V34_sc.jpg");
//            BufferedImage bufferedImage = ImageIO.read(f);
//            int w = bufferedImage.getWidth();
//            int h = bufferedImage.getHeight();
//            System.out.println("width:" + w + "height: " + h);
//        } catch (IOException e) {
//            System.out.println("***************88889900");
//            e.printStackTrace();
//        }
//
//    }



}