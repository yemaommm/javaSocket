package so.sao.shop.gpssocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import so.sao.shop.gpssocket.utils.BodyUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author negocat on 2017/10/27.
 */
@Component
@ConfigurationProperties(prefix = "socket.server")
public class NettyServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    private int port;

    private long readIdelTimeOut;
    private long writeIdelTimeOut;
    private long allIdelTimeOut;

    private byte[] headByte;
    private byte[] endByte;

    @Autowired
    private ApplicationContext context;

    public void run() throws Exception {
        BodyUtils.setEndByte(this.endByte);
        BodyUtils.setHeadByte(this.headByte);
        // 服务器线程组 用于网络事件的处理 一个用于服务器接收客户端的连接
        // 另一个线程组用于处理SocketChannel的网络读写
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // NIO服务器端的辅助启动类 降低服务器开发难度
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)// 类似NIO中serverSocketChannel
                    .option(ChannelOption.SO_BACKLOG, 1024)// 配置TCP参数
                    .childHandler(new ChildChannelHandler());// 最后绑定I/O事件的处理类

            // 处理网络IO事件
            // 服务器启动后 绑定监听端口 同步等待成功 主要用于异步操作的通知回调 回调处理用的ChildChannelHandler
            ChannelFuture f = serverBootstrap.bind(port).sync();
            LOGGER.info("SocketServer启动");

            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 退出 释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            LOGGER.info("服务器释放了线程资源...");
        }
    }

    public void setEndByte(byte[] endByte) {
        this.endByte = endByte;
    }

    public void setHeadByte(byte[] headByte) {
        this.headByte = headByte;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setReadIdelTimeOut(long readIdelTimeOut) {
        this.readIdelTimeOut = readIdelTimeOut;
    }

    public void setWriteIdelTimeOut(long writeIdelTimeOut) {
        this.writeIdelTimeOut = writeIdelTimeOut;
    }

    public void setAllIdelTimeOut(long allIdelTimeOut) {
        this.allIdelTimeOut = allIdelTimeOut;
    }

    /**
     * 网络事件处理器
     */
    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("idleStateHandler", new IdleStateHandler(readIdelTimeOut
                    , writeIdelTimeOut
                    , allIdelTimeOut
                    , TimeUnit.SECONDS));
            pipeline.addLast(new NettyHandler(context));
        }
    }
}
