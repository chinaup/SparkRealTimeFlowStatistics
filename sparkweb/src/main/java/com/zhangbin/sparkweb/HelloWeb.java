package com.zhangbin.sparkweb;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@RestController
public class HelloWeb {

    @RequestMapping(value = "hello",method = RequestMethod.GET)
    public String hello(){
       return "hellospoot";
    }

    @RequestMapping(value = "test1", method = RequestMethod.GET)
    public ModelAndView firstDemo() {
        return new ModelAndView("test1");
    }

    @RequestMapping(value = "test2", method = RequestMethod.GET)
    public ModelAndView secondDemo() {
        return new ModelAndView("test2");
    }
}
