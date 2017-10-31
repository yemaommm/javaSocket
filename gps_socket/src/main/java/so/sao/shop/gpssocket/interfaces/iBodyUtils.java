package so.sao.shop.gpssocket.interfaces;

import io.netty.buffer.ByteBuf;
import so.sao.shop.gpssocket.dto.MessageDto;

import java.io.UnsupportedEncodingException;

/**
 * @author negocat on 2017/10/30.
 */
public interface iBodyUtils {

    MessageDto readDecode(ByteBuf buf) throws UnsupportedEncodingException;
    void writeEncode(byte[] bytes, byte lprotocol);
}
