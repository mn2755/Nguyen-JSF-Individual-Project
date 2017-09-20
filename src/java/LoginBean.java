
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Named(value = "login")
@RequestScoped
@SessionScoped
@ManagedBean

public class LoginBean extends DBConnection implements Serializable {

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
            flag = "Username Cannot Be Blank";
        } else if (password.trim().equals("")) {
            flag = "Password Cannot Be Blank";
        } else if (containsUserDB(username)) {
            if (checkPassword(username, password)) {
                flag = "Success";
                ExternalContext etx = FacesContext.getCurrentInstance().getExternalContext();
                HttpServletRequest httpReq = (HttpServletRequest) etx.getRequest();
                HttpSession session = httpReq.getSession();
                session.setAttribute("username", "username");
                return "Menu?faces-redirect=true";
            } else {
                flag = "Invalid Password";
            }
        } else {
            flag = "Username Not Found";
        }
        return "Menu?faces-redirect=true";
    }

    public String create() {
        return "Account?faces-redirect=true";
    }

    public boolean containsUserDB(String user) throws ClassNotFoundException, SQLException {

        initializeJdbc();
        try {
//PreparedStatement ps = con.prepareStatement("SELECT * from user WHERE username = ?");
            userSelect.setString(1, user);
            ResultSet rs = userSelect.executeQuery();
            return rs.next();
        } catch (Exception ex) {

        }
        closeJdbc();
        return false;
    }

    public boolean checkPassword(String user, String pass) throws ClassNotFoundException, SQLException {
        initializeJdbc();
        try {
            //PreparedStatement ps = con.prepareStatement("SELECT * from user WHERE username = ? and password = ?");
            upSelect.setString(1, user);
            upSelect.setString(2, pass);
            ResultSet rs = upSelect.executeQuery();
            return rs.next();
        } catch (Exception ex) {
        }
        closeJdbc();
        return false;
    }

}
