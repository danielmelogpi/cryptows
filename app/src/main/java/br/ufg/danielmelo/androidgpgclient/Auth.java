package br.ufg.danielmelo.androidgpgclient;

/**
 * Created by daniel on 03/12/16.
 */

public class Auth {

    static String senhaSessao;

    public static String getSenhaSessao() {
        return senhaSessao;
    }

    public static void setSenhaSessao(String senha) {
        senhaSessao = senha;
    }
}
