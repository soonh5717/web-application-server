package webserver.component;

import org.junit.Assert;
import org.junit.Test;
import webserver.vo.HttpRequestVO;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class HttpRequestTest {
    private static final String TEST_DIRECTORY = "./src/test/resources/";
    private static final String TXT_HTTP_GET = "Http_GET.txt";
    private static final String TXT_HTTP_POST = "Http_POST.txt";

    @Test
    public void request_GET() throws Exception {
        // given
        InputStream in = new FileInputStream(new File(TEST_DIRECTORY + TXT_HTTP_GET));

        // when
        HttpRequestVO httpRequestVO = new HttpRequestVO(in);

        // then
        Assert.assertEquals("GET", httpRequestVO.getMethod());
        Assert.assertEquals("/user/create", httpRequestVO.getPath());
        Assert.assertEquals("keep-alive", httpRequestVO.getHeaderMap().get("Connection"));
        Assert.assertEquals("soon", httpRequestVO.getParamMap().get("userId"));
    }

    @Test
    public void request_POST() throws Exception {
        // given
        InputStream in = new FileInputStream(new File(TEST_DIRECTORY + TXT_HTTP_POST));

        // when
        HttpRequestVO httpRequestVO = new HttpRequestVO(in);

        // then
        Assert.assertEquals("POST", httpRequestVO.getMethod());
        Assert.assertEquals("/user/create", httpRequestVO.getPath());
        Assert.assertEquals("keep-alive", httpRequestVO.getHeaderMap().get("Connection"));
        Assert.assertEquals("soon", httpRequestVO.getParamMap().get("userId"));
    }
}
