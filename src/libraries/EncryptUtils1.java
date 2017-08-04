/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package libraries;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author umarmukhtar
 */
public class EncryptUtils1 {
    public static final String DEFAULT_ENCODING = "UTF-8";
    static BASE64Encoder enc = new BASE64Encoder();
    static BASE64Decoder dec = new BASE64Decoder();

    public static final String KEY = "D1n@rPAL_debitgram";
    
    public static String base64encode(String text) {
        try {
            return enc.encode(text.getBytes(DEFAULT_ENCODING));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }//base64encode

    public static String base64decode(String text) {
        try {
            return new String(dec.decodeBuffer(text), DEFAULT_ENCODING);
        } catch (IOException e) {
            return null;
        }
    }//base64decode

//    public static void main(String[] args) {
//        String txt = "some text to be encrypted";
//        String key = "key phrase used for XOR-ing";
//        System.out.println(txt + " XOR-ed to: " + (txt = xorMessage(txt, key)));
//
//        String encoded = base64encode(txt);
//        System.out.println(" is encoded to: " + encoded + " and that is decoding to: " + (txt = base64decode(encoded)));
//        System.out.print("XOR-ing back to original: " + xorMessage(txt, key));
//    }
    
    public static String enc(String txt) {
        txt = xorMessage(txt, KEY);
        String encoded = base64encode(txt);
        return encoded;
    }
    
    public static String dec(String code) {
        code = base64decode(code);
        String decoded = xorMessage(code, KEY);
        return decoded;
    }
    
//    public static void main(String[] args) {
//        String a = "aku nak makan ayam 123 #@!";
//        String b = enc(a);
//        String c = dec(b);
//        System.out.println("a:"+a+"|");
//        System.out.println("b:"+b+"|");
//        System.out.println("c:"+c+"|");
//    }

    public static String xorMessage(String message, String key) {
        try {
            if (message == null || key == null) {
                return null;
            }

            char[] keys = key.toCharArray();
            char[] mesg = message.toCharArray();

            int ml = mesg.length;
            int kl = keys.length;
            char[] newmsg = new char[ml];

            for (int i = 0; i < ml; i++) {
                newmsg[i] = (char) (mesg[i] ^ keys[i % kl]);
            }//for i

            return new String(newmsg);
        } catch (Exception e) {
            return null;
        }
    }//xorMessage
}
