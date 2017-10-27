package so.sao.shop.gpssocket;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

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
        SpringApplication springApplication = new SpringApplication(GpssocketApplication.class);
        springApplication.setWebEnvironment(false);
        springApplication.run(args);

        NettyServer bean = context.getBean(NettyServer.class);
        bean.run();
    }

}
