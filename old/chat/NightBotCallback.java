package old_java.chat;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Path("/callback")
public class NightBotCallback {

    @GET
    @Produces("text/plain")
    public String getToken(@Context UriInfo uriInfo)
    {
        System.out.println(uriInfo.getAbsolutePath());
        return "Success!";
    }
}
