package exampleOne;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/* Client-Server with TCP
 * TCP is connection-oriented, meaning an exclusive connection must
 * first be established between the client and server for communication
 * to take place.
 */
public class Servidor extends Thread {
  /* com o static, estamos dizendo que, cada vez que um objeto for criado,
  as variáveis 'clientsFromServer' e 'serverSocket' NAO serão instanciadas novamente. Ou seja,
  elas irão permanecer iguais, não sendo alteradas. */
  private static ArrayList<BufferedWriter> clientsFromServer;
  private static ServerSocket serverSocket;

  private String nameUser;
  private Socket clientSocket;
  private InputStream in;
  private InputStreamReader inr;
  private BufferedReader bfr;

  public Servidor(Socket clientSocket) {
    this.clientSocket = clientSocket;
    try {
      in = clientSocket.getInputStream();
      inr = new InputStreamReader(in);
      bfr = new BufferedReader(inr);
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(clientsFromServer);
  }

  /**
   * Método run
   */
  public void run() {
    try {
      String msg;
      OutputStream ou = this.clientSocket.getOutputStream();
      Writer ouw = new OutputStreamWriter(ou);
      BufferedWriter bfw = new BufferedWriter(ouw);
      clientsFromServer.add(bfw);
      nameUser = msg = bfr.readLine();

      while (!"Sair".equalsIgnoreCase(msg) && msg != null) {
        msg = bfr.readLine();
        sendToAll(bfw, msg);
        System.out.println(msg);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /***
   * Método usado para enviar mensagem para todos os clients
   * 
   * @param bwSaida do tipo BufferedWriter
   * @param msg     do tipo String
   * @throws IOException
   */
  public void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {
    BufferedWriter bwS;

    for (BufferedWriter bw : clientsFromServer) {
      bwS = (BufferedWriter) bw;
      if (!(bwSaida == bwS)) {
        bw.write(nameUser + " -> " + msg + "\r\n");
        bw.flush();
      }
    }
  }


  public static void main(String[] args) {

    try {
      // Cria os objetos necessário para instânciar o servidor
      //readBashScript();
      JLabel lblMessage = new JLabel("Porta do Servidor:");
      JTextField txtPorta = new JTextField("12345");
      Object[] texts = { lblMessage, txtPorta };
      JOptionPane.showMessageDialog(null, texts);
      /* 
       * Aqui, estamos chamando o servidor e atribuindo a ela uma porta
       * para ficar ouvindo. Dessa maneira, o servidor ficará apenas esperando,
       * ouvindo o socket para um cliente realizar a conexao no método
       * accept()
       */
      serverSocket = new ServerSocket(Integer.parseInt(txtPorta.getText()));
      clientsFromServer = new ArrayList<BufferedWriter>();
      JOptionPane.showMessageDialog(null, "Servidor ativo na porta: " + txtPorta.getText());

      while (true) {
        System.out.println("Aguardando conexão...");
        Socket clientSocket = serverSocket.accept();
        System.out.print(clientSocket);
        System.out.println("Cliente conectado...");
        Thread t = new Servidor(clientSocket);
        t.start();
      }

    } catch (Exception e) {

      e.printStackTrace();
    }
  }// Fim do método main
} // Fim da classe}
