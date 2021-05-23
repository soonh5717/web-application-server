package webserver.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;
import webserver.vo.RequestVO;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestBuilder {
    private static final String URL_SEPARATOR = " ";

    private static final Logger log = LoggerFactory.getLogger(RequestBuilder.class);

    public RequestVO buildRequestUrlVO(BufferedReader br) throws IOException {
        String urlLine = br.readLine();
        if (urlLine == null) {
            return null;
        }

        String[] requests = urlLine.split(URL_SEPARATOR);
        String[] urls = requests[1].split("\\?");

        String method = requests[0];
        String url = urls[0];

        Map<String, String> headerMap = new HashMap<String, String>();
        while (!"".equals(urlLine)) {
            urlLine = br.readLine();

            String[] requestHeader = urlLine.split(":");
            if (requestHeader.length == 2) {
                headerMap.put(requestHeader[0], requestHeader[1].trim());
            }
        };

        Map<String, String> parameterMap = null;
        if ("GET".equals(method) && urls.length == 2) {
            parameterMap = HttpRequestUtils.parseQueryString(urls[1]);
        }

        if ("POST".equals(method)) {
            int contentLength = Integer.parseInt(headerMap.get("Content-Length"));
            String body = IOUtils.readData(br, contentLength);
            log.info("body : {}", body);
            parameterMap = HttpRequestUtils.parseQueryString(body);
        }

        return new RequestVO(method, url, headerMap, parameterMap);
    }

}
