package com.kfood.media.api;

import com.kfood.base.model.PageParams;
import com.kfood.base.model.PageResult;
import com.kfood.media.model.dto.QueryMediaParamsDto;
import com.kfood.media.model.dto.UploadFileParamsDto;
import com.kfood.media.model.dto.UploadFileResultDto;
import com.kfood.media.model.po.MediaFiles;
import com.kfood.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @description 媒资文件管理接口
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
 @Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
 @RestController
public class MediaFilesController {


  @Autowired
  MediaFileService mediaFileService;


 @ApiOperation("媒资列表查询接口")
 @PostMapping("/files")
 public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto){
     //当前登录用户
     SecurityUtil.XcUser user = SecurityUtil.getUser();
     //用户所属机构id
     String companyId = null;
     if(StringUtils.isNotEmpty(user.getId())){
         companyId=user.getId();
         System.out.println(companyId);
     }
//     String companyId = 1232141426L;
  return mediaFileService.queryMediaFiels(companyId,pageParams,queryMediaParamsDto);

 }

 @ApiOperation("上传图片")
 @RequestMapping(value = "/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public UploadFileResultDto upload(@RequestPart("filedata")MultipartFile filedata,
                                  @RequestParam(value= "objectName",required=false) String objectName) throws IOException {
     //当前登录用户
//     SecurityUtil.XcUser user = SecurityUtil.getUser();
//     System.out.println(user);
//     //用户所属机构id
//     String companyId = null;
//     if(StringUtils.isNotEmpty(user.getId())){
//         companyId=user.getId();
//         System.out.println(companyId);
//     }
//     此处无法获取用户信息，默认将图片地址设为987654123，后果是无法在媒资管理处查询到课程封面图片，但是可以正常使用。
     String companyId="987654123";
    //准备上传文件的信息
     UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
     //原始文件名称
     uploadFileParamsDto.setFilename(filedata.getOriginalFilename());
     //文件大小
     uploadFileParamsDto.setFileSize(filedata.getSize());
     //文件类型
     uploadFileParamsDto.setFileType("001001");
     //创建一个临时文件
     File tempFile = File.createTempFile("minio", ".temp");
     filedata.transferTo(tempFile);

    //文件路径
     String localFilePath = tempFile.getAbsolutePath();

     //调用service上传图片
     UploadFileResultDto uploadFileResultDto = mediaFileService.uploadFile(companyId, uploadFileParamsDto, localFilePath,objectName);

     return uploadFileResultDto;
 }

}
