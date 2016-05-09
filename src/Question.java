/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author fabio
 */
public class Question {
    String question;
    String[] answers;
    int correct;
    int totalQuestions;
    
    public Question(String q){
        question = q;
        totalQuestions = 0;
        correct = -1;
        answers = new String[4];
    }
    
    public void addAnswer(String a, boolean c){
        if(totalQuestions < 4){
            answers[totalQuestions] = a;
            totalQuestions++;
            if(c){
                correct = totalQuestions-1;
            }
        }
    }
    
    public void checkQuestion(){
        if(correct == -1 && totalQuestions==3){
            answers[3] = "None of the above";
            correct = 3;
        }
    }
    
    public boolean checkAnswer(char a){
        int ans = Integer.parseInt(""+a);
        System.out.println("Answered Answer: " + ans);
        return ans==correct;
    }
}
