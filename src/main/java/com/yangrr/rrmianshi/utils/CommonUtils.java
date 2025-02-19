package com.yangrr.rrmianshi.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yangrr.rrmianshi.common.ErrorCode;
import com.yangrr.rrmianshi.domain.Users;
import com.yangrr.rrmianshi.dto.EmailDto;
import com.yangrr.rrmianshi.exception.BusinessException;
import com.yangrr.rrmianshi.mapper.UsersMapper;
import com.yangrr.rrmianshi.vo.SafetyUser;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.yangrr.rrmianshi.constant.UserConstant.LOGIN_USER;

/**
 * @PATH com.yangrr.rrmianshi.utils.CommonUtils
 * @Author YangRR
 * @CreateData 2025-01-28 18:14
 * @Description:
 */

@Component
public class CommonUtils {

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    public void sendMailUtils(EmailDto emailDto){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom("yangrrtec@163.com");
        simpleMailMessage.setTo(emailDto.getEmail());
        simpleMailMessage.setSubject("验证码");
        String code = Double.toString(Math.random()).substring(2, 8);

        String text = """                           
                    尊敬的用户：
                   
                    您好！
                                    
                    欢迎使用我们的服务。为了确保您的账户安全以及操作的合法性，我们需要对你的身份进行验证。
                                    
                    你的验证码为：code
                                    
                    该验证码将在[1分钟]内有效，请你尽快使用。如果你没有进行任何需要验证码的操作，请忽略此邮件。
                                    
                    请不要将验证码告知他人，以保障你的账户安全。
                                    
                    感谢你的理解和支持！
                                    
                    yangRR团队
                    DATE
                """;

        // 替换 code
        text = text.replace("code", code);


        LocalDateTime now = LocalDateTime.now();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(java.sql.Timestamp.valueOf(now));

        text = text.replace("DATE", formattedDate);

        simpleMailMessage.setText(text);
        javaMailSender.send(simpleMailMessage);

        //将验证码存入redis 一分钟失效
        redisTemplate.opsForValue().set(emailDto.getEmail(), code, 1, TimeUnit.MINUTES);
    }


    public static SafetyUser checkIsLogin(HttpServletRequest request){
        return (SafetyUser) request.getSession().getAttribute(LOGIN_USER);
    }
}
