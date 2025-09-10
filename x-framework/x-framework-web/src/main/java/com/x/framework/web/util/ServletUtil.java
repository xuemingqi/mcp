package com.x.framework.web.util;

import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;


@Slf4j
public class ServletUtil {
    private static final String[] ipHeaders = {"X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "x-forwarded-for"};


    /**
     * 获取请求对象
     *
     * @return HttpServlet请求对象
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Objects.requireNonNull(requestAttributes).getRequest();
    }


    /**
     * 获取响应对象
     *
     * @return HttpServlet响应对象
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Objects.requireNonNull(requestAttributes).getResponse();
    }


    /**
     * 获取全路径
     *
     * @return 全路径
     */
    public static String getRequestURL() {
        return getRequest().getRequestURL().toString();
    }


    /**
     * 获取项目路径
     *
     * @return 项目路径
     */
    public static String getContextPath() {
        return getRequest().getContextPath();
    }


    /**
     * 获取项目路径后续部分
     *
     * @return 项目路径后续部分
     */
    public static String getServletPath() {
        return getRequest().getServletPath();
    }


    /**
     * 获取除domain的路径
     *
     * @return 除domain的路径
     */
    public static String getRequestURI() {
        return getRequest().getRequestURI();
    }

    /**
     * 获取除请求头内容
     *
     * @param key 请求头
     * @return 请求头内容
     */

    public static String getHeader(String key) {
        return getRequest().getHeader(key);
    }

    /**
     * 设置请求头
     *
     * @param key   请求头
     * @param value 请求头内容
     */
    public static void setHeader(String key, String value) {
        getRequest().setAttribute(key, value);
    }


    /**
     * 获取客户端浏览器信息
     *
     * @return 客户端浏览器信息
     */
    public static UserAgent getUserAgent() {
        return UserAgent.parseUserAgentString(getRequest().getHeader("User-Agent"));
    }


    /**
     * 获取服务名称
     *
     * @return 服务名称
     */
    public static String getServerName() {
        return getRequest().getServerName();
    }


    /**
     * 获取服务监听端口
     *
     * @return 服务监听端口
     */
    public static int getServerPort() {
        return getRequest().getServerPort();
    }


    /**
     * 获取远程请求IP地址
     *
     * @return 远程请求IP地址
     */
    public static String getRemoteIP() {
        final HttpServletRequest request = getRequest();
        for (final String ipHeader : ipHeaders) {
            final String ip = request.getHeader(ipHeader);
            if (ip != null && !ip.trim().isEmpty()) {
                return ip.trim();
            }
        }
        return request.getRemoteAddr();
    }

    /**
     * 获取远程请求IP地址
     *
     * @return 远程请求IP地址
     */
    public static String getRemoteIP(HttpHeaders headers) {
        for (final String ipHeader : ipHeaders) {
            final String ip = headers.getFirst(ipHeader);
            if (ip != null && !ip.trim().isEmpty()) {
                return ip.trim();
            }
        }
        return null;
    }


    /**
     * 获取国家标识
     */
    public static Locale getRemoteCountry() {
        final HttpServletRequest request = getRequest();
        return request.getLocale();
    }


    /**
     * 获取远程请求端口
     *
     * @return 远程请求端口
     */
    public static int getRemotePort() {
        final HttpServletRequest request = getRequest();
        try {
            return Integer.parseInt(request.getHeader("X-Real-PORT"));
        } catch (final Exception e) {
            return request.getRemotePort();
        }
    }


    /**
     * 获取请求Scheme
     *
     * @return requestScheme
     */
    public static String getRequestScheme() {
        if (StringUtils.isBlank(getRequest().getHeader("X-Forwarded-Scheme"))) {
            return getRequest().getScheme();
        }
        return getRequest().getHeader("X-Forwarded-Scheme");
    }


    /**
     * 跳转到指定URL
     *
     * @param url URL地址
     */
    public static void redirectUrl(final String url) {
        final HttpServletResponse response = getResponse();
        try {
            response.sendRedirect(url);
            response.flushBuffer();
        } catch (final Exception e) {
            log.error("", e);
        }
    }

    public static BufferedInputStream getBufferedInputStream() throws IOException {
        return new BufferedInputStream(getRequest().getInputStream(), 32 * 1024);
    }


    public static BufferedOutputStream getBufferedOutputStream() throws IOException {
        return new BufferedOutputStream(getResponse().getOutputStream(), 32 * 1024);
    }
}
