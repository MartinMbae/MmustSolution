package studios.luxurious.mmustsolution.Leisure.LeaderBoard;

public class Leader {

    public String uid;
    public long highScores;


    public Leader() {
    }

    public Leader(String uid, long highScores) {
        this.uid = uid;
        this.highScores = highScores;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getHighScores() {
        return highScores;
    }

    public void setHighScores(long highScores) {
        this.highScores = highScores;
    }
}
