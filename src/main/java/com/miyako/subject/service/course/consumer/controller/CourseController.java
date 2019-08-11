package com.miyako.subject.service.course.consumer.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.miyako.subject.commons.domain.TbCourse;
import com.miyako.subject.commons.domain.TbCourselist;
import com.miyako.subject.commons.domain.TbStudent;
import com.miyako.subject.commons.domain.TbTeacher;
import com.miyako.subject.dubbo.aop.MethodLog;
import com.miyako.subject.service.course.api.TbCourseListService;
import com.miyako.subject.service.course.api.TbCourseService;
import com.miyako.subject.service.course.consumer.vo.CourseVo;
import com.miyako.subject.service.course.consumer.vo.PageDto;
import com.miyako.subject.service.redis.api.RedisService;
import com.miyako.subject.service.redis.key.CourseKey;
import com.miyako.subject.service.user.api.TbUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    @Reference
    private TbCourseListService tbCourseListService;
    @Reference
    private TbUserService tbUserService;

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
            TbCourselist tbCourselist = new TbCourselist();
            tbCourselist.setCourseid(tbCourse.getId());
            // 查出当前课程的选择人数
            Integer count = tbCourseListService.selectCount(tbCourselist);
            vo.setSurplus(tbCourse.getCoursecount()-count);
            //课程信息写入缓存
            redisService.set(CourseKey.getCourseById,tbCourse.getId().toString(),vo);
            //当前课程可选人数写入缓存
            redisService.set(CourseKey.getCourseSurplus,tbCourse.getId().toString(),vo.getSurplus());
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
    public String list(Model model,  TbStudent tbStudent) {
        getPage(model, 1, pageSize);
        model.addAttribute("currentIndex",1);
        model.addAttribute("student",tbStudent);
        return "course/list";
    }

    @GetMapping(value = "/list/{page}")
    @MethodLog(value = "CourseController", operationType = "路径访问", operationName = "查询课表分页", operationArgs = "分页索引")
    public String list(Model model, TbStudent tbStudent, @PathVariable("page") Integer page) {
        page = page==null?1:page;
        getPage(model, page, pageSize);
        model.addAttribute("currentIndex",page);
        model.addAttribute("student",tbStudent);
        return "course/list";
    }

    @PostMapping(value = "/add/{id}")
    @ResponseBody
    @MethodLog(value = "CourseController", operationType = "路径访问", operationName = "add", operationArgs = "课程id")
    public String add(Model model, TbStudent tbStudent, @PathVariable("id") Integer id){
        logger.info("course id===>"+id);
        logger.info("student===>"+tbStudent.getName());
        //选课流程
        //1.获得课程id，学生id，
        //2.判断是否可选，默认都可以选择
        //3.课程人数是否满足，选课名单中的人数小于课程的限制
        //从redis中获取可选人数
        Integer surplus = redisService.get(CourseKey.getCourseSurplus, String.valueOf(id), Integer.class);
        logger.info("redis course surplus===>"+surplus);
        if(redisService.decr(CourseKey.getCourseSurplus, String.valueOf(id)) <0){
            // 没有剩余人数可选
            redisService.incr(CourseKey.getCourseSurplus, String.valueOf(id));
            return "选课失败，选课人员已满";
        }
        //4.创建courselist表
        TbCourselist courselist = new TbCourselist();
        courselist.setStudentid(tbStudent.getId());
        courselist.setCourseid(id);
        //5.查询该生是否选择这门课
        if(tbCourseListService.selectOne(courselist) == null){
            // 未选择
            int i = tbCourseListService.insert(courselist);
            logger.info("course list id===>"+i);
            return "选课成功";
        }else{
            // 已选择，返回提示信息
            logger.info("course error===>");
            redisService.incr(CourseKey.getCourseSurplus, String.valueOf(id));
            return "选课失败，你已选择该课程，请前往选课名单";
        }
    }

    @PostMapping(value = "/addwithout/{id}")
    @ResponseBody
    @MethodLog(value = "CourseController", operationType = "路径访问", operationName = "addWithoutCookie", operationArgs = "学生id和课程id")
    public String addWithoutCookie(Model model, @RequestParam("student")Integer uid, @PathVariable("id") Integer id){
        logger.info("student id===>"+uid);
        logger.info("course id===>"+id);
        //选课流程
        //1.获得课程id，学生id，
        //2.判断是否可选，默认都可以选择
        //3.课程人数是否满足，选课名单中的人数小于课程的限制
        //从redis中获取可选人数
        Integer surplus = redisService.get(CourseKey.getCourseSurplus, String.valueOf(id), Integer.class);
        logger.info("redis course surplus===>"+surplus);
        if(redisService.decr(CourseKey.getCourseSurplus, String.valueOf(id)) <0){
            // 没有剩余人数可选
            redisService.incr(CourseKey.getCourseSurplus, String.valueOf(id));
            return "选课失败，选课人员已满";
        }
        //4.创建courselist表
        TbCourselist courselist = new TbCourselist();
        courselist.setStudentid(uid);
        courselist.setCourseid(id);
        //5.查询该生是否选择这门课
        if(tbCourseListService.selectOne(courselist) == null){
            // 未选择
            int i = tbCourseListService.insert(courselist);
            logger.info("course list id===>"+i);
            return "选课成功";
        }else{
            // 已选择，返回提示信息
            logger.info("course error===>");
            redisService.incr(CourseKey.getCourseSurplus, String.valueOf(id));
            return "选课失败，你已选择该课程，请前往选课名单";
        }
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


    @GetMapping(value = "/student/{id}")
    public String student(Model model, @PathVariable("id") Integer id){
        logger.info("course id===>"+id);
        //logger.info("student===>"+tbStudent.getName());
        //查询当前课程的已选名单
        TbCourselist tbCourselist = new TbCourselist();
        tbCourselist.setCourseid(id);
        List<TbCourselist> courselist = tbCourseListService.selectList(tbCourselist);
        int i = tbCourseListService.selectCount(tbCourselist);
        logger.info("student select count ===>"+i);
        logger.info("student count ===>"+courselist.size());
        List<TbStudent> students = new ArrayList<>(courselist.size());
        for( TbCourselist uid : courselist ){
            TbStudent tbStudent = tbUserService.selectById(uid.getStudentid());
            students.add(tbStudent);
        }
        model.addAttribute("students", students);
        return "course/student";
    }
}
