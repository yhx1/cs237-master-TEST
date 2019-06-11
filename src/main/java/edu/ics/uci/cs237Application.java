package edu.ics.uci;

import edu.ics.uci.resources.WebsocketResource;
import edu.ics.uci.resources.HelloWorldResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereServlet;

import javax.servlet.ServletRegistration;


public class cs237Application extends Application<cs237Configuration> {

    public static void main(final String[] args) throws Exception {
        new cs237Application().run(args);
    }

    @Override
    public String getName() {
        return "cs237";
    }

    @Override
    public void initialize(final Bootstrap<cs237Configuration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final cs237Configuration configuration,
                    final Environment environment) {
        final HelloWorldResource resource = new HelloWorldResource();
        environment.jersey().register(resource);
        // TODO: implement application

        AtmosphereServlet servlet = new AtmosphereServlet();
        servlet.framework().addInitParameter(ApplicationConfig.ANNOTATION_PACKAGE, WebsocketResource.class.getPackage().getName());
        servlet.framework().addInitParameter(ApplicationConfig.WEBSOCKET_SUPPORT, "true");
        ServletRegistration.Dynamic registration = environment.servlets().addServlet("atmosphere", servlet);
        registration.addMapping("/chat/*");
    }

}
