package com.hcr;

import com.hcr.pojo.Stu;
import org.elasticsearch.action.index.IndexRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationShop.class)
public class ESTest {

    /**
     * 不建议使用 ElasticsearchTemplate 对索引进行管理（创建索引，更新映射，删除索引）
     * 索引就像是数据库或者数据库中的表，我们平时是不会通过Java代码频繁的去创建修改删除数据库或者表的
     * 我们只会针对数据做CRUD的操作
     * 在es中也是一样，尽量使用 ElasticsearchTemplate 对文档数据做CRUD的操作
     */

    @Autowired
    private ElasticsearchTemplate es;

    @Test
    public void createIndexStu(){

        Stu stu = new Stu();
        stu.setAge(18);
        stu.setName("arvin");
        stu.setStuId(1001L);
        stu.setSign("i am s man");
        stu.setDescription("I w s");

        //构建索引查询
        IndexQuery indexQuery = new IndexQueryBuilder().withObject(stu).build();
        es.index(indexQuery);
    }

    /**
     * del
     * 此操作会将stu中所有数据删除
     */
    @Test
    public void delIndexStu(){
        es.deleteIndex(Stu.class);
    }

    /**
     * Upd
     */
    @Test
    public void updIndexStu(){
        Map<String,Object> sourceMap = new HashMap<>();
        sourceMap.put("age",33);

        //对修改体进行封装
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source(sourceMap);

        //update Sql语句
        UpdateQuery updateQuery = new UpdateQueryBuilder()
                                                    .withClass(Stu.class)
                                                    .withId("1002")
                                                    .withIndexRequest(indexRequest)
                                                    .build();
        es.update(updateQuery);
    }

    @Test
    private void getIndexStu(){
        GetQuery query = new GetQuery();
        query.setId("1002");
        Stu stu = es.queryForObject(query,Stu.class);
        System.out.println(stu);
    }

    @Test
    private void delIndexStuDoc(){
        es.delete(Stu.class,"1002");
    }
}
