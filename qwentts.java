// 请安装 DashScope SDK 的最新版本
import com.alibaba.dashscope.aigc.multimodalconversation.AudioParameters;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.alibaba.dashscope.utils.Constants;

public class Main {
    // 如需使用指令控制功能，请将MODEL替换为qwen3-tts-instruct-flash
    private static final String MODEL = "qwen3-tts-flash";
    public static void call() throws ApiException, NoApiKeyException, UploadFileException {
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .model(MODEL)
                // 新加坡和北京地域的API Key不同。获取API Key：https://help.aliyun.com/zh/model-studio/get-api-key
                // 若没有配置环境变量，请用阿里云百炼API Key将下行替换为：apiKey("sk-xxx")
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .text("Today is a wonderful day to build something people love!")
                .voice(AudioParameters.Voice.CHERRY)
                .languageType("English")
                // 如需使用指令控制功能，请取消下方注释，并将model替换为qwen3-tts-instruct-flash
                // .parameter("instructions","语速较快，带有明显的上扬语调，适合介绍时尚产品。")
                // .parameter("optimize_instructions",true)
                .build();
        MultiModalConversationResult result = conv.call(param);
        System.out.println(JsonUtils.toJson(result));
    }
    public static void main(String[] args) {
        // 以下为华北2（北京）地域的URL，调用时请将WorkspaceId替换为真实的业务空间ID，各地域的URL不同。
        Constants.baseHttpApiUrl = "https://{WorkspaceId}.cn-beijing.maas.aliyuncs.com/api/v1";
        try {
            call();
        } catch (ApiException | NoApiKeyException | UploadFileException e) {
            System.out.println(e.getMessage());
        }
        System.exit(0);
    }
}