/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tvd.review.service;

/**
 *
 * @author Tibi
 */
public class Util {
    /**
     *
     * @param paramStars
     * @return
     */
    public int formatStars(String paramStars) {
        int noOfStars = 0;
        try {
            noOfStars = Integer.valueOf(paramStars);
            if (noOfStars > 9999) {
                noOfStars = 9999;
            }
            if (noOfStars < 0) {
                noOfStars = 0;
            }
        } catch (NumberFormatException e) {
            noOfStars = 0;
        }
        return noOfStars;
    }
}
