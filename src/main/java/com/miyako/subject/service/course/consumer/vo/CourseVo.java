package com.miyako.subject.service.course.consumer.vo;

import com.miyako.subject.commons.domain.TbCourse;
import com.miyako.subject.commons.domain.TbTeacher;

import java.io.Serializable;

/**
 * ClassName CourseVo
 * Description //TODO
 * Author weila
 * Date 2019-08-10-0010 09:55
 */
public class CourseVo implements Serializable{

    private TbCourse course;
    private Integer grade;
    private TbTeacher teacher;
    private Integer surplus;

    public Integer getSurplus(){
        return surplus;
    }

    public void setSurplus(Integer surplus){
        this.surplus = surplus;
    }

    public TbCourse getCourse(){
        return course;
    }

    public void setCourse(TbCourse course){
        this.course = course;
    }

    public Integer getGrade(){
        return grade;
    }

    public void setGrade(Integer grade){
        this.grade = grade;
    }

    public TbTeacher getTeacher(){
        return teacher;
    }

    public void setTeacher(TbTeacher teacher){
        this.teacher = teacher;
    }

    @Override
    public String toString(){
        return "CourseVo{" +
               "course=" + course +
               ", grade=" + grade +
               ", teacher=" + teacher +
               ", surplus=" + surplus +
               '}';
    }
}
