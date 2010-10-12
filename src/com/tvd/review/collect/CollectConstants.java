/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tvd.review.collect;

/**
 *
 * @author Tibi
 */
public class CollectConstants {
    public static final String URL_AMAZON_PREFIX  = "http://www.amazon.com/review/product/";
    public static final String URL_AMAZON_POSTFIX = "http://www.amazon.com/review/product/";
    public static final String START_AMAZON_SUMMARY_TABLE = "<table id=\"productSummary\"";
    public static final String START_AMAZON_REVIEW_TABLE = "<table";
    public static final String END_AMAZON_REVIEW_TABLE = "</table>";

    public static final String URL_NEWEGG_PREFIX  = "http://www.newegg.com/Product/ProductReview.aspx?Item=";
    public static final String URL_NEWEGG_POSTFIX = "";
    public static final String START_NEWEGG_SUMMARY_TABLE = "<table class=\"reviewSummary\"";
    public static final String START_NEWEGG_REVIEW_TABLE = "id=\"EGG5AllRate\"";
    public static final String END_NEWEGG_REVIEW_TABLE = "<tr id=\"EGG52WRate\"";

    public static final int POINTS_LIMIT_LOW  = 0;
    public static final int POINTS_LIMIT_HIGH = 10000;
    public static final int METHOD_NONE   = 9;
    public static final int METHOD_AUTO   = 0;
    public static final int METHOD_MODEL  = 1;
    public static final int METHOD_SIMPLE = 2;
}
