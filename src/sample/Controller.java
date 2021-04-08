package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller {
    //11.0.1
    @FXML
    TextArea textArea;

    @FXML
    TextField textField;

    @FXML
    HBox bottomBox;

    @FXML
    HBox upperPanel;

    @FXML
    TextField loginField;

    @FXML
    TextField passwordField;

    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    public static final String ADDRESS = "localhost";
    public static final int PORT = 8008;


    private boolean isAuth;
    public void setAuth(boolean auth) {
        this.isAuth = auth;
        if (!isAuth){
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);

            bottomBox.setVisible(false);
            bottomBox.setManaged(false);
        } else {
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);

            bottomBox.setVisible(true);
            bottomBox.setManaged(true);
        }
    }


    @FXML
    void sendMsg(){

        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void connect() {

        try{
            socket = new Socket(ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try{

                    while(true){
                        String str = in.readUTF();
                        if ("/auth-OK".equals(str)){
                            setAuth(true);
                            textArea.clear();

                            break;
                        } else {
                            textArea.appendText(str + "\n");
                        }

                    }

                    while(true){
                        String str = in.readUTF();
                        if ("/serverClosed".equals(str)){
                            break;
                        }
                        textArea.appendText(str + "\n");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    setAuth(false);
                }

            }).start();

        } catch (IOException e) {
            e.printStackTrace();
            textArea.appendText("Connection refused\n");
        }

    }


    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()){
        connect();
        }
        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passwordField.getText());
            loginField.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(){
        try {
            out.writeUTF("/end");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
