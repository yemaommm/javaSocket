package so.sao.shop.gpssocket.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import so.sao.shop.gpssocket.dto.MessageDto;
import so.sao.shop.gpssocket.interfaces.iBodyUtils;
import so.sao.shop.gpssocket.interfaces.iService;
import so.sao.shop.gpssocket.utils.CodeUtils;

import java.util.Map;

/**
 * 登录
 * @author negocat on 2017/10/30.
 */
@Service("0x01")
public class Login implements iService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);

    @Override
    public void doService(iBodyUtils ctx, MessageDto messageDto, Map<String, Object> g) {
        LOGGER.info("body:" + CodeUtils.bcd2Str(messageDto.getBody()));

        g.put("IMEI", CodeUtils.bcd2Str(messageDto.getBody()));

        ctx.writeEncode(null, (byte) 0x01);//登录成功
        ctx.writeEncode(null, (byte) 0x44);//登录失败
    }
}
