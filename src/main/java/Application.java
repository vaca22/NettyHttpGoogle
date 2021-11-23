public class Application {

    public static void main(String[] args) throws Exception{
        HttpServer server = new HttpServer(9999);
        server.start();
    }
}
