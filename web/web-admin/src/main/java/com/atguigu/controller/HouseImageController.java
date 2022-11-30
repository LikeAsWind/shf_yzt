package com.atguigu.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.entity.HouseImage;
import com.atguigu.result.Result;
import com.atguigu.service.HouseImageService;
import com.atguigu.util.QiniuUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/houseImage")
public class HouseImageController {

    @Reference
    private HouseImageService houseImageService;

     // 去上传图片的页面
    @RequestMapping("/uploadShow/{houseId}/{type}")
    public String goUploadPage(@PathVariable("houseId") Long houseId, @PathVariable("type") Integer type, Map map) {

        map.put("houseId", houseId);
        map.put("type", type);
        return "house/upload";
    }

    @PostMapping("/upload/{houseId}/{type}")
    @ResponseBody
    public Result upload(@PathVariable Long houseId, @PathVariable Integer type, @RequestParam(value = "file") MultipartFile[] files) {
        try {
            if (files.length > 0) {
                for (MultipartFile file : files) {
                    //获取字节数组
                    byte[] bytes = file.getBytes();
                    //获取图片的名字
                    String originalFilename = file.getOriginalFilename();
                    // 通过UUID随机生成一个字符串作为上传到七牛云的图片的名字
                    String newFileName = UUID.randomUUID().toString();
                    //上传图片
                    QiniuUtil.upload2Qiniu(bytes, newFileName);
                    String url = "http://rlf8d4z5u.hn-bkt.clouddn.com/" + newFileName;
                    //创建HouseImage对象
                    HouseImage houseImage = new HouseImage();
                    houseImage.setHouseId(houseId);
                    houseImage.setType(type);
                    houseImage.setImageName(originalFilename);
                    houseImage.setImageUrl(url);
                    houseImageService.insert(houseImage);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Result.ok();
    }

    /**
     * 删除
     *
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/delete/{houseId}/{id}")
    public String delete(ModelMap model, @PathVariable Long houseId, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        HouseImage houseImage = houseImageService.getById(id);
        houseImageService.delete(id);
        QiniuUtil.deleteFileFromQiniu(houseImage.getImageUrl());
        return "redirect:/house/" + houseId;
    }
}
