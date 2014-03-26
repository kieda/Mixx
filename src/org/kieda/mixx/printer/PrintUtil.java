package org.kieda.mixx.printer;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author zkieda
 */
public class PrintUtil {
    
    /**
     * returns 2 if we appended nothing, 1 if we ended in a new line, and 0
     * if we did not end in a new line
     */
    public static byte printLeftColumnEnvironment(
            PrintStream out,
            List<String[]> buf, 
            int spacing
        ){
        final int bufSize = buf.size();
        
        //1st - determine the maximum length of a line
        //2nd - print each line as per the elements given from the first
        //loop
        /** Left - spacing 3.
         * [..........]012|[....]     |[........]012|
         * [......]       |[......]012|             |
         * [........]     |[]         |[]           |[.....]
         * 
         * 
         * GOOFY GOOBER   DOPERS     FRIEDFRUMP   
         * CHEAPNOX       DANNYBOY                
         * WILLOWCRUB     ME         NO           LISTERS
         */
        int numCols = 0;
        for (String[] s : buf) {
            if(s.length>numCols) numCols = s.length;
        }
        //the column widths
        int[] columnWidths = new int[numCols];
        
        //the maximum width of all of the columns
        for(String[] ss: buf){//go through all of the lines
            for(int i = 0; i < ss.length; i++){//go through the columns
                int colI = ss[i].length();//the column at i
                
                if(colI>columnWidths[i]){
                    columnWidths[i] = colI;
                        //if we have already allocated a position for 
                        //the column, and the size is greater, overwrite 
                        //it
                }
            }
        }
        byte re = 2;
        snow:for(int h = 0; h < bufSize; h++){
            String[] f = buf.get(h);
            //place each string into the beginning of the column,
            //and then append the number of spaces.
            if(h==bufSize-1 && f.length == 1 && f[0].isEmpty()){break snow;}
            StringBuilder prin = new StringBuilder();
            for(int i = 0; i < f.length; i++){
                char[] spa = new char[spacing + columnWidths[i] - f[i].length()];//length not used = spacing + colWid - f[i].length()
                Arrays.fill(spa, ' ');
                prin.append(f[i]).append(spa);
                     //words   spaces
            }
            if(h != bufSize-1){
                //if we're not on the last statement
                out.println(prin);
                re = 1;
            }else if(prin.length()!=0){
                //if the last statement was not empty, only print it out
                out.print(prin);
                
                //determine if last 
                re = toB(newLine(prin));
            }
            else {
                //there was an empty statement, then a \n was appended, so print a new line.
                out.print('\n');
                re = 1;
            }
        }
        return re;
    }
    
    public static byte toB(boolean b){
        return b?(byte)1:(byte)0;
    }
    public static boolean newLine(CharSequence cs){
        return isLineSeparator(cs.charAt(cs.length()-1));
    }
    public static boolean isLineSeparator(int c){
        return c=='\n'||c=='\r';
    }
}
