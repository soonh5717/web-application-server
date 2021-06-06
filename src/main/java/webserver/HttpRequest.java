package webserver;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    private static final String URL_SEPARATOR = " ";
    private static final String URL_QUESTION_MARK = "?";

    private String method;
    private String path;
    private Map<String, String> headerMap;
    private Map<String, String> paramMap;

    public HttpRequest(InputStream in) {
        try {
            makeHttpRequestVO(in);
        } catch (Exception e) {
            log.error("makeHttpRequestVO : {}", e.getMessage());
        }
    }

    public HttpRequest(String method,
                       String path,
                       Map<String, String> headerMap,
                       Map<String, String> paramMap) {
        this.method = method;
        this.path = path;
        this.headerMap = headerMap;
        this.paramMap = paramMap;
    }

    public String getMethod() {
        return this.method;
    }

    public String getPath() {
        return this.path;
    }

    public Map<String, String> getHeaderMap() {
        return this.headerMap;
    }

    public String getHeaderMap(String key) {
        return this.headerMap.get(key);
    }

    public Map<String, String> getParamMap() {
        return this.paramMap;
    }

    public String getParamMap(String key) {
        return this.paramMap.get(key);
    }

    @Override
    public String toString() {
        return "RequestUrlVO [method=" + method + ", url=" + path + ", headerMap=" + headerMap + ", parameterMap=" + paramMap + "]";
    }

    private void makeHttpRequestVO(InputStream in) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String url = br.readLine();
        if (ObjectUtils.isEmpty(url)) {
            return;
        }

        setUrl(url);

        headerMap = new HashMap<>();
        url = br.readLine();
        while (ObjectUtils.isNotEmpty(url)) {
            log.info("url : {}", url);
            String[] header = url.split(":");
            headerMap.put(header[0].trim(), header[1].trim());
            url = br.readLine();
        }

        setParamMap(br);
    }

    private void setUrl(String url) {
        String[] requests = url.split(URL_SEPARATOR);
        method = requests[0];

        if ("POST".equals(method)) {
            path = requests[1];
            return;
        }

        int pathIndex = requests[1].indexOf(URL_QUESTION_MARK);
        if (pathIndex == -1) {
            path = requests[1];
        } else {
            path = requests[1].substring(0, pathIndex);
            paramMap = HttpRequestUtils.parseQueryString(requests[1].substring(pathIndex + 1));
        }
    }

    private void setParamMap(BufferedReader br) throws IOException {
        if ("POST".equals(method)) {
            int contentLength = Integer.parseInt(headerMap.get("Content-Length"));
            String body = IOUtils.readData(br, contentLength);
            paramMap = HttpRequestUtils.parseQueryString(body);
        }
    }
}
