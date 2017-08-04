/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oms.rmi.server;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import libraries.EncryptUtils1;
import libraries.My_func;
import oms.rmi.db.DBConn;

public class MessageImpl extends UnicastRemoteObject implements Message {
    
    public final static int NUMBER_SESSION_ARRAY_SIZE = 24;

    public MessageImpl() throws RemoteException {
        super(Registry.REGISTRY_PORT);
        System.out.println("... Connected ...");
    }

    @Override
    public int getPlus(int a, int b) throws RemoteException {
        int c = a + b;
        return c;
    }

    @Override
    public boolean setQuerySQL(String query) throws RemoteException {
        boolean status = false;
        DBConn dbc = new DBConn();
        try {
            PreparedStatement ps = dbc.getConn().prepareStatement(query);
            ps.execute();
            status = true;
        } catch (Exception e) {
            status = false;
        } finally {
            try {
                dbc.getConn().close();
            } catch (SQLException ex) {
                System.out.println("Error: "+ex.getMessage()+"\nSQL State: "+ex.getSQLState());
            }
        }
        return status;
    }

    @Override
    public ArrayList<ArrayList<String>> getQuerySQL(String query) throws RemoteException {
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        DBConn dbc = new DBConn();
        try {
            PreparedStatement ps = dbc.getConn().prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ArrayList<String> d = new ArrayList<String>();
                try {
                    for (int i = 0; ; i++) {
                        d.add(rs.getString(i+1));
                    }
                } catch (Exception e) {
                }
                data.add(d);
            }
        } catch (Exception e) {
            data.removeAll(data);
            data = new ArrayList<ArrayList<String>>();
        } finally {
            try {
                dbc.getConn().close();
            } catch (SQLException ex) {
                Logger.getLogger(MessageImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return data;
    }

    @Override
    public void sendHello(String words) throws RemoteException {
        System.out.println(words);
    }

    @Override
    public String getNFCEncrypted(String txt) throws RemoteException {
        String code = My_func.dp_encrypt(txt);
        return code;
    }

    @Override
    public boolean addStaff(ArrayList<String> data) throws RemoteException {
        boolean status = false;
        try {
            String sql = "INSERT INTO dinarpal_login_nfc(dln_id, dln_user, dln_pass, dln_lock, dln_fullname) VALUES(?, ?, ?, ?, ?)";
            DBConn dbc = new DBConn();
            PreparedStatement ps = dbc.getConn().prepareStatement(sql);
            ps.setString(1, data.get(0));
            ps.setString(2, data.get(1));
            ps.setString(3, data.get(2));
            ps.setString(4, data.get(3));
            ps.setString(5, data.get(4));
            ps.execute();
            status = true;
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        return status;
    }

    @Override
    public boolean getLogin(String user, String pass) throws RemoteException {
        boolean status = false;
        try {
            
            String sql1 = "SELECT dln.dln_lock "
                    + "FROM dinarpal_login_nfc dln "
                    + "WHERE dln.dln_user = ? ";
            DBConn dbc1 = new DBConn();
            PreparedStatement ps1 = dbc1.getConn().prepareStatement(sql1);
            ps1.setString(1, user);
            ResultSet rs1 = ps1.executeQuery();
            String dln_lock = "0";
            if (rs1.next()) {
                dln_lock = rs1.getString("dln_lock");
            }
            
            pass = pass.toLowerCase().toUpperCase();
            String dln_pass_new = pass + dln_lock;
            String dln_pass = My_func.dp_encrypt(dln_pass_new);
            
            String sql2 = "SELECT dln.dln_id, dln.dln_pass "
                    + "FROM dinarpal_login_nfc dln "
                    + "WHERE dln.dln_user = ? "
                    + "AND dln.dln_pass = ? ";
            DBConn dbc2 = new DBConn();
            PreparedStatement ps2 = dbc2.getConn().prepareStatement(sql2);
            ps2.setString(1, user);
            ps2.setString(2, dln_pass);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                status = true;
            } else {
                status = false;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        return status;
    }

    @Override
    public ArrayList<String> dn_getData_basedNfcCode(String dn_nfccode) throws RemoteException {
        ArrayList<String> bdn = new ArrayList<String>();
        try {
            
            String sql = "SELECT "
                    + "me.me_id, "
                    + "me.me_username, "
                    + "me.me_firstname, "
                    + "me.me_lastname, "
                    + "me.me_phone_no, "
                    + "me.me_email, "
                    + "me.me_account_type, "
                    + "me.me_activation_status, "
                    + "me.mos_id, "
                    + "dn.* "
                    + "FROM members me, dinarpal_nfc dn "
                    + "WHERE me.me_id = dn.me_id "
                    + "AND dn.dn_nfccode = ? ";
            DBConn dbc = new DBConn();
            PreparedStatement ps = dbc.getConn().prepareStatement(sql);
            ps.setString(1, dn_nfccode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                try {
                    for (int i = 0; ; i++) {
                        bdn.add(rs.getString(i+1));
                    }
                } catch (Exception e) {
                }
            }
            
        } catch (Exception e) {
            bdn.removeAll(bdn);
            bdn = new ArrayList<String>();
        }
        return bdn;
    }

    @Override
    public ArrayList<String> dn_getData_basedMeId(int me_id) throws RemoteException {
        ArrayList<String> bdn = new ArrayList<String>();
        try {
            
            String sql = "SELECT dn.dn_id, dn.dn_datetime, dn.dn_nfccode, dn.me_id, dn.dn_pin, dn.dn_lock, dn.dn_status "
                    + "FROM dinarpal_nfc dn "
                    + "WHERE dn.me_id = ? ";
            DBConn dbc = new DBConn();
            PreparedStatement ps = dbc.getConn().prepareStatement(sql);
            ps.setInt(1, me_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                bdn.add(rs.getString("dn_id"));
                bdn.add(rs.getString("dn_datetime"));
                bdn.add(rs.getString("dn_nfccode"));
                bdn.add(rs.getInt("me_id")+"");
                bdn.add(rs.getString("dn_pin"));
                bdn.add(rs.getString("dn_lock"));
                bdn.add(rs.getInt("dn_status")+"");
            }
            
        } catch (Exception e) {
            bdn.removeAll(bdn);
            bdn = new ArrayList<String>();
        }
        return bdn;
    }

    @Override
    public ArrayList<String> me_getData(String me_username) throws RemoteException {
        ArrayList<String> data = new ArrayList<String>();
        try {
            
            String sql = "SELECT * "
                    + "FROM members me "
                    + "WHERE me.me_username = ? ";
            DBConn dbc = new DBConn();
            PreparedStatement ps = dbc.getConn().prepareStatement(sql);
            ps.setString(1, me_username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                try {
                    for (int i = 0; ; i++) {
                        data.add(rs.getString(i+1));
                    }
                } catch (Exception e) {
                }
            }
            
        } catch (Exception e) {
            data = new ArrayList<String>();
            data.removeAll(data);
        }
        return data;
    }

    @Override
    public boolean dn_isDelete_nfcCode(String nfccode) throws RemoteException {
        boolean status = false;
        try {
            
            String sql = "DELETE FROM dinarpal_nfc WHERE dn_nfccode = ?";
            DBConn dbc = new DBConn();
            PreparedStatement ps = dbc.getConn().prepareStatement(sql);
            ps.setString(1, nfccode);
            ps.execute();
            status = true;
            
        } catch (Exception e) {
            status = false;
        }
        return status;
    }

    @Override
    public boolean dn_isDelete_meId(int me_id) throws RemoteException {
        boolean status = false;
        try {
            
            String sql = "DELETE FROM dinarpal_nfc WHERE me_id = ?";
            DBConn dbc = new DBConn();
            PreparedStatement ps = dbc.getConn().prepareStatement(sql);
            ps.setInt(1, me_id);
            ps.execute();
            status = true;
            
        } catch (Exception e) {
            status = false;
        }
        return status;
    }

    @Override
    public boolean dn_addData(ArrayList<String> bean_dinarpal_nfc) throws RemoteException {
        boolean status = false;
        try {
            
            String sql = "INSERT INTO dinarpal_nfc(dn_id, dn_datetime, dn_nfccode, me_id, dn_pin, dn_lock, dn_status) "
                    + "VALUES(?, ?, ?, ?, ?, ?, ?) ";
            DBConn dbc = new DBConn();
            PreparedStatement ps = dbc.getConn().prepareStatement(sql);
            ps.setString(1, My_func.getCurrentTimestamp());
            ps.setString(2, My_func.getCurrentDatetime_sql());
//            String dn_nfccode = bean_dinarpal_nfc.get(2) + EncryptUtils1.KEY;
            String dn_nfccode = bean_dinarpal_nfc.get(2);
//            dn_nfccode = My_func.dp_encrypt(dn_nfccode);
            ps.setString(3, dn_nfccode);
            int me_id = 0;
            try {
                me_id = Integer.parseInt(bean_dinarpal_nfc.get(3));
            } catch (Exception e) {
                me_id = 0;
            }
            ps.setInt(4, me_id);
            String dn_pin = bean_dinarpal_nfc.get(4) + EncryptUtils1.KEY;
            dn_pin = My_func.dp_encrypt(dn_pin);
            ps.setString(5, dn_pin);
            ps.setString(6, EncryptUtils1.KEY);
            int dn_status = 1;
            try {
                dn_status = Integer.parseInt(bean_dinarpal_nfc.get(6));
            } catch (Exception e) {
                dn_status = 1;
            }
            ps.setInt(7, dn_status);
            ps.execute();
            status = true;
            
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        return status;
    }

    @Override
    public ArrayList<String> dn_getData(String dn_id) throws RemoteException {
        ArrayList<String> bdn = new ArrayList<String>();
        try {
            
            String sql = "SELECT dn.dn_id, dn.dn_datetime, dn.dn_nfccode, dn.me_id, dn.dn_pin, dn.dn_lock, dn.dn_status "
                    + "FROM dinarpal_nfc dn "
                    + "WHERE dn.dn_id = ? ";
            DBConn dbc = new DBConn();
            PreparedStatement ps = dbc.getConn().prepareStatement(sql);
            ps.setString(1, dn_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                bdn.add(rs.getString("dn_id"));
                bdn.add(rs.getString("dn_datetime"));
                bdn.add(rs.getString("dn_nfccode"));
                bdn.add(rs.getInt("me_id")+"");
                bdn.add(rs.getString("dn_pin"));
                bdn.add(rs.getString("dn_lock"));
                bdn.add(rs.getInt("dn_status")+"");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            bdn.removeAll(bdn);
            bdn = new ArrayList<String>();
        }
        return bdn;
    }

    @Override
    public boolean addMerchant(ArrayList<String> data) throws RemoteException {
        boolean status = false;
        try {
            String sql = "INSERT INTO dinarpal_nfc_merchant(dnm_id, me_id, dnm_user, dnm_pass, dnm_lock) "
                    + "VALUES(?, ?, ?, ?, ?)";
            DBConn dbc = new DBConn();
            PreparedStatement ps = dbc.getConn().prepareStatement(sql);
            ps.setString(1, data.get(0));
            ps.setString(2, data.get(1));
            ps.setString(3, data.get(2));
            ps.setString(4, data.get(3));
            ps.setString(5, data.get(4));
            ps.execute();
            status = true;
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        return status;
    }

    @Override
    public boolean getLoginMerchant(String user, String pass) throws RemoteException {
        boolean status = false;
        try {
            
            String sql1 = "SELECT dnm.dnm_lock "
                    + "FROM dinarpal_nfc_merchant dnm "
                    + "WHERE dnm.dnm_user = ? ";
            DBConn dbc1 = new DBConn();
            PreparedStatement ps1 = dbc1.getConn().prepareStatement(sql1);
            ps1.setString(1, user);
            ResultSet rs1 = ps1.executeQuery();
            String dnm_lock = "0";
            if (rs1.next()) {
                dnm_lock = rs1.getString("dnm_lock");
            }
            
            pass = pass.toLowerCase().toUpperCase();
            String dnm_pass_new = pass + dnm_lock;
            String dnm_pass = My_func.dp_encrypt(dnm_pass_new);
            
            String sql2 = "SELECT dnm.dnm_id, dnm.dnm_pass "
                    + "FROM dinarpal_nfc_merchant dnm "
                    + "WHERE dnm.dnm_user = ? "
                    + "AND dnm.dnm_pass = ? ";
            DBConn dbc2 = new DBConn();
            PreparedStatement ps2 = dbc2.getConn().prepareStatement(sql2);
            ps2.setString(1, user);
            ps2.setString(2, dnm_pass);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                status = true;
            } else {
                status = false;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        return status;
    }

    @Override
    public synchronized String[] tr_isSend_Payment_Dpgdps(String card_id, String pin_code, int me_id_from, int me_id_to, 
            double tr_amount, int at_id, int pt_id, String tr_notes) throws RemoteException {
        boolean status = false;
        String[] str_out = new String[2];
        str_out[0] = "-1";
        str_out[1] = "";
        try {
            
            if (me_id_from == me_id_to) {
                str_out[0] = "1";
                str_out[1] = "Cannot sent to yourself!";
                return str_out;
            }
            
            int it_id = (at_id == 17) ? (1) : ((at_id == 18) ? (2) : (0));
            int num_unit = (int) (tr_amount * 10);
            int tt_id = 89; // Debit Gram Transfer
            
            boolean s11 = false;
            boolean s12 = false;
            boolean s13 = false;
            boolean s14 = false;
            boolean s15 = false;
            String tr_datetime = My_func.getCurrentDatetime_sql();
            
            // 1.1. check card id and pin code
            String sql11 = "SELECT dn.dn_nfccode, dn.dn_pin, dn.dn_lock "
                    + "FROM dinarpal_nfc dn "
                    + "WHERE dn.dn_nfccode = ? "
                    + "AND dn.me_id = ? ";
            DBConn dbc11 = new DBConn();
            PreparedStatement ps11 = dbc11.getConn().prepareStatement(sql11);
            ps11.setString(1, card_id);
            ps11.setInt(2, me_id_from);
            ResultSet rs11 = ps11.executeQuery();
            if (rs11.next()) {
                String dn_lock = rs11.getString("dn_lock");
                String dn_pinx = rs11.getString("dn_pin");
                String dn_pin = My_func.dp_decrypt(dn_pinx);
                String check_pin = pin_code + dn_lock;
                dn_pin = dn_pin.toLowerCase().toUpperCase();
                check_pin = check_pin.toLowerCase().toUpperCase();
                if (dn_pin.equals(check_pin)) {
                    s11 = true;
                } else {
                    s11 = false;
                    str_out[0] = "1";
                    str_out[1] = "Invalid Pin Code!";
                }
            } else {
                s11 = false;
                str_out[0] = "1";
                str_out[1] = "Invalid Card ID / Customer!";
            }
            dbc11.getConn().close();
            if (s11 == false) {
                return str_out;
            }
            
            // 1.2. check sender
            String sql12 = "SELECT me.me_username "
                    + "FROM members me "
                    + "WHERE me.me_id = ? ";
            DBConn dbc12 = new DBConn();
            PreparedStatement ps12 = dbc12.getConn().prepareStatement(sql12);
            ps12.setInt(1, me_id_from);
            ResultSet rs12 = ps12.executeQuery();
            if (rs12.next()) {
                s12 = true;
            } else {
                s12 = false;
                str_out[0] = "1";
                str_out[1] = "Invalid customer!";
            }
            dbc12.getConn().close();
            if (s12 == false) {
                return str_out;
            }
            
            // 1.3. check receiver
            String sql13 = "SELECT me.me_username "
                    + "FROM members me "
                    + "WHERE me.me_id = ? ";
            DBConn dbc13 = new DBConn();
            PreparedStatement ps13 = dbc13.getConn().prepareStatement(sql13);
            ps13.setInt(1, me_id_to);
            ResultSet rs13 = ps13.executeQuery();
            if (rs13.next()) {
                s13 = true;
            } else {
                s13 = false;
                str_out[0] = "1";
                str_out[1] = "Invalid merchant!";
            }
            dbc13.getConn().close();
            if (s13 == false) {
                return str_out;
            }
            
            // 1.4. check amount and validate
            String sql141 = "UPDATE vault "
                    + "SET me_id_lock = 0 "
                    + "WHERE me_id_lock = ? ";
            DBConn dbc141 = new DBConn();
            PreparedStatement ps141 = dbc141.getConn().prepareStatement(sql141);
            ps141.setInt(1, me_id_from);
            ps141.execute();
            dbc141.getConn().close();
            String sql142 = "SELECT v.v_id "
                    + "FROM vault v, item_type_child itc, item_type_purity itp, "
                    + "item_type it, purity pu "
                    + "WHERE v.itc_id = itc.itc_id "
                    + "AND itc.itp_id = itp.itp_id "
                    + "AND itp.it_id = it.it_id "
                    + "AND itp.pu_id = pu.pu_id "
                    + "AND v.vt_id = 8 "
                    + "AND v.me_id = ? "
                    + "AND it.it_id = ? "
                    + "AND v.v_weight = 0.1 "
                    + "AND v.me_id_lock = 0 "
                    + "GROUP BY v.v_id "
                    + "ORDER BY v.v_id "
                    + "LIMIT ? ";
            DBConn dbc142 = new DBConn();
            PreparedStatement ps142 = dbc142.getConn().prepareStatement(sql142);
            ps142.setInt(1, me_id_from);
            ps142.setInt(2, it_id);
            ps142.setInt(3, num_unit);
            ResultSet rs142 = ps142.executeQuery();
            rs142.last();
            int rows142 = rs142.getRow();
            rs142.beforeFirst();
            DBConn dbc1421 = new DBConn();
            Connection conn1421 = dbc1421.getConn();
            try {
                if (rows142 > 0) {
                    if (rows142 == num_unit) {
                        ArrayList<Integer> v_ids = new ArrayList<Integer>();
                        while (rs142.next()) {
                            v_ids.add(rs142.getInt("v_id"));
                        }
                        
                        String sql1421 = "UPDATE vault "
                                + "SET me_id = ?, "
                                + "me_id_lock = 0, "
                                + "vt_id = 8, "
                                + "tt_id = ?, "
                                + "me_id_from = ?,"
                                + "v_changedatetime = ? "
                                + "WHERE v_id = ? ";
                        PreparedStatement ps1421 = conn1421.prepareStatement(sql1421);
                        conn1421.setAutoCommit(false);
                        for (int i = 0; i < v_ids.size(); i++) {
                            int v_id = v_ids.get(i);
                            ps1421.setInt(1, me_id_to);
                            ps1421.setInt(2, tt_id);
                            ps1421.setInt(3, me_id_from);
                            ps1421.setString(4, tr_datetime);
                            ps1421.setInt(5, v_id);
                            ps1421.addBatch();
                        }
                        ps1421.executeBatch();
                        conn1421.commit();
                        
                        s14 = true;
                    } else {
                        s14 = false;
                        str_out[0] = "1";
                        str_out[1] = "Insufficient funds of DPG/DPS!";
                    }
                } else {
                    s14 = false;
                    str_out[0] = "1";
                    str_out[1] = "Insufficient funds of DPG/DPS!";
                }
            } catch (Exception e) {
                e.printStackTrace();
                s14 = false;
                str_out[0] = "1";
                str_out[1] = "Error! "+e.getMessage();
                conn1421.rollback();
            } finally {
                conn1421.close();
            }
            dbc142.getConn().close();
            if (s14 == false) {
                return str_out;
            }
            
            // 1.5. check ownership and balance
            
            if (s11 && s12 && s13 && s14) {
                
                // 2.1. change ownership
                
                // 2.2. record transaction
                String sql22 = "INSERT INTO transaction(me_id_from, me_id_to, "
                        + "tr_amount, pt_id, at_id, ft_id, tr_notes, tr_datetime, "
                        + "tt_id, ts_id) VALUES(?, ?, ?, ?, ?, "
                        + "?, ?, ?, ?, ?)";
                DBConn dbc22 = new DBConn();
                PreparedStatement ps22 = dbc22.getConn().prepareStatement(sql22, Statement.RETURN_GENERATED_KEYS);
                ps22.setInt(1, me_id_from);
                ps22.setInt(2, me_id_to);
                ps22.setDouble(3, tr_amount);
                ps22.setInt(4, pt_id);
                ps22.setInt(5, at_id);
                ps22.setInt(6, 0);
                ps22.setString(7, tr_notes);
                ps22.setString(8, tr_datetime);
                ps22.setInt(9, tt_id);
                ps22.setInt(10, 1);
                ps22.executeUpdate();
                ResultSet rs22 = ps22.getGeneratedKeys();
                int tr_id = 0;
                if (rs22 != null && rs22.next()) {
                    tr_id = rs22.getInt(1);
                }
                dbc22.getConn().close();
                
                if (tr_id > 0) {
                    
                    // 2.3. record transfer dpg/dps
                    String sql23 = "INSERT INTO debit_gram_transfer(dgt_id, me_id_from, "
                            + "me_id_to, dgt_datetime, it_id, dgt_amount, tr_id) "
                            + "VALUES(?, ?, ?, ?, ?, ?, ?) ";
                    DBConn dbc23 = new DBConn();
                    PreparedStatement ps23 = dbc23.getConn().prepareStatement(sql23);
                    ps23.setString(1, My_func.getCurrentTimestamp());
                    ps23.setInt(2, me_id_from);
                    ps23.setInt(3, me_id_to);
                    ps23.setString(4, tr_datetime);
                    ps23.setInt(5, it_id);
                    ps23.setDouble(6, tr_amount);
                    ps23.setInt(7, tr_id);
                    ps23.execute();
                    
                    status = true;
                    str_out[0] = "-1";
                    str_out[1] = tr_id+"";
                    
                } else {
                    status = false;
                    str_out[0] = "1";
                    str_out[1] = "Error while saving the transaction!";
                }
            } else {
                status = false;
                str_out[0] = "1";
                str_out[1] = "Error in the transaction!";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
            str_out[0] = "1";
            str_out[1] = "Error! "+e.getMessage();
        }
        return str_out;
    }

    @Override
    public ArrayList<String> dgt_getDataForReceipt(int tr_id) throws RemoteException {
        ArrayList<String> data = new ArrayList<String>();
        DBConn dbc = new DBConn();
        Connection conn = dbc.getConn();
        try {
            
            String sql = "SELECT "
                    + "tt.tt_code, " //0
                    + "tr.tr_id, " //1
                    + "tt.tt_desc, " //2
                    + "ts.ts_desc, " //3
                    + "meto.me_username, " //4
                    + "meto.me_firstname, " //5
                    + "meto.me_lastname, " //6
                    + "mefrom.me_username, " //7
                    + "mefrom.me_firstname, " //8
                    + "mefrom.me_lastname, " //9
                    + "pt.pt_desc, " //10
                    + "at.at_desc, " //11
                    + "tr.tr_amount, " //12
                    + "tr.tr_datetime, " //13
                    + "tr.tr_notes " //14
                    + "FROM transaction tr, transaction_type tt, transaction_status ts, "
                    + "members meto, members mefrom, payment_type pt, account_type at "
                    + "WHERE tr.tt_id = tt.tt_id "
                    + "AND tr.ts_id = ts.ts_id "
                    + "AND tr.me_id_from = mefrom.me_id "
                    + "AND tr.me_id_to = meto.me_id "
                    + "AND tr.pt_id = pt.pt_id "
                    + "AND tr.at_id = at.at_id "
                    + "AND tr.tr_id = ? ";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, tr_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                try {
                    for (int i = 0; ; i++) {
                        data.add(rs.getString(i+1));
                    }
                } catch (Exception e) {
                }
            }
            
        } catch (Exception e) {
            data.removeAll(data);
            data = new ArrayList<String>();
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return data;
    }
}
