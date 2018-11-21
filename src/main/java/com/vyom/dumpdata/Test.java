package com.vyom.dumpdata;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

public class Test {

	
	public static boolean isValid(String s) 
    { 
        Pattern p = Pattern.compile("(0/91)?[7-9][0-9]{9}"); 
        Matcher m = p.matcher(s); 
        return (m.find() && m.group().equals(s)); 
    } 
  
    public static void main(String[] args) 
    { 
        String s = "9021122014";
        if (isValid(s))  
            System.out.println("Valid Number");         
        else 
            System.out.println("Invalid Number");         
    } 
    
    
    public void downloadJar() {
        String absolutePath = getPath();
        String from = absolutePath + "\\temp\\test.txt";
        String to = absolutePath + "\\test.txt";
        File fileTo = new File(to);
        File fileFrom = new File(from);


        try {
            FileUtils.moveFile(fileFrom, fileTo);
            JOptionPane.showMessageDialog(null, "test");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "io exce");
        }

    }

    public String getPath() {
        return System.getProperty("user.dir");
    }
	
}
