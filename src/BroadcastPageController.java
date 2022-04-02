import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BroadcastPageController {
    private static boolean playing = true;
    private static Image currentFrame;
    private static StringProperty statusMessage;

    static class GetBroadcastDataTask extends Task<Long> {
        private final int n;

        public GetBroadcastDataTask(int n) {
            this.n = n;
        }

        @Override
        protected Long call() throws Exception {
            long result = 0;
            try {
                MulticastSocket ms = new MulticastSocket(ServersListPageController.chosenPort);
                InetAddress ia = InetAddress.getByName(ServersListPageController.chosenIp);
                ms.joinGroup(ia);

                byte[] buffer;
                buffer = new byte[ms.getReceiveBufferSize()];
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                ms.receive(dp);
                byte[] buff = dp.getData();
                System.out.println("the length of the packet: " + buff.length);
                BufferedImage img = null;
                BufferedInputStream bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(buff));
                img = ImageIO.read(bufferedInputStream);
                bufferedInputStream.close();
                Image image = SwingFXUtils.toFXImage(img, null);
                currentFrame = image;
                System.out.println("Received " + img.getHeight() + "x" + img.getWidth() + ": " + System.currentTimeMillis());
                //ImageIO.write(img, "jpg", new File("C:\\Users\\ArmoPC\\Pictures\\img.jpg"));
                System.out.println("Done");
            } catch (IOException ex) {
                System.err.println(ex);
                updateMessage("خطایی هنگام دریافت اطلاعات رخ داده است!");
            }
            return result;
        }
    }

    @FXML
    Label statusLabel;

    public BroadcastPageController() {
        playing = true;
        statusMessage = new SimpleStringProperty("در حال پخش کانال ...");
    }

    private void startPlaying() {
        try {
            statusMessage.setValue("در حال پخش کانال ...");
            GetBroadcastDataTask task = new GetBroadcastDataTask(0);

            task.setOnRunning((succeesesEvent) -> {});

            task.setOnSucceeded((succeededEvent) -> {
                if(playing) {
                    currentImage.setImage(currentFrame);
                    startPlaying();
                }
            });

            ExecutorService executorService
                    = Executors.newFixedThreadPool(1);
            executorService.execute(task);
            executorService.shutdown();

        } catch (Exception e) {
            statusMessage.setValue("خطایی هنگام دریافت اطلاعات رخ داده است!");
        }
    }

    @FXML
    ImageView currentImage;

    @FXML
    Button pauseBtn;

    @FXML
    Button backBtn;

    @FXML
    Label chosenServer;

    @FXML
    Label playedShow;

    @FXML
    private void pauseBtnClicked(ActionEvent event) {
        event.consume();
        if(pauseBtn.getText().equalsIgnoreCase("توقف")) {
            pauseBtn.setText("ادامه");
            playing = false;
            statusMessage.setValue("دریافت اطلاعات متوقف شد");
        }
        else {
            pauseBtn.setText("توقف");
            playing = true;
            statusMessage.setValue("در حال پخش کانال ...");
            startPlaying();
        }
    }

    @FXML
    private void backBtnClicked(ActionEvent event) throws IOException {
        event.consume();
        playing = false;
        Parent root = FXMLLoader.load(getClass().getResource("ServersListPage.fxml"));
        Main.mainStage.setScene(new Scene(root, 1280, 720));
    }

    @FXML
    public void initialize() {
        statusLabel.textProperty().bind(statusMessage);
        chosenServer.setText(ServersListPageController.chosenIp + ":" + ServersListPageController.chosenPort);
        playedShow.setText(ServersListPageController.chosenShow);
        startPlaying();
    }

}
