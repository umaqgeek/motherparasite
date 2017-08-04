/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oms.rmi.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Message extends Remote {
    
    //example
    int getPlus(int a, int b) throws RemoteException;
    void sendHello(String words) throws RemoteException;

    // fast sql query
    boolean setQuerySQL(String query) throws RemoteException;
    ArrayList<ArrayList<String>> getQuerySQL(String query) throws RemoteException;
    
    // functions
    String getNFCEncrypted(String txt) throws RemoteException;
    boolean addStaff(ArrayList<String> data) throws RemoteException;
    boolean addMerchant(ArrayList<String> data) throws RemoteException;
    boolean getLogin(String user, String pass) throws RemoteException;
    boolean getLoginMerchant(String user, String pass) throws RemoteException;
    ArrayList<String> dn_getData_basedNfcCode(String dn_nfccode) throws RemoteException;
    ArrayList<String> dn_getData_basedMeId(int me_id) throws RemoteException;
    ArrayList<String> dn_getData(String dn_id) throws RemoteException;
    ArrayList<String> me_getData(String me_username) throws RemoteException;
    boolean dn_isDelete_nfcCode(String nfccode) throws RemoteException;
    boolean dn_isDelete_meId(int me_id) throws RemoteException;
    boolean dn_addData(ArrayList<String> bean_dinarpal_nfc) throws RemoteException;
    String[] tr_isSend_Payment_Dpgdps(String card_id, String pin_code, int me_id_from, int me_id_to, 
            double tr_amount, int at_id, int pt_id, String tr_notes) throws RemoteException;
    ArrayList<String> dgt_getDataForReceipt(int tr_id) throws RemoteException;
}


