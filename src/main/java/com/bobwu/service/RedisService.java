package com.bobwu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHH");

    public void recordAccess(String domain, String ip) {
        String currentHour = LocalDateTime.now().format(HOUR_FORMATTER);

        // 域名的IP访问计数
        redisTemplate.opsForHash().increment(domain + "_ip_count", ip, 1);

        // 域名的总访问次数
        redisTemplate.opsForValue().increment(domain + "_total_count");

        // 每小时的访问IP集合
        redisTemplate.opsForSet().add(domain + "_" + currentHour + "_ips", ip);

        // 设置过期时间为2小时
        redisTemplate.expire(domain + "_" + currentHour + "_ips", 2, TimeUnit.HOURS);
    }

    /**
     * 获取指定域名在指定小时内的访问IP集合
     * @param domain
     * @param hour：要查询的小时（通常是以yyyyMMddHH格式表示的时间）。
     * @return 返回一个包含访问该域名在指定小时内的所有IP地址的集合。
     *
     * ex: Set<String> ips = redisService.getHourlyIps("example.com", "2024010112");
     */
    public Set<String> getHourlyIps(String domain, String hour) {
        return redisTemplate.opsForSet().members(domain + "_" + hour + "_ips");
    }

    /**
     * 获取指定域名和IP地址的访问次数
     * @param domain
     * @param ip
     * @return 返回该IP地址对指定域名的访问次数。如果未找到记录，则返回0
     *
     * ex: int accessCount = redisService.getIpAccessCount("example.com", "192.168.1.100");
     */
    public int getIpAccessCount(String domain, String ip) {
        Object count = redisTemplate.opsForHash().get(domain + "_ip_count", ip);
        return count == null ? 0 : Integer.parseInt(count.toString());
    }

    /**
     * 获取指定域名的总访问次数
     * @param domain
     * @return 返回指定域名的总访问次数。如果未找到记录，则返回0
     *
     * ex: int totalAccessCount = redisService.getTotalAccessCount("example.com");
     */
    public int getTotalAccessCount(String domain) {
        String count = redisTemplate.opsForValue().get(domain + "_total_count");
        return count == null ? 0 : Integer.parseInt(count);
    }

    public void deleteHourlyIps(String domain, String hour) {
        redisTemplate.delete(domain + "_" + hour + "_ips");
    }
}

