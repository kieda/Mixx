package org.kieda.mixx.printer;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;


/**
 * Is a utility for advanced printing, including centering items, tabbing items, 
 * etc.
 * 
 * @author zkieda
 */
public class AdvPrinter {

    //number of tabs in the left hand tabbing environment
    private int tabs = 0;
    
    //are we at the beggining of a new line? If so, we should tab in how many 
    //times.
    private boolean newline = true;
    
    
    private boolean building = false;
    
    
    private boolean tabbing = true;
    
    //the writer we write to.
    private PrintStream out;
    
    //           ArrayList
    //array ["hel", "o", "world"]
    //array ["sdf", "safd"]
    //
    //Rows[ArrayList]    - lines.
    //Columns[Array]     - columns
    private ArrayList<String[]> buffer = new ArrayList<>();//the buffer used in the environment

    
    private String regex = null;
        //null signifies no regex, as no environment is active. null is also used for single column
        //spacing.
    
    //-1 signifies invalid spacing, as no environment is active
    private int spacing = -1;

    /**
     * by default, we split at ':', and print to System.out
     */
    public AdvPrinter() {
        out = System.out;
        regex = ":";
    }
    
    public AdvPrinter(PrintStream out){
        this.out = out;
    }
    public AdvPrinter(String splitRegex){
        this.regex = splitRegex;
    }
    public AdvPrinter(PrintStream out, String splitRegex){
        this.out = out;
        this.regex = splitRegex;
    }
    
    
    
    /**
     * represents the environment we are in 
     */
    private enum Envi{
        NO_ENVI, 
        LEFT_COLUMN_ENVI,
        RIGHT_COLUMN_ENVI,
        MIDDLE_COLUMN_ENVI,
        CREDITS
    }
    
    private Envi envi = Envi.NO_ENVI;//none by default
    
    /**
     * [Flush]
     * flushes all of the lines in the current environment. 
     * 
     * Current environments to be made
     *      - Centered Columns
     *      - CREDITS
     * Current supported environments
     *      - Left Hand Columns
     *      - Right Hand Columns
     */
    
    /**
     * beginEnv(CENTERED, String regex, int padding)
     * 
     * padding is the padding that the maximum line gets on either side
     * 
     * <b>CENTERED:</b>
     * beginEnv(CENTERED, ":", 4);
     * printel("helloworld");
     * printel("I am a");
     * printel("fish.");
     * printel("a VERY big one");
     * end();
     * ----------------
     * |  helloworld  |
     * |    I am a    |
     * |    fish.     |
     * |a VERY big one|
     * ----------------
     * 
     * 
     * ---------------------------------
     * determined by
     * ---------------------------------
     * <======Longest Line, Even======> [32 chars] = k    [   32    ]
     * <=(k-o)/2=>Odd line.<=(k-o/2+1=> [9 chars]  = o    [11][9][12]
     * <===(k-e)/2==>Even<==(k-e)/2===> [4 chars]  = e    [14][4][14]
     * 
     * <======The Longest Line, Odd======> [35 chars] = k    [   35    ]
     * <==(k-o)/2==>Odd line.<==(k-o)/2==> [9 chars]  = o    [13][9][13]
     * <===(k-e)/2===>Even<==(k-e)/2+1===> [4 chars]  = e    [15][4][16]
     * 
     * <b>COLUMNS:</b>
     * 
     */
    public void flush(){
        buffer.clear();
    }
    
    /**
     * [Flush]
     * flushes the last n lines in the given environment. 
     * 
     * Flushing more than there are lines will not throw an error, but will 
     * just flush the current environment.
     * 
     * Flushing does not exit the current enviroment
     */
    public String[][] flush(int n){
        String[][] buf = new String[n][];
        
        for(int i = 0; i < n; i++){
            buf[i] = buffer.remove(buffer.size()-1);//remove the last index n times
        }
        return buf;
    }
    
    /**
     * [End]
     * flushes, prints, and exits the current environment. This is what it all 
     * comes down to.
     */
    public void end(){
        switch(envi){
            case LEFT_COLUMN_ENVI:{
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
//                int maxLen = 0;
                ArrayList<Integer> colWid = new ArrayList<Integer>();
                //the maximum width of all of the columns
                Object[] buf = buffer.toArray();
                for(Object ss: buf){//go through all of the lines
                    String[] f = (String[]) ss;
                    for(int i = 0; i < f.length; i++){//go through the columns
                        int colI = f[i].length();//the column at i
                        if(i>=colWid.size()){//shouldn't ever be greater, but 
                                             //whatev
                            colWid.add(colI);
                                //if we want to add on another element onto the 
                                //end.
                        } else if(colI>colWid.get(i)){
                            colWid.set(i, colI);
                                //if we have already allocated a position for 
                                //the column, and the size is greater, overwrite 
                                //it
                        }
                    }
                }
                snow:for(int h = 0; h < buf.length; h++){
                    String[] f = (String[]) buf[h];
                    //place each string into the beginning of the column,
                    //and then append the number of spaces.
                    if(h==buf.length-1 && f.length == 1 && f[0].isEmpty()){break snow;}
                    String prin = "";
                    for(int i = 0; i < f.length; i++){
                        char[] spa = new char[spacing + colWid.get(i) - f[i].length()];//length not used = spacing + colWid - f[i].length()
                        Arrays.fill(spa, ' ');
                        prin += f[i]+new String(spa);
                             //words   spaces
                    }
                    if(h != buf.length-1)//if we're not on the last statement
                        println(prin);
                    else if(!prin.isEmpty()) print(prin);//if the last statement was not empty, only print it out
                    else println("");//there was an empty statement, then a \n was appended, so print a new line.
                }
            }break;
            case MIDDLE_COLUMN_ENVI:{
                /**
                 * Middle - spacing 3.
                 * |012[..........]012| [....]    |[........]012|
                 * |     [......]     |[......]012|             |
                 * |    [........]    |   []      |    []       |[.....]012|
                 */
                ArrayList<Integer> colWid = new ArrayList<>();
                //the maximum width of all of the columns
                Object[] buf = buffer.toArray();
                for(Object ss: buf){//go through all of the lines
                    String[] f = (String[]) ss;
                    for(int i = 0; i < f.length; i++){//go through the columns
                        int colI = f[i].length();//the column at i
                        if(i>=colWid.size()){//shouldn't ever be greater, but 
                                             //whatev
                            colWid.add(colI);
                                //if we want to add on another element onto the 
                                //end.
                        } else if(colI>colWid.get(i)){
                            colWid.set(i, colI);
                                //if we have already allocated a position for 
                                //the column, and the size is greater, overwrite 
                                //it
                        }
                    }
                }
                snow:for(int h = 0; h < buf.length; h++){
                    String[] f = (String[]) buf[h];
                    //place each string into the beginning of the column,
                    //and then append the number of spaces.
                    String prin = "";
                    if(h==buf.length-1 && f.length == 1 && f[0].isEmpty()){break snow;}
                    boolean exSp = ((spacing&1) == 1);
                    for(int i = 0; i < f.length; i++){
                        char[] whiteSpace1;// = new char[spacing + colWid.get(i) - f[i].length()];//length not used = spacing + colWid - f[i].length()
                        char[] whiteSpace2;// = new char[spacing + colWid.get(i) - f[i].length()];//length not used = spacing + colWid - f[i].length()
                        int n_splen = colWid.get(i) - f[i].length();
                        int splen = spacing+n_splen;
                        int half_splen = (splen)/2;
                        
                        if(i==0){
                            whiteSpace1 = new char[half_splen + spacing/2 + (exSp?1:0)];
//                                spacing on the left
                        } else{
                            whiteSpace1 = new char[half_splen];
                        }
                        if(i == f.length-1){
                            if((splen&1) == 1){//odd
                                whiteSpace2 = new char[half_splen + spacing/2 +1];
                            } else{//even
                                whiteSpace2 = new char[half_splen + spacing/2 ];
                            }
                        } else{
                            if((splen&1) == 1){//odd
                                whiteSpace2 = new char[half_splen + 1];
                            } else{//even
                                whiteSpace2 = new char[half_splen];
                            }
                        }
                        Arrays.fill(whiteSpace1, ' ');
                        Arrays.fill(whiteSpace2, ' ');
                        prin += String.copyValueOf(whiteSpace1)+f[i]+String.copyValueOf(whiteSpace2);
                             //words   spaces
                    }
                    if(h != buf.length-1)//if we're not on the last statement
                        println(prin);
                    else if(!prin.isEmpty()) print(prin);//if the last statement was not empty, only print it out
                    else println("");//there was an empty statement, then a \n was appended, so print a new line.
                }
                }break;
            case RIGHT_COLUMN_ENVI:{
                ArrayList<Integer> colWid = new ArrayList<>();
                //the maximum width of all of the columns
                Object[] buf = buffer.toArray();
                for(Object ss: buf){//go through all of the lines
                    String[] f = (String[]) ss;
                    for(int i = 0; i < f.length; i++){//go through the columns
                        int colI = f[i].length();//the column at i
                        if(i>=colWid.size()){//shouldn't ever be greater, but 
                                             //whatev
                            colWid.add(colI);
                                //if we want to add on another element onto the 
                                //end.
                        } else if(colI>colWid.get(i)){
                            colWid.set(i, colI);
                                //if we have already allocated a position for 
                                //the column, and the size is greater, overwrite 
                                //it
                        }
                    }
                }
                snow:for(int h = 0; h < buf.length; h++){
                    String[] f = (String[]) buf[h];
                    if(h==buf.length-1 && f.length == 1 && f[0].isEmpty()){break snow;}
                    //place each string into the beginning of the column,
                    //and then append the number of spaces.
                    String prin = "";
                    for(int i = 0; i < f.length; i++){
                        char[] spa = new char[spacing + colWid.get(i) - f[i].length()];//length not used = spacing + colWid - f[i].length()
                        Arrays.fill(spa, ' ');
                        prin += String.copyValueOf(spa)+f[i];
                             //words   spaces
                    }
                    if(h != buf.length-1)//if we're not on the last statement
                        println(prin);
                    else if(!prin.isEmpty()) print(prin);//if the last statement was not empty, only print it out
                    else println("");//there was an empty statement, then a \n was appended, so print a new line.
                }
            }break;
            case CREDITS:{
                int largest_width = 0;//largest string line
                //the maximum width of all of the columns
                Object[] buf = buffer.toArray();
                for(Object ss: buf){//go through all of the lines
                    String[] f = (String[]) ss;//current line
                    int curr_len = 0;//added together
                    int curr_max = 0;//the largest string
                    for(int i = 0; i < f.length; i++){//go through the columns
                        int colI = f[i].length();//the column at i's length
                        curr_max = Math.max(curr_max, colI);//setting the 
                                                            //largest string
                    }
                    /**
                     * CREDITS - spacing 3.
                     *    [..........]         [....]          [........]
                     * ##################==================%%%%%%%%%%%%%%%%%%
                     *          [......]                  [......]         
                     * ###########################===========================
                     *    [......]        []            []         [.....]
                     * #############==============%%%%%%%%%%%%%%&&&&&&&&&&&&& 
                     * 
                     * 
                     * 
                     * example 2 - 
                     *   [.......]     [........]     [......]
                     * ##############==============%%%%%%%%%%%%%%
                     */
                    if(f.length<=2||curr_max==(f[0].length())||curr_max==(f[f.length-1].length())){
                        curr_len = (curr_max + 2*spacing)*(f.length);
                    } else{
                        curr_len = curr_max*f.length + spacing*(2 + f.length-1);
                    }
                    largest_width = Math.max(largest_width, curr_len);
                        //set the largest width
                }
                snow:for(int h = 0; h < buf.length; h++){
                    String[] f = (String[]) buf[h];
                    if(h==buf.length-1 && f.length == 1 && f[0].isEmpty()){break snow;}
                    //place each string into the beginning of the column,
                    //and then append the number of spaces.
                    String prin = "";
                    {char[] spa = new char[largest_width];
                    Arrays.fill(spa, ' ');
                    for(int i = 0; i < f.length; i++){
                        int pos;//the insert position
                        if(2*i<f.length){
                            pos = (largest_width*(2*i+1))/(2*f.length) - f[i].length()/2;
                        } else{
                            pos = (largest_width*(2*i+1))/(2*f.length) - (f[i].length()-1)/2 - 1;
                        }

                        char[] dss = f[i].toCharArray();
                        for(int ii = pos; ii<dss.length + pos; ii++){
                            spa[ii] = dss[ii-pos];
                        }
                             //words   spaces
                    }
                    prin = String.copyValueOf(spa);
                    }
                    if(h != buf.length-1)//if we're not on the last statement
                        println(prin);
                    else if(!prin.isEmpty()) print(prin);//if the last statement was not empty, only print it out
                    else println("");//there was an empty statement, then a \n was appended, so print a new line.
                }
                break;
            }case NO_ENVI:
                throw new IllegalStateException("no environment has been opened.");
        }
        endS();
    }
    
    /**
     * [End Silent]
     * flushes, and exits the current environment. Exits silently
     */
    public void endS(){
        flush();
        regex = null;
        spacing = -1;
        envi = Envi.NO_ENVI;
        building = false;
    }
    
    /**
     * [Open Left Column Environment]
     * opens an even-spacing tabbing environment based around a regex
     * and an even spacing
     * <b>example:</b>
     * openlc(":", 4);
     * printe("h:ello world");
     * printe("hell:o world");
     * printe("hello w:orld");
     * printe("hello worl:d");
     * end();
     * <b>will print to </b>
     *      h             ello world
     *      hell          o world
     *      hello w       orld
     *      hello worl    d
     * 
     * [assuming that you are in a tabbing env. with one tab]
     * You may disable and re-enable tabs while in an environment
     * <b>example</b>
     * openlc(":", 4);
     * disableTabs();
     * printe("h:ello world");
     * printe("hell:o world");
     * printe("hello w:orld");
     * printe("hello worl:d");
     * reenableTabs();
     * end();
     * printl("yay")
     * <b>will print to</b>
     * h             ello world
     * hell          o world
     * hello w       orld
     * hello worl    d
     *     yay
     * 
     * 
     * 
     * NOTE: environments do not overlap. 
     */
    public void openLC(int sp, String regex){
        this.regex = regex;
        this.spacing = sp;
        envi = Envi.LEFT_COLUMN_ENVI;
        building = true;
    }
    
    /**[Open Right Column Environment]
     * no regex - set regex to null
     * no spacing - set sp to 0*/
    public void openRC(int sp, String regex){
        this.regex = regex;
        this.spacing = sp;
        envi = Envi.RIGHT_COLUMN_ENVI; 
        building = true;
    }
    /**[Open Middle Column Environment]
     * no regex - set regex to null
     * no spacing - set sp to 0*/
    public void openMC(int sp, String regex){
        this.regex = regex;
        this.spacing = sp;
        envi = Envi.MIDDLE_COLUMN_ENVI;
        building = true;
    }
    /**[Open Credits Environment]
     * no regex - set regex to null
     * no spacing - set sp to 0*/
    public void openCE(int spacing, String regex){
        this.regex = regex;
        this.spacing = spacing;
        envi = Envi.CREDITS;
        building = true;
    }
    
    /**[Open Left Column Environment]
     * single column*/
    public void openLC(int sp){openLC(sp, null);}
    
    /**[Open Right Column Environment]
     * single column.
     * no spacing - set sp to 0*/
    public void openRC(int sp){openRC(sp, null);}
    
    /**[Open Middle Column Environment]
     * single column
     * no spacing - set sp to 0*/
    
    public void openMC(int sp){openMC(sp, null);}
    
    /**[Open Credits Environment]
     * single column
     * no spacing - set sp to 0*/
    public void openCE(int sp){openCE(sp, null);}
    
    /**
     * Sends a string to the current environment
     * 
     * Left - spacing 3.
     * [..........]012|[....]     |[........]012|
     * [......]       |[......]012|             |
     * [........]     |[]         |[]           |[.....]
     * 
     * 
     * GOOFY GOOBER   DOPERS     FRIEDFRUMP   
     * CHEAPNOX       DANNYBOY                
     * WILLOWCRUB     ME         NO           LISTERS
     * 
     * Right - spacing 3.
     * 
     * [..........]|     [....]|012[........]|
     *     [......]|012[......]|             |
     *   [........]|         []|           []|012[.....]
     * 
     * RIGHTSPACERS     CRABSN   SLCRULES!!
     *     VOLUMPER   WAYNOPES             
     *   HEYDOODOOP         NO           YU   CORNPOP
     * 
     * CREDITS - spacing 3.
     *    [..........]         [....]          [........]
     * ##################==================%%%%%%%%%%%%%%%%%%
     *          [......]                  [......]         
     * ###########################===========================
     *    [......]        []            []         [.....]
     * #############==============%%%%%%%%%%%%%%&&&&&&&&&&&&&
     * 
     * Middle - spacing 3.
     * |012[..........]012| [....]    |[........]012|
     * |     [......]     |[......]012|             |
     * |    [........]    |   []      |    []       |[.....]012|
     */
    public void printe(String s){
        //only allocate a new part in the arraylist when a \n occurs
        //no actual printing occurs
        String[] lines = breakpoint(s, "\n");
        //lines is now all of the new lines
        ArrayList<String[]> addition = new ArrayList<String[]>(lines.length);
        for(String l : lines){//goes through list of new lines
            if(regex!=null)
                addition.add(breakpoint(l, regex));
            else {
                String[] asd = new String[1];
                asd[0] = l;
                addition.add(asd);
            }
        }
        if(buffer.isEmpty()) buffer.addAll(addition);
        else if(!addition.isEmpty()){
            int b = buffer.size()-1;
            int l = buffer.get(b).length-1;
//            buffer.get(b)[l].concat(addition.get(0)[0]);
            
            //original:
            //asd safd sadf
            //sadf sadf asdf
            //
            //add : 
            //ddfdf sdaf fds
            //      asdf
            //--->: 
            //asd safd sadf
            //sadf sadf asdfddfdf sdaf fds
            //asdf
            String[] sail = Arrays.copyOf(buffer.get(b), l+ addition.get(0).length);
            sail[l] += addition.get(0)[0];
            for(int i = l+1; i < sail.length; i++){
                 sail[i] = addition.get(0)[i-l];
            }
//            System.arraycopy(addition.get(0), l+1, sail, l+1, sail.length - (l+1));
            buffer.set(b,sail);//put the new row on
            addition.remove(0);//remove the last index
            buffer.addAll(addition);//add the lines to the end of the buffer
        }
    }
    public void printel(String s){
        //allocate a whole new string[] in the araylist at the end of the function (and if any \n occurs.)
        //done by calling print with a new line appended at the end.
        printe(s+"\n");
    }
    private String[] breakpoint(String s, String regex){
        int len = regex.length();
        String[] gg;
        {Deque<String> addit  = new ArrayDeque<>();
        {
        int l = s.length()-len;
        if(l>=0){
            String c = s.substring(l, l+len);
            laf:while(c.equals(regex)){
                addit.add("");
                l-=len;
                if(l<0) break laf;
                c = s.substring(l, l+len);
            }
        }}
        String[] ss = s.split(regex);
        int size = addit.size();
        gg = Arrays.copyOf(ss, size + ss.length);
        for(int i = ss.length; i < size + ss.length; i++){
            gg[i] = addit.pop();
        }}
        if(gg==null) throw new Error("Critical System Error");
        return gg;
    }
    
    
    /**
     * [Print Verbose]
     * prints without any specified formatting
     * Essentially <code>System.out.print(String s)</code>
     */
    public void printV(String s){
        //if we exit on a new line, we're on a new line
        if(s.length() == 0){}
        else if(s.charAt(s.length()-1)=='\n')newline = true;
        else newline = false;
        out.print(s);
    }
    
    /**
     * [Print Verbose Line]
     * prints a line without any specified formatting.
     * 
     * Essentially <code>System.out.println(String s)</code>
     */
    public void printVl(String s){
        out.println(s);
        newline = true;
    }
    /**
     * [Print]
     * Immediately prints according to the current specified format. Includes
     * tabbing. Does not include environments.
     */
    public void print(String s){
        if(tabbing && tabs!=0){
            out.print(tabify(s, new StringBuilder()));
        } else{
            if(s.length()==0) return ;//no action occurs
            
            out.print(s);
            if(s.charAt(s.length()-1)=='\n') newline = true;
            else newline = false;
        }
    }
    
    /**
     * s is the string, sb is the modified
     * input string: safsafd\nsd\n
     *              (if new line) tabs
     *              append "    "
     *              append s, append a, append f, append s, append a, append f, append d
     *              append \n, newline = true, tabify(sd\n)
     *              append "    ", append s, append d, append \n.
     * output - 
     *     sadsafd\n    sd\n
     * OR
     *     sadsafd
     *     sd
     */
    private StringBuilder tabify(String s, StringBuilder sb){
        if(s.isEmpty()) return sb;//we don't want to have extra white space at the end of a new line
        if(newline){
            for(int i = 0; i< tabs; i++) sb.append("    ");
            newline = false;
            
                //add on the number of tabs based on how many tabs should be 
                //put on.
        } for(int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if(c=='\n'){
                sb.append('\n');
                newline = true;
                return tabify(s.substring(i+1), sb);
            } else{
                sb.append(c);
            }
        }
        return sb;
    }
    
    /**
     * [Print Line]
     * Immediately prints according to the current format. Includes tabbing.
     * does not print in an environment. 
     */
    public void println(String s){
        if(tabbing && tabs!=0){
            out.println(tabify(s, new StringBuilder()));
        }
        else  out.println(s);
        newline = true;
    }
    
    /**
     * [Push Tab]
     * move the tabbing forward in the console by one
     */
    public void pushTab(){
        if(tabbing)//tabbing has to be enabled
            tabs++;
    }
    /**
     * [Pop Tab]
     * move the tabbing int the console back by one
     */
    public void popTab(){
        if(tabbing&&tabs>0)
            tabs--;
    }
    
    private int tabH;//tab holder
    public void enableTabs(){
        tabbing = true;
    }
    public void reenableTabs(){
        if(!tabbing){//can't re-enable something that's already enabled
            tabbing = true;
            tabs = tabH;
        }
    }
    public void disableTabs(){
        if(tabbing){
            tabbing = false;
            tabH = tabs;//tab holder
            tabs = 0;
        }
    }

    public void setOut(PrintStream out) {
        this.out = out;
    }

    public PrintStream getOut() {
        return out;
    }
    public String getRegex() {
        return regex;
    }
} 

//abstract class Module{
//    public Module(){   //adds this Module to the Console.
//        Console.addModule(this);
//    }
//    /**
//     * This is the string used when the user calls the function "--help" on your
//     * function
//     */
//    abstract public String helpF();
//    /**
//     * this is is the name that is used for an equation parse.
//     */
//    abstract public String name();
//    
//    /**
//     * this function executes based on the input parameters.
//     */
//    abstract public void execute(String... params);
//    
//    @Override public String toString(){ return name();}
//    
//    public int hashCode(){
//        return name().hashCode();
//    }
//    public boolean equals(Object other){
//        if(other instanceof String) return name().equals((String)other);
//        else if(other instanceof Module) return name().equals(((Module)other).name());
//        return false;
//    }
//} 