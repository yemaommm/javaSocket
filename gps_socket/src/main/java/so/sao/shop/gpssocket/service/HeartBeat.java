package so.sao.shop.gpssocket.service;

import org.springframework.stereotype.Service;
import so.sao.shop.gpssocket.dto.MessageDto;
import so.sao.shop.gpssocket.interfaces.iBodyUtils;
import so.sao.shop.gpssocket.interfaces.iService;

import java.util.Map;

/**
 * 心跳包
 * @author negocat on 2017/10/30.
 */
@Service("0x08")
public class HeartBeat implements iService {
    @Override
    public void doService(iBodyUtils ctx, MessageDto messageDto, Map<String, Object> g) {

    }
}
