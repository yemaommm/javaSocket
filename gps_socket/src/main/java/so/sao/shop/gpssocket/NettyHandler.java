package so.sao.shop.gpssocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import so.sao.shop.gpssocket.dto.MessageDto;
import so.sao.shop.gpssocket.interfaces.iService;
import so.sao.shop.gpssocket.utils.BodyUtils;

/**
 * server端网络IO事件处理
 * @author negocat on 2017/10/27.
 */
public class NettyHandler extends ChannelHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyHandler.class);

    public BodyUtils bodyUtils = new BodyUtils();
    private ApplicationContext context = null;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        bodyUtils.setCtx(ctx);
    }
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
            LOGGER.info("ReadMassage: " + ByteBufUtil.hexDump((ByteBuf) msg));

            MessageDto messageDto = bodyUtils.readDecode((ByteBuf) msg);
            while (messageDto != null){
                LOGGER.info("AnalyzeReadMassage: " + messageDto);
                String format = String.format("0x%02x", messageDto.getProtocol());

                if (this.context.containsBean(format)){
                    iService bean = (iService) this.context.getBean(format);
                    bean.doService(bodyUtils, messageDto);
                }

                messageDto = bodyUtils.readDecode((ByteBuf) msg);
            }

        }catch (Exception e){
            LOGGER.error("GPSERROR: ", e);
            e.printStackTrace();
        }finally {
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
        cause.printStackTrace();
        LOGGER.error("服务器异常退出"+cause.getMessage());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                ctx.close();
                LOGGER.info("READER_IDLE 读超时");
            } else if (e.state() == IdleState.WRITER_IDLE) {
///                ByteBuf buff = ctx.alloc().buffer();
///                buff.writeBytes("mayi test".getBytes());
///                ctx.writeAndFlush(buff);
///                LOGGER.info("WRITER_IDLE 写超时");
            }
        }
    }

    public NettyHandler(ApplicationContext context){
        this.context = context;
    }

}
