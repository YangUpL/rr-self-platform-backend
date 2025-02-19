package com.yangrr.rrmianshi;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yangrr.rrmianshi.domain.Question;
import com.yangrr.rrmianshi.domain.Users;
import com.yangrr.rrmianshi.enums.UserRoleEnum;
import com.yangrr.rrmianshi.excel.QuestionInfo;
import com.yangrr.rrmianshi.service.QuestionService;
import com.yangrr.rrmianshi.service.UsersService;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.Cipher;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@SpringBootTest
class RrMianshiApplicationTests {

    @Autowired
    private UsersService usersService;

    @Autowired
    private QuestionService questionService;

    @Test
    void testService() {
        Users users = new Users();
        users.setUsername("yangrr");
        users.setPassword("123456");
        users.setEmail("yangrr@163.com");
        usersService.save(users);
    }


    @Test
    void testEnums() {
        ////System.out.println(UserRoleEnum.ADMIN.getRole());
    }

    //测试爬取网页数据
    @Test
    void testCrawl() {
        OkHttpClient client = new OkHttpClient();

        // 定义请求体的 JSON 数据
        String json = "{\"current\": 1, \"pageSize\": 200, \"questionBankCategoryId\": \"1821883312558432257\"}";

        // 创建 MediaType 对象，指定请求体的类型为 JSON
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        // 创建 RequestBody 对象，将 JSON 数据封装进去
        RequestBody body = RequestBody.create(json, JSON);

        // 创建 Request 对象，设置请求方法为 POST 并添加请求体
        Request request = new Request.Builder()
                .url("https://api.mianshiya.com/api/questionBankCategory/list_questionBank")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseData = response.body().string();
//                //System.out.println("responseData = " + responseData);

                // 使用 Gson 解析 JSON 数据
                JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();

                // 检查是否存在 data 字段
                if (jsonObject.has("data")) {
                    JsonObject dataObject = jsonObject.getAsJsonObject("data");

                    // 检查 data 字段下是否存在 title 字段
                    if (dataObject.has("records")) {
                        //records 是一个数组，需要遍历
                        JsonArray recordsArray = dataObject.getAsJsonArray("records");
                        for (JsonElement recordElement : recordsArray) {
                            JsonObject recordObject = recordElement.getAsJsonObject();
                            if (recordObject.has("title")) {
                                String title = recordObject.get("title").getAsString();
                                //System.out.println(title);
                            } else {
                                //System.out.println("响应数据中 data 字段下不存在 title 字段。");
                            }

                            //description
                            if (recordObject.has("description")) {
                                String description = recordObject.get("description").getAsString();
                                //System.out.println(description);
                            }
                        }
                    } else {
                        //System.out.println("响应数据中不存在 records 字段。");
                    }
                } else {
                    //System.out.println("响应数据中不存在 data 字段。");
                }
            } else {
                //System.out.println("请求失败，响应码: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取题目到excel
    @Test
    void testCrawl2() {
        OkHttpClient client = new OkHttpClient();
        // 请求体 JSON 数据
        String requestBodyJson = "{\"current\": 1, \"pageSize\": 59, \"questionBankId\": \"1787463103423897602\"}";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(requestBodyJson, JSON);
        // 创建请求
        Request request = new Request.Builder()
                .url("https://api.mianshiya.com/api/question_bank/list_question")
                .post(body)
                .build();
        List<QuestionInfo> questionInfoList = new ArrayList<>();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseData = response.body().string();
                // 解析 JSON 数据
                JSONObject jsonObject = new JSONObject(responseData);
                if (jsonObject.has("data")) {
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    if (dataObject.has("records")) {
                        JSONArray recordsArray = dataObject.getJSONArray("records");
                        // 遍历 records 数组
                        for (int i = 0; i < recordsArray.length(); i++) {
                            JSONObject recordObject = recordsArray.getJSONObject(i);
                            // 提取所需字段
                            String title = recordObject.getString("title");
                            int difficulty = recordObject.getInt("difficulty");
                            int needVip = recordObject.getInt("needVip");
                            String tagList = recordObject.optString("tagList");
                            QuestionInfo questionInfo = new QuestionInfo(title, difficulty, needVip, tagList);
                            questionInfoList.add(questionInfo);
                        }
                    }
                }
            } else {
                //System.out.println("请求失败，响应码: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        // 使用 EasyExcel 将数据写入 Excel 文件
        String fileName = "question_info.xlsx";
        EasyExcel.write(fileName, QuestionInfo.class).sheet("Question Info").doWrite(questionInfoList);
    }


    //读取excel题目构建sql插入数据库
    @Test
    void testCrawl4() {
        String fileName = "question_info.xlsx";
        // 读取 Excel 文件
        EasyExcel.read(fileName, QuestionInfo.class, new QuestionInfoListener(questionService)).sheet().doRead();
    }

    // 自定义监听器
    public static class QuestionInfoListener extends AnalysisEventListener<QuestionInfo> {
        private static final int BATCH_COUNT = 100;
        private List<QuestionInfo> list = new ArrayList<>(BATCH_COUNT);
        private QuestionService questionService;

        public QuestionInfoListener(QuestionService questionService) {
            this.questionService = questionService;
        }

        @Override
        public void invoke(QuestionInfo questionInfo, AnalysisContext analysisContext) {
            list.add(questionInfo);
            if (list.size() >= BATCH_COUNT) {
                saveData();
                list.clear();
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {
            saveData();
        }

        private void saveData() {
            List<Question> questionList = QuestionConverter.convertToQuestionList(list);
            questionService.saveBatch(questionList);
        }
    }

    public class QuestionConverter {
        public static List<Question> convertToQuestionList(List<QuestionInfo> questionInfoList) {
            List<Question> questionList = new ArrayList<>();
            for (QuestionInfo questionInfo : questionInfoList) {
                Question question = new Question();
                // 假设 Question 和 QuestionInfo 有相同字段，进行属性复制
                question.setQuestion(questionInfo.getTitle());
                question.setDifficulty(questionInfo.getDifficulty());
                question.setNeedVip(questionInfo.getNeedVip());
                question.setTaglist(questionInfo.getTagList());
                questionList.add(question);
            }
            return questionList;
        }
    }

    //获取答案
    @Test
    void testCrawl3() {
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.mianshiya.com/api/question/safe/get/vo?body=UXZIOTNNdUtidDN5eUliSlllcVM2YWdUUmg1T2MzS0RUVTlOelpsUm1oMWlmZ2F5WTF1enZ2cEdVQTBhdVJOUmtBMUhqMmtJaFZTY3Z4bjlaZVp4RGNhRnd2d1VGL2JOL2pXa01xalZ1MlFoeXN3V2kyVHZxZWRRRG1WVEE3UjJOdk1PSm9uRnd6RmZyOVpacERHNjhBLytUeVg3Ukw0VmVROFNKbjFLenJDYm9rR0FhdFp4UHdwUklIdWRYY3FhUUFUTzJjSFNLRWxmdjUvUVBjV0lHUUMwUXJCMkJpVUF5NER6Mnl2UzJUZzJydFhZZjh3Yk5GYThqdGlSeE1sbXhTY2VoU2VMQkR4WHVCQlQzTWQyc3dlQVZHMVg3ZEdCcU1WOTFvMUR5U01pOE1SMVZvZzlmZ0x4R1ZkTmJsSWZjd0NPRzM0akpHTVFqMG96TDZTZHFBPT0";

        // 创建请求
        Request request = new Request.Builder()
                .url(url)
                //指定post请求
                .post(RequestBody.create("", MediaType.parse("application/json")))
                //设置请求头(登陆后获取)
                .addHeader("Cookie", "SESSION=MGQ2N2E5MTUtNTQ0OS00ZjNiLWE5MmEtOTM3OGNhNjE4MmJm; Hm_lvt_8abb85f1b5cfd5f406cdcc6454141898=1738548878; HMACCOUNT=019AE5541A024920; Hm_lvt_c7cedf2eca8990b32ef9f1a0412e7102=1738548878; Hm_lpvt_8abb85f1b5cfd5f406cdcc6454141898=1738554276; Hm_lpvt_c7cedf2eca8990b32ef9f1a0412e7102=1738554276")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseData = response.body().string();
                // 解析 JSON 响应
                JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();
                if (jsonObject.has("data")) {
                    JsonObject dataObject = jsonObject.getAsJsonObject("data");
                    if (dataObject.has("bestQuestionAnswer")) {
                        JsonObject bestQuestionAnswerObject = dataObject.getAsJsonObject("bestQuestionAnswer");
                        if (bestQuestionAnswerObject.has("content")) {
                            String content = bestQuestionAnswerObject.get("content").getAsString();
                            //System.out.println("data.bestQuestionAnswer.content: " + content);
                        } else {
                            //System.out.println("响应中不存在 bestQuestionAnswer.content 字段");
                        }
                    } else {
                        //System.out.println("响应中不存在 bestQuestionAnswer 字段");
                    }
                } else {
                    //System.out.println("响应中不存在 data 字段");
                }
            } else {
                //System.out.println("请求失败，响应码: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testRSA() throws Exception {

        // 生成密钥对
        KeyPair keyPair = generateKeyPair();

        // 获取公钥和私钥
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        //System.out.println("publicKey = " + publicKey);
        //System.out.println("privateKey = " + privateKey);

        // 将公钥和私钥编码为Base64字符串
        String publicKeyBase64 = encodeBase64(publicKey.getEncoded());
        String privateKeyBase64 = encodeBase64(privateKey.getEncoded());

        //System.out.println("公钥: " + publicKeyBase64);
        //System.out.println("私钥: " + privateKeyBase64);

        //将公钥和私钥   的Base64编码保存到 类路径resources文件夹中
        Files.write(Paths.get("publicKey.txt"), publicKeyBase64.getBytes());
        Files.write(Paths.get("privateKey.txt"), privateKeyBase64.getBytes());


        PublicKey publicKey1 = decodePublicKey(publicKeyBase64);
        PrivateKey privateKey1 = decodePrivateKey(privateKeyBase64);
        //System.out.println("publicKey1 = " + publicKey1);
        //System.out.println("privateKey1 = " + privateKey1);

        // 示例：使用公钥加密，私钥解密
        String originalMessage = "Hello, World!";
        String encryptedMessage = encrypt(originalMessage, publicKey);
        String decryptedMessage = decrypt(encryptedMessage, privateKey);

        //System.out.println("原始消息: " + originalMessage);
        //System.out.println("加密后的消息: " + encryptedMessage);
        //System.out.println("解密后的消息: " + decryptedMessage);
    }

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    public static String encodeBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String encrypt(String message, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return encodeBase64(encryptedBytes);
    }

    public static String decrypt(String encryptedMessage, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedMessage);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    public static PublicKey decodePublicKey(String base64PublicKey) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey decodePrivateKey(String base64PrivateKey) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }


}
