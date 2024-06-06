package com.bobwu.aop;

import com.bobwu.service.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Set;


@Slf4j
@Aspect
@Component
public class AccessAspect {

    @Autowired
    private RedisService redisService;


    @Before("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Controller *)")
    public void recordAccess(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        // 获取请求的IP地址和请求的URL
        String ip = request.getRemoteAddr();
//        String url = request.getRequestURL().toString();
        String domain = request.getServerName();

        log.info("aop recordAccess, ip = {} ,domain = {}",ip , domain);

        // 记录访问信息
        redisService.recordAccess(domain, ip);

        // 获取指定域名在指定小时内的访问IP集合
        Set<String> ips = redisService.getHourlyIps(domain ,"2024060622");
//        if(ips.isEmpty()){
//            log.info("ips = {}",ips);
//        }else{
//            for(String s : ips){
//                log.info("ip = {} ", s);
//            }
//        }

        // 获取指定域名和IP地址的访问次数
        int accessCount = redisService.getIpAccessCount(domain ,ip);

        // 获取指定域名的总访问次数
        int totalAccessCount = redisService.getTotalAccessCount(domain);

        log.info("aop recordAccess redis ips = {} ,accessCount = {} ,totalAccessCount = {}" ,ips.toArray() ,accessCount ,totalAccessCount);
    }
}

