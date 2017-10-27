package so.sao.shop.gpssocket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author negocat on 2017/10/27.
 */
public class NettyClient {

    /**
     * 连接服务器
     * @param port
     * @param host
     * @throws Exception
     */
    public void connect(int port, String host) throws Exception {
        //配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            //客户端辅助启动类 对客户端配置
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });
            //异步链接服务器 同步等待链接成功
            ChannelFuture f = b.connect(host, port).sync();
            //等待链接关闭
            f.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully();
            System.out.println("客户端优雅的释放了线程资源...");
        }
    }

    /**

     * Client 网络IO事件处理

     * @author xwalker

     *

     */

    public class ClientHandler extends ChannelHandlerAdapter {

        private final Logger logger= LoggerFactory.getLogger(ClientHandler.class.getName());
        private ByteBuf firstMessage;

        public ClientHandler(){
            byte[] req ="QUERY TIME ORDER".getBytes();
            firstMessage= Unpooled.buffer(req.length);
            firstMessage.writeBytes(req);

        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(firstMessage);
            System.out.println("客户端active");

        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg)
                throws Exception {
            System.out.println("客户端收到服务器响应数据");
            ByteBuf buf=(ByteBuf) msg;
            byte[] req=new byte[buf.readableBytes()];
            buf.readBytes(req);
            String body=new String(req,"UTF-8");
            System.out.println("Now is:"+body);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
            System.out.println("客户端收到服务器响应数据处理完成");
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            logger.info("Unexpected exception from downstream:"+cause.getMessage());
            ctx.close();
            System.out.println("客户端异常退出");
        }
    }


    public static void main(String[] args) throws Exception {
        new NettyClient().connect(9999, "127.0.0.1");
    }
}