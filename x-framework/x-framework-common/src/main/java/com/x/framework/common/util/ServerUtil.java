package com.x.framework.common.util;

import com.x.framework.common.constants.CommonConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

/**
 * @author : xuemingqi
 * @since : 2023/1/10 16:50
 */
public class ServerUtil {

    /**
     * 获取traceId,获取不到设置为async可能是内部线程调用
     */
    public static String getTraceId() {
        String traceId = MDC.get(CommonConstant.TRACE_ID);
        return !StringUtils.isBlank(traceId) ? traceId : "async";
    }
}
