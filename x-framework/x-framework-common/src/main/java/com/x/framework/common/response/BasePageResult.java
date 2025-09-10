package com.x.framework.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : xuemingqi
 * @since : 2023/1/13 17:45
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasePageResult<T> implements Serializable {

    private PageDataResult pageData;

    private T results;

    public static <T> BasePageResult<T> getInstance(Integer page, Integer pageSize, Integer total, T results) {
        int pageTotal = (total - 1) / pageSize + 1;
        PageDataResult pageData = PageDataResult.builder()
                .page(page)
                .pageTotal(pageTotal)
                .pageSize(pageSize)
                .hasNextPages(page < pageTotal)
                .build();
        BasePageResult<T> pageResult = new BasePageResult<>();
        pageResult.setPageData(pageData);
        pageResult.setResults(results);
        return pageResult;
    }

}
