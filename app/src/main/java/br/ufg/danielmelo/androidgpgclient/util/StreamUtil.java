package br.ufg.danielmelo.androidgpgclient.util;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import br.ufg.danielmelo.androidgpgclient.Constants;

/**
 * Created by daniel on 02/10/16.
 */

public class StreamUtil {


    public static InputStream getInStream(String source) {
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(source.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(Constants.TAG, "UnsupportedEncodingException", e);
        }
        return is;
    }

}
