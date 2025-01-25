import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import proto.MessagePack;

/**
 * @author: Elon
 * @title: MessageEncoder
 * @projectName: im-system
 * @description: 消息编码类,私有协议规则,前4位表示长度，接着command 四位 后面是数据
 * @date: 2025/1/24 21:18
 */
public class MessageEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object object, ByteBuf byteBuf) throws Exception {
        if(object instanceof MessagePack){
            MessagePack msgBody = (MessagePack) object;
            String s = JSONObject.toJSONString(msgBody.getData());
            byte[] bytes = s.getBytes();
            byteBuf.writeInt(msgBody.getCommand());
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        }
    }
}
