package com.example.demo.test;

import com.example.demo.comm.tools.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ProjectName: demo
 * @Author: 王先望
 * @Description: 测试链接
 * @Date: 2021/8/19 16:06
 * @Version: 1.0
 */
@Controller
@RequestMapping("/test")
@Slf4j
public class test {
    @RequestMapping("/demo")
    public JsonResult demo(){
        return JsonResult.getSuccessResponse("成功").setData("nb");
    }

}
