package com.zhangbin.dao

import com.zhangbin.domain.{CategoryClickCount, CategorySearchClickCount}
import com.zhangbin.utils.HBaseUtils
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable.ListBuffer


object CategorySearchClickCountDAO {

  val tableName = "category_search_count"
  val cf = "info"
  val qualifier = "click_count"

  //保存数据到HBase
  def save(list: ListBuffer[CategorySearchClickCount]): Unit ={
    val table = HBaseUtils.getInstance.getHtable(tableName)
    for(els <- list){
      table.incrementColumnValue(Bytes.toBytes(els.day_search_categoryId),Bytes.toBytes(cf),Bytes.toBytes(qualifier),els.clickCount)
    }
  }


  def count(day_search_categoryId:String):Long = {
    val table = HBaseUtils.getInstance.getHtable(tableName)
    val get = new Get(Bytes.toBytes(day_search_categoryId))
    val value = table.get(get).getValue(Bytes.toBytes(cf),Bytes.toBytes(qualifier))

    if(value == null)
      0L
    else
      Bytes.toLong(value)
  }
}


















