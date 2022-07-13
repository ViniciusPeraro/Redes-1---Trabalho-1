import java.io.*;
import java.net.*;
import java.util.*;

//Trabalho de Redes 1 - Servidor Multithread
//Aluno: Vinicius Peraro de Oliveira - 11721ECP007

public final class WebServer {
    public static void  main(String arvg[]) throws Exception {
        int port = 3030;
        try {
            ServerSocket server = new ServerSocket(port);
            while(true) {
                Socket client = server.accept();
                HttpRequest request = new HttpRequest(client);
                Thread thread = new Thread(request);
                thread.start();
            }
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}

final class HttpRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;

    public HttpRequest(Socket socket) throws Exception {
        this.socket = socket;
    }

    public void run() {
        try{
            processRequest();
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

        InputStreamReader ir = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(ir);

        String requestLine = br.readLine();

        System.out.println();
        System.out.println(requestLine);

        String headerLine = null;
        while((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }

        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken();

        String fileName = tokens.nextToken();
        
        System.out.println(fileName);

        FileInputStream fis = null;
        Boolean fileExists = true;
        Boolean loadFirstPage = true;

        if(fileName.equals("/")){
            loadFirstPage = true;    
        } else {
            loadFirstPage = false;  
        }

        fileName = "Servidor/Arquivos/" + fileName;

        try {
            fis = new FileInputStream(fileName);
        } catch(FileNotFoundException e) {
            fileExists = false;
        }
        
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;

        if(fileExists) {
            statusLine = "HTTP/1.0 200 OK " + CRLF;
            contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
        } else {
            statusLine = "HTTP/1.0 404 Not Found " + CRLF;
            contentTypeLine = "Content-type: text/html " + CRLF;
            if(loadFirstPage){
                entityBody = "<HTML5>" + 
                                "<HEAD>" + 
                                    "<TITLE>Hello World</TITLE>" + 
                                    "<STYLE>" + 
                                        "body{background-color: rgb(212, 188, 60);}" +
                                        "h1,h2,h3{" +
                                            "display: flex;" +
                                            "justify-content: center;" +
                                            "align-items: center;}" +
                                    "</STYLE>" + 
                                "</HEAD>" + 
                                "<BODY>" + 
                                    "<h1 style = \"font-family:courier,arial,helvetica;\"> Pagina Inicial </h1>" + 
                                    "<h2 style = \"font-family:courier,arial,helvetica;\"> Vinicius Peraro de Oliviera - 1721ECP007 </h3>" + 
                                    "<h3 style = \"font-family:courier,arial,helvetica;\"> Trabalho 1 - Redes 1 - Servidor Multithread </h3>" + 
                            
                                    "<div style = \"display:flex; justify-content: center;\">" + 
                                        "<img src = \"./gato.gif\"/>" + 
                                    "</div>" + 
                                "</BODY>" + 
                            "</HTML5>";
            } else {
                entityBody = "<HTML5>" + 
                                    "<HEAD>" + 
                                        "<TITLE>404 Error</TITLE>" + 
                                        "<STYLE>" + 
                                            "body{background-color: rgb(240, 93, 88);}" +
                                        "</STYLE>" + 
                                    "</HEAD>" + 
                                    "<BODY>" + 
                                        "<div style = \"display:flex; justify-content: center;\">" + 
                                            "<img src = \"./404_error.jpg\"/>" + 
                                        "</div>" + 
                                    "</BODY>" + 
                                "</HTML5>";
            }
            
        }

        System.out.println(statusLine);
        System.out.println(contentTypeLine);
        System.out.println(fileName);

        os.writeBytes(statusLine);
        os.writeBytes(contentTypeLine);
        os.writeBytes(CRLF);

        if(fileExists) {
            sendBytes(fis, os);
            fis.close();
        } else {
            os.writeBytes(entityBody);
        }

        os.close();
        br.close();
        socket.close();
    }

    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes = 0;

        while((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    private static String contentType(String fileName) {      

        if(fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
            return "image/jpeg";
        }
        if(fileName.endsWith(".png")) {
            return "image/png";
        }  
        if(fileName.endsWith(".gif")) {
            return "image/gif";
        } 
        if(fileName.endsWith(".mp4")) {
            return "video/mp4";
        }  
        if(fileName.endsWith(".txt")){
            return "text/plain";
        }
        if(fileName.endsWith(".ogg")){
            return "audio/ogg";
        }
        return "application/octet-stream";
    }
}