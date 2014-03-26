package org.kieda.mixx.printer;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author zkieda
 */
public class CharOutputStream extends OutputStream{
    private char[] stream;
    private int insertPos;
    
    public CharOutputStream(){
        setUp(32);
    }
    public CharOutputStream(int len){
        setUp(len==0?1:len);
    }

    private void setUp(int len){
        stream = new char[len];
        insertPos = 0;
    }
    
    @Override
    public void flush() throws IOException {
        setUp(32);
        super.flush();
    }

    @Override
    public void close() throws IOException {
        stream = null;
        super.close();
    }
    
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if(off+len>=b.length || (off|len)<0) 
            throw new IndexOutOfBoundsException();
        checkForAddition(len-1);
        for (int i = off; i < len; i++) {
            stream[insertPos++] = (char)b[i];
        }
    }
    
    @Override
    public void write(int b) throws IOException {
        checkForAddition(0);
        stream[insertPos++] = (char)b;
    }
    
    private void checkForAddition(int count){
        int pos = insertPos+count;
        if(pos>=stream.length){
            char[] temp = new char[pos<<1];
            System.arraycopy(stream, 0, temp, 0, insertPos);
            stream = temp;
        }
    }
}
