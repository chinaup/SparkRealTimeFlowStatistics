package com.zhangbin.dao;

import com.zhangbin.domain.CategoryClickCount;
import com.zhangbin.utils.HBaseUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CategoryClickCountDAO {

    public List<CategoryClickCount> query(String day) {

        List<CategoryClickCount> list = new ArrayList();

        Map<String, Long> map = null;
        try {
            map = HBaseUtils.getInstance().query("category_click_count", day);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, Long> entry : map.entrySet()) {
            CategoryClickCount model = new CategoryClickCount();
            model.setCategory(entry.getKey());
            model.setValue(entry.getValue());
            list.add(model);
        }

        return list;
    }

//    public static void main(String[] args) throws IOException {
//        CategoryClickCountDAO dao = new CategoryClickCountDAO();
//        List<CategoryClickCount> list = dao.query("2018");
//        for (CategoryClickCount c : list) {
//            System.out.println(c.getValue());
//        }
//    }
}
