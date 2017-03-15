package com.mno.example.copyart.model;

/**
 * Created by m-dev on 3/15/17.
 */

public class Picture {

    private String id;
    private String path;
    private String name;


    public Picture(String id, String path, String name) {
        this.id = id;
        this.path = path;
        this.name = name;
    }

    public Picture() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
