package com.recommender.selection;

import com.recommender.dataStructures.*;
import com.recommender.similarity.UserTreeComparison;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.common.TasteException;

import java.util.*;

/**
 * Created by Usuario on 11/07/2016.
 */
public class PreferenceAwareCandidateSelection {

    private static final Logger logger = LogManager.getLogger(PreferenceAwareCandidateSelection.class);

    private final int venuesToRecommend = 50000;

    private String city;
    private double latitude;
    private double longitude;
    private double radius;
    private long userId;
    private UserCategoryTree userTree;
    private Set<Venue> selectedVenues;
    private Set<User> localExperts;
    private HashMap<Long,Double> venuesRating;

    public PreferenceAwareCandidateSelection(String city, double lat, double lon, double radius, long userId){
        this.city = city;
        this.latitude = lat;
        this.longitude = lon;
        this.radius = radius;
        this.userId = userId;
        this.selectedVenues = new HashSet<Venue>();
        this.localExperts = new HashSet<User>();
        this.venuesRating = new HashMap<Long, Double>();
        this.userTree = DataCenter.getInstance().getUserTree(this.userId);
        this.userTree.printTree();
        this.process();
    }

    public void printData(){
        int i = 0;
        logger.debug("Selected venues:");
        logger.debug("================");
        for( Venue v : this.selectedVenues ){
            logger.debug(String.valueOf(i) + " -> " + String.valueOf(v.getId()));
            i++;
        }
        logger.debug("Local experts:");
        logger.debug("==============");
        i= 0;
        for( User u : this.localExperts ){
            logger.debug(String.valueOf(i) + " -> " + String.valueOf(u.getUserId()));
            i++;
        }
    }

    public HashMap<Long, Double> getUserRatings(){
        return this.venuesRating;
    }

    public void calculateRatings() throws TasteException {
        User currentUser = DataCenter.getInstance().getUser(this.userId);
        UserTreeComparison sim = new UserTreeComparison();
        for(Venue v : this.selectedVenues ){
            Long vid = v.getId();
            Double vrating = null;
            if( !currentUser.hasVisitVenue(v.getId()) ){
                logger.debug("User has not visited this venue before");
                for( User u : this.localExperts ){
                    if( u.hasVisitVenue(v.getId()) ){
                        double tmpRating = sim.userSimilarity(this.userId, u.getUserId());
                        tmpRating *= u.getUserCheckinsAt(v.getId());
                        vrating = new Double(tmpRating);
                    }
                }
            }else{
                logger.debug("User already visited this venue");
                vrating = new Double((double)currentUser.getUserCheckinsAt(v.getId()));
            }
            logger.debug("Rating to " + vid + " is " + vrating );
            this.venuesRating.put(vid, vrating);
        }
        HashMap<Long, Long> userVisitedVenues = currentUser.getUserCheckins();
        for( Map.Entry<Long, Long> entry : userVisitedVenues.entrySet() ){
            if( !this.venuesRating.containsKey(entry.getKey()) ){
                this.venuesRating.put(entry.getKey(), new Double(entry.getValue().doubleValue()));
            }
        }
    }

    private void process(){
        if( userTree != null ){
            HashMap<Long, User> users = DataCenter.getInstance().getUsers();
            int usersCompared = 0;
            for( int  i = userTree.getLevels() - 1 ; i >= 0  ; i-- ){// go level to level
                double min = userTree.getLevelMinScore(i);
                if( min > 0 ){
                    List<UserCategoryNode> categoryNodes = userTree.getLevel(i);
                    for( UserCategoryNode category : categoryNodes ){// go category by category
                        int k = (int) Math.ceil(category.getScore() / min);
                        TreeSet<UserCategoryNode> kUsers = new TreeSet<UserCategoryNode>();
                        for( Map.Entry<Long, User> entry : users.entrySet()  ){//candidate local experts
                            if( entry.getKey().longValue() == this.userId ) continue;// if the user is the same one, don't process
                            UserCategoryTree tmpTree = TreesLoader.getInstance().getUserTree(entry.getKey());
                            if( tmpTree != null ){
                                UserCategoryNode tmpNode = tmpTree.getCategoryByLevel(category.getId(),i);
                                if( tmpNode != null ) {
                                    kUsers.add(tmpNode);
                                }
                            }
                        }
                        int j = 0;
                        for(Iterator<UserCategoryNode> it = kUsers.descendingIterator(); it.hasNext() && j < k;) {
                            UserCategoryNode itNode = it.next();
                            User u = DataCenter.getInstance().getUser(itNode.getUserId());
                            this.localExperts.add(u);
                            HashMap<Long, Long> candidateVenues = u.getUserCheckins();
                            Set<Long> tmpVenues = candidateVenues.keySet();
                            for( Long venueId: tmpVenues ){
                                this.selectedVenues.add(DataCenter.getInstance().getVenue(venueId));
                                if( this.selectedVenues.size() == this.venuesToRecommend ) return;
                            }
                            j++;
                        }
                    }
                }
            }
        }
    }
}
