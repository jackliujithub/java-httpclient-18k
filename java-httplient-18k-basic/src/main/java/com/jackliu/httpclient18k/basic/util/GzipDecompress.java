package com.jackliu.httpclient18k.basic.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

public class GzipDecompress {

	private static final int BUFFER = 1024;
	
	public static byte[] decompress(byte[] data) throws Exception {  
        ByteArrayInputStream bais = new ByteArrayInputStream(data);  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        // 解压缩  

        decompress(bais, baos);  

        data = baos.toByteArray();  

        baos.flush();  
        baos.close();  

        bais.close();  

        return data;  
    }  
	
	 /** 
     * 数据解压缩 
     *  
     * @param is 
     * @param os 
     * @throws Exception 
     */  
    private static void decompress(InputStream is, OutputStream os)  
            throws Exception {  
        GZIPInputStream gis = new GZIPInputStream(is);  
        int count;  
        byte data[] = new byte[BUFFER];  
        while ((count = gis.read(data, 0, BUFFER)) != -1) {  
            os.write(data, 0, count);  
        }  
        gis.close();  
    }  
}
