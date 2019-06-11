package edu.ics.uci.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.*;
import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@AtmosphereHandlerService
public class WebsocketResource implements AtmosphereHandler{

    private static final String ROUND_BROADCASTER_NAME = "round";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    private Broadcaster getBroadcaster(BroadcasterFactory broadcasterFactory) {
        return broadcasterFactory.lookup(ROUND_BROADCASTER_NAME, true);
    }

    @Override
    public void onRequest(AtmosphereResource atmosphereResource) throws IOException{

        AtmosphereRequest request = atmosphereResource.getRequest();
        /*
        if (!request.getPathInfo().equals("/round")){
            atmosphereResource.getResponse().setStatus(HttpStatus.NOT_FOUND_404);
            atmosphereResource.write("Websocket endpoint not found");
            atmosphereResource.close();
            return;
        }*/
        String requestBodyJson = request.body().asString();


        BroadcasterFactory broadcasterFactory = atmosphereResource.getAtmosphereConfig().getBroadcasterFactory();
        Broadcaster broadcaster = getBroadcaster(broadcasterFactory);
        atmosphereResource.setBroadcaster(broadcaster);
        atmosphereResource.getResponse().setContentType(MediaType.APPLICATION_JSON);
        atmosphereResource.suspend();

        if (requestBodyJson!=null){
            Message message = OBJECT_MAPPER.readValue(requestBodyJson, Message.class);
            Response response = new Response(message.author, message.message);
            String json = OBJECT_MAPPER.writeValueAsString(response);
            System.out.println(json);
            broadcaster.broadcast(json);
        }



    }

    private boolean isBroadcast(AtmosphereResourceEvent event){
        return event.getMessage()!=null && !event.isCancelled() && !event.isClosedByClient() && !event.isClosedByApplication();
    }

    @Override
    public void onStateChange(AtmosphereResourceEvent event) throws IOException{
        AtmosphereResource atmosphereResource = event.getResource();

        if(isBroadcast(event)){
            atmosphereResource.write(event.getMessage().toString());

            switch(atmosphereResource.transport()){
                case WEBSOCKET:
                case STREAMING:
                    atmosphereResource.getResponse().flushBuffer();
                    break;
                default:
                    atmosphereResource.resume();
                    break;
            }
        }
    }

    @Override
    public void destroy(){

    }

}
