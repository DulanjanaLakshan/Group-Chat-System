import javafx.scene.Scene;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverthing(socket, bufferedReader, bufferedWriter);
        }
    }
    public void sendMessage(){
        try{
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner=new Scanner(System.in);
            while (socket.isConnected()){
                String messagetosend=scanner.nextLine();
                bufferedWriter.write(username + ": "+messagetosend);
                bufferedWriter.newLine();
                bufferedWriter.flush();

            }
        } catch (IOException e) {
            closeEverthing(socket, bufferedReader, bufferedWriter);
        }
    }
    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                  String msgFormChat;
                  while (socket.isConnected()){
                    try{
                        msgFormChat=bufferedReader.readLine();
                        System.out.println(msgFormChat);
                    }catch (IOException e) {
                        closeEverthing(socket, bufferedReader, bufferedWriter);
                    }
                  }
            }
        }).start();
    }
    public void closeEverthing(Socket socket,BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner=new Scanner(System.in);
        System.out.println("Enter your username for group chat : ");
        String username=scanner.nextLine();
        Socket socket=new Socket("localhost",1234);
        Client client=new Client(socket,username);
        client.listenForMessage();
        client.sendMessage();

    }
}
