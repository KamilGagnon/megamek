package megamek.common;

public interface IPlayerWithRating {
    public int getEloRating();
    public int modifyEloRating(int delta);
    public void resetEloRating();
}
