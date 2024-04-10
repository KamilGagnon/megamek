package megamek.common;

public interface IPlayerWithRating {
    public float getEloRating();
    public float modifyEloRating(float delta);
    public void resetEloRating();
}
