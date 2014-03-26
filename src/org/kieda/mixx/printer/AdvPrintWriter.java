package org.kieda.mixx.printer;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * @author zkieda
 */
public abstract class AdvPrintWriter extends PrintWriter{
//    private boolean newLine=true;
    
    public AdvPrintWriter(OutputStream out) {
        super(out);
    }

    public abstract boolean isNewLine();
    
    public abstract boolean isTabbingEnabled();
    public abstract void setTabbing(boolean enabled);
    
    public abstract int getTabLength();
    public abstract void setTabLength(int len);
    
    public abstract void pushTab();
    public abstract void popTab();
    
    public abstract int getTabs();
    
//    @Override
//    public void write(int c) {
//        super.write(c);
//        newLine = PrintUtil.isLineSeparator(c);
//    }
//
//    @Override
//    public void write(String s, int off, int len) {
//        super.write(s, off, len); //To change body of generated methods, choose Tools | Templates.
//        newLine = PrintUtil.isLineSeparator(s.charAt(off+len-1));
//    }
//
//    @Override
//    public void write(char[] buf, int off, int len) {
//        super.write(buf, off, len); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public void println() {
//        super.println(); //To change body of generated methods, choose Tools | Templates.
//        newLine=true;
//    }
//    
//    public boolean isNewLine(){
//        return newLine;
//    }
}
