package com.recommender;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.RefreshHelper;
import org.apache.mahout.cf.taste.impl.recommender.AbstractRecommender;
import org.apache.mahout.cf.taste.impl.recommender.EstimatedPreferenceCapper;
import org.apache.mahout.cf.taste.impl.recommender.TopItems;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Rescorer;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.LongPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.recommender.geo.Point;

/**
 * <p>
 * A simple {@link org.apache.mahout.cf.taste.recommender.Recommender} which
 * uses a given {@link DataModel} and {@link UserNeighborhood} to produce
 * recommendations.
 * </p>
 */
public class MyUserBasedRecommender extends AbstractRecommender implements UserBasedRecommender {

	private static final Logger log = LoggerFactory.getLogger(MyUserBasedRecommender.class);

	private final UserNeighborhood neighborhood;
	private final UserSimilarity similarity;
	private final RefreshHelper refreshHelper;
	private EstimatedPreferenceCapper capper;

	public MyUserBasedRecommender(DataModel dataModel, UserNeighborhood neighborhood, UserSimilarity similarity) {
		super(dataModel);
		Preconditions.checkArgument(neighborhood != null, "neighborhood is null");
		this.neighborhood = neighborhood;
		this.similarity = similarity;
		this.refreshHelper = new RefreshHelper(new Callable<Void>() {

			public Void call() {
				capper = buildCapper();
				return null;
			}
		});
		refreshHelper.addDependency(dataModel);
		refreshHelper.addDependency(similarity);
		refreshHelper.addDependency(neighborhood);
		capper = buildCapper();
	}

	public UserSimilarity getSimilarity() {
		return similarity;
	}

	public List<RecommendedItem> recommend(long userID, int howMany, IDRescorer rescorer, boolean includeKnownItems)
			throws TasteException {
		Preconditions.checkArgument(howMany >= 1, "howMany must be at least 1");

		log.debug("Recommending items for user ID '{}'", userID);

		PreferenceCandidatesVenuesSelection p = new PreferenceCandidatesVenuesSelection();
		Point testPoint = new Point(40.684871, -73.995012);
		try {
			//p.selectVenuesAndUsers(userID, testPoint);
			p.selectVenuesAndUsersByCity(userID, "New York");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// long[] theNeighborhood = neighborhood.getUserNeighborhood(userID);
		long[] theNeighborhood = p.getFastSelectedUsers();

		if (theNeighborhood.length == 0) {
			return Collections.emptyList();
		}

		// FastIDSet allItemIDs = getAllOtherItems(theNeighborhood, userID,
		// includeKnownItems);
		FastIDSet allItemIDs = new FastIDSet(p.getFastSelectedVenues());
		// System.out.println(allItemIDs.size());
		// System.out.println(theNeighborhood);
		// System.out.println(allItemIDs);
		// System.exit(0);

		TopItems.Estimator<Long> estimator = new Estimator(userID, theNeighborhood);

		List<RecommendedItem> topItems = TopItems.getTopItems(howMany, allItemIDs.iterator(), rescorer, estimator);

		log.debug("Recommendations are: {}", topItems);
		return topItems;
	}

	public float estimatePreference(long userID, long itemID) throws TasteException {
		DataModel model = getDataModel();
		Float actualPref = model.getPreferenceValue(userID, itemID);
		if (actualPref != null) {
			return actualPref;
		}
		long[] theNeighborhood = neighborhood.getUserNeighborhood(userID);
		return doEstimatePreference(userID, theNeighborhood, itemID);
	}

	public long[] mostSimilarUserIDs(long userID, int howMany) throws TasteException {
		return mostSimilarUserIDs(userID, howMany, null);
	}

	public long[] mostSimilarUserIDs(long userID, int howMany, Rescorer<LongPair> rescorer) throws TasteException {
		TopItems.Estimator<Long> estimator = new MostSimilarEstimator(userID, similarity, rescorer);
		return doMostSimilarUsers(howMany, estimator);
	}

	private long[] doMostSimilarUsers(int howMany, TopItems.Estimator<Long> estimator) throws TasteException {
		DataModel model = getDataModel();
		return TopItems.getTopUsers(howMany, model.getUserIDs(), null, estimator);
	}

	protected float doEstimatePreference(long theUserID, long[] theNeighborhood, long itemID) throws TasteException {
		if (theNeighborhood.length == 0) {
			return Float.NaN;
		}
		DataModel dataModel = getDataModel();
		double preference = 0.0;
		double totalSimilarity = 0.0;
		int count = 0;
		for (long userID : theNeighborhood) {
			if (userID != theUserID) {
				// See GenericItemBasedRecommender.doEstimatePreference() too
				Float pref;
				try {
					 pref= dataModel.getPreferenceValue(userID, itemID);
				} catch (Exception e) {
					pref = null;
				}
				if (pref != null) {
					double theSimilarity = similarity.userSimilarity(theUserID, userID);
					if (!Double.isNaN(theSimilarity)) {
						preference += theSimilarity * pref;
						totalSimilarity += theSimilarity;
						count++;
					}
				}

			}
		}
		// Throw out the estimate if it was based on no data points, of course,
		// but also if based on
		// just one. This is a bit of a band-aid on the 'stock' item-based
		// algorithm for the moment.
		// The reason is that in this case the estimate is, simply, the user's
		// rating for one item
		// that happened to have a defined similarity. The similarity score
		// doesn't matter, and that
		// seems like a bad situation.
		if (count <= 1) {
			return Float.NaN;
		}
		float estimate = (float) (preference / totalSimilarity);
		if (capper != null) {
			estimate = capper.capEstimate(estimate);
		}
		return estimate;
	}

	protected FastIDSet getAllOtherItems(long[] theNeighborhood, long theUserID, boolean includeKnownItems)
			throws TasteException {
		DataModel dataModel = getDataModel();
		FastIDSet possibleItemIDs = new FastIDSet();
		for (long userID : theNeighborhood) {
			possibleItemIDs.addAll(dataModel.getItemIDsFromUser(userID));
		}
		if (!includeKnownItems) {
			possibleItemIDs.removeAll(dataModel.getItemIDsFromUser(theUserID));
		}
		return possibleItemIDs;
	}

	public void refresh(Collection<Refreshable> alreadyRefreshed) {
		refreshHelper.refresh(alreadyRefreshed);
	}

	public String toString() {
		return "GenericUserBasedRecommender[neighborhood:" + neighborhood + ']';
	}

	private EstimatedPreferenceCapper buildCapper() {
		DataModel dataModel = getDataModel();
		if (Float.isNaN(dataModel.getMinPreference()) && Float.isNaN(dataModel.getMaxPreference())) {
			return null;
		} else {
			return new EstimatedPreferenceCapper(dataModel);
		}
	}

	private static final class MostSimilarEstimator implements TopItems.Estimator<Long> {

		private final long toUserID;
		private final UserSimilarity similarity;
		private final Rescorer<LongPair> rescorer;

		private MostSimilarEstimator(long toUserID, UserSimilarity similarity, Rescorer<LongPair> rescorer) {
			this.toUserID = toUserID;
			this.similarity = similarity;
			this.rescorer = rescorer;
		}

		public double estimate(Long userID) throws TasteException {
			// Don't consider the user itself as a possible most similar user
			if (userID == toUserID) {
				return Double.NaN;
			}
			if (rescorer == null) {
				return similarity.userSimilarity(toUserID, userID);
			} else {
				LongPair pair = new LongPair(toUserID, userID);
				if (rescorer.isFiltered(pair)) {
					return Double.NaN;
				}
				double originalEstimate = similarity.userSimilarity(toUserID, userID);
				return rescorer.rescore(pair, originalEstimate);
			}
		}
	}

	private final class Estimator implements TopItems.Estimator<Long> {

		private final long theUserID;
		private final long[] theNeighborhood;

		Estimator(long theUserID, long[] theNeighborhood) {
			this.theUserID = theUserID;
			this.theNeighborhood = theNeighborhood;
		}

		public double estimate(Long itemID) throws TasteException {
			return doEstimatePreference(theUserID, theNeighborhood, itemID);
		}
	}
}
