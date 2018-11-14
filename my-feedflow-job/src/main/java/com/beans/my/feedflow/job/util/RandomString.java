package com.beans.my.feedflow.job.util;

import java.util.Random;

public class RandomString {

    final static String randomCarts = "abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
    final static String randomCarts_UP = "ABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
    final static String randomCarts_LOW = "abcdefghigklmnopqrstuvwxyz0123456789";
    final static String randomCarts_NUMBER = "0123456789";
    
    static Random random = new Random();
    
    public static String next(int size) {
    	StringBuilder builder = new StringBuilder();
    	while (size-->0) {
    		builder.append(randomCarts.charAt(random.nextInt(randomCarts.length())));
		}
        return builder.toString();
    }
    
    public static String nextUPCase(int size) {
    	StringBuilder builder = new StringBuilder();
    	while (size-->0) {
    		builder.append(randomCarts_UP.charAt(random.nextInt(randomCarts_UP.length())));
		}
        return builder.toString();
    }
    
    public static String nextLOWCase(int size) {
    	StringBuilder builder = new StringBuilder();
    	while (size-->0) {
    		builder.append(randomCarts_LOW.charAt(random.nextInt(randomCarts_LOW.length())));
		}
        return builder.toString();
    }
    
    public static String nextNumber(int size) {
    	StringBuilder builder = new StringBuilder();
    	while (size-->0) {
    		builder.append(randomCarts_NUMBER.charAt(random.nextInt(randomCarts_NUMBER.length())));
		}
        return builder.toString();
    }
}
