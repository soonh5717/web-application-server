package webserver.vo;

import java.util.Map;

public class RequestVO {
    private String method;
    private String url;
    private Map<String, String> headerMap;
    private Map<String, String> parameterMap;

    public RequestVO(String method, String url,
                     Map<String, String> headerMap, Map<String, String> parameterMap) {
        this.method = method;
        this.url = url;
        this.headerMap = headerMap;
        this.parameterMap = parameterMap;
    }

    public String getMethod() {
        return this.method;
    }

    public String getUrl() {
        return this.url;
    }

    public Map<String, String> getHeaderMap() {
        return this.headerMap;
    }

    public Map<String, String> getParameterMap() {
        return this.parameterMap;
    }

    @Override
    public String toString() {
        return "RequestUrlVO [method=" + method + ", url=" + url + ", headerMap=" + headerMap + ", parameterMap=" + parameterMap + "]";
    }
}
