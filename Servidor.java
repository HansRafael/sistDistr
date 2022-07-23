package chat;

/* Variaveis necessárias para a leitura dos dados de entrada */
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
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
  private InputStream inputSocket;
  private InputStreamReader inputSocketReader;
  private BufferedReader bufferRead;

  public Servidor(Socket clientSocket) {
    this.clientSocket = clientSocket;
    try {
      inputSocket = clientSocket.getInputStream();                 //Pega o input do socket do cliente
      inputSocketReader = new InputStreamReader(inputSocket);      //Realiza o buffer da string que vem do fluxo do input, 
      bufferRead = new BufferedReader(inputSocketReader);          //Tornando a leitura dos caracteres/linhas mais eficiente
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Método run
   */
  public void run() {                                            // método run é onde as threads ficam executando até serem interrompidas ou terminadas pelo cliente ou pelo servidor
    try {
      String message;                                              // variável para receber a mensagem do servidor
      OutputStream outStream = this.clientSocket.getOutputStream();  // recebe o output stream do socket
      Writer outWriter = new OutputStreamWriter(outStream);               // converte o output stream para output stream writer
      BufferedWriter bfw = new BufferedWriter(outWriter);         // cria um buffer writer para escrever o output stream writer
      clientsFromServer.add(bfw);                          // adiciona o buffer writer ao array de buffer writers
      nameUser = message = bufferRead.readLine();                    // lê a mensagem do servidor

      while (!"Sair".equalsIgnoreCase(message) && message != null) {    //enquanto ninguem escrever "sair", as linhas do
        message = bufferRead.readLine();                               // do input continuarão sendo lidas e enviadas
        sendToAll(bfw, message);
        System.out.println(message);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //Script para subir o servidor NGROK que irá servir como um tunel paral a tcp://localhost:$PORT
  public static String upServ(){
    String path = Paths.get("").toAbsolutePath().toString(); //pega o diretorio atual
    System.out.println("Working Directory = " + path);
    String values = "";
    try {
      Process proc = Runtime.getRuntime().exec(path+"/upServ.sh"); //cria um processo
      BufferedReader read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
      try {
        proc.waitFor();
      } catch (InterruptedException e) {
        System.out.println(e.getMessage());
      }
        if (read.ready()) {
          values = read.readLine();
        }
      } catch (IOException e) {
        System.out.println(e.getMessage());
      }
      return values;
  }
  
  //Script para pegar as informacoes do servidor TCP criado pelo NGROK
  public static String readBashScript() {
    String path = Paths.get("").toAbsolutePath().toString();
    System.out.println("Working Directory = " + path);
    String values = "";
    try {
      Process proc = Runtime.getRuntime().exec(path+"/getURL.sh"); 
      BufferedReader read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
      try {
        proc.waitFor();
      } catch (InterruptedException e) {
        System.out.println(e.getMessage());
      }
        if (read.ready()) {
          values = read.readLine();
        }
      } catch (IOException e) {
        System.out.println(e.getMessage());
      }
      return values;
  }

  public void sendToAll(BufferedWriter bwOutput, String message) throws IOException {
    BufferedWriter bufferOutput;

    for (BufferedWriter bw : clientsFromServer) {                     //irá pegar todos os sockets que estao
      bufferOutput = (BufferedWriter) bw;                            // dentro da lista clientsFromServer         
      if (!(bwOutput == bufferOutput)) {     
        if(message != null){                                        // e irá escrever dentro do buffer(no caso, a tela)
          bw.write(nameUser + " diz -> " + message + "\r\n");           // de todos menos de qm enviou
          bw.flush();
        }                       
      }
    }
  }


  public static void main(String[] args) {
    String tcp;
    String portLocal;
    try {
      // Cria os objetos e variaveis necessárias para instânciar o servidor
      portLocal = upServ();
      System.out.println("Porta Local: " + portLocal);
      tcp = readBashScript();
      System.out.println("TCP FROM NGROK: " + tcp);
      //faz o tratamento da string TCP que veio do script, splitando nos lugares
      String[] words = tcp.split("//");
      String[] newW = words[1].split(":");
      String url, port;
      url = newW[0];
      port = newW[1];
      System.out.println("url: "+url);
      System.out.println("port: "+port);
      JLabel lblMessage = new JLabel("URL do Servidor: "+url);
      JLabel lblMessageTwo = new JLabel("Porta do Servidor: "+port);
      Object[] text = {lblMessage, lblMessageTwo};
      JOptionPane.showMessageDialog(null, text);

      Object[] texts = { lblMessage};
      JOptionPane.showMessageDialog(null, texts);
      /* 
       * Aqui, estamos chamando o servidor e atribuindo a ela uma porta
       * para ficar ouvindo. Dessa maneira, o servidor ficará apenas esperando,
       * ouvindo o socket para um cliente realizar a conexao no método
       * accept()
       */
      serverSocket = new ServerSocket(Integer.parseInt(portLocal));
      clientsFromServer = new ArrayList<BufferedWriter>();
      JOptionPane.showMessageDialog(null, "Servidor ativo na porta: " + port);

      while (true) {
        System.out.println("Aguardando conexão...");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Cliente conectado...");
        Thread t = new Servidor(clientSocket);
        t.start();
      }

    } catch (Exception e) {

      e.printStackTrace();
    }
  }// Fim do método main
} // Fim da classe}
