import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServersListPageController {
    private static String[] availableServers;
    public static String chosenShow;
    public static String chosenIp;
    public static int chosenPort;
    private static String statusMsg = "";

    public ServersListPageController() throws IOException {
        try {
            Socket conn = new Socket("127.0.0.1", 1235);
            System.out.println("Established TCP Connection with http://127.0.0.1:1235");
            DataOutputStream dos =
                    new DataOutputStream(conn.getOutputStream());
            DataInputStream dis =
                    new DataInputStream(conn.getInputStream());
            String tmp = dis.readUTF();
            System.out.println(tmp);
            tmp = dis.readUTF();
            String msg = "";
            while (!tmp.trim().isEmpty()) {
                msg += tmp;
                tmp = dis.readUTF();
            }
            availableServers = msg.split("\n");
            dos.close();
            dis.close();
            conn.close();
        } catch (Exception e) {
            statusMsg = "مشکلی در دریافت کانال‌ها به وجود آمده است";
        }
    }

    @FXML
    Button joinGroupBtn;

    @FXML
    ListView serversList;

    @FXML
    Label statusLabel;

    @FXML
    private void joinGroupBtnClicked(ActionEvent event) throws IOException {
        event.consume();
        System.out.println("Joining Group");
        statusLabel.setText("در حال پیوستن به گروه ...");
        String listItem = (String) serversList.getSelectionModel().getSelectedItem();
        if(listItem == null) {
            statusLabel.setText("لطفاً یکی از گروه‌ها را انتخاب کنید");
            return;
        }
        String[] chosenOne = listItem.split(" -- ");
        System.out.println("You Chose Poorly -> " + chosenOne);
        chosenShow = chosenOne[0];
        chosenIp = chosenOne[1].split(":")[0];
        chosenPort = Integer.parseInt(chosenOne[1].split(":")[1]);
        statusLabel.setText("با موفقیت به گروه پیوستیم");


        Parent root = FXMLLoader.load(getClass().getResource("BroadcastPage.fxml"));
        Main.mainStage.setScene(new Scene(root, 1280, 720));
    }


    @FXML
    public void initialize() {
        String[] channelInfo;
        if(availableServers != null) {
            for (String server : availableServers) {
                channelInfo = server.split(":");
                serversList.getItems().add(channelInfo[0] + " -- " + channelInfo[1] + ":" + channelInfo[2]);
            }
        }
        statusLabel.setText(statusMsg);
    }
}
