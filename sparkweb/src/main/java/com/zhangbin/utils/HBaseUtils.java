package com.zhangbin.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class HBaseUtils {

    HBaseAdmin admin = null;
    Configuration configration = null;


    private HBaseUtils(){
        configration = new Configuration();
        configration.set("hbase.zookeeper.quorum", "master:2181");
        configration.set("hbase.rootdir", "hdfs://master:9000/hbase");
        try {
            admin = new HBaseAdmin(configration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HBaseUtils instance = null;

    public static synchronized HBaseUtils getInstance() {
        if (null == instance) {
            instance = new HBaseUtils();
        }
        return instance;
    }

    /**
     * 根据表名获取htable实例
     * @param tableName
     * @return
     */
    public  HTable getHtable(String tableName){
        HTable table = null;
        try {
            table = new HTable(configration,tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return table;

    }

    /**
     * 添加数据到hbase里面
     * @param tableName 表名
     * @param rowKey 对应key的值
     * @param cf    hbase列簇
     * @param colum hbase对应的列
     * @param value hbase对应的值
     */
    public  void put(String tableName,String rowKey,String cf,String colum,String value){
        HTable table = getHtable(tableName);
        Put put = new Put(Bytes.toBytes(rowKey));
        put.add(Bytes.toBytes(cf),Bytes.toBytes(colum),Bytes.toBytes(value));
        try {
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据表名输入条件获取 Hbase 的记录数
     */
    public Map<String, Long> query(String tableName, String condition) throws IOException {
        Map<String, Long> map = new HashMap<>();

        HTable table = getHtable(tableName);

        String cf = "info";
        String qualifier = "click_count";

        Scan scan = new Scan();
        Filter filter = new PrefixFilter(Bytes.toBytes(condition));
        scan.setFilter(filter);
        ResultScanner rs = table.getScanner(scan);

        for (Result result : rs) {
            String row = Bytes.toString(result.getRow());
            long clickCount = Bytes.toLong(result.getValue(cf.getBytes(), qualifier.getBytes()));
            map.put(row, clickCount);
        }
        return map;
    }


//    public static void main(String[] args) throws IOException {
//        Map<String, Long> map = HBaseUtils.getInstance().query("category_click_count", "20181226");
//
//        for (Map.Entry<String, Long> entry : map.entrySet()) {
//            System.out.println(entry.getKey() + " : " + entry.getValue());
//        }
//    }

}
