package so.sao.shop.gpssocket.Dto;

/**
 * @author negocat on 2017/10/27.
 */
public class MessageDto {

    private String body;
    private int protocol;

    public MessageDto(String body, int protocol) {
        this.body = body;
        this.protocol = protocol;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
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
        return "MessageDto{" +
                "body='" + body + '\'' +
                ", protocol=" + protocol +
                '}';
    }
}
