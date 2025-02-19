package com.yangrr.rrmianshi.controller;

import com.yangrr.rrmianshi.annotation.AuthCheck;
import com.yangrr.rrmianshi.common.BaseResponse;
import com.yangrr.rrmianshi.common.ResultUtils;
import com.yangrr.rrmianshi.dto.*;
import com.yangrr.rrmianshi.service.UsersService;
import com.yangrr.rrmianshi.vo.PublicKeyVo;
import com.yangrr.rrmianshi.vo.SafetyUser;
import com.yangrr.rrmianshi.vo.SafetyUserPageVo;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.yangrr.rrmianshi.constant.UserConstant.ADMIN;
import static com.yangrr.rrmianshi.constant.UserConstant.LOGIN_USER;

/**
 * @PATH com.yangrr.rrmianshi.controller.UserController
 * @Author YangRR
 * @CreateData 2025-01-27 15:15
 * @Description:
 */

@RestController
@RequestMapping("/user")
@CrossOrigin(value = "http://localhost:8000", allowCredentials = "true", allowedHeaders = "*")
public class UserController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @PostMapping("/login")
    public BaseResponse<Long> login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        //usersService处理业务逻辑
        return usersService.login(loginDto, request);
    }

    @PostMapping("/register")
    public BaseResponse<SafetyUser> register(@RequestBody RegisterDto registerDto) {
        return usersService.register(registerDto);
    }

    @PostMapping("/sendMail")
    public BaseResponse<String> sendMail(@RequestBody EmailDto emailDto) {

        // 发送邮件
        usersService.sendMail(emailDto);
        return ResultUtils.success("邮件发送成功");
    }

    @GetMapping("getCurrent")
    public BaseResponse<SafetyUser> getCurrentUser(HttpServletRequest request) {
        return ResultUtils.success(usersService.getCurrentUser(request));
    }

    @GetMapping("logout")
    public BaseResponse<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute(LOGIN_USER);
        return ResultUtils.success("退出成功");
    }

    @PostMapping("addUser")
    @AuthCheck(mustRole = ADMIN)
    public BaseResponse<Boolean> addUser(@RequestBody AddUserDto addUserDto) {
        return ResultUtils.success(usersService.addUser(addUserDto));
    }

    @GetMapping("deleteUser/{id}")
    @AuthCheck(mustRole = ADMIN)
    public BaseResponse<Boolean> deleteUser(@RequestParam("id") Long id) {
        return ResultUtils.success(usersService.deleteUser(id));
    }

    @PostMapping("getUserList")
    @AuthCheck(mustRole = ADMIN)
    public BaseResponse<SafetyUserPageVo> getUserList(@RequestBody GlobalDto globalDto) {
        return ResultUtils.success(usersService.getUserList(globalDto));
    }

    @GetMapping("getUserById")
    @AuthCheck(mustRole = ADMIN)
    public BaseResponse<SafetyUser> getUserById(Long id) {
        SafetyUser userById = usersService.getUserById(id);
        return ResultUtils.success(userById);
    }

    @PostMapping("updateUser")
    @AuthCheck(mustRole = ADMIN)
    public BaseResponse<Boolean> updateUser(@RequestBody UpdateUserDto updateUserDto,
                                            HttpServletRequest request) {
        return ResultUtils.success(usersService.updateUser(updateUserDto, request));

    }

    //重构密码
    @GetMapping("resetPassword")
    @AuthCheck(mustRole = ADMIN)
    public BaseResponse<Boolean> resetPassword(@RequestParam("id") Integer id) {
        return ResultUtils.success(usersService.resetPassword(id));
    }


    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(file.getOriginalFilename())
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            //得到url
            return minioClient.getObjectUrl(bucketName, file.getOriginalFilename());
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return "Error uploading file to MinIO: " + e.getMessage();
        }
    }


}
