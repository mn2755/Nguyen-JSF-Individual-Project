
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequestScoped
@Named(value = "questionBean")
@SessionScoped
@ViewScoped

public class QuestionBean implements Serializable {

    private int chapter = 0;
    private String id;
    private ArrayList<Question> questionsArray;
    private boolean isAnswered = false;
    private String username = "";
    private String chapterSelect;

    public void chapterNoToArray() throws ClassNotFoundException, SQLException {
        questionsArray = new ArrayList();
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://liang.armstrong.edu:3306/nguyen", "nguyen", "tiger");
        //Connection con = DriverManager.getConnection("jdbc:mysql://localhost/selftest", "root", "123qwe");
        try {
            PreparedStatement ps = con.prepareStatement("select * from intro10equiz where chapterno = ?");
            ps.setString(1, Integer.toString(chapter));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Question question = new Question();
                question.setChapterNo(rs.getInt("chapterNo"));
                question.setQuestionNo(rs.getInt("questionNo"));
                question.setQuestionText(rs.getString("question"));
                question.setChoiceA(rs.getString("choiceA"));
                question.setChoiceB(rs.getString("choiceB"));
                question.setChoiceC(rs.getString("choiceC"));
                question.setChoiceD(rs.getString("choiceD"));
                question.setChoiceE(rs.getString("choiceE"));
                question.setAnswerKey(rs.getString("answerKey"));
                question.setHint(rs.getString("hint"));
                questionsArray.add(question);
            }

            for (int i = 0; i <= questionsArray.size(); i++) {
                ps = con.prepareStatement("SELECT * from intro10e WHERE chapterNo =? and questionNo =? and username =?");
                ps.setString(1, Integer.toString(chapter));
                ps.setString(2, Integer.toString(i + 1));
                ps.setString(3, getUsername());
                rs = ps.executeQuery();
                while (rs.next()) {
                    questionsArray.get(i).setIsCorrect(rs.getBoolean("isCorrect"));
                    questionsArray.get(i).setSelectedBooleans(rs.getBoolean("answerA"), rs.getBoolean("answerB"), rs.getBoolean("answerC"), rs.getBoolean("answerD"), rs.getBoolean("answerE"));
                    questionsArray.get(i).setRadioOrCheck();
                }
            }
        } catch (IOException | SQLException ex) {
        }
    }

    public String getId() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> paramMap = context.getExternalContext().getRequestParameterMap();
        id = paramMap.get("id");
        if (id == null) {
            return Integer.toString(chapter);
        }
        return id;
    }

    public void setId(String id) {
        Map<String, String> params = FacesContext.getCurrentInstance().
                getExternalContext().getRequestParameterMap();
        id = params.get("id");
        chapter = Integer.parseInt(id);
        this.id = id;
    }

    public ArrayList<Question> getQuestionsArray(String i) throws ClassNotFoundException, SQLException {
        chapter = Integer.parseInt(i);
        setQuestionsArray();
        return questionsArray;
    }

    public void setQuestionsArray() throws ClassNotFoundException, SQLException {
        chapterNoToArray();
    }

    public String click(int index) throws ClassNotFoundException, SQLException, IOException {
        if (containsDB(getUsername(), chapter, index + 1)) {
            delete(getUsername(), chapter, index + 1);
            isAnswered = true;
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String host = httpServletRequest.getRemoteAddr();
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://liang.armstrong.edu:3306/nguyen", "nguyen", "tiger");
        //Connection con = DriverManager.getConnection("jdbc:mysql://localhost/selftest", "root", "123qwe");
        try {
            PreparedStatement ps = con.prepareStatement("insert into intro10e values(?,?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(1, chapter);
            ps.setInt(2, index + 1);
            ps.setBoolean(3, questionsArray.get(index).getIsCorrect());
            ps.setTimestamp(4, new Timestamp(new Date().getTime()));
            ps.setString(5, host);
            ps.setBoolean(6, questionsArray.get(index).selected.contains("a"));
            ps.setBoolean(7, questionsArray.get(index).selected.contains("b"));
            ps.setBoolean(8, questionsArray.get(index).selected.contains("c"));
            ps.setBoolean(9, questionsArray.get(index).selected.contains("d"));
            ps.setBoolean(10, questionsArray.get(index).selected.contains("e"));
            ps.setString(11, getUsername());

            int i = ps.executeUpdate();
        } catch (IOException | SQLException ex) {
        }

        return "Questions?faces-redirect=true&username=" + username + "&id=" + id;
    }

    public boolean isIsAnswered() {
        return isAnswered;
    }

    public void setIsAnswered(boolean isAnswered) {
        this.isAnswered = isAnswered;
    }

    public String logout() {
        ExternalContext etx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest httpReq = (HttpServletRequest) etx.getRequest();
        HttpSession session = httpReq.getSession();
        session.setAttribute("username", "");
        return "Login?faces-redirect=true";
    }

    public boolean containsDB(String user, int chapterNo, int questionNo) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://liang.armstrong.edu:3306/nguyen", "nguyen", "tiger");
        //Connection con = DriverManager.getConnection("jdbc:mysql://localhost/selftest", "root", "123qwe");
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * from intro10e WHERE chapterNo =? and questionNo =? and username =?");
            ps.setString(1, Integer.toString(chapterNo));
            ps.setString(2, Integer.toString(questionNo));
            ps.setString(3, user);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
        }
        return false;
    }

    public void delete(String user, int chapterNo, int questionN0) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://liang.armstrong.edu:3306/nguyen", "nguyen", "tiger");
        //Connection con = DriverManager.getConnection("jdbc:mysql://localhost/selftest", "root", "123qwe");
        PreparedStatement ps = con.prepareStatement("DELETE from intro10e WHERE chapterNo =? and questionNo =? and username =?");
        ps.setString(1, Integer.toString(chapterNo));
        ps.setString(2, Integer.toString(questionN0));
        ps.setString(3, user);
        ps.executeUpdate();
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public String getUsername() throws IOException {
        ExternalContext etx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest httpReq = (HttpServletRequest) etx.getRequest();
        HttpSession session = httpReq.getSession();
        username = (String) session.getAttribute("username");
        if (username == null || username.trim().equals("")) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("Login.xhtml");
        }
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<String> getChapterArray() {
        ArrayList<String> chapters = new ArrayList<>();
        for (int i = 1; i <= 44; i++) {
            chapters.add("Chapter " + i);
        }
        return chapters;
    }

    public void selectChapter() {
        chapter = Integer.parseInt(chapterSelect.trim());
    }

    public String getChapterSelect() {
        return chapterSelect;
    }

    public void setChapterSelect(String chapterSelect) {
        this.chapterSelect = chapterSelect.substring(chapterSelect.indexOf(" "));
    }

}
