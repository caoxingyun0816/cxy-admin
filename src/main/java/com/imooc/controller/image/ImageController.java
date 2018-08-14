package com.imooc.controller.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by caoxingyun on 2018/7/23.
 */
@Controller
@RequestMapping("/img")
public class ImageController {

    private Logger log = LoggerFactory.getLogger(ImageController.class);

    @Value("${image.savePath}")
    private String imageSavePath;

    @GetMapping("/images")
    public String imagesView(){
       return "/img/images";
    }

    @GetMapping("/addImg")
    public String addImages(){
        log.info("上传图片");

        return "/img/addImg";
    }

    /***
     * 上传图片
     * @param file  MultipartFile是Spring上传文件的封装类
     * 多文件上传可以用数组来接收 MultipartFile[] files
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/upload")
    public ModelAndView uploadImages(@RequestParam("file")MultipartFile file , RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();
        if (file.isEmpty()) {
//            redirectAttributes.addAttribute("message", "please select a file to uplaod!");

            modelAndView.setViewName("/uploadResult");
            modelAndView.addObject("message","please select a file to uplaod!");
            return modelAndView;
        }
        File path = new File(imageSavePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File saveFile = new File(path +"/"+ fileName);
        try {
//            // Get the file and save it somewhere
//            byte[] bytes = file.getBytes();
//            Path paths = Paths.get(path  +"/"+ fileName);
//            Files.write(path, bytes);
            file.transferTo(saveFile);
            modelAndView.setViewName("/uploadResult");
            modelAndView.addObject("message","UploadSuccess");
        } catch (IOException e) {
            log.error("上传出错!");
        }
        return modelAndView;
    }
}
