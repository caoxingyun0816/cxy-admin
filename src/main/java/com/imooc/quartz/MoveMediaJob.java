//package com.imooc.quartz;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.scheduling.quartz.QuartzJobBean;
//
///**
// * Created by caoxingyun on 2018/9/4.
// * 定时任务，执行搬迁视频。配合线程使用。搬迁时用 java 程序执行windows命令或shell命令。move 文件。
// */
//
//
//public class MoveMediaJob extends QuartzJobBean {
//
//    private static final Logger log = LoggerFactory.getLogger(MoveMediaJob.class);
//    private AssetMediaDao assetMediaDao = (AssetMediaDao) Constants.ctx.getBean("assetMediaDao");
//    private AssetDao assetDao = (AssetDao) Constants.ctx.getBean("assetDao");
//
//    @Override
//    protected void executeInternal(JobExecutionContext context)
//            throws JobExecutionException {
//        log.info(" ------------------------------------------MoveSourceJob start  ------------------------------------------");
//
//        int MOVETASK_COUNT =20;// 每次执行的搬迁任务数,如果没有配置默认20
//        int MOVETASK_MAX = 100;// 正在搬迁任务最大数，如果没有配置默认100
//        String movecount = ConfigUtil.getString("movetask_count");
//        String movemax = ConfigUtil.getString("movetask_max");
//
//        if(!StringUtil.isNullStr(movecount)){
//            MOVETASK_COUNT = Integer.valueOf(movecount);
//        }
//        if(!StringUtil.isNullStr(movemax)){
//            MOVETASK_MAX = Integer.valueOf(movemax);
//        }
//
//        int count = assetDao.queryMovingCount();
//        if(count >= MOVETASK_MAX){
//            log.info(" #####################  MoveSourceJob System has reached the maximum number of relocation   #######################");
//        }
//        else{
//            List<Asset> assets = assetDao.queryByMoveStatus(MOVETASK_COUNT);
//
//            for(Asset asset : assets){
//                MoveSourceThread mst = new MoveSourceThread(asset);
//                mst.start();
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    log.error(e.getMessage(),e);
//                }
//            }
//        }
//
//        log.info(" ------------------------------------------MoveSourceJob end  ------------------------------------------");
//
//    }
//
//
//    class MoveSourceThread extends Thread {
//        private final Log log = LogFactory.getLog(MoveSourceThread.class);
//        private Asset asset;
//
//        public MoveSourceThread(Asset asset) {
//            this.asset = asset;
//        }
//
//        @Override
//        public void run() {
//            log.info(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%move media："
//                    + asset.getAid()
//                    + " start  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ");
//
//            asset.setMoveStatus(String.valueOf(MamConstants.MOVE_STATUS_1));
//            assetDao.save(asset);
//
//            AssetMedia media = getSourceMedia(asset.getAid());
//            if(media != null){
//                //只搬迁源文件
//                String mpath = FilePathHelper.joinPath(FilePathHelper.getRoot(MamConstants.STORETMP_PATH), media.getMpath());
//                log.info("filePath : " + mpath);
//                try {
//                    File file = new File(mpath);
//                    if (file.exists()) {
////						String rootPath = FilePathHelper.getRoot(MamConstants.STORE_PATH);
//                        String rootPath = FilePathHelper.getRoot(MamConstants.CFG_STORE_PATH);
//                        String newPath = FilePathHelper.getAbsolutelyPath(rootPath ,String.valueOf(media.getAssetId()));
//                        File newfile = new File(newPath);
//                        if (!newfile.exists()) {
//                            newfile.mkdir();
//                        }
//                        FileTool.moveFile(mpath, FilePathHelper.joinPath(newPath),media.getName());
//                        asset.setMoveStatus(String.valueOf(MamConstants.MOVE_STATUS_2));
//                        assetDao.save(asset);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    log.error(e.getMessage(),e);
//                    asset.setMoveStatus(String.valueOf(MamConstants.MOVE_STATUS_3));
//                    assetDao.save(asset);
//                }
//            }
//            else{
//                asset.setMoveStatus(String.valueOf(MamConstants.MOVE_STATUS_4));
//                assetDao.save(asset);
//            }
//
//            log.info(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%move media："
//                    + asset.getAid()
//                    + " end  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ");
//        }
//
//        private AssetMedia getSourceMedia(Long aid){
//            AssetMedia tempMedia = null;
//            List<AssetMedia> mediaList = assetMediaDao.queryMediaByAid(aid);
//
//            for(AssetMedia media : mediaList){
//                if(media.getIsSource() != null && media.getIsSource()){
//                    tempMedia = media;
//                }
//            }
//            return tempMedia;
//        }
//    }
//}
