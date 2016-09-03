package com.lianshang.node.freemarker.utils;

import java.io.*;

/**
 * Created by beven on 2016/2/26.
 */
public class UFile {

    public static boolean exists(String file){
        return (new File(file)).exists();
    }

    public static String read(String path,String encoding) {
        BufferedReader br= null;
        StringBuffer sb=new StringBuffer();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path),("utf-8")));
            String temp=null;
            temp=br.readLine();
            while(temp!=null){
                sb.append(temp+"\n");
                temp=br.readLine();
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static boolean write(String path, String content, String encoding, boolean append) {
        boolean state = false;
        try {
            ensureDir(path);
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(path, append), encoding);
            Writer writer = new BufferedWriter(out);
            writer.write(content);
            writer.flush();
            writer.close();
            state = true;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return state;
    }

    public static void ensureDir(String path){
        java.io.File dir = new java.io.File(path).getParentFile();

        if(!dir.exists()){
            dir.mkdir();
        }
    }
}
