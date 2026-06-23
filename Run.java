public class Run {
    public static void main(String[] args) {
        // create new Database 'Chronicles', with admin ID '*'
        Database chronicles = new Database("Chronicles", "*");
        // loop to keep program running indefinitely, post-system logout
        while(true) {chronicles.start();}
    }
}