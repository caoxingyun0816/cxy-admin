package com.wondertek.mam.util.backupUtils.transform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.wondertek.mam.util.image.ImageUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Task {
	public static Log log = LogFactory.getLog(Task.class);
	private String srcImagePath;
	private String targetImagePath;
	public Task(String srcImagePath,String targetImagePath) {
		this.srcImagePath=srcImagePath;
		this.targetImagePath=targetImagePath;
	}

	public void execute() {
		long startTime=System.currentTimeMillis();
		File srcFolder=new File(srcImagePath);
		File destFolder=new File(targetImagePath);
		log.info("SRC FOLDER:"+srcFolder);
		log.info("DEST FOLDER:"+destFolder);
		String tmpPath=srcFolder+"/tmp.gif";
		List<File> list=listDirectoryFiles(srcFolder);
		log.info("BEGIN EXECUTE....."+"SIZE:"+list.size());
		int errorSize=0;
		for(File file:list){
			String absoPath=file.getAbsolutePath();
			String fileName=file.getName();
			String fileType= ImageUtils.getFileSuffix(fileName);
			String destFilePath=destFolder+"/"+absoPath.substring(srcFolder.getAbsolutePath().length());
			if(!ImageUtils.grayToBit8(absoPath, destFilePath, tmpPath, fileType))errorSize++;
		}
		log.info("EXECUTE FINISHED ,TOTAL SIZE:"+list.size()+",ERRORSIZE:"+errorSize+",EXECUTE TIME:"+(System.currentTimeMillis()-startTime)/(1000*60));
	}

	public static List<File>  listDirectoryFiles(File folderFile){
		List<File> fileList=new ArrayList<File>();
		if(folderFile.exists()&&folderFile.isDirectory()){
			File[] fileArr=folderFile.listFiles();
			if(fileArr!=null&&fileArr.length>0){
				for(File file:fileArr){
					if(file.isDirectory()){
						fileList.addAll(listDirectoryFiles(file));
					}else {
						fileList.add(file);
					}
				}
			}
		}
		return fileList;
	}
}
