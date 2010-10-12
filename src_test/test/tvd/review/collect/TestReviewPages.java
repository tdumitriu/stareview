package test.tvd.review.collect;

import com.tvd.review.collect.CollectAmazonReview;
import com.tvd.common.review.ReviewConstants;
import com.tvd.common.review.ScoreReview;
import com.tvd.common.review.UserReview;
import com.tvd.review.collect.CollectNeweggReview;
import com.tvd.review.service.ReviewService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;
/**
 *
 * @author Tibi
 */
public class TestReviewPages {
    
    //@Test
    public void testGetAmazonReviewScore() {
        String itemASIN = "B0013FJBX8";  // 0,1,0,0,0
        //String itemASIN = "B001FVRGJ6";  // 0,2,0,0,0
        //String itemASIN = "B001413D94";  // 320,36,6,3,2
        //String itemASIN = "B000U9ZCS6";  // 173,20,1,2,3
        //String itemASIN = "B001413DF8";  // 219,21,9,11,18
        //String itemASIN = "B0010X50J2";  // 0,0,0,0,0

        ScoreReview sr = CollectAmazonReview.getAmazonScoreByItem(itemASIN, 100, 1);
        sr.printData(true);
    }

    //@Test
    public void testGetNeweggReviewScoreWithItem() {
        String itemCode = "N82E16834115675";

        ScoreReview sr = CollectNeweggReview.getNeweggScoreByItem(itemCode, 10, 1);
        sr.printData(true);
    }

    //@Test
    public void testGetNeweggReviewScoreWithPage() {
        String itemReviewPage = "http://www.newegg.com/Product/ProductReview.aspx?Item=N82E16834115675";

        ScoreReview sr = CollectNeweggReview.getNeweggScoreByPage(itemReviewPage, 10, 1);
        sr.printData(true);
    }

    //@Test
    public void testGetNeweggReviewScore() {
        //String itemElement = "N82E16834115675";
        String itemElement = "http://www.newegg.com/Product/ProductReview.aspx?Item=N82E16834115675";

        ScoreReview sr = CollectNeweggReview.getNeweggScore(itemElement, 10, 1);
        sr.printData(true);
    }

    @Test
    public void testGetStarCoordinates() {
        //String paramStarsInput = "165 29 4 0 5";
        //String paramStarsInput = "N82E16834220504";
        //String paramStarsInput = "http://www.newegg.com/Product/ProductReview.aspx?Item=N82E16834220504";
        String paramStarsInput = "http://www.newegg.com/Product/Product.aspx?Item=N82E16834157046";

        int[] point = null;

        String regex1 = "http://www.newegg.com";
        Pattern pattern1 = Pattern.compile(regex1);
        Matcher matcher1 = pattern1.matcher(paramStarsInput);
        if(matcher1.find()) {
            System.out.println("http://www.newegg.com/Product/ProductReview.aspx?Item=N82E16834220504");
            point = CollectNeweggReview.getNeweggPointsByPage(paramStarsInput);
        } else if(paramStarsInput.length() == 15) {
            System.out.println("N82E16834220504");
            int indexSpace = paramStarsInput.indexOf(" ");
            boolean isNoSpace = (indexSpace < 0) ? true : false;
            if(isNoSpace) {
                char firstChar = paramStarsInput.charAt(0);
                boolean isFirstCharN = (firstChar == 'N') ? true : false;
                if(isFirstCharN) {
                    point = CollectNeweggReview.getNeweggPointsByItem(paramStarsInput);
                }
            }
        } else {
            System.out.println("165 29 4 0 5");
            point = getNumberOfStars(paramStarsInput);
        }
        for(int i = 0; i < point.length; i++) {
            System.out.println(i+"star = ["+point[i]+"]");
        }
    }

    private int[] getNumberOfStars(String text) {
        String pattern = "\\D+";
        String patternRaw = "\\D+";
        String patternAmazon = "\\D+";
        String patternNewegg = "www.amazon.com";
        String empty_space = "";
        int maxNumberOfStars = 5;

        int[] stars = new int[maxNumberOfStars];
        // Code
        String[] params = Pattern.compile(pattern).split(formatNumericRequest(text));
        // Amazon
        //http://www.amazon.com/Learning-Python-Animal-Guide-Mark/product-reviews/0596158068/ref=dp_top_cm_cr_acr_pop_hist_all?ie=UTF8&showViewpoints=1
        // Newegg
        //http://www.newegg.com/Product/ProductReview.aspx?Item=N82E16889005079
        if(true) {
            int k = 0;
            for(String param : params) {
                if(param.equals(empty_space)) {continue;}
                if(k >= maxNumberOfStars) {break;}
                stars[k++] = Integer.valueOf(param);
            }
        }

        return reverse(stars);
    }

    private static int[] reverse(int[] stars) {
        int j=0;
        int[] temp = new int[5];
        for (int i=stars.length-1; i >=0; i--) {
            temp[j++] = stars[i];
        }
        for (int i=0; i<stars.length; i++) {
            stars[i] = temp[i];
        }
        return stars;
    }

    private static String formatNumericRequest(String s) {
        StringBuilder b = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch >= '0' && ch <= '9') {
                b.append(ch);
            } else {
                b.append(" ");
            }
        }
        return b.toString();
    }

    //@Test
    public void testGetReviewMultipleScores() {
        String[] asin = {
            "B0013FJBX8",  // 0,1,0,0,0
            "B001FVRGJ6",  // 0,2,0,0,0
            "B001413D94",  // 320,36,6,3,2
            "B000U9ZCS6",  // 173,20,1,2,3
            "B000U9XMCE"   // 219,21,9,11,18
        };
        for(int i = 0; i < asin.length; i++) {
            ScoreReview sr = CollectAmazonReview.getAmazonScoreByItem(asin[i], 10, 0);
            sr.printData(true);
        }
    }

    //@Test
    public void testUserReviewScore() {
        UserReview ur = new UserReview();
        for(int i=1; i<101; i++) {
            System.out.print(i);
            for(int j=1; j < 5; j++) {
                int[] point = {200, 0, 0, 0, 0, 50};
                point[j] = i;
                ScoreReview score = ur.getScore(point, ReviewConstants.METHOD_MODEL);
                System.out.print("\t" + score.getScore());
            }
            System.out.println();
        }
    }

    //@Test
    public void testUserReviewScoreWithMethodAuto() {
        UserReview ur = new UserReview();
        //            {5, 4, 3, 2, 1} stars
        int[] point = {85, 22, 6, 4, 7, 100};
        ScoreReview score = ur.getScore(point, ReviewConstants.METHOD_AUTO);
        System.out.println(score.getScore());
    }

    //@Test
    public void testUserReviewScores() {
        UserReview ur = new UserReview();
        //            {5, 4, 3, 2, 1} stars
        int count = 0;
        for(int i=0; i< 5; i++) {
            for(int j=0; j< 5; j++) {
                for(int k=0; k< 5; k++) {
                    for(int m=0; m< 5; m++) {
                        for(int n=0; n< 5; n++) {
                            int[] point = {i, j, k, m, n, 0};
                            ScoreReview score = ur.getScore(point, ReviewConstants.METHOD_AUTO);
                            System.out.println((++count)+".\t("+i+", "+j+", "+k+", "+m+", "+n+") = "+score.getScore());
                        }
                    }
                }
            }
        }
    }
}
