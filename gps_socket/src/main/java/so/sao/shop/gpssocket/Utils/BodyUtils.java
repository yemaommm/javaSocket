package so.sao.shop.gpssocket.Utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import so.sao.shop.gpssocket.Dto.MessageDto;
import so.sao.shop.gpssocket.Interface.iBodyUtils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

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

    private StringBuffer ephemeralData = new StringBuffer();
    private int bodylength = -1;
    private ChannelHandlerContext ctx;

    @Override
    public MessageDto readDecode(ByteBuf buf) throws UnsupportedEncodingException {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        //加入临时缓存中
        ephemeralData.append(new String(bytes));
        MessageDto messageDto = dataSlice();

        return messageDto;
    }

    public MessageDto dataSlice(){
        String ret = "";
        //当bodylength小于0，并且ephemeralData长度大于6的时候，做协议解析
        if (bodylength < 0){
            int len = headByte.length();
            //数据不足六位，不做处理
            if (ephemeralData.length() < 4+len){return null;}
            //起始2byte魔数验证
            String str = ephemeralData.substring(0, len);
            if (!str.equals(headByte)){ctx.close();}
            //获取1byte数据长度
            bodylength = ephemeralData.charAt(len)-1;
            //获取1byte协议
            protocol = ephemeralData.charAt(len+1);

            ephemeralData.delete(0, len+2);
        }
        //当bodylength大于0并且临时数据ephemeralData大于数据长度bodylength的时候，获取数据内容
        if (bodylength > 0 && bodylength <= ephemeralData.length()){
            //根据数据长度获取数据内容
            ret = ephemeralData.substring(0, bodylength);
            ephemeralData.delete(0, bodylength);
            bodylength = 0;
        }
        //当bodylength等于0，已全部获取完数据的时候，且临时数据长度大于等于2的时候，解析尾部协议
        if (bodylength == 0 && ephemeralData.length() >= endByte.length()){
            String str = ephemeralData.substring(0, endByte.length());
            ephemeralData.delete(0, endByte.length());
            if (!str.equals(endByte)){
                return null;
            }
            bodylength = -1;
            return new MessageDto(ret, protocol);
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
