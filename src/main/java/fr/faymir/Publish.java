package fr.faymir;

import fr.faymir.Model.ConnectedUsers;
import fr.faymir.Model.ScanMessage;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

/**
 * Root resource (exposed at "test" path)
 */
@Path("publish")
public class Publish {
    private static final String defaultData = "#$#%$%%&&dsfduhsi$%*⁽";
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String post(String data, @Context UriInfo uriInfo){

        if (!data.contains("uniqueId")) throw new UnauthorizedException();

        JSONObject infos = new JSONObject(data);
        if (!ConnectedUsers.idExist(infos.getString("uniqueId")))   throw new UnauthorizedException();

            try{
                ConnectedUsers.resetUserTimer(infos.getString("uniqueId"));
            }
            catch (JSONException e){
                e.printStackTrace();
                return new JSONObject("{\"Type\": \"error\", \"message\":\"invalid request\"}").toString();
            }
        System.out.println("data = [" + data + "], uriInfo = [" + uriInfo + "]");

        ConnectedUsers.updateUsersStatus();
        MultivaluedMap<String, String> params= uriInfo.getQueryParameters();
        System.out.println("data = [" + data + "], uriInfo = [" + uriInfo.getRequestUri() + "]");
        JSONObject obj = new JSONObject();
        obj
            .put("Type", ScanMessage.ScanType.RETURN_INFORMATION)
            .put("message", "ok")
            .put("users", ConnectedUsers.get());
        return obj.toString();
    }
}
