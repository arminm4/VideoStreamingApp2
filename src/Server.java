import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;
import java.util.LinkedList;

public class Server {
    private static LinkedList<LinkedList<String>> serversList;
    private static final int serverCount = 8;
    private static final int imageCount = 500;
    private static File[][] images;
    private static byte ttl = (byte) 1;

    private static class ServerListThread extends Thread{
        private Socket s1;
        public ServerListThread(Socket ts1){
            s1 = ts1;
        }
        public void run () {
            try{
                OutputStream s1out = s1.getOutputStream();
                DataOutputStream dos = new DataOutputStream (s1out);
                dos.writeUTF("\nHi there \nHere\'s The List of Available Servers:\n");
                String[] socketAddress;
                for (LinkedList server : serversList) {
                    socketAddress = ((String) server.get(1)).split(":");
                    dos.writeUTF(server.get(0) + ":" + socketAddress[0] + ":" + socketAddress[1] + "\n");
                }
                dos.writeUTF(" ");
                dos.close();
                s1out.close();
                s1.close();

            } catch(IOException e){
            }
        }
    }

    private static class MultiCastServerThread extends Thread{
        private String ip;
        private int port;
        private int serverNo;
        private String showPlaying;

        public MultiCastServerThread(String ip, int port, int serverNo, String showPlaying){
            this.ip = ip;
            this.port = port;
            this.serverNo = serverNo;
            this.showPlaying = showPlaying;
        }
        public void run () {
            try {
                MulticastSocket ms = new MulticastSocket(port);
                InetAddress ia = InetAddress.getByName(ip);
                System.out.println("Multicast Server on " + ip + ":" + port);
                ms.setTimeToLive(ttl);
                ms.joinGroup(ia);

                while(true) {
                    for (File file : images[serverNo]) {
                        BufferedImage image = ImageIO.read(file);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "jpg", baos);
                        byte[] bytes = baos.toByteArray();
                        DatagramPacket dp = new DatagramPacket(bytes, bytes.length, ia, port);
                        ms.send(dp);
                        Thread.sleep(16);
                    }
                    //BufferedImage image = ImageIO.read(new File("C:\\Users\\ArmoPC\\Pictures\\s.jpg"));
                }
            } catch (IOException ex) {
                System.err.println(ex);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String args[]) throws IOException {
        ServerSocket s = new ServerSocket(1235);
        System.out.println("TCP Server Running on http://127.0.0.1:1235");
        serversList = new LinkedList<>();
        for(int i = 0; i < serverCount; i++) {
            serversList.add(new LinkedList<>());
        }
        //Random randomIp = new Random();
        //Random randomPort = new Random();

        serversList.get(0).add("Breaking Bad");
        serversList.get(0).add("224.0.0.0:1235");
        serversList.get(0).add(imageCount + "");

        serversList.get(1).add("Attack on Titan");
        serversList.get(1).add("224.0.1.2:1545");
        serversList.get(1).add(imageCount + "");

        serversList.get(2).add("Vikings");
        serversList.get(2).add("225.1.1.2:1232");
        serversList.get(2).add(imageCount + "");

        serversList.get(3).add("Game of Thrones");
        serversList.get(3).add("229.1.1.3:1435");
        serversList.get(3).add(imageCount + "");

        serversList.get(4).add("Peaky Blinders");
        serversList.get(4).add("228.2.0.0:1235");
        serversList.get(4).add(imageCount + "");

        serversList.get(5).add("House of Cards");
        serversList.get(5).add("226.3.1.2:1545");
        serversList.get(5).add(imageCount + "");

        serversList.get(6).add("The Blacklist");
        serversList.get(6).add("227.4.1.2:1232");
        serversList.get(6).add(imageCount + "");

        serversList.get(7).add("Person of Interest");
        serversList.get(7).add("225.5.1.3:1435");
        serversList.get(7).add(imageCount + "");

        images = new File[serverCount][imageCount];
        for (int i = 0; i < serverCount; i++) {
            for (int j = 0; j < Integer.parseInt(serversList.get(i).get(2)); j++) {
                images[i][j] = new File("./images/server" + (i+1) + "/" + (j+1) + ".jpg");
            }
        }
        int i = 0;
        String[] socketAddress;
        for (LinkedList server : serversList) {
            int serverNo = i;
            socketAddress = ((String) server.get(1)).split(":");
            MultiCastServerThread m = new MultiCastServerThread(socketAddress[0], Integer.parseInt(socketAddress[1]), serverNo, (String) server.get(0));
            m.start();
            i++;
        }
        while(true){
            Socket connection=s.accept();
            ServerListThread slt = new ServerListThread(connection);
            slt.start();
        }
    }
}


