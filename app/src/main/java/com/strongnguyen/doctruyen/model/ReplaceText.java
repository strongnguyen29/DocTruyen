package com.strongnguyen.doctruyen.model;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 11/12/2018.
 * Email: vancuong2941989@gmail.com
 */
public class ReplaceText {
    private String currentText;
    private String newText;
    private boolean active;

    public ReplaceText() {
        currentText = "";
        newText = "";
        active = true;
    }

    public ReplaceText(String currentText, String newText) {
        this.currentText = currentText;
        this.newText = newText;
        active = true;
    }

    public String getCurrentText() {
        return currentText;
    }

    public void setCurrentText(String currentText) {
        this.currentText = currentText;
    }

    public String getNewText() {
        return newText;
    }

    public void setNewText(String newText) {
        this.newText = newText;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
