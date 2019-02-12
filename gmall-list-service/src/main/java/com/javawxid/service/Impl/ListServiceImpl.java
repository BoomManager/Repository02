package com.javawxid.service.Impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.javawxid.bean.BaseCatalog1;
import com.javawxid.bean.SkuLsInfo;
import com.javawxid.bean.SkuLsParam;
import com.javawxid.service.ListService;
import com.javawxid.mapper.BaseCatalog1Mapper;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ListServiceImpl implements ListService {
    @Autowired
    JestClient jestClient;

    @Autowired
    BaseCatalog1Mapper catalog1Mapper;

    @Override
    public List<BaseCatalog1> catalogJson() {
        List<BaseCatalog1> baseCatalog1List = catalog1Mapper.selectCatalog1();
        return baseCatalog1List;
    }

    @Override
    public List<SkuLsInfo> list(SkuLsParam skuLsParam) {
        List<SkuLsInfo> skuLsInfos = new ArrayList<>();
        // 执行查询之前先要去测试类中将test02（）方法执行一次，将数据库中的数据导入es中
        String dsl = getMyDsl(skuLsParam);
        Search build = new Search.Builder(dsl).addIndex("gmall").addType("SkuLsInfo").build();
        SearchResult execute = null;
        try {
            execute = jestClient.execute(build);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Long total = execute.getTotal();
        if(total>0){
            List<SearchResult.Hit<SkuLsInfo, Void>> hits = execute.getHits(SkuLsInfo.class);
            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
                SkuLsInfo source = hit.source;
                Map<String, List<String>> highlight = hit.highlight;
                if(highlight!=null){
                    List<String> skuName = highlight.get("skuName");
                    String s = skuName.get(0);
                    source.setSkuName(s);
                }
                skuLsInfos.add(source);
            }
        }
        // 取聚合值
        List<String> attrValueIdList=new ArrayList<>();
        MetricAggregation aggregations = execute.getAggregations();
        TermsAggregation groupby_attr = aggregations.getTermsAggregation("valueIdAggs");
        if(groupby_attr!=null){
            List<TermsAggregation.Entry> buckets = groupby_attr.getBuckets();
            for (TermsAggregation.Entry bucket : buckets) {
                attrValueIdList.add( bucket.getKey()) ;
            }
        }
        return skuLsInfos;
    }


    public String getMyDsl(SkuLsParam skuLsParam) {
        // 查询语句封装
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 联合查询，java操作es的多条件组合精确查询BoolQuery
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        // 三级分类id
        String catalog3Id = skuLsParam.getCatalog3Id();
        if(StringUtils.isNotBlank(catalog3Id)){
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }
        // 分类属性值
        String[] valueId = skuLsParam.getValueId();
        if(valueId!=null&&valueId.length>0){
            for (String id : valueId) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", id);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        // 关键字
        String keyword = skuLsParam.getKeyword();
        if(StringUtils.isNotBlank(keyword)){
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }
        searchSourceBuilder.query(boolQueryBuilder);
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red;font-weight:bolder;'>");
        highlightBuilder.field("skuName");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlight(highlightBuilder);
        // 查询数量
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(100);
        // 属性值id的聚合
        TermsBuilder groupby_attr = AggregationBuilders.terms("valueIdAggs").field("skuAttrValueList.valueId");
        searchSourceBuilder.aggregation(groupby_attr);
        return searchSourceBuilder.toString();
    }
}
