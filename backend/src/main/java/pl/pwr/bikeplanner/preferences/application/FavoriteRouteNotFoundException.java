package pl.pwr.bikeplanner.preferences.application;

public class FavoriteRouteNotFoundException extends RuntimeException {

    public FavoriteRouteNotFoundException(long id) {
        super("Favorite route " + id + " not found");
    }
}
