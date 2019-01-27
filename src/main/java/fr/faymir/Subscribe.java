package fr.faymir;

import fr.faymir.Model.ConnectedUsers;
import fr.faymir.Model.Type;
import fr.faymir.Model.ServerUser;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.UUID;

@Path("subscribe")
public class Subscribe {
    private static final String defaultUsername = "#$#%$%%&&dsfduhsi$%*⁽";
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@DefaultValue(defaultUsername) @QueryParam("username") String username, @Context UriInfo uriInfo, @Context HttpServletRequest request){
        ConnectedUsers.updateUsersStatus();
        JSONObject obj = new JSONObject();
        System.out.println("username = [" + username + "], uriInfo = [" + uriInfo.getRequestUri().getQuery() + "]");
        obj.put("Type", Type.BAD_USERNAME);
        if (uriInfo.getQueryParameters().size() == 0 || username.equals(defaultUsername)) {
            obj.put("message", "No username provided");
        }
        else if(ConnectedUsers.contains(username)){
            obj.put("message", "Username already exist");
        }
        else if(username.isEmpty()){
                obj.put("message", "Empty username not allowed");
        }
        else if(username.equals("delete")){
            ConnectedUsers.connectedServerUsers.clear();
            obj.put("message", "User list Cleared");
        }
        else{
            String uuid = UUID.randomUUID().toString();
            ConnectedUsers.add(new ServerUser(request.getRemoteAddr(), true, username, uuid));
            obj.put("Type", Type.GOOD_USERNAME);
            obj.put("usersNumber", ConnectedUsers.connectedServerUsers.size());
            obj.put("message", "ok");
            obj.put("uniqueId", uuid);
            obj.put("users", ConnectedUsers.connectedServerUsers);
//            System.out.println("users = [" + (SerializationUtils.serialize(ConnectedUsers.connectedServerUsers)) + "]");
//            System.out.println("byte array = [" + SerializationUtils.serialize(ConnectedUsers.connectedServerUsers) + "]");
//            byte[] base64Decoded = DatatypeConverter.parseBase64Binary(base64Encoded);
        }
        return obj.toString();
    }



}
