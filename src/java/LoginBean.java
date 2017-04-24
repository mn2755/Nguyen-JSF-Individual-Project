
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequestScoped
@Named(value = "login")
@SessionScoped
@ManagedBean
@ViewScoped
public class LoginBean implements Serializable {

    private String username;
    private String password;
    private String flag = "";

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

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String submit() throws ClassNotFoundException, SQLException {
        if (username.trim().equals("")) {
            flag = "Can't Leave username Blank";
        } else if (password.trim().equals("")) {
            flag = "Can't Leave Password Blank";
        } else if (containsUserDB(username)) {
            if (checkPassword(username, password)) {
                flag = "ACCESS GRANTED";
                ExternalContext etx = FacesContext.getCurrentInstance().getExternalContext();
                HttpServletRequest httpReq = (HttpServletRequest) etx.getRequest();
                HttpSession session = httpReq.getSession();
                session.setAttribute("username", username);
                return "Menu?faces-redirect=true";
            } else {
                flag = "Password Invalid";
            }
        } else {
            flag = "username Not Found";
        }

        return "";
    }

    public String create() {
        return "Account?faces-redirect=true";
    }

    public boolean containsUserDB(String user) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://liang.armstrong.edu:3306/nguyen", "nguyen", "tiger");
        //Connection con = DriverManager.getConnection("jdbc:mysql://localhost/selftest", "root", "123qwe");
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * from user WHERE username = ?");
            ps.setString(1, user);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception ex) {
        }
        return false;
    }

    public boolean checkPassword(String user, String pass) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://liang.armstrong.edu:3306/nguyen", "nguyen", "tiger");
        //Connection con = DriverManager.getConnection("jdbc:mysql://localhost/selftest", "root", "123qwe");
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * from user WHERE username = ? and password = ?");
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception ex) {
        }
        return false;
    }

}
