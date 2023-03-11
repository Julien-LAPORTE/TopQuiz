package fr.samneo.android.model;

public class User {
    private String m_firstName;
    private int m_score = 0;

    public String getFirstName() {
        return m_firstName;
    }

    public void setFirstName(String firstName) {
        m_firstName = firstName;
    }

    public int getScore() {
        return m_score;
    }

    public void setScore(int score) {
        m_score = score;
    }
}
