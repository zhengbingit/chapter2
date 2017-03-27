package org.smart4j.chapter2.test;

/**
 * Created by zhengbinMac on 2017/3/25.
 */

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smart4j.chapter2.model.Customer;
import org.smart4j.chapter2.service.CustomerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单元测试
 */
public class CustomerServiceTest {

    private final CustomerService customerService;

    public CustomerServiceTest() {
        this.customerService = new CustomerService();
    }

    @Before
    public void init() {
        //TODO 初始化数据库
    }

    @Test
    public void getCustomerListTest() {
        List<Customer> customerList = customerService.getCustomerList();
        System.out.println(customerList);
        Assert.assertEquals(2, customerList.size());
    }

    @Test
    public void getCustomerTest()  {
        long id = 1;
        Customer customer = customerService.getCustomer(id);
        Assert.assertNotNull(customer);
    }

    @Test
    public void createCustomerTest() {
        Map<String, Object> fieldMap = new HashMap<String, Object>();
        fieldMap.put("name", "customer100");
        fieldMap.put("contact", "John");
        fieldMap.put("telephone", 12312312);
        boolean result = customerService.createCustomer(fieldMap);
        Assert.assertTrue(result);
    }

    @Test
    public void updateCustomerTest() {
        long id = 1;
        Map<String, Object> fieldMap = new HashMap<String, Object>();
        fieldMap.put("contact", "Eric");
        boolean result = customerService.updateCustomer(id, fieldMap);
        Assert.assertTrue(result);
    }

    @Test
    public void deleteCustomerTest() {
        long id = 1;
        boolean result = customerService.deleteCustomer(id);
        Assert.assertTrue(result);
    }

    @Test
    public void test1() {
        String sql = "update tablename set a=?, b=?, c=?,";
        sql = sql.substring(0, sql.lastIndexOf(","));
        System.out.println(sql);
    }
}
