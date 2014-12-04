package hu.bute.auctionapp.data;

/**
 * Created by Andras on 2014.12.04..
 */
public class UserData {
    private String name;
    private String passwordMD5;

    public UserData(String name) {
        this.name = name;
    }

    public String getPasswordMD5() {
        return passwordMD5;
    }

    public void setPasswordMD5(String passwordMD5) {
        this.passwordMD5 = passwordMD5;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
