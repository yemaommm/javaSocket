package so.sao.shop.gpssocket;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import so.sao.shop.gpssocket.Dto.MessageDto;
import so.sao.shop.gpssocket.Utils.BodyUtils;
import so.sao.shop.gpssocket.Utils.CodeUtils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author negocat on 2017/10/27.
 */
@Component
@ConfigurationProperties(prefix = "socket.server")
public class NettyClient {
    private byte[] headByte;
    private byte[] endByte;

    public void setHeadByte(byte[] headByte) {
        this.headByte = headByte;
    }

    public void setEndByte(byte[] endByte) {
        this.endByte = endByte;
    }

    /**
     * 连接服务器
     * @param port
     * @param host
     * @throws Exception
     */
    public void connect(int port, String host) throws Exception {
        BodyUtils.setEndByte(this.endByte);
        BodyUtils.setHeadByte(this.headByte);
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
        public BodyUtils bodyUtils = new BodyUtils();

        public ClientHandler(){
            byte[] req ="发送QUERY TIME ORDER".getBytes();
            firstMessage= Unpooled.buffer(req.length);
            firstMessage.writeBytes(req);

        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            bodyUtils.setCtx(ctx);
            System.out.println("客户端active");

//            bodyUtils.writeEncode("hhhh".getBytes(), (byte)1);
//            byte[] b = {0x0A,0x03,0x17,0x0F,0x32,0x17, (byte) 0x9C,0x02,0x6B,0x3F,0x3E,0x0C,0x22, (byte) 0xAD,0x65,0x1F,0x34,0x60};
//            bodyUtils.writeEncode(b, (byte) 0x10);
//
//            bodyUtils.writeEncode("".getBytes(), (byte)0x08);

            byte[] bytes = CodeUtils.str2Bcd("787805");
            ByteBuf buffer = ctx.alloc().buffer();
            buffer.writeBytes(bytes);
            ctx.writeAndFlush(buffer);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(4000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    byte[] bytes1 = CodeUtils.str2Bcd("01686868680d0a787813100a03170f32179c026b3f3e0c22ad651f34600d0a787801080d0a");
                    ByteBuf buffer1 = ctx.alloc().buffer();
                    buffer1.writeBytes(bytes1);
                    ctx.writeAndFlush(buffer1);
                }
            }).run();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg)
                throws Exception {
            MessageDto messageDto = bodyUtils.readDecode((ByteBuf) msg);
            while (messageDto != null){
                System.out.println("收到服务器响应数据: " + messageDto);
                messageDto = bodyUtils.readDecode((ByteBuf) msg);
            }
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
        SpringApplication springApplication = new SpringApplication(GpssocketApplication.class);
        springApplication.setWebEnvironment(false);
        springApplication.run(args);

        NettyClient bean = GpssocketApplication.context.getBean(NettyClient.class);
        bean.connect(9999, "127.0.0.1");

//        byte[] b = {0x0A,0x03,0x17,0x0F,0x32,0x17, (byte) 0x9C,0x02,0x6B,0x3F,0x3E,0x0C,0x22, (byte) 0xAD,0x65,0x1F,0x34,0x60};
//
//        byte[] bytes = {0x02, 0x6B, 0x3F, 0x3E};
////        bytes = new byte[]{0x0C, 0x22, (byte) 0xAD, 0x65};
//        int val = CodeUtils.bytesToInt2(bytes, 0);
//
//        double dblVal = val / 30000.0;
//        int intVal = (int) (dblVal / 60);
//        System.out.println(intVal);
//        float fltVal = (float) (dblVal - intVal * 60);
//        System.out.println(fltVal);

//        String stmp = "00000000";
//        byte[] bytes = {0x00, 0x00, 0x34, 0x60};
//        String s = CodeUtils.bytesToBinaryString(bytes);
//        System.out.println(s);
//        System.out.println(ByteArrayUtil.toHexString(CodeUtils.binaryToBytes(s)));
//        byte[] bytes1 = Arrays.copyOfRange(bytes, 1, 2);
//        byte[] bytes2 = Arrays.copyOfRange(bytes, 2, 3);
//        System.out.println(ByteArrayUtil.toHexString(bytes1));
//        System.out.println(ByteArrayUtil.toHexString(bytes2));
//
//        String in = "0001100000";
//        System.out.println(new BigInteger(in, 2));
//        System.out.println(in.substring(2));
    }
}
