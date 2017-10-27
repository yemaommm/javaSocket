package so.sao.shop.gpssocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * server端网络IO事件处理
 * @author negocat on 2017/10/27.
 */
public class NettyHandler extends ChannelHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyHandler.class);

    private ApplicationContext context = null;

    /**
     * 接受数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf in = (ByteBuf) msg;
        try {
            System.out.println("服务器读取到客户端请求...");
            ByteBuf buf=(ByteBuf) msg;
            byte[] req=new byte[buf.readableBytes()];
            buf.readBytes(req);
            String body=new String(req,"UTF-8");
            System.out.println("body: "+body);

        } finally {
            // 以静默方式丢弃接收的数据
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        LOGGER.debug("服务器readComplete 响应完成");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 出现异常时关闭连接。
        ctx.close();
        LOGGER.error("服务器异常退出"+cause.getMessage());
    }

    public NettyHandler(ApplicationContext context){
        this.context = context;
    }

}
