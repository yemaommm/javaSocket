package so.sao.shop.gpssocket.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import so.sao.shop.gpssocket.Dto.MessageDto;
import so.sao.shop.gpssocket.Interface.iBodyUtils;
import so.sao.shop.gpssocket.Interface.iService;
import so.sao.shop.gpssocket.Utils.CodeUtils;

/**
 * 登录
 * @author negocat on 2017/10/30.
 */
@Service("0x01")
public class Login implements iService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);

    @Override
    public void doService(iBodyUtils ctx, MessageDto messageDto) {
        LOGGER.info("body:" + CodeUtils.bcd2Str(messageDto.getBody().getBytes()));

        ctx.writeEncode(null, (byte) 0x01);//登录成功
        ctx.writeEncode(null, (byte) 0x44);//登录失败
    }
}
