package com.zhangbin.sparkweb;

import com.zhangbin.dao.CategoryClickCountDAO;
import com.zhangbin.dao.CategorySearchCountDAO;
import com.zhangbin.domain.CategoryClickCount;
import com.zhangbin.domain.CategorySearchCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.*;


@RestController
public class SparkStatApp {

    private static Map<String,String> courses = new HashMap<>();
    static {
        courses.put("1","偶像爱情");
        courses.put("2","宫斗谋权");
        courses.put("3","玄幻史诗");
        courses.put("4", "都市生活");
        courses.put("5", "罪案谍战");
        courses.put("6", "历险科幻");
    }

    private static Map<String,String> search = new HashMap<>();
    static {
        search.put("cn.bing.com","必应");
        search.put("search.yahoo.com","雅虎");
        search.put("www.baidu.com","百度");
        search.put("www.sogou.com", "搜狗");
    }

    @Autowired
    CategoryClickCountDAO categoryClickCountDAO = new CategoryClickCountDAO();


    @RequestMapping(value = "CategoryClickCount", method = RequestMethod.POST)
    public List<CategoryClickCount> query_click() {
        //查询HBase数据库
        List<CategoryClickCount> list = categoryClickCountDAO.query("20181227");

        //把日期替换为类别
        for (CategoryClickCount c : list){
            c.setCategory(courses.get(c.getCategory().substring(9)));
        }

        return list;
    }


    @RequestMapping(value = "category", method = RequestMethod.GET)
    public ModelAndView echarts_click() {
        return new ModelAndView("CategoryClickCount");
    }



    @Autowired
    CategorySearchCountDAO categorySearchCountDAO = new CategorySearchCountDAO();


    @RequestMapping(value = "CategorySearchCount", method = RequestMethod.POST)
    public List<CategorySearchCount> query_search() {
        //查询HBase数据库
        List<CategorySearchCount> list = categorySearchCountDAO.query("20181227");
        List<CategorySearchCount> res = new ArrayList<>();
        Map<String,Long> map = new HashMap<>();

        //把网址替换为名称
        for (CategorySearchCount c : list){
            c.setSearch(search.get(c.getSearch().split("_")[1])+"_"+courses.get(c.getSearch().split("_")[2]));

            String search = c.getSearch().split("_")[0];
            if(map.containsKey(search)){
                map.put(search,c.getValue()+map.get(search));
            }
            else
                map.put(search,c.getValue());
        }

        for (Map.Entry<String, Long> entry : map.entrySet()) {
            CategorySearchCount model = new CategorySearchCount();
            model.setSearch(entry.getKey());
            model.setValue(entry.getValue());
            res.add(model);
        }
        return res;
    }


    @RequestMapping(value = "search", method = RequestMethod.GET)
    public ModelAndView echarts_search() {
        return new ModelAndView("CategorySearchCount");
    }



//    public static void main(String[] args) throws IOException {
//        SparkStatApp s = new SparkStatApp();
//        List<CategoryClickCount> list = s.query();
//        for (CategoryClickCount c : list) {
//            System.out.println(c.getDay()+"="+c.getValue());
//        }
//    }


//    public static void main(String[] args) throws IOException {
//        SparkStatApp s = new SparkStatApp();
//        List<CategorySearchCount> list = s.query_search();
//        for (CategorySearchCount c : list) {
//            System.out.println(c.getSearch()+"="+c.getValue());
//        }
//    }


}
