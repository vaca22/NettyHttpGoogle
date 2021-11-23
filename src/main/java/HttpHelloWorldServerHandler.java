import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.alibaba.fastjson.serializer.JSONSerializer;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import kotlin.text.Charsets;
import org.slf4j.Logger;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author james
 */
public class HttpHelloWorldServerHandler extends SimpleChannelInboundHandler<HttpObject> {




    private HttpHeaders headers;
    private HttpRequest request;
    private FullHttpRequest fullRequest;

    private static final String FAVICON_ICO = "/favicon.ico";
    private static final AsciiString CONTENT_TYPE = AsciiString.cached("Content-Type");
    private static final AsciiString CONTENT_LENGTH = AsciiString.cached("Content-Length");
    private static final AsciiString CONNECTION = AsciiString.cached("Connection");
    private static final AsciiString KEEP_ALIVE = AsciiString.cached("keep-alive");


    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {


        if (msg instanceof HttpRequest){
            request = (HttpRequest) msg;
            headers = request.headers();
            String uri = request.uri();

            if (uri.equals(FAVICON_ICO)){
                return;
            }
            HttpMethod method = request.method();
            if (method.equals(HttpMethod.GET)){


            }else if (method.equals(HttpMethod.POST)){
                //POST请求,由于你需要从消息体中获取数据,因此有必要把msg转换成FullHttpRequest
                fullRequest = (FullHttpRequest)msg;
                //根据不同的Content_Type处理body数据
                dealWithContentType();


            }



            byte[] content = "fuck".getBytes();


            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(content));
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

            boolean keepAlive = HttpUtil.isKeepAlive(request);
            if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, KEEP_ALIVE);
                ctx.write(response);
            }
        }
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private void dealWithContentType() throws Exception{
        String contentType = getContentType();

        if(contentType.equals("application/json")){
            String jsonStr = fullRequest.content().toString();
            JSONObject obj = JSON.parseObject(jsonStr);
            for(Entry<String, Object> item : obj.entrySet()){

            }

        }else if(contentType.equals("application/x-www-form-urlencoded")){

			String jsonStr = fullRequest.content().toString();
			QueryStringDecoder queryDecoder = new QueryStringDecoder(jsonStr, false);
			Map<String, List<String>> uriAttributes = queryDecoder.parameters();
            for (Entry<String, List<String>> attr : uriAttributes.entrySet()) {
                for (String attrVal : attr.getValue()) {

                }
            }

        }else if(contentType.equals("multipart/form-data")){

        }else{

        }
    }
    private String getContentType(){
        String typeStr = headers.get("Content-Type").toString();
        String[] list = typeStr.split(";");
        return list[0];
    }

}
