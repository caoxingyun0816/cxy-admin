package com.wondertek.mam.util.others;

import com.wondertek.mam.model.ImageStrategy;
import com.wondertek.mam.model.StarImage;
import com.wondertek.mam.util.ST;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class C2SFileTools {
	private static final Log log = LogFactory.getLog(C2SFileTools.class);
	public static String IMG_TYPE_NAME = "JPEG";

	public static class C2SFileToolsParams {
		public static final String STAR_IMG_STORE = "store";
		public static final String STAR_IMG_DEPOSITORY = "depository";
	}

	public static class CharEncoding {
		public static final String UTF8 = "UTF-8";
		public static final String GBK = "GBK";
		public static final String GB2312 = "GB2312";
		public static final String ISO88591 = "ISO-8859-1";
		public static final Map<String, String> encodeMap = new HashMap<String, String>() {
			{
				put(UTF8, UTF8);
				put(GBK, GBK);
				put(GB2312, GB2312);
				put(ISO88591, ISO88591);
			}
		};
		public static final List<String> encodeList = new ArrayList<String>() {
			{
				add(UTF8);
				add(GBK);
				add(GB2312);
				add(ISO88591);
			}
		};

	}

	/**
	 * 根据图片实际宽高,最大裁剪出符合参数比例的图片,并返回路径
	 *
	 * @param srcFile
	 *            图片源文件
	 * @param distFile
	 *            图片目标文件
	 * @param widthScale
	 *            图片宽度比,如: 4
	 * @param heightScale
	 *            图片高度比,如: 3
	 * @return 裁剪后图片 路径
	 */
	public static File formatImageScale(File srcFile, File distFile, int widthScale, int heightScale,
			String imageSuffix) throws IOException {
		if (srcFile == null || !srcFile.exists() || distFile == null)
			return null;
		if (widthScale <= 0 || heightScale <= 0) { // 默认 宽高比 4:3
			widthScale = 4;
			heightScale = 3;
		}
		if (imageSuffix == null || "".equals(imageSuffix.trim())) {
			imageSuffix = getFileSuffix(srcFile);
		}
		InputStream is = null;
		BufferedImage srcImage = null;
		File resultFile = null;
		try {
			is = new FileInputStream(srcFile);
			srcImage = ImageIO.read(is);
			Map<String, Integer> imageScopeMap = getProperImageSize(srcImage, widthScale, heightScale);
			// if (distFile.exists()) {
			// distFile.delete();
			// }
			String resPath = imgCut(srcFile.getPath(), distFile.getPath(), imageScopeMap.get("x"),
					imageScopeMap.get("y"), imageScopeMap.get("w"), imageScopeMap.get("h"), imageSuffix);
			resultFile = new File(resPath);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException(
					"C2SFileTools.formatImageScale: Image srcFile or distFile convert to Stream failure!"
							+ e.getMessage());
		} catch (IOException e) {
			throw new IOException("C2SFileTools.formatImageScale: image parse failure !", e);
		} finally {
			if (srcImage != null) {
				srcImage.flush();
			}
			if (is != null) {
				is.close();
			}
		}
		return resultFile.exists() ? resultFile : null;
	}

	/**
	 * 根据 width,height 缩放srcFile ,存入disFile
	 *
	 * @param srcFile
	 *            待缩放文件
	 * @param distFile
	 *            缩放后保存位置文件
	 * @param width
	 *            缩放后的宽度
	 * @param height
	 *            缩放后的高度
	 * @return 缩放后的图片文件 路径 // 默认输出的图片类型为IMG_TYPE_NAME 当前为"JPEG"
	 */
	public static File zoomImageScale(File srcFile, File distFile, int width, int height, String distFileName,
			String imageSuffix, boolean assemblable) throws IOException {
		if (!(srcFile != null && srcFile.exists() && srcFile.isFile() && distFile != null))
			return null;

		if (assemblable) {
			StringBuilder pathBuilder = new StringBuilder(distFile.getPath());
			if (distFile.getPath().contains(".") || distFile.getPath().endsWith(File.separator)) {
				pathBuilder.insert(distFile.getPath().lastIndexOf(File.separator),
						File.separator + width + "_" + height);
			} else {
				pathBuilder.append(File.separator + width + "_" + height + File.separator);
			}
			distFile = new File(pathBuilder.toString());
		}
		if (!distFile.getPath().contains(".")) {
			String tempFileName = distFileName != null && !"".equals(distFileName.trim()) ? distFileName
					: srcFile.getName();
			distFile = new File(distFile.getPath().endsWith(File.separator) ? distFile.getPath() + tempFileName
					: distFile.getPath() + File.separator + tempFileName);
		}
		if (!distFile.getParentFile().exists()) {
			distFile.getParentFile().mkdirs();
		}
		if (imageSuffix == null || "".equals(imageSuffix.trim())) {
			imageSuffix = getFileSuffix(srcFile);
		}
		try {
			BufferedImage srcImageBuffer = ImageIO.read(srcFile);
			int imageWidth = srcImageBuffer.getWidth();
			int imageHeight = srcImageBuffer.getHeight();
			srcImageBuffer.flush();
			File srcImageFile = srcFile;
			if (((double) imageWidth / imageHeight - (double) width / height) != 0) {
				File tempFile = formatImageScale(srcFile, srcFile, width, height, imageSuffix);
				if (tempFile != null) {
					srcImageFile = tempFile;
				}
			}
			BufferedImage srcBuffer = ImageIO.read(srcImageFile); // 将图片读入内存
			Image image = srcBuffer.getScaledInstance(width, height, Image.SCALE_DEFAULT);
			BufferedImage distBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // 声明内存空画布
			Graphics g = distBuffer.getGraphics();
			g.drawImage(image, 0, 0, null); // 绘制缩小后的图
			g.dispose();
			image.flush();
			ImageIO.write(distBuffer, imageSuffix, distFile);// 输出到文件流
			srcBuffer.flush();
			distBuffer.flush();
			return distFile;
		} catch (IOException e) {
			throw new IOException("图片缩放失败!", e);
		}
	}

	/**
	 * 等比例(scale) 缩放图片
	 *
	 * @param scale
	 *            缩放比例
	 * @param imageSuffix
	 *            生成图片后缀
	 */
	public static File zoomImageScale(File srcFile, File distFile, double scale, String imageSuffix)
			throws IOException {
		if (!(srcFile != null && srcFile.exists() && srcFile.isFile() && distFile != null))
			return null;
		if (scale < 1) {
			scale = 1;
		}

		if (!distFile.exists()) {
			distFile.getParentFile().mkdirs();
		}
		try {
			BufferedImage srcImageBuffer = ImageIO.read(srcFile);
			int imageWidth = (int) (srcImageBuffer.getWidth() / scale);
			int imageHeight = (int) (srcImageBuffer.getHeight() / scale);
			srcImageBuffer.flush();

			BufferedImage srcBuffer = ImageIO.read(srcFile); // 将图片读入内存
			Image image = srcBuffer.getScaledInstance(imageWidth, imageHeight, Image.SCALE_DEFAULT);
			BufferedImage distBuffer = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB); // 声明内存空画布
			Graphics g = distBuffer.getGraphics();
			g.drawImage(image, 0, 0, null); // 绘制缩小后的图
			g.dispose();
			image.flush();
			ImageIO.write(distBuffer, imageSuffix != null ? imageSuffix : C2SFileTools.getFileSuffix(srcFile),
					distFile);// 输出到文件流
			srcBuffer.flush();
			distBuffer.flush();
			return distFile;
		} catch (IOException e) {
			throw new IOException("图片缩放失败!", e);
		}
	}

	public static File zoomImageScale(File srcFile, File distFile, int w, int h, String imageSuffix)
			throws IOException {
		if (!(srcFile != null && srcFile.exists() && srcFile.isFile() && distFile != null && w > 0 && h > 0))
			return null;

		if (!distFile.exists()) {
			distFile.getParentFile().mkdirs();
		}
		try {
			BufferedImage srcBuffer = ImageIO.read(srcFile); // 将图片读入内存
			Image image = srcBuffer.getScaledInstance(w, h, Image.SCALE_DEFAULT);
			BufferedImage distBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB); // 声明内存空画布
			Graphics g = distBuffer.getGraphics();
			g.drawImage(image, 0, 0, null); // 绘制缩小后的图
			g.dispose();
			image.flush();
			ImageIO.write(distBuffer, imageSuffix, distFile);// 输出到文件流
			srcBuffer.flush();
			distBuffer.flush();
			return distFile;
		} catch (IOException e) {
			throw new IOException("图片缩放失败!", e);
		}
	}

	public static Map<String, String> zoomImageScale1(File srcFile, File distFile, List<ImageStrategy> strategies,
			boolean assemblable) throws IOException {
		if (strategies == null || strategies.size() <= 0)
			return null;
		Map<String, String> resultMap = new HashMap<String, String>();

		if (strategies != null && strategies.size() > 0) {
			for (ImageStrategy strategy : strategies) {
				String suffix = C2SFileTools.getFileSuffix(srcFile);
				String distFileName = strategy.getType() + "_" + strategy.getWidth() + "_" + strategy.getHeight() + "."
						+ suffix;
				File file = zoomImageScale(srcFile, distFile, strategy.getWidth(), strategy.getHeight(), distFileName,
						suffix, assemblable);
				if (file != null) {
					resultMap.put("scale", strategy.getWidth() + "_" + strategy.getHeight());
					resultMap.put("path", file.getPath());
				}
			}
		}
		return resultMap;
	}

	public static StarImage zoomImageScale(File srcFile, File distFile, StarImage sImg, final String depositoryPath,
			List<ImageStrategy> strategies, boolean assemblable, boolean containSrcImg) throws IOException {
		StarImage starImage = sImg;
		String imgId = (sImg != null && sImg.getId() != null) ? String.valueOf(sImg.getId()) : "";
		final String imgName = sImg != null ? sImg.getName() : "";
		if (strategies != null && strategies.size() > 0) {
			for (ImageStrategy strategy : strategies) {
				String suffix = C2SFileTools.getFileSuffix(srcFile);
				String distFileName = strategy.getType() + "_" + imgId + "_" + strategy.getWidth() + "_"
						+ strategy.getHeight() + "." + suffix;
				File file = zoomImageScale(srcFile, distFile, strategy.getWidth(), strategy.getHeight(), distFileName,
						suffix, assemblable);
				final int strategyWidth = strategy.getWidth();
				final int strategyHeight = strategy.getHeight();
				if (file != null) {
					final String filePath = file.getPath();
					starImage.getScaleImgs().add(new HashMap<String, String>() {
						{
							put("name", imgName);
							put("src", "0");
							put("scale", strategyWidth + "_" + strategyHeight);
							put("path", filePath.substring(filePath.indexOf(depositoryPath)));
						}
					});
				}
			}
		}
		if (containSrcImg) {
			BufferedImage bi = ImageIO.read(srcFile);
			final int w = bi.getWidth(), h = bi.getHeight();
			String distFilePath = distFile.getPath() + File.separator
					+ "star_src_".concat(imgId).concat("_").concat(String.valueOf(w)).concat("_")
							.concat(String.valueOf(h)).concat(".").concat(C2SFileTools.getFileSuffix(srcFile));
			File dFile = new File(distFilePath);
			if (!dFile.getParentFile().exists()) {
				dFile.getParentFile().mkdirs();
			}
			final File resultFile = saveOverFile(srcFile, dFile);
			starImage.getScaleImgs().add(new HashMap<String, String>() {
				{
					put("name", imgName);
					put("src", "1");
					put("scale", String.valueOf(w).concat("_").concat(String.valueOf(h)));
					String fp = resultFile.getPath();
					put("path", fp.substring(fp.indexOf(depositoryPath)));
				}
			});
			// 处理头像
			if (sImg.getType() == 1 || sImg.getType() == 5 || sImg.getType() == 0) {
				sImg.setPath(distFilePath.substring(distFilePath.indexOf(depositoryPath)));
			} else {
				sImg.setPath(null);
			}
		}

		return starImage;
	}

	public static StarImage zoomImageScaleForPreview(File srcFile, File distFile, StarImage sImg,
			final String depositoryPath, List<ImageStrategy> strategies, boolean assemblable, boolean containSrcImg)
					throws IOException {
		StarImage starImage = sImg;
		String imgId = (sImg != null && sImg.getId() != null) ? String.valueOf(sImg.getId()) : "";
		final String imgName = sImg != null ? sImg.getName() : "";

		if (strategies != null && strategies.size() > 0) {
			for (ImageStrategy strategy : strategies) {
				String suffix = C2SFileTools.getFileSuffix(srcFile);
				String distFileName = strategy.getType() + "_" + imgId + "_" + strategy.getWidth() + "_"
						+ strategy.getHeight() + "." + suffix;
				final int strategyWidth = strategy.getWidth();
				final int strategyHeight = strategy.getHeight();
				final String filePath = distFile.getPath() + File.separator + distFileName;
				if (!ST.isNull(filePath)) {
					starImage.getScaleImgs().add(new HashMap<String, String>() {
						{
							put("name", imgName);
							put("src", "0");
							put("scale", strategyWidth + "_" + strategyHeight);
							put("path", filePath.substring(filePath.indexOf(depositoryPath)));
						}
					});
				}
			}
		}
		if (containSrcImg) {
			BufferedImage bi = ImageIO.read(srcFile);
			final int w = bi.getWidth(), h = bi.getHeight();
			bi.flush();
			final String distFilePath = distFile.getPath() + File.separator
					+ "star_src_".concat(imgId).concat("_").concat(String.valueOf(w)).concat("_")
							.concat(String.valueOf(h)).concat(".").concat(C2SFileTools.getFileSuffix(srcFile));
			starImage.getScaleImgs().add(new HashMap<String, String>() {
				{
					put("name", imgName);
					put("src", "1");
					put("scale", String.valueOf(w).concat("_").concat(String.valueOf(h)));
					String fp = distFilePath;
					put("path", fp.substring(fp.indexOf(depositoryPath)));
				}
			});

			// 处理头像
			if (sImg.getType() == 1 || sImg.getType() == 5 || sImg.getType() == 0) {
				sImg.setPath(distFilePath.substring(distFilePath.indexOf(depositoryPath)));
			} else {
				sImg.setPath(null);
			}
		}

		return starImage;
	}

	public static Map<String, Integer> getProperImageSize(BufferedImage srcImageBuffer, int width, int height) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		int imageWidth = srcImageBuffer.getWidth();
		int imageHeight = srcImageBuffer.getHeight();
		int w = imageWidth;
		int h = imageHeight;
		int x = 0;
		int y = 0;
		if (imageWidth - h * width / height > 0) {
			w = h * width / height;
			x = (imageWidth - w) / 2;
		} else {
			h = w * height / width;
			y = (imageHeight - h) / 2;
		}
		result.put("w", w);
		result.put("h", h);
		result.put("x", x);
		result.put("y", y);
		srcImageBuffer.flush();
		return result;
	}

	public static String imgCut(String srcpath, String subpath, int x, int y, int width, int height, double scaleWidth,
			double scaleHeight) throws IOException {
		if (scaleHeight > 0 && scaleWidth > 0) {
			BufferedImage srcImageBuffer = ImageIO.read(new File(srcpath));
			int imageWidth = srcImageBuffer.getWidth();
			int imageHeight = srcImageBuffer.getHeight();
			srcImageBuffer.flush();
			x = (int) (((double) x) * imageWidth / scaleWidth);
			y = (int) (((double) y) * imageHeight / scaleHeight);
		}
		return imgCut(srcpath, subpath, x, y, width, height);

	}

	/**
	 * 复制
	 * 
	 * @param url1
	 * @param url2
	 * @throws Exception
	 */
	public static void copy(String url1, String url2) throws Exception {
		FileInputStream in = new FileInputStream(new File(url1));
		FileOutputStream out = new FileOutputStream(new File(url2));
		byte[] buff = new byte[512];
		int n = 0;
		System.out.println("复制文件：" + "\n" + "源路径：" + url1 + "\n" + "目标路径：" + url2);
		while ((n = in.read(buff)) != -1) {
			out.write(buff, 0, n);
		}
		out.flush();
		in.close();
		out.close();
		System.out.println("复制完成");
	}

	public static String imgCutAndScale(String srcpath, String subpath, int x, int y, int width, int height,
			int genuineWidth, int genuineHeight) throws IOException {
		String subP = null;
		File f = new File(srcpath);
		BufferedImage reader = ImageIO.read(f);
		BufferedImage bi = reader.getSubimage(x, y, width, height);
		String fileName = f.getName();
		String formatName = fileName.substring(fileName.lastIndexOf('.') + 1);
		File subFile = new File(subpath);
		if (!subFile.getParentFile().exists())
			subFile.getParentFile().mkdirs();
		Image image = bi.getScaledInstance(genuineWidth, genuineHeight, Image.SCALE_DEFAULT);
		BufferedImage distBuffer = new BufferedImage(genuineWidth, genuineHeight, BufferedImage.TYPE_INT_RGB); // 声明内存空画布
		Graphics g = distBuffer.getGraphics();
		g.drawImage(image, 0, 0, null); // 绘制缩小后的图
		g.dispose();
		image.flush();
		ImageIO.write(distBuffer, formatName, subFile);// 输出到文件流
		distBuffer.flush();
		bi.flush();
		subP = subpath;
		return subP;
	}

	/***
	 * 格式转换为jpg
	 * 
	 * @param ff
	 *            图片路径
	 * @return
	 */
	public static String change2jpg(String ff) {
		BufferedImage bufferedImage;
		try {
			// 1.读取图片
			bufferedImage = ImageIO.read(new File(ff));
			// 2.创建一个空白大小相同的RGB背景
			BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
					BufferedImage.TYPE_INT_RGB);
			newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
			// 3.重命名
			StringBuilder sb = new StringBuilder();
			File f = new File(ff);
			File temp = f;
			while (temp.getParentFile() != null && temp.getParentFile().getName().length() != 0) {
				sb.insert(0, "/" + temp.getParentFile().getName());
				temp = temp.getParentFile();
			}
			sb.append("/");
			log.info("转换路径:" + sb);
			// 截取图片名称
			File tempFile = new File(ff.trim());
			String fileName = tempFile.getName();
			log.info("转换名称" + fileName);
			// 截取图片点缀如.jpg
			String dName = ".jpg";
			// 截取点缀前面的name
			String tuName = fileName.substring(0, fileName.length() - 4);
			System.out.println(tuName);
			// 图片name重组
			String fff = sb + tuName + dName;
			log.info("重组name:" + fff);
			// 4.再次写入的时候以jpeg图片格式
			ImageIO.write(newBufferedImage, "jpg", new File(fff));
			ff = fff;
			log.info("成功将图片转换为jpg格式");

		} catch (IOException e) {

			e.printStackTrace();

		}
		return ff;
	}

	public static boolean processImg(String veido_path, String ffmpeg_path,String imgPath,int offset,String width,String height) {
		File file = new File(veido_path);
		if (!file.exists()) {
			log.info("路径：" + veido_path + "相对应的视频文件不存在");
			return false;
		}
		List<String> commands = new java.util.ArrayList<String>();
		commands.add(ffmpeg_path);
		commands.add("-i");
		commands.add(veido_path);
		/*commands.add("-vframes");
		commands.add("1");
		commands.add("-r");
		commands.add("1");
		commands.add("-ac");
		commands.add("1");
		commands.add("-ab");
		commands.add("2");*/
		commands.add("-y");
		commands.add("-f");
		commands.add("image2");
		commands.add("-ss");
		//commands.add(String.valueOf(offset));
		commands.add("8");
		commands.add("-s");
		commands.add(width+"*"+height);
		commands.add(imgPath);
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(commands);
			builder.start();
			log.info("截取成功！");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;

	}

	/**
	 * 图片裁剪
	 * 
	 * @param srcImageFile
	 *            图片裁剪地址
	 * @param result
	 *            图片输出文件夹
	 * @param destWidth
	 *            图片裁剪宽度
	 * @param destHeight
	 *            图片裁剪高度
	 */
	public static void cutImage(String srcImageFile, String result, int destWidth, int destHeight) {
		try {
			Iterator iterator = ImageIO
					.getImageReadersByFormatName("JPEG");/* PNG,BMP */
			ImageReader reader = (ImageReader) iterator.next();/* 获取图片尺寸 */
			InputStream inputStream = new FileInputStream(srcImageFile);
			ImageInputStream iis = ImageIO.createImageInputStream(inputStream);
			reader.setInput(iis, true);
			ImageReadParam param = reader.getDefaultReadParam();
			Rectangle rectangle = new Rectangle(0, 0, destWidth, destHeight);/* 指定截取范围 */
			param.setSourceRegion(rectangle);
			BufferedImage bi = reader.read(0, param);
			ImageIO.write(bi, "JPEG", new File(result));
			log.info("图片裁剪成功");
		} catch (Exception e) {
			log.error("图片裁剪出现异常:" + e);

		}

	}

	public static String imgCut(String srcpath, String subpath, int x, int y, int width, int height)
			throws IOException {
		String subP = null;
		File f = new File(srcpath);
		BufferedImage reader = ImageIO.read(f);
		BufferedImage bi = reader.getSubimage(x, y, width, height);
		String fileName = f.getName();
		String formatName = fileName.substring(fileName.lastIndexOf('.') + 1);
		File subFile = new File(subpath);
		if (!subFile.getParentFile().exists())
			subFile.getParentFile().mkdirs();
		ImageIO.write(bi, formatName, subFile);
		bi.flush();
		subP = subpath;
		return subP;
	}

	public static String imgCut(String srcpath, String subpath, int x, int y, int width, int height, String imageSuffix)
			throws IOException {
		if (imageSuffix == null || "".equals(imageSuffix.trim())) {
			imageSuffix = getFileSuffix(srcpath);
		}
		String subP = "";
		FileInputStream is = null;
		ImageInputStream iis = null;
		try {
			is = new FileInputStream(srcpath);
			String suffix = srcpath.substring(srcpath.lastIndexOf(".") + 1);
			Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(suffix);
			ImageReader reader = it.next();
			iis = ImageIO.createImageInputStream(is);
			reader.setInput(iis, true);
			ImageReadParam param = reader.getDefaultReadParam();
			Rectangle rect = new Rectangle(x, y, width, height);
			param.setSourceRegion(rect);
			BufferedImage bi = reader.read(0, param);
			File subFile = new File(subpath);
			if (!subFile.getParentFile().exists())
				subFile.getParentFile().mkdirs();
			ImageIO.write(bi, imageSuffix, subFile);
			bi.flush();
			subP = subpath;
		} finally {
			if (is != null)
				is.close();
			if (iis != null)
				iis.close();
		}
		return subP;
	}

	/**
	 * 图片加水印
	 *
	 * @param imageFilePath
	 *            图片文件路径
	 * @param waterMarkContent
	 *            水印文字内容
	 * @param contentFont
	 *            水印 字体.大小等 例:new Font("宋体",Font.PLAIN,20)
	 */
	public void imageWaterMark(String imageFilePath, String waterMarkContent, Font contentFont, String imageSuffix) {
		File srcFile = new File(imageFilePath);
		if (!srcFile.exists())
			return;
		if (imageSuffix == null || "".equals(imageSuffix.trim())) {
			imageSuffix = getFileSuffix(srcFile);
		}
		try {
			Image src = ImageIO.read(srcFile);
			int wideth = src.getWidth(null);
			int height = src.getHeight(null);
			BufferedImage image = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.createGraphics();
			g.drawImage(src, 0, 0, wideth, height, null);
			g.setColor(Color.RED);
			g.setFont(contentFont);
			g.drawString(waterMarkContent, wideth - 150, height - 10);
			g.dispose();
			FileOutputStream out = new FileOutputStream(imageFilePath);
			ImageIO.write(image, imageSuffix, out);// 输出到文件流
			image.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 图片加水印
	 *
	 * @param imageFilePath
	 *            图片文件路径
	 * @param waterMarkImageFile
	 *            水印图片内容
	 * @param contentFont
	 *            水印 字体.大小等 例:new Font("宋体",Font.PLAIN,20)
	 */
	public void imageWaterMark(String imageFilePath, File waterMarkImageFile, Font contentFont, String imageSuffix) {
		File srcFile = new File(imageFilePath);
		if (waterMarkImageFile == null || !waterMarkImageFile.exists())
			return;
		if (!srcFile.exists())
			return;
		if (imageSuffix == null || "".equals(imageSuffix.trim())) {
			imageSuffix = getFileSuffix(srcFile);
		}
		try {
			Image src = ImageIO.read(srcFile);
			int wideth = src.getWidth(null);
			int height = src.getHeight(null);
			BufferedImage image = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.createGraphics();
			g.drawImage(src, 0, 0, wideth, height, null);
			Image imageLogo = ImageIO.read(waterMarkImageFile);
			int wideth_logo = imageLogo.getWidth(null);
			int height_logo = imageLogo.getHeight(null);
			g.drawImage(imageLogo, (wideth - wideth_logo) / 2, (height - height_logo) / 2, wideth_logo, height_logo,
					null);
			g.dispose();
			FileOutputStream out = new FileOutputStream(imageFilePath);
			ImageIO.write(image, imageSuffix, out);// 输出到文件流
			image.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 浏览器下载文件
	 *
	 * @param srcFile
	 *            将要下载的文件File对象
	 * @param response
	 *            HttpServletResponse
	 * @param request
	 *            HttpServletRequest
	 * @param fileName
	 *            指定文件下载到客户端后的文件名; 可设为null,则默认使用srcFile的文件名
	 */
	public static boolean downloadFile(File srcFile, HttpServletResponse response, HttpServletRequest request,
			String fileName) {
		boolean flag = false;
		if (fileName == null) {
			fileName = srcFile.getName();
		}
		try {
			flag = downloadFile(new FileInputStream(srcFile), response, request, fileName);
		} catch (FileNotFoundException e) {
			log.error("C2SFileTools.downloadFile() : 下载源文件没有找到！", e);
		}
		return flag;
	}

	public static boolean downloadFile(InputStream is, HttpServletResponse response, HttpServletRequest request,
			String fileName) {
		boolean flag = false;
		if (response == null || is == null) {
			return flag;
		}
		response.setContentType("multipart/form-data");
		try {
			if (request.getHeader("User-agent") != null
					&& request.getHeader("User-agent").toLowerCase().contains("firefox")) {

				response.setHeader("Content-Disposition", "attachment;fileName=" + MimeUtility.encodeWord(fileName));
			} else {
				response.setHeader("Content-Disposition",
						"attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			log.error("C2SFileTools.downloadFile() : 文件名解码失败，不支持的编码类型", e);
		}
		OutputStream os = null;
		try {
			os = response.getOutputStream();
			int len = 0;
			byte[] data = new byte[1024];
			while ((len = is.read(data)) != -1) {
				os.write(data, 0, len);
			}
			flag |= true;
		} catch (IOException e) {
			log.error("C2SFileTools.downloadFile() : 文件名解码失败，不支持的编码类型", e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.flush();
					os.close();
					flag &= true;
				}
			} catch (IOException e) {
				log.error("C2SFileTools.downloadFile() : 文件输入输出流关闭失败", e);
			}
		}
		return flag;
	}

	public static List<File> saveFiles(File[] srcFiles, File[] distFile) throws IOException {
		List<File> resl = new ArrayList<File>();
		if (srcFiles == null || distFile == null || srcFiles.length == 0)
			return resl;
		for (int i = 0; i < srcFiles.length && i < distFile.length; i++) {
			resl.add(saveOverFile(srcFiles[i], distFile[i]));
		}
		return resl;
	}

	/**
	 * 覆盖(如果文件已存在) 拷贝文件
	 */
	public static File saveOverFile(File srcFile, File distFile) {
		if (srcFile == null || distFile == null)
			return null;
		if (distFile.exists())
			removeFiles(distFile, null);
		return saveFile(srcFile, distFile);
	}

	/**
	 * [不拷贝(如果文件已存在)] 拷贝文件
	 */
	public static File saveFile(File srcFile, File distFile) {
		if (srcFile == null || distFile == null)
			return null;
		if (!distFile.getParentFile().exists())
			distFile.getParentFile().mkdirs();

		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(srcFile);
			os = new FileOutputStream(distFile);
			int len = 0;
			byte[] data = new byte[1024];
			while ((len = is.read(data)) != -1) {
				os.write(data, 0, len);
			}
		} catch (IOException e) {
			log.error("C2SFileTools.saveFile():拷贝文件失败！", e);
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					log.error("C2SFileTools.saveFile():输入流关闭失败！", e);
				}
			if (os != null)
				try {
					os.close();
				} catch (IOException e) {
					log.error("C2SFileTools.saveFile():关闭流关闭失败！", e);
				}
		}
		return distFile;
	}

	public static boolean saveFile(InputStream is, File distFile) {
		boolean flag = false;
		if (!distFile.getParentFile().exists()) {
			distFile.getParentFile().mkdirs();
		}
		try {
			saveFile(is, new FileOutputStream(distFile));
			flag = true;
		} catch (FileNotFoundException e) {
			log.error("File : " + distFile.getPath() + "can be found !", e);
		}
		return flag;
	}

	public static void saveFile(InputStream is, OutputStream os) {
		if (os == null || is == null)
			return;
		try {
			int len = 0;
			byte[] data = new byte[1024];
			while ((len = is.read(data)) != -1) {
				os.write(data, 0, len);
			}
		} catch (IOException e) {
			log.error("C2SFileTools.saveFile():拷贝文件失败！", e);
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					log.error("C2SFileTools.saveFile():输入流关闭失败！", e);
				}
			if (os != null)
				try {
					os.close();
				} catch (IOException e) {
					log.error("C2SFileTools.saveFile():关闭流关闭失败！", e);
				}
		}
	}

	public static boolean removeFile(File file) {
		if (file == null || !file.exists()) {
			return false;
		}
		return file.delete();
	}

	/**
	 * 删除文件 或 文件夹(及其下所有文件) (向下级删除 与 deleteFileTree 向上级删除 方向相反)
	 *
	 * @param file
	 *            要删除的文件
	 * @param sBuilder
	 *            StringBuilder 记录删除文件成功或失败的个数;例如:
	 *            "1110101"-->成功删除5个(统计1的数量),失败2个(统计0的数量)
	 */
	public static void removeFiles(File file, StringBuilder sBuilder) {
		if (file == null || !file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				removeFiles(f, sBuilder);
			}
		}
		boolean i = file.delete();
		if (sBuilder != null) {
			sBuilder.append(i ? "1" : "0");
		}
	}

	/**
	 * 获取文件 后缀
	 */
	public static String getFileSuffix(String filePath) {
		String fileSuffixName = "";
		if (filePath != null && !"".equals(filePath.trim()) && filePath.contains(".")) {
			fileSuffixName = filePath.substring(filePath.lastIndexOf(".") + 1);
		}
		return fileSuffixName;
	}

	/**
	 * 获取文件 后缀
	 */
	public static String getFileSuffix(File file) {
		return getFileSuffix(file.getName());
	}

	/**
	 * 获取文件 名称
	 */
	public static String getFileName(String filePath) {
		return getFileName(new File(filePath));
	}

	/**
	 * 获取文件 名称
	 */
	public static String getFileName(File file) {
		String fileName = "";
		String filePath = "";
		if (file != null && !ST.isNull(filePath = file.getPath()) && filePath.contains(".")) {
			fileName = filePath.substring(0, filePath.lastIndexOf("."));
			fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
		}
		return fileName;
	}

	/**
	 * 根据id 生成存储 相对路径 结果形式如: "2000/1"
	 */
	public static String getStorePathById(Long starId, int offset) {
		StringBuilder idBuilder = new StringBuilder(String.valueOf(starId));
		idBuilder.insert(offset, File.separatorChar);
		return idBuilder.toString();
	}

	/**
	 * 根据id 生成存储 指定位数的相对路径 结果形式如: "0000/2000/1" id位数不足时,左侧补0
	 */
	public static String getPublishPathById(Long starId, int perOffset, int length) {
		StringBuilder idBuilder = new StringBuilder(String.valueOf(starId));
		for (int i = 0, len = length - idBuilder.length(); i < len; i++) {
			idBuilder.insert(0, "0");
		}
		if (idBuilder.length() > perOffset) {
			for (int i = 0, len = idBuilder.length(); i < len; i++) {
				if ((i + 1) % perOffset == 0) {
					idBuilder.insert(i + 1, File.separatorChar);
				}
			}
		}
		return idBuilder.toString();
	}

	/**
	 * 根据id 生成存储 指定位数的相对路径 结果形式如: starId:20001 --> offset:[4,3,3] -->
	 * "0000/020/001" id位数不足时,左侧补0
	 */
	public static String getPublishPathById(Long starId, int[] offset) {
		if (offset == null || starId == null)
			return null;
		// 反转 id 串
		StringBuilder idBuilder = new StringBuilder(String.valueOf(starId)).reverse();
		if (idBuilder.length() <= 0)
			return null;
		// 声明结果变量
		String[] results = new String[offset.length];
		StringBuilder tempSBuilder = new StringBuilder();
		// 倒序遍历offset(i倒序变量) 反转后的id串(r循环当前所在位置,从0起)(curr每次循环所在串的起始位置= 当前r +
		// offset[i],从0起)
		for (int i = offset.length - 1, curr = 0, r = 0; i >= 0; curr += offset[i], i--, r++) {
			String temp = idBuilder.toString();
			if (curr + offset[i] <= temp.length()) {
				results[r] = temp.substring(curr, curr + offset[i]);
			} else {
				tempSBuilder.delete(0, tempSBuilder.length());
				if (curr + 1 <= temp.length()) {
					tempSBuilder.append(temp.substring(curr, temp.length()));
					for (int j = 0; j < offset[i]; j++) {
						if (tempSBuilder.length() < offset[i]) {
							tempSBuilder.append("0");
						}
					}
				} else {
					for (int j = 0; j < offset[i]; j++) {
						tempSBuilder.append("0");
					}
				}

				results[r] = tempSBuilder.toString();
			}
		}
		idBuilder.delete(0, idBuilder.length());
		for (String res : results) {
			idBuilder.append(res).append(File.separator);
		}
		idBuilder.deleteCharAt(idBuilder.length() - 1);
		return idBuilder.reverse().toString();
	}

	/**
	 * 将人物临时图片拷贝到人物图片存储目录
	 *
	 * @param srcTempImgPath
	 *            临时图片路径
	 * @param starId
	 *            图片所属人物的Id
	 * @param type
	 *            图片保存 路径类型 {C2SFileToolsParams.STAR_IMG_STORE-->人物图片保存目录;
	 *            C2SFileToolsParams.STAR_IMG_DEPOSITORY-->人物图片发布目录}
	 * @param imgType
	 *            图片类型(可选，传入则拼入图片路径的名称末尾) ：
	 *            {0:头像(方图);1:头像(横图);2:剧照;3:生活照;4:写真;5:头像(竖图)}
	 * @return 拷贝后的图片路径
	 */
	public static String transferStarImage2Store(String srcTempImgPath, Long starId, String type, String imgType) {
		Map<String, String> starImgStorePaths;
		if (C2SFileToolsParams.STAR_IMG_STORE.equalsIgnoreCase(type)) {
			starImgStorePaths = new C2SFilePathTools().getPaths("starImageStore");
		} else {
			starImgStorePaths = new C2SFilePathTools().getPaths("starImgPublish");
		}
		if (starImgStorePaths == null || !starImgStorePaths.containsKey("a") || !starImgStorePaths.containsKey("r")) {
			log.error(
					"C2SFileTools.transferStarImage2Store(~): 人物图片 保存路径获取失败,执行new C2SFilePathTools().getPaths(\"starImageStore\")时出错！");
			return null;
		}
		String resultPath = null;
		File srcFile = new File(srcTempImgPath);
		if (!srcFile.exists()) {
			String imgPath = srcFile.getPath().substring(lastIndexOffset(srcFile.getPath(), File.separator, 4) + 1);
			resultPath = starImgStorePaths.get("r") + File.separator + imgPath;
			return resultPath;
		}
		if (!ST.isNull(imgType)) {
			imgType = "_".concat(imgType);
		} else {
			imgType = "";
		}
		// String distFilePath = starImgStorePaths.get("a") + File.separator +
		// C2SFileTools.getPublishPathById(starId, new int[]{4, 3, 3}) +
		// File.separator +
		// srcFile.getPath().substring(srcFile.getPath().lastIndexOf(File.separator)
		// + 1);
		String distFilePath = starImgStorePaths.get("a") + File.separator
				+ C2SFileTools.getPublishPathById(starId, new int[] { 4, 3, 3 }) + File.separator + C2SFileTools
						.getFileName(srcFile).concat(imgType).concat(".").concat(C2SFileTools.getFileSuffix(srcFile));
		File distFile = new File(distFilePath);
		File resultFile = saveOverFile(srcFile, distFile);
		deleteFileTree(srcFile);
		resultPath = resultFile.exists()
				? resultFile.getPath().substring(resultFile.getPath().indexOf(starImgStorePaths.get("r"))) : null;
		return resultPath;
	}

	/**
	 * 清理文件，删除此文件及其下所有文件，若此文件的上级(如a目录)只包含此文件,则a也会被清除,并向上上级依次类推 (向上级删除 包含
	 * removeFiles 向上级删除)
	 * 
	 * @param tempImgFile
	 */
	public static void deleteFileTree(File tempImgFile) {
		deleteFileTree(tempImgFile, null, 1);
	}

	private static void deleteFileTree(File tempImgFile, File childFile, int index) {
		if (index >= 50) {
			return;
		}
		if (tempImgFile == null || !tempImgFile.exists()) {
			return;
		}
		if ((!tempImgFile.isDirectory() && childFile == null)
				|| (tempImgFile.isDirectory() && tempImgFile.listFiles().length <= 1)) {
			File rmFile = tempImgFile.getParentFile();
			deleteFileTree(rmFile, tempImgFile, ++index);
		} else {
			C2SFileTools.removeFiles(childFile, null);
		}
	}

	public static HttpURLConnection postAction(String urlStr, String sessionId) {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true); // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true,
									// 默认情况下是false;
			conn.setUseCaches(false); // Post 请求不能使用缓存
			conn.setInstanceFollowRedirects(true);

			// conn.setRequestProperty("Content-type",
			// "application/x-java-serialized-object"); //声明可传送序列化modal对象
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestMethod("POST");
			if (!ST.isNull(sessionId))
				conn.setRequestProperty("Cookie", sessionId);
			// conn.connect();
			return conn;
		} catch (MalformedURLException e) {
			log.error("new URL(urlStr) 构造url异常！", e);
		} catch (ProtocolException e) {
			log.error("conn.setRequestMethod(\"POST\"):设置请求方式失败！", e);
		} catch (IOException e) {
			log.error("url --> openConnection 或 connect 出现异常！", e);
		}
		return null;
	}

	public static HttpURLConnection setHttpURLConnParams(HttpURLConnection conn, Map<String, String> params) {
		if (conn == null || params == null || params.size() <= 0)
			return conn;
		try {
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			StringBuilder content = new StringBuilder();
			Set<String> mapKeys = params.keySet();
			for (String mapKey : mapKeys)
				content.append("&").append(mapKey).append("=").append(URLEncoder.encode(params.get(mapKey), "UTF-8"));
			content.deleteCharAt(content.indexOf("&"));
			// DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写到流里面
			out.writeBytes(content.toString());
			out.flush();
			out.close();
			// conn.disconnect();
			return conn;
		} catch (UnsupportedEncodingException e) {
			log.error("不支持的编码类型！", e);
		} catch (IOException e) {
			log.error("参数 写入url输出流失败！", e);
		}
		return null;
	}

	public static String getHttpURLConnResponse(HttpURLConnection conn) {
		String line = null;
		if (conn == null)
			return line;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			line = reader.readLine();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}

	public static String array2String(Object[] objs, String splite) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < objs.length; i++) {
			if (i == objs.length - 1)
				splite = "";
			result.append(String.valueOf(objs[i])).append(splite);
		}
		return result.toString();
	}

	public static <T> T[] str2ObjArray(String[] str, Class<T> cls) {
		if (str == null || str.length <= 0)
			return null;
		T[] result = (T[]) Array.newInstance(cls, str.length);
		Method valueOf = null;
		try {
			valueOf = cls.getDeclaredMethod("valueOf", String.class);
		} catch (NoSuchMethodException e) {
		}
		if (valueOf == null) {
			log.error("parameter: cls --> no the method which name is valueOf() !");
			return null;
		}
		for (int i = 0; i < str.length; i++) {
			try {
				Array.set(result, i, valueOf.invoke(null, str[i]));
			} catch (Exception e) {
				log.error("parameter: str[" + i + "] = " + str[i] + " --> can not valueOf to parameter:cls !");
			}
		}
		return result;
	}

	public static int lastIndexOffset(String str, String regex, int num) {
		int result = -1;
		num--;
		for (int i = 0, n = 0; i <= num; i++) {
			int t = str.lastIndexOf(regex);
			n = t;
			if (i == num) {
				result = n;
			} else {
				str = str.substring(0, t);
			}
		}
		return result;
	}

	public static List<String> execLinuxCmd(String cmd) {
		String[] execObj = { "/bin/sh", "-c", cmd };
		List<String> lines = new ArrayList<String>();
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(execObj);
		} catch (IOException e) {
			log.error("C2SFileTools.execLinuxCmd() : can not execute the cmd -->" + cmd + "<--", e);
		}
		if (process != null) {
			LineNumberReader lnr = new LineNumberReader(new InputStreamReader(process.getInputStream()));

			try {
				String line = null;
				while (!ST.isNull(line = lnr.readLine())) {
					lines.add(line);
				}
			} catch (IOException e) {
				log.error("C2SFileTools.execLinuxCmd() : can not obtain the results of the cmd -->" + cmd + "<--", e);
			}
		}
		return lines;
	}

	public static int[] countPageLimit(Integer recordCount, int rows, int page) {
		int[] res = { -1, -1, 1 }; // {start,limit,pageCount}
		if (recordCount != null) {
			if (rows > 0 && page >= 1) {
				int realPageCount = (recordCount + rows - 1) / rows;
				page = realPageCount < page ? realPageCount : page;
				res[0] = (page - 1) * rows;
				res[1] = rows;
				res[2] = realPageCount;
			}
		}
		return res;
	}

	/**
	 * 判断字符是否是中文
	 *
	 * @param c
	 *            字符
	 * @return 是否是中文
	 */
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * 判断字符串是否是乱码
	 *
	 * @param strName
	 *            字符串
	 * @return 是否是乱码 true:乱码
	 */
	public static boolean isMessyCode(String strName) {
		Pattern p = Pattern.compile("\\s*|t*|r*|n*");
		Matcher m = p.matcher(strName);
		String after = m.replaceAll("");
		String temp = after.replaceAll("\\p{P}", "");
		char[] ch = temp.trim().toCharArray();
		float chLength = ch.length;
		float count = 0;
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (!Character.isLetterOrDigit(c)) {
				if (!isChinese(c)) {
					count = count + 1;
				}
			}
		}
		float result = count / chLength;
		if (result > 0.4) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取中文字符串 srcStr 的字符编码
	 *
	 * @param srcStr
	 * @return
	 */
	public static String charsetCode(String srcStr) {
		String charEncode = null;
		try {
			for (String enc : CharEncoding.encodeList) {
				if (!C2SFileTools.isMessyCode(new String(srcStr.getBytes(enc), enc))) {
					charEncode = enc;
					break;
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return charEncode;
	}

	public static String concatPath(String... paths) {
		String path = "";
		if (paths.length <= 0)
			return path;

		for (String s : paths) {
			path = concatPath(path, s);
		}
		return path;
	}

	public static String concatPath(String pathL, String pathR) {

		if (ST.isNull(pathL) && ST.isNull(pathR)) {
			return "";
		} else {
			if (!ST.isNull(pathL)) {
				if (pathL.endsWith(File.separator))
					pathL = pathL.substring(0, pathL.length() - 1);
			} else {
				pathL = "";
			}
			if (!ST.isNull(pathR)) {
				if (pathR.startsWith(File.separator))
					pathR = pathR.substring(1);
			} else {
				pathR = "";
			}
		}
		return pathL.concat(File.separator).concat(pathR);
	}
}
