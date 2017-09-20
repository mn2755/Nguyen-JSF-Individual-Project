
import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Named(value = "questionBean")
@RequestScoped
@ManagedBean

public class QuestionBean extends DBConnection implements Serializable {

    private int chapter = 0;
    private String id;
    private ArrayList<Question> questionsArray;
    private boolean isAnswered = false;
    private String username = "";
    private String chapterSelect;

    public void chapterNoToArray() throws ClassNotFoundException, SQLException {
        questionsArray = new ArrayList();
        initializeJdbc();
        try {
            //PreparedStatement ps = con.prepareStatement("select * from intro10equiz where chapterno = ?");
            chNoSelect.setString(1, Integer.toString(chapter));
            ResultSet rs = chNoSelect.executeQuery();
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
closeJdbc();
            for (int i = 0; i <= questionsArray.size(); i++) {
                //ps = con.prepareStatement("SELECT * from intro10e WHERE chapterNo =? and questionNo =? and username =?");
                scoreSelect.setString(1, Integer.toString(chapter));
                scoreSelect.setString(2, Integer.toString(i + 1));
                scoreSelect.setString(3, getUsername());
                rs = scoreSelect.executeQuery();
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
            closeJdbc();
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String host = httpServletRequest.getRemoteAddr();
        initializeJdbc();
        try {
            //PreparedStatement ps = con.prepareStatement("insert into intro10e values(?,?,?,?,?,?,?,?,?,?,?)");
            intro10eInsert.setInt(1, chapter);
            intro10eInsert.setInt(2, index + 1);
            intro10eInsert.setBoolean(3, questionsArray.get(index).getIsCorrect());
            intro10eInsert.setTimestamp(4, new Timestamp(new Date().getTime()));
            intro10eInsert.setString(5, host);
            intro10eInsert.setBoolean(6, questionsArray.get(index).selected.contains("a"));
            intro10eInsert.setBoolean(7, questionsArray.get(index).selected.contains("b"));
            intro10eInsert.setBoolean(8, questionsArray.get(index).selected.contains("c"));
            intro10eInsert.setBoolean(9, questionsArray.get(index).selected.contains("d"));
            intro10eInsert.setBoolean(10, questionsArray.get(index).selected.contains("e"));
            intro10eInsert.setString(11, getUsername());
            intro10eInsert.executeUpdate();
        } catch (IOException | SQLException ex) {
        }
closeJdbc();
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
        return "Account?faces-redirect=true";
    }

    public boolean containsDB(String user, int chapterNo, int questionNo) throws ClassNotFoundException, SQLException {
       initializeJdbc();
        try {
            //PreparedStatement ps = con.prepareStatement("SELECT * from intro10e WHERE chapterNo =? and questionNo =? and username =?");
            intro10eSelect.setString(1, Integer.toString(chapterNo));
            intro10eSelect.setString(2, Integer.toString(questionNo));
            intro10eSelect.setString(3, user);
            ResultSet rs = intro10eSelect.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
        }
        closeJdbc();
        return false;
    }

    public void delete(String user, int chapterNo, int questionN0) throws ClassNotFoundException, SQLException {
        initializeJdbc();
        //PreparedStatement ps = con.prepareStatement("DELETE from intro10e WHERE chapterNo =? and questionNo =? and username =?");
        intro10eDelete.setString(1, Integer.toString(chapterNo));
        intro10eDelete.setString(2, Integer.toString(questionN0));
        intro10eDelete.setString(3, user);
        intro10eDelete.executeUpdate();
        closeJdbc();
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
            FacesContext.getCurrentInstance().getExternalContext().redirect("Menu.xhtml");
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
