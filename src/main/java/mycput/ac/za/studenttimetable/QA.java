/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mycput.ac.za.studenttimetable;

import java.util.List;

/**
 *
 * @author mogamatyaseenkannemeyer
 */
public class QA {
    private String category;
    public String question;           // old single question
    public List<String> questionList; // new multiple questions
    public String answer;
    public List<String> keywords;

    public QA() {} // default constructor for Gson

    public QA(String category, List<String> questionList, String answer, List<String> keywords) {
        this.category = category;
        this.questionList = questionList;
        this.answer = answer;
        this.keywords = keywords;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<String> questionList) {
        this.questionList = questionList;
    }

    @Override
    public String toString() {
        return "QA{" + "category=" + category + ", question=" + question + ", questionList=" + questionList + ", answer=" + answer + ", keywords=" + keywords + '}';
    }

    

    
    
}