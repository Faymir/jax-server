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
import java.util.Vector;

@Path("subscribe")
public class Subscribe {
    private static final String defaultUsername = "#$#%$%%&&dsfduhsi$%*⁽";
    private static final String defaultuniqueId = "0000-0000-0000-0000";
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@DefaultValue(defaultUsername) @QueryParam("username") String username,
                      @DefaultValue(defaultuniqueId) @QueryParam("uniqueId") String uniqueId,
                      @DefaultValue("NONE") @QueryParam("localIp") String localIp,
                      @Context UriInfo uriInfo,
                      @Context HttpServletRequest request){
        ConnectedUsers.updateUsersStatus();
        JSONObject obj = new JSONObject();
        System.out.println("username = [" + username + "], ip = [" + request.getRemoteAddr() + "]");
        obj.put("Type", Type.BAD_USERNAME);
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        if (uriInfo.getQueryParameters().size() == 0 || username.equals(defaultUsername)) {
            obj.put("message", "No username provided");
        }
        else if(localIp.equals("NONE")){
            obj.put("message", "you didn't provided you localIp Adress");
        }
        else if(ConnectedUsers.contains(username)){
            if (!uniqueId.equals(defaultuniqueId) && ConnectedUsers.idExist(uniqueId)){
                fillReturnInfo(uniqueId, obj, ipAddress, localIp);
                ConnectedUsers.resetUserTimer(uniqueId);
            }
            else
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

            System.out.println("\n===================== ip = [" + ipAddress + "] =========== \n");
            ConnectedUsers.add(new ServerUser(ipAddress, true, username, uuid, localIp));
            fillReturnInfo(uuid, obj, ipAddress, localIp);
//            System.out.println("users = [" + (SerializationUtils.serialize(ConnectedUsers.connectedServerUsers)) + "]");
//            System.out.println("byte array = [" + SerializationUtils.serialize(ConnectedUsers.connectedServerUsers) + "]");
//            byte[] base64Decoded = DatatypeConverter.parseBase64Binary(base64Encoded);
        }
        return obj.toString();
    }

    private void fillReturnInfo(String uniqueId, JSONObject obj, String ipAddress, String localIp) {
        obj.put("Type", Type.GOOD_USERNAME);
        obj.put("usersNumber", ConnectedUsers.connectedServerUsers.size());
        obj.put("message", "ok");
        obj.put("uniqueId", uniqueId);
        Vector<ServerUser> users = (Vector<ServerUser>)ConnectedUsers.connectedServerUsers.clone();

        users.forEach( u -> {
            if (getFirstPart(u.getIp()).equals(getFirstPart(ipAddress))){
                if (!localIp.equals("NONE"))
                    u.setIp(u.getLocalIp());
            }
        });

        obj.put("users", users);
    }

    private String getFirstPart(String ip){
        if(!ip.contains("."))
            return ip;
        String [] list = ip.split(".");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.length - 1; i++) {
            sb.append(list[i]);
        }

        return sb.toString();
    }

}
