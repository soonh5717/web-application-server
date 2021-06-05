package webserver;

import org.junit.Assert;
import org.junit.Test;

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
        HttpRequest httpRequest = new HttpRequest(in);

        // then
        Assert.assertEquals("GET", httpRequest.getMethod());
        Assert.assertEquals("/user/create", httpRequest.getPath());
        Assert.assertEquals("keep-alive", httpRequest.getHeaderMap().get("Connection"));
        Assert.assertEquals("soon", httpRequest.getParamMap().get("userId"));
    }

    @Test
    public void request_POST() throws Exception {
        // given
        InputStream in = new FileInputStream(new File(TEST_DIRECTORY + TXT_HTTP_POST));

        // when
        HttpRequest httpRequest = new HttpRequest(in);

        // then
        Assert.assertEquals("POST", httpRequest.getMethod());
        Assert.assertEquals("/user/create", httpRequest.getPath());
        Assert.assertEquals("keep-alive", httpRequest.getHeaderMap().get("Connection"));
        Assert.assertEquals("soon", httpRequest.getParamMap().get("userId"));
    }
}
