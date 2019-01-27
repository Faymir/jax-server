package fr.faymir.Model;

import java.io.Serializable;
import java.util.Vector;

public class ConnectedUsers implements Serializable {
    public static Vector<ServerUser> connectedServerUsers = new Vector<>();
    public static long timeout = 10000;

    public static void resetUserTimer(String id){
        connectedServerUsers.forEach( u -> {
            if(u.getUniqueId().equals(id))
                u.resetTimer();
        });
    }

    public static void updateUsersStatus(){
        connectedServerUsers.forEach( u -> {
            u.updateOnlineStatus(timeout);
        });
    }

    public static Vector<ServerUser> get(){
        return connectedServerUsers;
    }

    public static void add(ServerUser serverUser){
        connectedServerUsers.add(serverUser);
    }

    public static boolean remove(ServerUser serverUser){
        return connectedServerUsers.remove(serverUser);
    }


    public static boolean contains(String username){
        for (ServerUser connectedServerUser : connectedServerUsers) {
            if (connectedServerUser.getUsername().equalsIgnoreCase(username))
                return true;
        }
        return false;
    }

    public static boolean idExist(String uniqueId){
        for (ServerUser connectedServerUser : connectedServerUsers) {
            if (connectedServerUser.getUniqueId().equalsIgnoreCase(uniqueId))
                return true;
        }
        return false;
    }
}
