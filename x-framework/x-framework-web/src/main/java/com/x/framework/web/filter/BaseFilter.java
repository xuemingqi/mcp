package com.x.framework.web.filter;

import com.x.framework.common.constants.CommonConstant;
import com.x.framework.common.util.IdUtil;
import com.x.framework.common.util.UserThreadLocalUtil;
import com.x.framework.web.util.ServletUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

/**
 * @author xuemingqi
 */
@AutoConfiguration
public class BaseFilter {

    @Bean
    public Filter filter() {
        return new Filter() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                try {
                    HttpServletRequest req = (HttpServletRequest) servletRequest;
                    String traceId = req.getHeader(CommonConstant.TRACE_ID);
                    if (StringUtils.isBlank(traceId)) {
                        traceId = IdUtil.randomAlphanumeric(10);
                    }
                    MDC.put(CommonConstant.TRACE_ID, traceId);
                    MDC.put(CommonConstant.IP, ServletUtil.getRemoteIP());
                    filterChain.doFilter(servletRequest, servletResponse);
                } finally {
                    MDC.clear();
                    UserThreadLocalUtil.clear();
                }
            }

            @Override
            public void destroy() {

            }

            @Override
            public void init(FilterConfig arg0) {
            }
        };
    }
}
