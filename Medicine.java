public class Medicine{
    private String id;
    private String name;
    private long price;
    private int quantity;
    private int quantitySold;
    private String distributor;

    public Medicine(String id, String name, long price, int quantity, String distributor){
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.quantitySold = 0;
        this.distributor = distributor;
    }

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public long getPrice(){
        return price;
    }

    public int getQuantity(){
        return quantity;
    }

    public int getQuantitySold(){
        return quantitySold;
    }

    public int getQuantityRemaining(){
        return quantity - quantitySold;
    }

    public String getDistributor(){
        return distributor;
    }


    public void setId(String id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPrice(long price){
        this.price = price;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    public void setQuantitySold(int quantitySold){
        this.quantitySold = quantitySold;
    }

    public void setDistributor(String distributor){
        this.distributor = distributor;
    }

    public boolean sellMedicine(int amount){
        if(getQuantityRemaining() >= amount){
            this.quantitySold += amount;
            return true;
        }
        return false;
    }

    public String toString(){
        return id + "," + name + "," + price + "," + quantity + "," + quantitySold + "," + distributor;
    }
}
