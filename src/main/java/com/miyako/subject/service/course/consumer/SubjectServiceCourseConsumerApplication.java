package com.miyako.subject.service.course.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * ClassName SubjectServiceCourseConsumerApplication
 * Description //TODO
 * Author weila
 * Date 2019-08-07-0007 11:29
 */
@EnableHystrix
@EnableHystrixDashboard
@SpringBootApplication(scanBasePackages = "com.miyako.subject")
public class SubjectServiceCourseConsumerApplication{
    public static void main(String[] args) {
        SpringApplication.run(SubjectServiceCourseConsumerApplication.class, args);
    }
}
