package so.sao.shop.gpssocket.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import so.sao.shop.gpssocket.dto.MessageDto;
import so.sao.shop.gpssocket.interfaces.iBodyUtils;

import java.io.UnsupportedEncodingException;

/**
 * @author negocat on 2017/10/27.
 */
public class BodyUtils implements iBodyUtils {

    /**
     * 魔数
     */
    private static String headByte;

    /**
     * 结尾
     */
    private static String endByte;

    /**
     * 协议
     */
    private int protocol;

    private byte[] ret = new byte[0];

    private ByteBuf ephemeralData = Unpooled.buffer(102400);
    private int bodylength = -1;
    private ChannelHandlerContext ctx;

    @Override
    public MessageDto readDecode(ByteBuf buf) throws UnsupportedEncodingException {
        //加入临时缓存中
        if (buf.readableBytes() > 0){
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);

            ephemeralData.writeBytes(bytes);
        }

        MessageDto messageDto = dataSlice();

        //临时缓存全部处理完毕之后，清理缓存
        if (ephemeralData.readableBytes() <= 0){
            ephemeralData.release();
            ephemeralData = ephemeralData.alloc().buffer();
        }

        return messageDto;
    }

    public MessageDto dataSlice(){
        //当bodylength小于0，并且ephemeralData长度大于6的时候，做协议解析
        if (bodylength < 0){
            int len = headByte.length();
            //数据不足六位，不做处理
            if (ephemeralData.readableBytes() < 4+len){return null;}
            //起始2byte魔数验证
            byte[] str = new byte[len];
            ephemeralData.readBytes(str);
            if (!new String(str).equals(headByte)){ctx.close();}
            //获取1byte数据长度
            bodylength = ephemeralData.readByte()-1;
            //获取1byte协议
            protocol = ephemeralData.readByte();

//            ephemeralData.readBytes(len+2);
        }
        //当bodylength大于0并且临时数据ephemeralData大于数据长度bodylength的时候，获取数据内容
        if (bodylength > 0 && bodylength <= ephemeralData.readableBytes()){
            //根据数据长度获取数据内容
            ret = new byte[bodylength];
            ephemeralData.readBytes(ret);
            bodylength = 0;
        }
        //当bodylength等于0，已全部获取完数据的时候，且临时数据长度大于等于2的时候，解析尾部协议
        if (bodylength == 0 && ephemeralData.readableBytes() >= endByte.length()){
            byte[] str = new byte[2];
            ephemeralData.readBytes(str);
            if (!new String(str).equals(endByte)){
                return null;
            }
            bodylength = -1;
            MessageDto messageDto = new MessageDto(ret, protocol);
            ret = new byte[0];
            return messageDto;
        }

        return null;
    }

    @Override
    public void writeEncode(byte[] bytes, byte lprotocol){
        if (bytes == null){
            bytes = "".getBytes();
        }
        ByteBuf buff = ctx.alloc().buffer();
        buff.writeBytes(headByte.getBytes());//写入头
        buff.writeByte(bytes.length+1);//写入数据长度
        buff.writeByte(lprotocol);//写入协议
        buff.writeBytes(bytes);//写入内容
        buff.writeBytes(endByte.getBytes());//写入结尾
        ctx.writeAndFlush(buff);
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public static String getHeadByte() {
        return headByte;
    }

    public static void setHeadByte(byte[] headByte) {
        BodyUtils.headByte = new String(headByte);
    }

    public static String getEndByte() {
        return endByte;
    }

    public static void setEndByte(byte[] endByte) {
        BodyUtils.endByte = new String(endByte);
    }
}
