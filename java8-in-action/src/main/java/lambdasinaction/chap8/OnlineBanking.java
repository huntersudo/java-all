package lambdasinaction.chap8;


import lambdasinaction.chap3.ConsumerInterface;

import java.util.function.Consumer;

abstract class OnlineBanking {
    public void processCustomer(int id){
        Customer c = Database.getCustomerWithId(id);
        makeCustomerHappy(c);
    }
    abstract void makeCustomerHappy(Customer c);

    // dummy Customer class
    static private class Customer {}
    // dummy Datbase class
    static private class Database{
        static Customer getCustomerWithId(int id){ return new Customer();}
    }

    public void processCustomer(int id, Consumer<Customer> makeCustomerHappy){
        Customer c=Database.getCustomerWithId(id);
        makeCustomerHappy.accept(c);
    }


    public static void main(String[] args) {

    }
}


