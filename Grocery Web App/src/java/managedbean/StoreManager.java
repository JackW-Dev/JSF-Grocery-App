package managedbean;

import pojo.User;
import pojo.Store;
import dbase.DbManager;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import pojo.Product;

@Named(value = "storeManager")
@RequestScoped
public class StoreManager implements Serializable {

    private ArrayList<Store> stores;
    private String name;
    private String address;
    private String postcode;
    private String manager;
    private Date founded;

    public StoreManager() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public Date getFounded() {
        return founded;
    }

    public void setFounded(Date founded) {
        this.founded = founded;
    }

    public String newStore() throws SQLException {
        boolean dataOK = false;
        try {

            Connection conn = DbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Stores (name, address, postcode, manager, founded) VALUES (?, ?, ?, ?, ?)");
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, postcode);
            stmt.setString(4, manager);
            stmt.setDate(5, founded);
            int rows = stmt.executeUpdate();

            dataOK = rows == 1;
            stmt.close();
            conn.close();
        } catch (Exception e) {
            //Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, e.toString());
        }
        if (dataOK) {
            getStores();
            return "viewStores";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Failed to add store"));
            return null;
        }
    }

    public ArrayList<Store> getStores() {
        return stores;
    }

    public void setStores() {
        ArrayList<Store> stor = new ArrayList<>();
        try {
            Connection conn = DbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Stores");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Store store = new Store();
                store.setName(rs.getString("name"));
                store.setAddress(rs.getString("address"));
                store.setPostcode(rs.getString("postcode"));
                store.setManager(rs.getString("manager"));
                store.setFounded(rs.getDate("founded"));

                stor.add(store);
            }

        } catch (Exception e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, e.toString());
        }
        stores = stor;
    }

    public String viewStores() {
        setStores();
        return "viewStores";
    }

    public String removeStore() {
        try {
            Connection conn = DbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM STORES WHERE NAME = ?;");
            stmt.setString(1, name);
        } catch (Exception e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, e.toString());
        }
        return "viewStores";
    }

    public String storeEdit() {
        setStores();
        return "viewStoresMan";
    }

    public String editStore(Store sto) {
        name = sto.getName();
        address = sto.getAddress();
        postcode = sto.getPostcode();
        manager = sto.getManager();
        return "viewProduct";
    }

    public String updateStore(Store sto) {
        try {
            Connection conn = DbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE STORES SET ADDRESS = ?, POSTCODE = ? WHERE NAME = ?");
            stmt.setString(1, address);
            stmt.setString(2, postcode);
            stmt.setString(3, name);
        } catch (Exception e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, e.toString());
        }
        return "viewStores";
    }
}
