package com.torshare;


import com.despegar.sparkjava.test.SparkClient;
import com.despegar.sparkjava.test.SparkServer;
import com.torshare.webservice.WebService;
import org.junit.ClassRule;
import org.junit.Test;
import spark.servlet.SparkApplication;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created by tyler on 11/30/16.
 */
public class WebServiceTest {

    public static class TestContollerTestSparkApplication implements SparkApplication {
        @Override
        public void init() {
            try {
                WebService.main(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @ClassRule
    public static SparkServer<TestContollerTestSparkApplication> testServer = new SparkServer<>(WebServiceTest.TestContollerTestSparkApplication.class, 4567);

    @Test
    public void hello() throws Exception {
        SparkClient.UrlResponse response = testServer.getClient().doMethod("GET", "/hello", null);
        assertEquals(200, response.status);
        assertEquals("hello", response.body);
        assertNotNull(testServer.getApplication());
    }

    @Test
    public void searchResults() throws Exception {
        SparkClient.UrlResponse response = testServer.getClient().doMethod("GET",
                "/search" +
                        "?limit=4" +
                        "&page=1" +
                        "&orderBy=name_desc" +
                        "&q=corp" +
                        "&orderBy=seeders_desc" +

                        "", null);
        assertEquals(response.body, "{\"results\":[{\"age\":\"2016-10-01T07:00:00Z\",\"created\":\"2016-12-01T05:02:45Z\",\"id\":1,\"info_hash\":\"c6ca71741152a467c0dbaaa9802bedd69dee1714\",\"leechers\":32,\"name\":\"The Corporation 2003 (Dvdrip) XviD\",\"seeders\":25,\"size_bytes\":1400}],\"count\": 1,\"page\":1}");
        assertEquals(200, response.status);

    }
}
