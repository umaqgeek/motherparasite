/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package libraries;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 *
 * @author umarmukhtar
 */
public class My_func {
    
    public static final String BASIC_PIN = "123456";
    
    public static String getCurrentTimestamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.getTime()+"";
    }
    
    public static String getCurrentDatetime_sql() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        return sdf.format(timestamp);
    }
    
    public static String dp_encrypt(String str) {
        String str_enc = str;
        try {
            str_enc = EncryptUtils1.enc(str);
        } catch (Exception e) {
            System.out.println("Error: "+e.getMessage());
        }
        return str_enc;
    }
    
    public static String dp_decrypt(String str_enc) {
        String str_dec = str_enc;
        try {
            str_dec = EncryptUtils1.dec(str_enc);
        } catch (Exception e) {
        }
        return str_dec;
    }
}
