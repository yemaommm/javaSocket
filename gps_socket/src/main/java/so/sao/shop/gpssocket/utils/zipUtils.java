package so.sao.shop.gpssocket.utils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author negocat on 2017/11/10.
 */
public class zipUtils {

    /**
     * zip文件压缩
     * @param sourceFileName
     * @param zipFileName
     */
    public static void toZip(String zipFileName, String sourceFileName) throws Exception {
        //File zipFile = new File(zipFileName);
        System.out.println("压缩中...");

        //创建zip输出流
        ZipOutputStream out = new ZipOutputStream( new FileOutputStream(zipFileName));

        //创建缓冲输出流
        BufferedOutputStream bos = new BufferedOutputStream(out);

        File sourceFile = new File(sourceFileName);

        //调用函数
        compress(out,bos,sourceFile,sourceFile.getName());

        bos.close();
        out.close();
        System.out.println("压缩完成");
    }

    /**
     * zip文件压缩实现
     * @param out
     * @param bos
     * @param sourceFile
     * @param base
     * @throws Exception
     */
    public static void compress(ZipOutputStream out,BufferedOutputStream bos,File sourceFile,String base) throws Exception
    {
        //如果路径为目录（文件夹）
        if(sourceFile.isDirectory())
        {

            //取出文件夹中的文件（或子文件夹）
            File[] flist = sourceFile.listFiles();

            if(flist.length==0)//如果文件夹为空，则只需在目的地zip文件中写入一个目录进入点
            {
                System.out.println(base+"/");
                out.putNextEntry(  new ZipEntry(base+"/") );
            }
            else//如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
            {
                for(int i=0;i<flist.length;i++)
                {
                    compress(out,bos,flist[i],base+"/"+flist[i].getName());
                }
            }
        }
        else//如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
        {
            out.putNextEntry( new ZipEntry(base) );
            FileInputStream fos = new FileInputStream(sourceFile);
            BufferedInputStream bis = new BufferedInputStream(fos);

            int tag;
            int count = 0;
            System.out.println(base);
            //将源文件写入到zip文件中
            while((tag=bis.read())!=-1)
            {
                bos.write(tag);
                if (++count > 500000){
                    bos.flush();
                    count = 0;
                }
            }
            bos.flush();
            bis.close();
            fos.close();

        }
    }

    public static String readZipFileToString(String zipFileName, String fileName) throws IOException {
        ZipFile zf = new ZipFile(zipFileName);

        ZipInputStream om = new ZipInputStream(new FileInputStream(zipFileName));
        ZipEntry nextEntry;
        while ((nextEntry = om.getNextEntry()) != null){
            if (fileName.equals(nextEntry.getName())){
                StringBuffer sb = new StringBuffer();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(zf.getInputStream(nextEntry)));
                String str;
                while ((str = br.readLine()) != null){
                    sb.append(str);
                }
                br.close();
                return sb.toString();
            }

        }
        return "";
    }

}
