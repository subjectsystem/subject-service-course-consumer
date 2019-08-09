package com.miyako.subject.service.course.consumer.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.miyako.subject.commons.domain.TbCourse;
import com.miyako.subject.service.course.api.TbCourseService;
import com.miyako.subject.service.redis.api.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * ClassName CourseController
 * Description //TODO
 * Author weila
 * Date 2019-08-07-0007 13:49
 */
@Controller
@RequestMapping(value = "/course")
public class CourseController{

    private static Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Reference
    private TbCourseService tbCourseService;
    @Reference
    private RedisService redisService;

    @Value("${page.size}")
    private Integer pageSize;

    private PageInfo<TbCourse> getPage(Model model, int page, int size){
        PageInfo<TbCourse> pageInfo = tbCourseService.page(page, size);
        logger.info("===>:pageInfo size "+ pageInfo.getSize());
        model.addAttribute("pageInfo", pageInfo);
        return pageInfo;
    }


    @GetMapping(value = "/list")
    public String list(Model model) {
        logger.info("enter request mapping: /course/list");
        getPage(model, 1, pageSize);
        return "course/list";
    }

    @GetMapping(value = "/list/{page}")
    public String list(Model model, @PathVariable("page") Integer page) {
        page = page==null?1:page;
        logger.info("enter request mapping: /user/list/ "+ page);
        getPage(model, page, pageSize);
        model.addAttribute("currentIndex",page);
        return "course/list";
    }

    @GetMapping(value = "/details/{id}")
    public String details(Model model, @PathVariable("id") Integer id){
        //TbCourse student = redisService.get(StudentKey.getById.getPrefix() + "->" + id, TbStudent.class);
        //if(student ==null){
        //    student = tbStudentService.selectById(id);
        //    logger.info("database : "+ student.getId());
        //}else{
        //    logger.info("redis cache: "+ student.getId());
        //}
        //model.addAttribute("tbStudent", student);
        return "user/details.html";
    }
}
