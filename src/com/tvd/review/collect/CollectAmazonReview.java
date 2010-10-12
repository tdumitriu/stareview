package com.tvd.review.collect;

import com.tvd.common.review.ReviewConstants;
import com.tvd.common.review.ScoreReview;
import com.tvd.common.review.UserReview;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

/**
 *
 * @author tdumitri
 */
public class CollectAmazonReview {

    /**
     * Get scoring information from Amazon for a given item code
     *
     * @param itemASIN
     * @param confidence
     * @return
     */
    public static ScoreReview getAmazonScoreByItem(String itemASIN, int confidence, int method) {
        ScoreReview reviewsScore = null;
        int[] point = new int[6];
        long startTime = 0L;
        long intTime   = 0L;
        long parseTime = 0L;
        long urTime    = 0L;
        // Start time
        Calendar dateOne = Calendar.getInstance();
        startTime = dateOne.getTimeInMillis();

        // Get the reviews summary table from the review url
        String urlReview = CollectConstants.URL_AMAZON_PREFIX + itemASIN + CollectConstants.URL_AMAZON_POSTFIX;
        StringBuffer tr = getAmazonTableReviewFromInputStream(urlReview);
        // url retrieving time
        Calendar dateTwo = Calendar.getInstance();
        intTime = dateTwo.getTimeInMillis();
        
        // Parse the table end get the review's number of stars
        point = parseAmazonTableReview(tr);
        // Validate the equation's points
        validatePoints(point, confidence);
        // review table parsing time
        Calendar dateThre = Calendar.getInstance();
        parseTime = dateThre.getTimeInMillis();
        
        // Get the reviews score
        UserReview ur = new UserReview();
        reviewsScore = ur.getScore(point, method);

        // Algorithm time
        Calendar dateFour = Calendar.getInstance();
        urTime = dateFour.getTimeInMillis();

        // Set the data to the ReviewsScore object
        reviewsScore.setMethod(method);
        reviewsScore.setCodeASIN(itemASIN);
        reviewsScore.setNumberOfFiveStars(point[0]);
        reviewsScore.setNumberOfFourStars(point[1]);
        reviewsScore.setNumberOfThreeStars(point[2]);
        reviewsScore.setNumberOfTwoStars(point[3]);
        reviewsScore.setNumberOfOneStars(point[4]);
        reviewsScore.setConfidence(point[5]);
        reviewsScore.setPostfixURL(CollectConstants.URL_AMAZON_POSTFIX);
        reviewsScore.setPrefixURL(CollectConstants.URL_AMAZON_PREFIX);
        reviewsScore.setValidReview(true);
        reviewsScore.setUrlRetrievingTime(intTime-startTime);
        reviewsScore.setParsingTime(parseTime-intTime);
        reviewsScore.setAlgorithmTime(urTime-parseTime);

        return reviewsScore;
    }

    /**
     * Get scoring information from Amazon for a given web page
     *
     * @param itemASIN
     * @param confidence
     * @return
     */
    public static ScoreReview getAmazonScoreByPage(String urlReview, int confidence, int method) {
        ScoreReview reviewsScore = null;
        int[] point = new int[6];
        long startTime = 0L;
        long intTime   = 0L;
        long parseTime = 0L;
        long urTime    = 0L;
        // Start time
        Calendar dateOne = Calendar.getInstance();
        startTime = dateOne.getTimeInMillis();

        // Get the reviews summary table from the review url
        StringBuffer tr = getAmazonTableReviewFromInputStream(urlReview);
        // url retrieving time
        Calendar dateTwo = Calendar.getInstance();
        intTime = dateTwo.getTimeInMillis();

        // Parse the table end get the review's number of stars
        point = parseAmazonTableReview(tr);
        // Validate the equation's points
        validatePoints(point, confidence);
        // review table parsing time
        Calendar dateThre = Calendar.getInstance();
        parseTime = dateThre.getTimeInMillis();

        // Get the reviews score
        UserReview ur = new UserReview();
        reviewsScore = ur.getScore(point, method);

        // Algorithm time
        Calendar dateFour = Calendar.getInstance();
        urTime = dateFour.getTimeInMillis();

        // Set the data to the ReviewsScore object
        reviewsScore.setMethod(method);
        reviewsScore.setCodeASIN(getAmazonItemFromPage(urlReview));
        reviewsScore.setNumberOfFiveStars(point[0]);
        reviewsScore.setNumberOfFourStars(point[1]);
        reviewsScore.setNumberOfThreeStars(point[2]);
        reviewsScore.setNumberOfTwoStars(point[3]);
        reviewsScore.setNumberOfOneStars(point[4]);
        reviewsScore.setConfidence(point[5]);
        reviewsScore.setPostfixURL(CollectConstants.URL_AMAZON_POSTFIX);
        reviewsScore.setPrefixURL(CollectConstants.URL_AMAZON_PREFIX);
        reviewsScore.setValidReview(true);
        reviewsScore.setUrlRetrievingTime(intTime-startTime);
        reviewsScore.setParsingTime(parseTime-intTime);
        reviewsScore.setAlgorithmTime(urTime-parseTime);

        return reviewsScore;
    }

    /**
     * Get the amazon item code from the web page address
     *
     * @param urlReview
     * @return
     */
    private static String getAmazonItemFromPage(String urlReview) {

        return "";
    }

    /**
     * 
     * @param tableReview
     * @return
     */
    public static int[] parseAmazonTableReview(StringBuffer tableReview) {
        int[] reviewScore = new int[6];
        StringBuffer sb = new StringBuffer();
        int index[] = new int[6];
        index[5] = tableReview.indexOf("5 star");
        index[4] = tableReview.indexOf("4 star");
        index[3] = tableReview.indexOf("3 star");
        index[2] = tableReview.indexOf("2 star");
        index[1] = tableReview.indexOf("1 star");
        if(index[5] >= 0 && index[4] >= 0 && index[3] >= 0 && index[2] >= 0 && index[1] >= 0) {
            for(int i = 5; i >= 1; i--) {
                sb.delete(0,sb.length());
                int markCounter = 0;
                boolean startReading = false;
                boolean startReadingNumber = false;
                boolean stopReading = false;
                boolean startMark = false;
                boolean confirmReading = false;
                for(int j = index[i]; j < tableReview.length(); j++) {
                    char crtC = tableReview.charAt(j);
                    StringBuffer cMark = new StringBuffer();
                    for(int k=0; k < 3; k++) {
                        cMark.append(tableReview.charAt(j+k));
                    }
                    if(cMark.toString().equalsIgnoreCase("<td")) {
                        if(markCounter < 1) {
                            markCounter++;
                        } else {
                            startMark = true;
                        }
                    }
                    if(startMark) {
                        if(crtC == (int)'>') {
                            startReading = true;
                        }
                        if(startReading) {
                            if(crtC == (int)'(') {
                                startReadingNumber = true;
                            }
                            if(startReadingNumber) {
                                if(confirmReading) {
                                    if(crtC == (int)')') {
                                        stopReading = true;
                                    }
                                    if(!stopReading) {
                                        sb.append(crtC);
                                    } else {
                                        break;
                                    }
                                } else {
                                    confirmReading = true;
                                }
                            }
                        }
                    }
                }
                try {
                    reviewScore[5-i] = Integer.valueOf(sb.toString());
                } catch(NumberFormatException e) {
                    reviewScore[5-i] = 0;
                }
            }
        } else {
            System.out.println("Star statistics incomplete!");
        }
        return reviewScore;
    }
    
    /**
     * Get Amazon table review from the input stream
     *
     * @param urlReview
     * @return
     */
    public static StringBuffer getAmazonTableReviewFromInputStream(String urlReview) {
        String inputLine = null;
        boolean startReading  = false;
        boolean startReadingTable = false;
        String startBlock = CollectConstants.START_AMAZON_SUMMARY_TABLE;
        String startTable = CollectConstants.START_AMAZON_REVIEW_TABLE;
        String endTable = CollectConstants.END_AMAZON_REVIEW_TABLE;
        StringBuffer tableReview = new StringBuffer();
        try{
            URL url = new URL(urlReview);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((inputLine = in.readLine()) != null) {
                int startIndexBlock = inputLine.indexOf(startBlock);
                if(startIndexBlock >= 0) {
                    startReading = true;
                }
                if(startReading) {
                    int startIndexReading = inputLine.indexOf(startTable);
                    if(startIndexReading >= 0 && startIndexBlock < 0) {
                        startReadingTable = true;
                    }
                    if(startReadingTable) {
                        if(inputLine.indexOf(endTable) < 0) {
                            tableReview.append(inputLine + "\n");
                        } else {
                            tableReview.append(inputLine + "\n");
                            break; 
                        }
                    }
                }
            }            
            in.close();
        } catch(MalformedURLException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return tableReview;
    }
    
    /**
     * Validate points
     *
     * @param point
     * @param confidence
     */
    private static void validatePoints(int[] point, int confidence) {
        // 1.   Validate star points
        // 1.1. Range limits
        for(int i=5; i >=1; i--) {
            if(point[i] < 0) {point[i] = ReviewConstants.POINTS_LIMIT_LOW;}
            if(point[i] > 10000) {point[i] = ReviewConstants.POINTS_LIMIT_HIGH;}
        }
        // 2. Validate confidence value
        if(confidence < 0) {confidence = 0;}
        if(confidence > 1000) {confidence = 1000;}
        point[5] = confidence;
    }
}
