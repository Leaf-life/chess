package server;

import chess.*;
import dataaccess.*;
import dataaccess.SqlAccessAuth;
import service.ChessService;

public class ServerMain {
    public static void main(String[] args) {
        try {
            var port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }
            AuthAccess authaccess = new SqlAccessAuth();
            GameAccess gameaccess = new SqlAccessGame();
            UserAccess useraccess = new SqlAccessUser();
            ChessService service = new ChessService(useraccess, gameaccess, authaccess);
            Server server = new Server(service);
            server.run(port);


            System.out.println("♕ 240 Chess Server");
        }catch (Throwable ex){
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}
