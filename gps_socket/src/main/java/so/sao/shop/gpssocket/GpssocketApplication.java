package so.sao.shop.gpssocket;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import so.sao.shop.gpssocket.utils.zipUtils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@SpringBootApplication
public class GpssocketApplication implements ApplicationContextAware {

    @Value("${socket.server.port}")
    private String port;

    public static ApplicationContext context = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        GpssocketApplication.context = applicationContext;
    }

	public static void main(String[] args) throws Exception {
//        SpringApplication springApplication = new SpringApplication(GpssocketApplication.class);
//        springApplication.setWebEnvironment(false);
//        springApplication.run(args);
//
//        NettyServer bean = context.getBean(NettyServer.class);
//        bean.run();


        String s = zipUtils.readZipFileToString("E:\\test.zip", "gradle/init.d/readme.txt");
        System.out.println(s);

    }



}
