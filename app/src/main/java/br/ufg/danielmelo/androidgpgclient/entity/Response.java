package br.ufg.danielmelo.androidgpgclient.entity;

import com.fasterxml.jackson.jr.ob.JSON;

import java.io.IOException;
import java.util.List;

/**
 * Created by daniel on 26/11/16.
 */

public class Response {

    String content;
    List<String> ids;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public String toJson() {
        try {
            return JSON.std.asString(this);
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR: "+e.getLocalizedMessage();
        }
    }
}
