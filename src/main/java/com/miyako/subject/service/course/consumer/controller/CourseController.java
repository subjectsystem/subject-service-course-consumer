package com.miyako.subject.service.course.consumer.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.miyako.subject.commons.domain.TbCourse;
import com.miyako.subject.service.course.api.TbCourseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Reference
    private TbCourseService tbCourseService;

    @GetMapping(value = "/list")
    public String list(Model model) {
        System.out.println("....");
        List<TbCourse> tbCourses = tbCourseService.selectAll();
        model.addAttribute("tbCourses", tbCourses);
        for (TbCourse tbCourse : tbCourses) {
            System.out.println(tbCourse.getTitle());
        }
        return "course/list";
    }
}
