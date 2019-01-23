package com.javawxid;

import com.alibaba.dubbo.config.annotation.Reference;
import com.javawxid.bean.SkuInfo;
import com.javawxid.bean.SkuLsInfo;
import com.javawxid.service.ListService;
import com.javawxid.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import net.minidev.json.JSONUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import javax.swing.text.Highlighter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListServiceApplicationTests {

    @Autowired
    JestClient jestClient;

    @Reference
    SkuService skuService;

    /**
     * 之前将数据保存到es中就可以使用以下的方法
     * @throws IOException
     */
    //@Test
    public void contextLoads() throws IOException {
        // 查询
        String dsl = getMyDsl();
        System.out.println(dsl);
        Search build = new Search.Builder(dsl).addIndex("gmall").addType("SkuLsInfo").build();
        SearchResult execute = jestClient.execute(build);
        List<SearchResult.Hit<SkuLsInfo, Void>> hits = execute.getHits(SkuLsInfo.class);
        List<SkuLsInfo> skuLsInfos = new ArrayList<>();
        for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
            SkuLsInfo source = hit.source;
            skuLsInfos.add(source);
        }
        System.out.println(skuLsInfos.size());
    }

    /**
     * 解决
     * @return
     */
    public String getMyDsl() {
        // 查询语句封装
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 联合查询
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
/*      TermQueryBuilder termQueryBuilder = new TermQueryBuilder(null, null);
        boolQueryBuilder.filter(termQueryBuilder);*/
        //分词查询：按skuName中有360查询
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", "360");
        boolQueryBuilder.must(matchQueryBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("skuName");
        searchSourceBuilder.highlight(highlightBuilder);
        return searchSourceBuilder.toString();
    }

    /**
     * 解决
     * @throws IOException
     */
    @Test
    public void test02() throws IOException {
        // 查询sku表中的所有数据skuInfo，查询之前先要再kibana中put制定数据结构
        List<SkuInfo> skuInfos = skuService.SkuListByCatalog3Id("4");

        // skuInfo转化成skuLsInfo
        for (SkuInfo skuInfo : skuInfos) {
            SkuLsInfo skuLsInfo = new SkuLsInfo();
            BeanUtils.copyProperties(skuInfo,skuLsInfo);
            // 将skuLsInfo导入到es中
            Index index = new Index.Builder(skuLsInfo).index("gmall").type("SkuLsInfo").id(skuLsInfo.getId()).build();
            jestClient.execute(index);
        }
    }

}


