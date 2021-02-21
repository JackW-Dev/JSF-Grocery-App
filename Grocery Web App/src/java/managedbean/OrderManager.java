
package managedbean;

import pojo.Item;
import java.io.Serializable;
import java.util.ArrayList;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named(value = "order")
@SessionScoped
public class OrderManager implements Serializable{
    private ArrayList<Item> contents;
    private ProductManager pm;
    
    public OrderManager(){
    }
        
    public String addProduct(String productName, double productPrice){
        Item a = new Item();
        a.setProductName(productName);
        a.setProdcutPrice(productPrice);
        contents.add(a);
        return "index?faces-redirect=true";
    }
    
    public ArrayList<Item> getContents(){
        return contents;
    }
    
    public String purchase(){
        for(int i = 0; i < contents.size(); i++){
            pm.stockAdjust(contents.get(i).getProductName());
        }
        return "index?faces-redirect=true";
    }
    
    public String viewBasket(){
        
        return "viewBasket?faces-redirect=true";
    }
}
