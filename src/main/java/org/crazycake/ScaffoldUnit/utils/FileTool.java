package org.crazycake.ScaffoldUnit.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class FileTool {
	Logger logger = Logger.getLogger(this.getClass());
	
	public static boolean isInUse(File f){
		return !f.renameTo(f);
	}
	
	public static String readFile(final String filename){
		StringBuffer sb = new StringBuffer();
		String m = "";
		BufferedReader file;
		try {
			file = new BufferedReader(new FileReader(filename));
			while ((m = file.readLine()) != null) {
				sb.append(m);
			
			}
			file.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	

	public static List<String> readLinedFile(String filename){
		List<String> filecon = new ArrayList<String>();
		String m = "";
		BufferedReader file = null;
		try{
			file = new BufferedReader(new FileReader(filename));
			while ((m = file.readLine()) != null) {
				if (!m.equals(""))
				{
					filecon.add(m);
				}

			}
			file.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return filecon;
	}
	
	public static List<String> readLinedFile(InputStream in){
        List<String> filecon = new ArrayList<String>();
        String m = "";
        BufferedReader file = null;
        try{
            file = new BufferedReader(new InputStreamReader(in));
            while ((m = file.readLine()) != null) {
                if (!m.equals(""))
                {
                    filecon.add(m);
                }

            }
            file.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        
        return filecon;
    }

	public static List<String> readLinedFile(String filename,String encode){
		List<String> filecon = new ArrayList<String>();
		String m = "";
		BufferedReader file = null;
		try{
			file = new BufferedReader(new InputStreamReader(new FileInputStream(filename),encode));
			while ((m = file.readLine()) != null) {
				if (!m.equals(""))
				{
					filecon.add(m);
				}

			}
			file.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return filecon;
	}

	public static void writeLinedFile(List lst, String filePath) throws IOException {
		File file = new File(filePath);
		BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
		for (int i = 0; i < lst.size(); i++) {
			String temp = (String) lst.get(i);
			if (!StringUtils.isBlank(temp)) {
				out.write(temp);
				out.newLine();
			}
		}

		out.close();
		out = null;
		file=null;

	}
	
	public static void writeFile(String content, String filePath){
		File file = new File(filePath);
		OutputStreamWriter out;
		try {
			out = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
			out.write(content);
			out.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out = null;
		file=null;

	}

	
	public static int getSize(String path) throws IOException{
		return getSize(new File(path));
	}
	
	public static int getSize(File file) throws IOException{
		FileInputStream fis = new FileInputStream(file);
		return fis.available();
	}
	

	public static int getImageWidth(File image) throws IOException{
		BufferedImage bi = ImageIO.read(image);
		return bi.getWidth();
	}

	
}
