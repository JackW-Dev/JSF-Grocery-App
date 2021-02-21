package managedbean;

import pojo.User;
import dbase.DbManager;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named(value = "userManager")
@SessionScoped
public class UserManager implements Serializable {

    private ArrayList<User> users;
    private String forename;
    private String surname;
    private String username;
    private String password;
    private boolean credentialsOK = false;
    private boolean isManager = false;
    private Date registerDate;
    private String password1;
    private String password2;

    public UserManager() {
    }

    public String register() {
        boolean dataOK = false;

        if (password1.equals(password2)) {
            try {
                byte[] hash
                        = MessageDigest.getInstance("SHA-256")
                                .digest(password1.getBytes(StandardCharsets.UTF_8));

                password1 = Base64.getEncoder().encodeToString(hash);
                registerDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());

                Connection conn = DbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO Users (forename, surname, username, password, manager, creation) VALUES (?, ?, ?, ?, ?, ?)");
                stmt.setString(1, forename);
                stmt.setString(2, surname);
                stmt.setString(3, username);
                stmt.setString(4, password1);
                stmt.setBoolean(5, false);
                stmt.setDate(6, registerDate);
                int rows = stmt.executeUpdate();

                dataOK = rows == 1;

                stmt.close();
                conn.close();
            } catch (Exception e) {
                //Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, e.toString());
            }
        }

        if (dataOK) {
            return "login?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Credentials are not correct"));
            return null;
        }
    }

    public String checkCredentials() {
        credentialsOK = false;
        try {
            Connection conn = DbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            credentialsOK = rs.next() && rs.getString("password").equals(password);
            isManager = rs.getBoolean("manager");
            forename = rs.getString("forename");
            surname = rs.getString("surname");

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, e.toString());
        }

        if (credentialsOK) {
            if (isManager) {
                return "managerUI?faces-redirect=true";
            } else {
                return "index?faces-redirect=true";
            }
        } else {
            clearCredentials();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Login credentials are not correct"));
            return null;
        }

    }

    private void clearCredentials() {
        this.forename = "";
        this.surname = "";
        this.username = "";
        this.password = "";
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }

    public boolean credentialsAreOK() {
        return credentialsOK;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getForename() {
        return forename;
    }

    public String getSurname() {
        return surname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String logOff() {
        clearCredentials();
        return "login?faces-redirect=true";
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public boolean isIsManager() {
        return isManager;
    }

    public void setPassword(String password) {
        try {
            byte[] hash
                    = MessageDigest.getInstance("SHA-256")
                            .digest(password.getBytes(StandardCharsets.UTF_8));

            this.password
                    = Base64.getEncoder().encodeToString(hash);

        } catch (NoSuchAlgorithmException ex) {
            this.password = "";
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers() {
        ArrayList<User> a = new ArrayList<>();
        try {
            Connection conn = DbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User b = new User();

                b.setForename(rs.getString("forename"));
                b.setSurname(rs.getString("surname"));
                b.setUsername(rs.getString("username"));
                b.setRegisterDate(rs.getDate("creation"));
                a.add(b);
            }

        } catch (Exception e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, e.toString());
        }
        users = a;
    }

    public String viewUsers() {
        setUsers();
        return "viewUsers?faces-redirect=true";
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getPassword1() {
        return password1;
    }

    public String getPassword2() {
        return password2;
    }
    
    public String updateDetails() {
        boolean dataOK = false;

        if (password1.equals(password2)) {
            try {
                byte[] hash
                        = MessageDigest.getInstance("SHA-256")
                                .digest(password1.getBytes(StandardCharsets.UTF_8));

                password1 = Base64.getEncoder().encodeToString(hash);
                registerDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());

                Connection conn = DbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement("UPDATE Users SET PASSWORD = ? WHERE USERNAME = ?");
                stmt.setString(1, password1);
                stmt.setString(2, username);
                int rows = stmt.executeUpdate();

                dataOK = rows == 1;

                stmt.close();
                conn.close();
            } catch (Exception e) {
                //Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, e.toString());
            }
        }

        if (dataOK) {
            return "login?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(""));
            return null;
        }
    }
}
