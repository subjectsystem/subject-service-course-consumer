package com.miyako.subject.service.course.consumer.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.miyako.subject.commons.domain.TbCourse;
import com.miyako.subject.commons.domain.TbCourselist;
import com.miyako.subject.commons.domain.TbStudent;
import com.miyako.subject.commons.domain.TbTeacher;
import com.miyako.subject.dubbo.aop.MethodLog;
import com.miyako.subject.service.course.api.TbCourseService;
import com.miyako.subject.service.course.consumer.vo.CourseVo;
import com.miyako.subject.service.course.consumer.vo.PageDto;
import com.miyako.subject.service.redis.api.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
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

    private PageDto<CourseVo> dto = new PageDto<>();

    private void updatePage(PageDto<CourseVo> dto, PageInfo<TbCourse> pageInfo){
        List<CourseVo> list = new ArrayList<>(pageInfo.getList().size());
        for (TbCourse tbCourse : pageInfo.getList()){
            CourseVo vo = new CourseVo();
            vo.setCourse(tbCourse);
            vo.setGrade(Integer.valueOf(tbCourse.getCoursenum().substring(2,6)));
            TbTeacher tbTeacher = new TbTeacher();
            tbTeacher.setAge(21);
            tbTeacher.setName("测试教师-1");
            vo.setTeacher(tbTeacher);
            list.add(vo);
        }
        dto.setData(list);
    }

    @MethodLog(value = "CourseController", operationType = "封装DTO", operationName = "getPage", operationArgs = "分页信息")
    public PageDto<CourseVo> getPage(Model model, int page, int size){
        logger.info("page===>"+page);
        if(dto != null && dto.getData() !=null){
            PageInfo<TbCourse> pageInfo = tbCourseService.page(page, size);
            // 原先查询过数据集
            updatePage(dto, pageInfo);
            model.addAttribute("pageDto", dto);
            return dto;
        }else{
            // 未查询过数据集
            logger.info("select page info...");
            PageInfo<TbCourse> pageInfo = tbCourseService.page(page, size);
            dto.setPageSize(pageInfo.getSize());
            dto.setPageNums(pageInfo.getPages());
            dto.setSize(pageInfo.getTotal());
            updatePage(dto,pageInfo);
            model.addAttribute("pageDto", dto);
            return dto;
        }
    }



    @GetMapping(value = "/list")
    @MethodLog(value = "CourseController", operationType = "路径访问", operationName = "查询课表")
    public String list(Model model) {
        getPage(model, 1, pageSize);
        model.addAttribute("currentIndex",1);
        return "course/list";
    }

    @GetMapping(value = "/list/{page}")
    @MethodLog(value = "CourseController", operationType = "路径访问", operationName = "查询课表分页", operationArgs = "分页索引")
    public String list(Model model, @PathVariable("page") Integer page) {
        page = page==null?1:page;
        getPage(model, page, pageSize);
        model.addAttribute("currentIndex",page);
        return "course/list";
    }

    @PostMapping(value = "/add/{id}")
    @MethodLog(value = "CourseController", operationType = "路径访问", operationName = "add", operationArgs = "课程id")
    public String add(Model model, TbStudent tbStudent, @PathVariable("id") Integer id){
        logger.info("course id===>"+id);
        logger.info("student===>"+tbStudent.getName());
        //选课流程
        //1.获得课程id，学生id，
        //2.判断是否可选，默认都可以选择
        //3.创建courselist表
        TbCourselist courselist = new TbCourselist();
        courselist.setStudentid(tbStudent.getId());
        courselist.setCourseid(id);
        return "success";
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
