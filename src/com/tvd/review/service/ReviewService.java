package com.tvd.review.service;

import com.tvd.common.review.ScoreReview;
import com.tvd.common.review.UserReview;
import com.tvd.review.collect.CollectNeweggReview;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

/**
 *
 * @author Tibi
 */
public class ReviewService extends HttpServlet {

    private static Util util = new Util();

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * 
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        //
        String paramStarsInput    = request.getParameter("stars");
        String paramConfidence    = request.getParameter("cfd");
        String paramScoringMethod = request.getParameter("mtd");
        String paramDecimals      = request.getParameter("dec");
        //
        int[] star        = getStarCoordinates(paramStarsInput);
        int confidence    = formatConfidence(paramConfidence);
        int scoringMethod = formatScoringMethod(paramScoringMethod);
        //
        try {
            double score = userReviewScore(star[4], star[3], star[2], star[1], star[0], confidence, scoringMethod);
            Map<String, String> params = new HashMap<String, String>();
            params.put("score", String.valueOf(score));
            params.put("decimals", paramDecimals);
            params.put("message", getStarsAsString(star));
            out.println(encodeToJson(params));
        } finally {
            out.close();
        }
    }

    /**
     * Get the star numbers as string
     *
     * @param stars
     * @return
     */
    private String getStarsAsString(int[] stars) {
        StringBuffer sb = new StringBuffer();
        for(int i = (stars.length - 1); i >= 0; i--) {
            sb.append(stars[i]);
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * 
     * @param paramStarsInput
     * @return
     */
    private int[] getStarCoordinates(String paramStarsInput) {
        // http://www.newegg.com/Product/ProductReview.aspx?Item=N82E16834115675
        String regex1 = "http://www.newegg.com";
        Pattern pattern1 = Pattern.compile(regex1);
        Matcher matcher1 = pattern1.matcher(paramStarsInput);
        if(matcher1.find()) {
            return CollectNeweggReview.getNeweggPointsByPage(paramStarsInput);
        }
        // N82E16834115675
        int itemLength = paramStarsInput.length();
        boolean isValidLength = (itemLength == 15) ? true : false;
        if(isValidLength) {
            int indexSpace = paramStarsInput.indexOf(" ");
            boolean isNoSpace = (indexSpace < 0) ? true : false;
            if(isNoSpace) {
                char firstChar = paramStarsInput.charAt(0);
                boolean isFirstCharN = (firstChar == 'N') ? true : false;
                if(isFirstCharN) {
                    return CollectNeweggReview.getNeweggPointsByItem(paramStarsInput);
                }
            }
        }
        // 125 25 5 2 1
        return getNumberOfStars(paramStarsInput);
    }

    /**
     * 
     * @param starsInput
     * @return
     */
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

    /**
     * Reverse array order
     *
     * @param array
     * @return
     */
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

    /**
     * Format the string numeric request
     *
     * @param s
     * @return
     */
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

    /**
     * Get the reviews score
     *
     * @param fiveStars
     * @param fourStars
     * @param threeStars
     * @param twoStars
     * @param oneStar
     * @param confidence
     * @param scoringMethod
     * @return
     */
    private double userReviewScore(
            int fiveStars,
            int fourStars,
            int threeStars,
            int twoStars,
            int oneStar,
            int confidence,
            int scoringMethod) {
        UserReview ur = new UserReview();
        ScoreReview scoreReview = null;
        if (validReviewInput(fiveStars, fourStars, threeStars, twoStars, oneStar, confidence, scoringMethod)) {
            int[] points = {fiveStars, fourStars, threeStars, twoStars, oneStar, confidence};
            scoreReview = ur.getScore(points, scoringMethod);
        } else {
            return -1.0;
        }
        double score = scoreReview.getScore();
        return score;
    }

    /**
     *
     * @param fiveStars
     * @param fourStars
     * @param threeStars
     * @param twoStars
     * @param oneStar
     * @param confidence
     * @param scoringMethod
     * @return
     */
    private boolean validReviewInput(
            int fiveStars,
            int fourStars,
            int threeStars,
            int twoStars,
            int oneStar,
            int confidence,
            int scoringMethod) {
        boolean validReviewInput = true;

        return validReviewInput;
    }

    /**
     * Get the confidence value from the parameter
     *
     * @param paramConfidence
     * @return
     */
    private int formatConfidence(String paramConfidence) {
        int confidence = 10;
        try {
            confidence = Integer.valueOf(paramConfidence);
            if (confidence > 9999) {
                confidence = 9999;
            }
            if (confidence < 1) {
                confidence = 10;
            }
        } catch (NumberFormatException e) {
            confidence = 10;
        }
        return confidence;
    }

    /**
     * Get the scoring method from the parameter
     *
     * @param paramScoringMethod
     * @return
     */
    private int formatScoringMethod(String paramScoringMethod) {
        int scoringMethod = 0;
        try {
            scoringMethod = Integer.valueOf(paramScoringMethod);
            if (scoringMethod == 0 ||
                    scoringMethod == 1 ||
                    scoringMethod == 2 ||
                    scoringMethod == 9) {
                ;
            } else {
                scoringMethod = 0;
            }
        } catch (NumberFormatException e) {
            scoringMethod = 0;
        }
        return scoringMethod;
    }

    /**
     * Encode map to json
     *
     * @param params
     * @return
     */
    private String encodeToJson(Map<String, String> params) {
        JSONObject jsonString = new JSONObject();
        for(String key : params.keySet()) {
            Object value = params.get(key);
            jsonString.put(key, value);
        }

        return jsonString.toString();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
