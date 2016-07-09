package com.recommender;

import com.recommender.dataset.Dataset;
import com.recommender.dataset.PostgresDataset;
import com.recommender.dataset.entities.User;
import com.recommender.dataset.entities.Venue;
import com.recommender.filter.QueryFilter;
import com.recommender.filter.sql.CoordanatesFilter;
import com.recommender.filter.sql.SimpleFieldFilter;
import com.recommender.geo.Point;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

class PreferenceCandidatesVenuesSelection {

    private List<Venue> selectedVenues;
    private List<User> selectedUsers;

    private static final Logger logger = LogManager.getLogger(PreferenceCandidatesVenuesSelection.class);

    public static void main(String[] args) throws SQLException {
        PreferenceCandidatesVenuesSelection pcvs = new PreferenceCandidatesVenuesSelection();
        Point testPoint = new Point(40.684871, -73.995012);
        pcvs.selectVenuesAndUsers(45, testPoint);
        System.exit(0);
    }

    public void selectVenuesAndUsers(long userId, Point point) throws SQLException {
        QueryFilter coordinates = new CoordanatesFilter(point, (double) 10.0);
        Dataset dataset = new PostgresDataset();
        List<Venue> venues = dataset.getVenues(coordinates);
        List<User> users = dataset.getUsersWithVenue(venues);
		/*for (int i = 0; i < result.size(); i++) {
			logger.debug("Venue_id: " + result.get(i).getVenueId());
			logger.debug("Distance: " + Coordinates.getDistanceInKm(33.676877,-117.214273, result.get(i).getLatitude(),
					result.get(i).getLongitude()));
		}*/
        this.selectedUsers = users;
        this.selectedVenues = venues;
    }

    public List<Venue> getSelectedVenues() {
        return selectedVenues;
    }

    public void setSelectedVenues(List<Venue> selectedVenues) {
        this.selectedVenues = selectedVenues;
    }

    public List<User> getSelectedUsers() {
        return selectedUsers;
    }

    public long[] getFastSelectedVenues() {
        long data[] = new long[this.selectedVenues.size()];
        int cont = 0;
        for (Venue venue : this.selectedVenues) {
            data[cont] = venue.getId();
        }
        return data;
    }

    public long[] getFastSelectedUsers() {
        long data[] = new long[this.selectedUsers.size()];
        int cont = 0;
        for (User user : this.selectedUsers) {
            data[cont] = user.getUserId();
        }
        return data;
    }

    public void setSelectedUsers(List<User> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }


    public void selectVenuesAndUsersByCity(long userID, String city)throws SQLException {
        Dataset dataset = new PostgresDataset();
        QueryFilter filter = new SimpleFieldFilter("city",city);
        List<Venue> venues = dataset.getVenues(filter);
        List<User> users=dataset.getUsersWithVenue(venues);
        this.selectedUsers=users;
        this.selectedVenues=venues;
    }
}