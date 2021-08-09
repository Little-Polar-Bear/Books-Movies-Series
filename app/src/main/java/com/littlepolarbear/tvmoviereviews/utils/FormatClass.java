package com.littlepolarbear.tvmoviereviews.utils;

/*A class designed to take parameters(input) and return a correctly formatted output*/
public class FormatClass {

    /*Method to take a number and return it as the string ordinal value.
     * Eg: convert 1 to "1st"*/
    public static String numberFormatOrdinal(int value) {
        // 0 -9 correct suffixes in string array
        String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th", "th"};
        // special cases where the remainder does not match the algorithm.
        // 11th, 12th and 13th do not match 1st,2nd, 3rd, 103rd, 71st etc...
        switch (value % 100) {
            case 11:
            case 12:
            case 13:
                return value + "th";
            default:
                // divide by 10 and find the remainder,
                // that will give the correct suffix position to tac onto the number value.
                //eg: 57 % 10 equals 5 remainder 7 -> go to position 7 -> tac on 'th' -> return 57th
                return value + suffixes[value % 10];
        }
    }

    /*A method to extract a number from a string*/
    public static int extractNumber(String string){
        // make sure string has a number
        if (string.matches(".*\\d.*")){
            // use a regular expression to replace all non numbers with empty string.
            int value = Integer.parseInt(string.replaceAll("[^0-9]", ""));
            // return the value
            return value;
        }
        // return -1 to indicate there was no number in the string
        return -1;
    }
}
