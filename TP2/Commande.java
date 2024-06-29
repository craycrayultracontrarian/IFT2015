public class Commande {
    String name;
    int amount;

    // Constructor
    Commande(String name, int amount){
        this.name = name;
        this.amount  = amount;
    }

    public String getName(){
        return  name;
    }
    public int getAmount(){
        return amount;
    }
}
