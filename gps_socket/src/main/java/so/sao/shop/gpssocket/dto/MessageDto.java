package so.sao.shop.gpssocket.dto;

import so.sao.shop.gpssocket.utils.CodeUtils;

/**
 * @author negocat on 2017/10/27.
 */
public class MessageDto {

    private byte[] body;
    private int protocol;

    public MessageDto(byte[] body, int protocol) {
        this.body = body;
        this.protocol = protocol;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    @Override
    public String toString() {
        if (body == null || body.length <= 0){
            return "MessageDto{" +
                    "body=''" +
                    ", protocol=" + protocol +
                    '}';
        }
        return "MessageDto{" +
                "body='" + CodeUtils.bytesToHexString(body) + '\'' +
                ", protocol=" + protocol +
                '}';
    }
}
