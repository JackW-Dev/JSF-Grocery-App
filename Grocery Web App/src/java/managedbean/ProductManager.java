package managedbean;

import pojo.User;
import pojo.Product;
import dbase.DbManager;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

@Named(value = "productManager")
@RequestScoped
public class ProductManager implements Serializable {

    private ArrayList<Product> products;
    private String name;
    private double price;
    private double cost;
    private int stock;

    public ProductManager() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String newProduct() throws SQLException {
        boolean dataOK = false;

        try {

            Connection conn = DbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Products (name, price, cost, stock) VALUES (?, ?, ?, ?)");
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setDouble(3, cost);
            stmt.setInt(4, stock);
            int rows = stmt.executeUpdate();

            dataOK = rows == 1;
            stmt.close();
            conn.close();
        } catch (Exception e) {
            //Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, e.toString());
        }
        if (dataOK) {
            getProducts();
            return "viewProducts";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Failed to add product"));
            return null;
        }
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts() {
        ArrayList<Product> prod = new ArrayList<>();
        try {
            Connection conn = DbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Products");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setName(rs.getString("name"));
                product.setPrice(rs.getDouble("price"));
                product.setCost(rs.getDouble("cost"));
                product.setStock(rs.getInt("stock"));

                prod.add(product);
            }

        } catch (Exception e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, e.toString());
        }
        products = prod;
    }

    public String viewProducts() {
        setProducts();
        return "viewProducts";
    }

    public String removeProduct() {
        try {
            Connection conn = DbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM PRODUCTS WHERE NAME = ?");
            stmt.setString(1, name);
        } catch (Exception e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, e.toString());
        }
        return "viewProducts";
    }

    public String stockAdjust(String productName) {
        try {
            Connection conn = DbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE PRODUCTS SET STOCK = STOCK-1 WHERE NAME = ?");
            stmt.setString(1, productName);
            stmt.executeQuery();
        } catch (Exception e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, e.toString());
        }
        return "viewProducts";
    }

    public String editProduct(Product pro) {
        name = pro.getName();
        price = pro.getPrice();
        cost = pro.getCost();
        stock = pro.getStock();
        return "viewProduct";
    }

    public String updateProduct() {
        try {
            Connection conn = DbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE PRODUCTS SET PRICE = ?, COST = ?, STOCK = ? WHERE NAME = ?");
            stmt.setDouble(1, price);
            stmt.setDouble(2, cost);
            stmt.setInt(3, stock);
            stmt.setString(4, name);
        } catch (Exception e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, e.toString());
        }
        return "viewProducts";
    }

    public String productEdit() {
        setProducts();
        return "viewProductsMan";
    }
}
