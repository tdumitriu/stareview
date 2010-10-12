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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author tdumitri
 */
public class CollectNeweggReview {

    /**
     * Get scoring information from Newegg for a given item code
     *
     * @param itemASIN
     * @param confidence
     * @return
     */
    public static ScoreReview getNeweggScoreByItem(String itemCode, int confidence, int method) {
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
        String urlReview = CollectConstants.URL_NEWEGG_PREFIX + itemCode + CollectConstants.URL_NEWEGG_POSTFIX;
        StringBuffer tr = getNeweggTableReviewFromInputStream(urlReview);
        // url retrieving time
        Calendar dateTwo = Calendar.getInstance();
        intTime = dateTwo.getTimeInMillis();
        
        // Parse the table end get the review's number of stars
        point = parseNeweggTableReview(tr);
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
        reviewsScore.setCodeASIN(itemCode);
        reviewsScore.setNumberOfFiveStars(point[0]);
        reviewsScore.setNumberOfFourStars(point[1]);
        reviewsScore.setNumberOfThreeStars(point[2]);
        reviewsScore.setNumberOfTwoStars(point[3]);
        reviewsScore.setNumberOfOneStars(point[4]);
        reviewsScore.setConfidence(point[5]);
        reviewsScore.setPostfixURL(CollectConstants.URL_NEWEGG_POSTFIX);
        reviewsScore.setPrefixURL(CollectConstants.URL_NEWEGG_PREFIX);
        reviewsScore.setValidReview(true);
        reviewsScore.setUrlRetrievingTime(intTime-startTime);
        reviewsScore.setParsingTime(parseTime-intTime);
        reviewsScore.setAlgorithmTime(urTime-parseTime);

        return reviewsScore;
    }

    /**
     * Get scoring information from Newegg for a given web page
     *
     * @param itemASIN
     * @param confidence
     * @return
     */
    public static ScoreReview getNeweggScoreByPage(String urlReview, int confidence, int method) {
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
        StringBuffer tr = getNeweggTableReviewFromInputStream(urlReview);
        // url retrieving time
        Calendar dateTwo = Calendar.getInstance();
        intTime = dateTwo.getTimeInMillis();

        // Parse the table end get the review's number of stars
        point = parseNeweggTableReview(tr);
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
        reviewsScore.setCodeASIN(getNeweggItemFromPage(urlReview));
        reviewsScore.setNumberOfFiveStars(point[0]);
        reviewsScore.setNumberOfFourStars(point[1]);
        reviewsScore.setNumberOfThreeStars(point[2]);
        reviewsScore.setNumberOfTwoStars(point[3]);
        reviewsScore.setNumberOfOneStars(point[4]);
        reviewsScore.setConfidence(point[5]);
        reviewsScore.setPostfixURL(CollectConstants.URL_NEWEGG_POSTFIX);
        reviewsScore.setPrefixURL(CollectConstants.URL_NEWEGG_PREFIX);
        reviewsScore.setValidReview(true);
        reviewsScore.setUrlRetrievingTime(intTime-startTime);
        reviewsScore.setParsingTime(parseTime-intTime);
        reviewsScore.setAlgorithmTime(urTime-parseTime);

        return reviewsScore;
    }

    /**
     *
     * @param itemCode
     * @param confidence
     * @param method
     * @return
     */
    public static int[] getNeweggPointsByItem(String itemCode) {
        int[] point = new int[6];
        // Get the reviews summary table from the review url
        String urlReview = CollectConstants.URL_NEWEGG_PREFIX + itemCode + CollectConstants.URL_NEWEGG_POSTFIX;
        StringBuffer tr = getNeweggTableReviewFromInputStream(urlReview);
        // Parse the table end get the review's number of stars
        point = parseNeweggTableReview(tr);
        return point;
    }

    /**
     * 
     * @param urlReview
     * @param confidence
     * @param method
     * @return
     */
    public static int[] getNeweggPointsByPage(String urlReview) {
        int[] point = new int[6];
        // Get the reviews summary table from the review url
        if(!isReviewPage(urlReview)) {
            urlReview = getReviewPage(urlReview);
        }
        StringBuffer tr = getNeweggTableReviewFromInputStream(urlReview);
        // Parse the table end get the review's number of stars
        point = parseNeweggTableReview(tr);
        return point;
    }

    /**
     * Check if the current page address is the review page address
     * 
     * @param urlReview
     * @return
     */
    private static boolean isReviewPage(String urlReview) {
        boolean result = false;
        String regex = "ProductReview";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(urlReview);
        while(matcher.find()) {
            result = true;
        }
        return result;
    }

    /**
     * Get the review page address from the product page address
     *
     * @param urlReview
     * @return
     */
    private static String getReviewPage(String urlReview) {
        String oldValue = "Product.aspx";
        String newValue = "ProductReview.aspx";
        Pattern pattern = Pattern.compile(oldValue);
        Matcher matcher = pattern.matcher(urlReview);
        urlReview = matcher.replaceAll(newValue);

        return urlReview;
    }

    /**
     * Get the Newegg item code from the web page address
     *
     * @param urlReview
     * @return
     */
    private static String getNeweggItemFromPage(String urlReview) {
        String regex = "Item=\\w+";
        String result = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(urlReview);
        while(matcher.find()) {
            String candidate = matcher.group();
            int index = candidate.indexOf("=");
            result = candidate.substring(index + 1);
        }

        return result;
    }

    /**
     * 
     * @param urlInput
     * @param confidence
     * @param method
     * @return
     */
    public static ScoreReview getNeweggScore(String urlInput, int confidence, int method) {
        ScoreReview sr = null;
        String regex = "Item=\\w+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(urlInput);
        if(matcher.find()) {
            sr = getNeweggScoreByPage(urlInput, confidence, method);
        } else {
            sr = getNeweggScoreByItem(urlInput, confidence, method);
        }

        return sr;
    }

    /**
     * 
     * @param tableReview
     * @return
     */
    public static int[] parseNeweggTableReview(StringBuffer tableReview) {
        int[] reviewScore = new int[5];

        String candidate = tableReview.toString();
        String regex = ">(\\d+)Reviews<";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(candidate);
        int count = 4;
        while (matcher.find()) {
            String candidate1 = matcher.group();
            String regex1 = "\\d+";
            Pattern pattern1 = Pattern.compile(regex1);
            Matcher matcher1 = pattern1.matcher(candidate1);
            while (matcher1.find()) {
                try {
                    reviewScore[count] = Integer.parseInt(matcher1.group());
                } catch(NumberFormatException e) {
                    ;//nothing
                }
                --count;
            }
        }

        return reviewScore;
    }
    
    /**
     * Get Newegg table review from the input stream
     *
     * @param urlReview
     * @return
     */
    public static StringBuffer getNeweggTableReviewFromInputStream(String urlReview) {
        String inputLine = null;
        boolean startReading  = false;
        boolean startReadingTable = false;
        String startBlock = CollectConstants.START_NEWEGG_SUMMARY_TABLE;
        String startTable = CollectConstants.START_NEWEGG_REVIEW_TABLE;
        String endTable = CollectConstants.END_NEWEGG_REVIEW_TABLE;
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
