package com.atguigu;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.junit.Test;

public class QiniuTest {
        // 测试像七牛云上传图片 Zone.zone2()  2华南
    @Test
    public void testUplod() {
        //构造一个带指定 Region 对象的配置类
        //去个人中心，密钥管理中心将AK SK考本过来
        Configuration cfg = new Configuration(Zone.zone2()); // 2华南
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = "6fD16nSzEbXzohxsQJ55H1SROgk33o9X35FGfjFD";
        String secretKey = "19EtAlVPnRa1r6cXu6HNZt_DHG_VaJQYyOdKPQhG";
        //空间名称
        String bucket = "shf20221116";
        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
        //设置本地文件路径
        String localFilePath = "C:/Users/Admin/Pictures/Camera Roll/2.png";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
    }
}
