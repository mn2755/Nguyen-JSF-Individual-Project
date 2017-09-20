import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;

@Named(value = "account")
@RequestScoped
@SessionScoped
@ManagedBean


public class AccountBean extends DBConnection implements Serializable {
    private String firstname;
    private String lastname;
    private String mi;
    private String username;
    private String password;
    private String confirmPassword;
    private String flag = "";

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMi() {
        return mi;
    }

    public void setMi(String mi) {
        this.mi = mi;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String submit() throws ClassNotFoundException, SQLException {
        boolean go = true;
        if (containsUserDB(username)) {
            flag = flag + "Username Already Exists \n";
            go = false;
        }
        if (firstname.trim().equals("")) {
            flag = flag + "First Name Cannot Be Blank \n";
            go = false;
        }
        if (lastname.trim().equals("")) {
            flag = flag + "Last Name Cannot Be Blank \n";
            go = false;
        }
        if (mi.trim().equals("")) {
            flag = flag + "MI Cannot Be Blank \n";
            go = false;
        }
        if (username.trim().equals("")) {
            flag = flag + "Username Cannot Be Blank \n";
            go = false;
        }
        if (password.trim().equals("")) {
            flag = flag + "Password Cannot Be Blank \n";
            go = false;
        }
        if (confirmPassword.trim().equals("")) {
            flag = flag + "Confirm Password Cannot Be Blank \n";
            go = false;
        }
        if (!password.trim().equals(confirmPassword.trim())) {
            flag = flag + "Passwords Do Not Match \n";
            go = false;
        }
        if (go) {
            initializeJdbc();           
            insertUser.setString(1, username);
            insertUser.setString(2, firstname);
            insertUser.setString(3, mi);
            insertUser.setString(4, lastname);
            insertUser.setString(5, password);
            insertUser.setTimestamp(6, new Timestamp(new Date().getTime()));
            insertUser.executeUpdate();
            flag = "Account Succesfully Created";
            closeJdbc();
        }

        return "";
    }

    public String back() {
        return "Login?faces-redirect=true";
    }

    public boolean containsUserDB(String user) throws ClassNotFoundException, SQLException {
        initializeJdbc();          
            userSelect.setString(1, user);
            ResultSet rs = userSelect.executeQuery();
            return rs.next(); 
    }
}
