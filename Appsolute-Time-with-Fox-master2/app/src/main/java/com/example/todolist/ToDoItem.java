package com.example.todolist;

import io.realm.RealmObject;

// TODO: DB에 저장될 객체의 클래스 (D-Day여부: isDDay를 통해 알 수 있음)

public class ToDoItem extends RealmObject {
    private String date;
    private String content;
    private boolean isDDay;
    private boolean checked;

    public ToDoItem(){}

    public ToDoItem(String date, String content, boolean isDDay){
        this.date = date;
        this.content = content;
        this.isDDay = isDDay;
        this.checked = false;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDDay(boolean DDay) {
        isDDay = DDay;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public boolean isDDay() {
        return isDDay;
    }

    public boolean isChecked() {
        return checked;
    }
}
