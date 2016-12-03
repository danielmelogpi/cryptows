package br.ufg.danielmelo.androidgpgclient.entity;

/**
 * HTTP Service default input message
 */

public class Message {

    String content;


    String protocol;
    String receiver;


    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String toString() {
        return "Message{" +
                " content='" + content + '\'' +
                ", protocol='" + protocol + '\'' +
                ", receiver='" + receiver + '\'' +
                '}';
    }

}
