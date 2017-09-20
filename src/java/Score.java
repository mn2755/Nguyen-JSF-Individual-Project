
import java.sql.ResultSet;
import java.sql.SQLException;

public class Score extends DBConnection {

    int chapter;
    int numQuestions = 0;
    int numAttempted = 0;
    int numCorrect = 0;

    Score(int chapter, String username) throws SQLException, ClassNotFoundException {
        initializeJdbc();
        try {
            //PreparedStatement ps = con.prepareStatement("SELECT * FROM intro10equiz WHERE chapterNo =?");
            intro10equizSelect.setString(1, Integer.toString(chapter));
            ResultSet rs = intro10equizSelect.executeQuery();
            while (rs.next()) {
                numQuestions++;
            }

            for (int i = 0; i <= numQuestions; i++) {
                //ps = con.prepareStatement("SELECT * from intro10e WHERE chapterNo =? and questionNo =? and username =?");
                intro10eSelect.setString(1, Integer.toString(chapter));
                intro10eSelect.setString(2, Integer.toString(i + 1));
                intro10eSelect.setString(3, username);
                rs = intro10eSelect.executeQuery();
                while (rs.next()) {

                    if (rs.getBoolean("isCorrect")) {
                        numCorrect++;
                    }

                    if (rs.getBoolean("answerA") || rs.getBoolean("answerB") || rs.getBoolean("answerC") || rs.getBoolean("answerD") || rs.getBoolean("answerE")) {
                        numAttempted++;
                    }
                }
            }
        } catch (Exception ex) {
        }

        this.chapter = chapter;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public int getNumQuestions() {
        return numQuestions;
    }

    public void setNumQuestions(int numQuestions) {
        this.numQuestions = numQuestions;
    }

    public int getNumAttempted() {
        return numAttempted;
    }

    public void setNumAttempted(int numAttempted) {
        this.numAttempted = numAttempted;
    }

    public int getNumCorrect() {
        return numCorrect;
    }

    public void setNumCorrect(int numCorrect) {
        this.numCorrect = numCorrect;
    }

}
