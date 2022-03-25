package robot_navigator;


public class Robot {

    private Position currentPosition;
    private String clientUsername;
    private int hash;
    private int keyID;

    public Robot(String clientUsername) {
        this.currentPosition = new Position();
        this.clientUsername = clientUsername;
    }

    public String getClientUsername() {
        return clientUsername;
    }
    public int getHash(){
        return this.hash;
    }

    public boolean setKeyID(int keyID){
        if(keyID > -9999 && keyID < 9999){
            this.keyID = keyID;
            return true;
        }
        return false;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }


}
