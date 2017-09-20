import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBConnection {
    PreparedStatement insertUser;
    PreparedStatement userSelect;
    PreparedStatement upSelect;
    PreparedStatement chNoSelect;
    PreparedStatement scoreSelect;
    PreparedStatement intro10eInsert;
    PreparedStatement intro10eSelect;
    PreparedStatement intro10eDelete;
    PreparedStatement intro10equizSelect;
    Connection con;
    
    public void initializeJdbc() throws ClassNotFoundException, SQLException{
    Class.forName("com.mysql.jdbc.Driver");
    con = DriverManager.getConnection("jdbc:mysql://aal2rblybq2rx3.cti6dcqnzk8e.us-east-2.rds.amazonaws.com:3306/quizz", "mary", "marymary");
    //con = DriverManager.getConnection("jdbc:mysql://localhost/quizz", "root", "123qwe");
    insertUser = con.prepareStatement("insert into users values(?,?,?,?,?,?)");
    userSelect = con.prepareStatement("SELECT * from users WHERE username = ?");
    upSelect = con.prepareStatement("SELECT * from users WHERE username = ? and password = ?");
    chNoSelect = con.prepareStatement("select * from intro10equiz where chapterno = ?");
    scoreSelect = con.prepareStatement("SELECT * from intro10e WHERE chapterNo =? and questionNo =? and username =?");
    intro10eInsert = con.prepareStatement("insert into intro10e values(?,?,?,?,?,?,?,?,?,?,?)");
    intro10eSelect =con.prepareStatement("SELECT * from intro10e WHERE chapterNo =? and questionNo =? and username =?");
    intro10eDelete = con.prepareStatement("DELETE from intro10e WHERE chapterNo =? and questionNo =? and username =?");
    intro10equizSelect = con.prepareStatement("SELECT * FROM intro10equiz WHERE chapterNo =?");
    }
    
    public void closeJdbc() throws SQLException{
        con.close();
        
    }
    
}
