package so.sao.shop.gpssocket.interfaces;

import so.sao.shop.gpssocket.dto.MessageDto;

import java.util.Map;

/**
 * @author negocat on 2017/10/30.
 */
public interface iService {

    void doService(iBodyUtils ctx, MessageDto messageDto, Map<String, Object> g);
}
