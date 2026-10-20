package com.kfood.content.service;

import com.kfood.base.model.PageParams;
import com.kfood.base.model.PageResult;
import com.kfood.content.model.dto.AddCourseDto;
import com.kfood.content.model.dto.CourseBaseInfoDto;
import com.kfood.content.model.dto.EditCourseDto;
import com.kfood.content.model.dto.QueryCourseParamsDto;
import com.kfood.content.model.po.CourseBase;

/**
 * @author Mr.M
 * @version 1.0
 * @description 课程信息管理接口
 * @date 2023/2/12 10:14
 */
public interface CourseBaseInfoService {

    /**
     * 课程分页查询
     * @param pageParams 分页查询参数
     * @param courseParamsDto 查询条件
     * @param companyId 培训机构id
     * @return 查询结果
     */
    public PageResult<CourseBase> queryCourseBaseList(String companyId,PageParams pageParams, QueryCourseParamsDto courseParamsDto);

    /**
     * 新增课程
     * @param companyId 机构id
     * @param addCourseDto 课程信息
     * @return 课程详细信息
     */
    public CourseBaseInfoDto createCourseBase(String companyId,AddCourseDto addCourseDto);

    /**
     * 根据课程id查询课程信息
     * @param courseId 课程id
     * @return 课程详细信息
     */
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    /**
     * 修改课程
     * @param companyId 机构id
     * @param editCourseDto 修改课程信息
     * @return 课程详细信息
     */
    public CourseBaseInfoDto updateCourseBase(String companyId,EditCourseDto editCourseDto);

}
