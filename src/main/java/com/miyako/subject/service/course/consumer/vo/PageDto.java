package com.miyako.subject.service.course.consumer.vo;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName PageDto
 * Description //TODO
 * Author weila
 * Date 2019-08-10-0010 09:59
 */
public class PageDto<T> implements Serializable{

    private List<T> data;
    /** 总计条数 **/
    private long size;
    /** 每页条数 **/
    private long pageSize;
    /** 总计页数 **/
    private long pageNums;

    public List<T> getData(){
        return data;
    }

    public void setData(List<T> data){
        this.data = data;
    }

    public long getSize(){
        return size;
    }

    public void setSize(long size){
        this.size = size;
    }

    public long getPageSize(){
        return pageSize;
    }

    public void setPageSize(long pageSize){
        this.pageSize = pageSize;
    }

    public long getPageNums(){
        return pageNums;
    }

    public void setPageNums(long pageNums){
        this.pageNums = pageNums;
    }
}
