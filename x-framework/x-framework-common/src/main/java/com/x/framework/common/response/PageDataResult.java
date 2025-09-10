package com.x.framework.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageDataResult {

    private Integer page;

    private Integer pageTotal;

    private Integer pageSize;

    private Boolean hasNextPages;

}
