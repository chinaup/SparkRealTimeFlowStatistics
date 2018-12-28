package com.zhangbin.dao;

import com.zhangbin.domain.CategoryClickCount;
import com.zhangbin.domain.CategorySearchCount;
import com.zhangbin.utils.HBaseUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CategorySearchCountDAO {

    public List<CategorySearchCount> query(String day) {

        List<CategorySearchCount> list = new ArrayList();

        Map<String, Long> map = null;
        try {
            map = HBaseUtils.getInstance().query("category_search_count", day);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, Long> entry : map.entrySet()) {
            CategorySearchCount model = new CategorySearchCount();
            model.setSearch(entry.getKey());
            model.setValue(entry.getValue());
            list.add(model);
        }

        return list;
    }

//    public static void main(String[] args) throws IOException {
//        CategorySearchCountDAO dao = new CategorySearchCountDAO();
//        List<CategorySearchCount> list = dao.query("2018");
//        for (CategorySearchCount c : list) {
//            System.out.println(c.getValue());
//        }
//    }
}
