package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIP {
    public static boolean compress(String iFilename, String oFilename){
        try {
            // prepare le gzip de sortie
            GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(oFilename));
            // ouvre le fichier d'entree
            FileInputStream in = new FileInputStream(iFilename);
            //transfere les bytes vers le gzip
            byte[] buf = new byte[1024];
            int offset;
            while ((offset = in.read(buf)) > 0) 
                out.write(buf, 0, offset);
            in.close();
        
            // On complete le fichier GZIP
            out.finish();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean uncompress(String iFilename, String oFilename){
        try {
            // Ouvre le fichier compresse
            GZIPInputStream in = new GZIPInputStream(new FileInputStream(iFilename));
            // ouvre le fichier de sortie
            OutputStream out = new FileOutputStream(oFilename);
            // transfere les byte compresse vers le fichier de sortie
            byte[] buf = new byte[1024];
            int offset;
            while ((offset = in.read(buf)) > 0)
                out.write(buf, 0, offset);
        
            // Ferme le fichier et le gzip
            in.close();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
