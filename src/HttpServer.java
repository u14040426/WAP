/**
 * Created by Brenton on 3/7/2016.
 */
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
public class HttpServer extends Thread
{
    Socket client = null;
    BufferedReader readIn = null;
    DataOutputStream outTo = null;
    public static void main(String[] args) throws Exception
    {
        ServerSocket socket = new ServerSocket(55555);
        System.out.println("Waiting for connection");
        while (true)
        {
            Socket connected = socket.accept();
            (new HttpServer(connected)).start();
        }
    }
    public HttpServer(Socket client)
    {
        this.client = client;
    }

    public void run()
    {

        int answerCounter = 0;
        int questionCounter = -1;
        int questionAskedCounter = -1;
        ArrayList<Question> questions = new ArrayList<Question>();
        File file = new File("QnA.txt");
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;
            while ((text = reader.readLine()) != null) {
                if(text.charAt(0) == '?'){
                    questionCounter++;
                    questions.add(questionCounter, new Question(text.substring(1)));
                }else if(text.charAt(0) == '-'){
                    questions.get(questionCounter).addAnswer(text.substring(1), false);
                }else if(text.charAt(0) == '+'){
                    questions.get(questionCounter).addAnswer(text.substring(1), true);
                }
                questions.get(questionCounter).checkQuestion();
            }

        } catch(Exception ex){
            System.out.println(ex);
        }
        while(true){
            String req = null;
            try{
                readIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
                outTo = new DataOutputStream(client.getOutputStream());
                req = readIn.readLine();
            } catch(Exception ex){
                System.out.println("Error[0]: " + ex);
            }

            StringTokenizer tok = new StringTokenizer(req);
            String method = tok.nextToken();
            String query = tok.nextToken();
            StringBuffer response = new StringBuffer();
            try{
                while(readIn.ready())
                {
                    System.out.println(req);
                    req = readIn.readLine();
                }
            } catch(Exception ex){
                System.out.println("Error[1]: " + ex);
            }

            String html = "";
            file = new File("index.wml");
            reader = null;

            try {
                reader = new BufferedReader(new FileReader(file));
                String text = null;

                while ((text = reader.readLine()) != null) {
                    html += text;
                }
            } catch(Exception ex){
                System.out.println(ex);
            }


            response.append("HTTP/1.1 200 OK\r\n");
            response.append("Server: Java HTTPServer\r\n");
            response.append("Content-Type: text/vnd.wap.wml\r\n");

            CharSequence numberRegex = "{{number}}";
            CharSequence questionRegex = "{{question}}";
            CharSequence ans1Regex = "{{ans1}}";
            CharSequence ans2Regex = "{{ans2}}";
            CharSequence ans3Regex = "{{ans3}}";
            CharSequence ans4Regex = "{{ans4}}";

            CharSequence number = "";
            CharSequence question = "";
            Question currentQuestion = null;


            if(method.equals("GET"))
            {
                if(query.equals("/"))
                {
                    questionAskedCounter =0;
                    answerCounter = 0;
                    currentQuestion = questions.get(questionAskedCounter);

                    number = ""+ answerCounter;
                    html = html.replace(numberRegex, number);

                    question = currentQuestion.question;
                    html = html.replace(questionRegex, question);

                    html = html.replace(ans1Regex, currentQuestion.answers[0]);

                    html = html.replace(ans2Regex, currentQuestion.answers[1]);

                    html = html.replace(ans3Regex, currentQuestion.answers[2]);

                    html = html.replace(ans4Regex, currentQuestion.answers[3]);
                }else if(query.contains("/?n=")){

                    currentQuestion = questions.get(questionAskedCounter);
                    char ans = query.charAt(4);
                    if(currentQuestion.checkAnswer(ans)){
                        answerCounter++;
                    }

                    questionAskedCounter++;
                    if(questionAskedCounter < questionCounter){
                        currentQuestion = questions.get(questionAskedCounter);
                    }else{
                        questionAskedCounter =0;
                        answerCounter = 0;

                        currentQuestion = questions.get(questionAskedCounter);
                    }

                    number = ""+ answerCounter;
                    html = html.replace(numberRegex, number);

                    question = currentQuestion.question;
                    html = html.replace(questionRegex, question);

                    html = html.replace(ans1Regex, currentQuestion.answers[0]);

                    html = html.replace(ans2Regex, currentQuestion.answers[1]);

                    html = html.replace(ans3Regex, currentQuestion.answers[2]);

                    html = html.replace(ans4Regex, currentQuestion.answers[3]);


                }

                response.append("Content-length:" + html.getBytes().length + "\r\n");
                response.append("Connection: keep-alive\r\n\r\n");
                response.append(html);
                try{
                    outTo.writeBytes(response.toString());
                }catch(Exception ex){
                    System.out.println("Error[2]: " + ex);
                }

            }
        }
    }
}
