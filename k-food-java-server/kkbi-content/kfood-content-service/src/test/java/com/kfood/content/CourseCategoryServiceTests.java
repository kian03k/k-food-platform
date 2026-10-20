package com.kfood.content;

import com.kfood.base.model.PageParams;
import com.kfood.base.model.PageResult;
import com.kfood.content.model.dto.CourseCategoryTreeDto;
import com.kfood.content.model.dto.QueryCourseParamsDto;
import com.kfood.content.model.po.CourseBase;
import com.kfood.content.service.CourseBaseInfoService;
import com.kfood.content.service.CourseCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2023/2/12 9:24
 */
@SpringBootTest
public class CourseCategoryServiceTests {

    @Autowired
    CourseCategoryService courseCategoryService;

    @Test
    public void testCourseCategoryService() {

        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryService.queryTreeNodes("1");
        System.out.println(courseCategoryTreeDtos);

    }
}
