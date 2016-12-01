package com.torshare;


import com.despegar.sparkjava.test.SparkClient;
import com.despegar.sparkjava.test.SparkServer;
import com.torshare.webservice.WebService;
import org.junit.ClassRule;
import org.junit.Test;
import spark.servlet.SparkApplication;

import static junit.framework.Assert.assertTrue;
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
                        //"&q=corp" +
                        "&orderBy=seeders_desc" +
                        "", null);
        assertTrue(response.body.contains("c6ca71741152a467c0dbaaa9802bedd69dee1714"));
        assertTrue(response.body.contains("c6ca71741152a467c0dbaaa9sdfbedd69dee1714"));
        assertEquals(200, response.status);

    }
}
