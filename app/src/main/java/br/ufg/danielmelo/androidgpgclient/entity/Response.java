package br.ufg.danielmelo.androidgpgclient.entity;

import com.fasterxml.jackson.jr.ob.JSON;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * HTTP default response entity
 */
public class Response {

    String protocol;
    String plainContent;
    String cipherContent;
    String statusMessage;
    long time;
    int code;

    public Response() {
        time = new Date().getTime();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getPlainContent() {
        return plainContent;
    }

    public void setPlainContent(String plainContent) {
        this.plainContent = plainContent;
    }

    public String getCipherContent() {
        return cipherContent;
    }

    public void setCipherContent(String cipherContent) {
        this.cipherContent = cipherContent;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
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
