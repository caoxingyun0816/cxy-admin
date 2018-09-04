package com.wondertek.mam.util.others;

public class NumberIncrementor {
	private static int counter = 1;

    public static void main(String[] args) {
        long sec = 0;
        while (sec++<99999999)
            System.out.println(getSequence());
    }

    synchronized public static String getSequence(){
        if (++counter>99999999) counter=1;
        return String.format("%08d", counter);
    }
}
