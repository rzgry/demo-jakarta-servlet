package it.io.openliberty.sample;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.openliberty.sample.system.HelloServlet;

@RunWith(Arquillian.class)
public class WLPIntegrationClientIT {

    @Deployment(testable = false)
    public static EnterpriseArchive createDeployment() {
        return ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
                .addAsModule(ShrinkWrap.create(WebArchive.class, "test1.war").addClass(HelloServlet.class))
                .addAsModule(ShrinkWrap.create(JavaArchive.class, "test.jar").addClass(WLPIntegrationClientIT.class)
                        .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml"));
    }

    @Test
    public void shouldBeAbleToInvokeServletInDeployedWebApp(@ArquillianResource URL url) throws Exception {
        URL helloEndpoint = new URL(url, HelloServlet.URL_PATTERN);

        HttpURLConnection conn = (HttpURLConnection) helloEndpoint.openConnection();
        Assert.assertEquals(
                "The url: " + helloEndpoint + " response code should be 200 but was: " + conn.getResponseCode(),
                HttpURLConnection.HTTP_OK, conn.getResponseCode());

        String body = readAllAndClose(helloEndpoint.openStream());
        Assert.assertEquals("Verify that the servlet was deployed and returns expected result", HelloServlet.MESSAGE,
                body);
    }

    private String readAllAndClose(InputStream is) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int read;
            while ((read = is.read()) != -1) {
                out.write(read);
            }
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
        return out.toString();
    }

}