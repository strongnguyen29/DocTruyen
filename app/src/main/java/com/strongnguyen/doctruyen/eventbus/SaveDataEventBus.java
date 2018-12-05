package com.strongnguyen.doctruyen.eventbus;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 5/27/2018.
 * Email: vancuong2941989@gmail.com
 */
public class SaveDataEventBus {
    private String chapName;

    public SaveDataEventBus(String chapName) {
        this.chapName = chapName;
    }

    public void setChapName(String chapName) {
        this.chapName = chapName;
    }

    public String getChapName() {
        return chapName;
    }
}
