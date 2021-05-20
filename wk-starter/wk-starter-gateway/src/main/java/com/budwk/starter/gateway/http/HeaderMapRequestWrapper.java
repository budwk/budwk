package com.budwk.starter.gateway.http;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

/**
 * @author wizzer@qq.com
 */
@Slf4j
public class HeaderMapRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, String> headerMap = new HashMap<>();

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public HeaderMapRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * add a header with given name and value
     *
     * @param name
     * @param value
     */
    public void addHeader(String name, String value) {
        headerMap.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String headerValue = super.getHeader(name);
        if (headerMap.containsKey(name)) {
            headerValue = headerMap.get(name);
        }
        return headerValue;
    }

    /**
     * get the Header names
     */
    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> names = Collections.list(super.getHeaderNames());
        for (String name : headerMap.keySet()) {
            names.add(name);
        }
        return Collections.enumeration(names);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> values = Collections.list(super.getHeaders(name));
        if (headerMap.containsKey(name)) {
            values = Arrays.asList(headerMap.get(name));
        }
        return Collections.enumeration(values);
    }
}
