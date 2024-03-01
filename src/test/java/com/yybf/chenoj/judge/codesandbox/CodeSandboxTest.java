package com.yybf.chenoj.judge.codesandbox;

import com.yybf.chenoj.judge.codesandbox.impl.ExampleCodeSandbox;
import com.yybf.chenoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yybf.chenoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.yybf.chenoj.model.enums.QuestionSubmitLanguageEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author yangyibufeng
 * @Description
 * @date 2024/2/6-19:38
 */
@SpringBootTest
class CodeSandboxTest {

    //$ 表示这个值动态传入 : 表示设置一个默认值
    // 这个值就是在配置文件中编写的用来给用户自定义的变量的值
    @Value("${codesandbox.type:example}")
    private String configType;

    /**
     * @return void:
     * @author yangyibufeng
     * @description 正常使用抽象接口创建实例
     * @date 2024/2/6 21:14
     */
    @Test
    void executeCode() {
        CodeSandbox codeSandbox = new ExampleCodeSandbox();
        String code = "int main() {}";
        String language = QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4", "114 514");
        // 使用builder建造者模式，不用get，set方法，直接通过.的方式将属性传入构造器中
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        // 判断对象是否为空
//        Assertions.assertNotNull(executeCodeResponse);
    }

    /**
     * @return void:
     * @author yangyibufeng
     * @description 将需要创建的代码沙箱类型通过配置文件指定来控制实例的生成
     * @date 2024/2/6 21:14
     */
    @Test
    void executeCodeByConfig() {
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(configType);
        String code = "int main() {}";
        String language = QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4");
        // 使用builder建造者模式，不用get，set方法，直接通过.的方式将属性传入构造器中
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        // 判断对象是否为空
//        Assertions.assertNotNull(executeCodeResponse);
    }

    /**
     * @return void:
     * @author yangyibufeng
     * @description 通过使用代理类来实现代码沙箱实例功能的增强 -- 即开启日志
     * @date 2024/2/6 21:17
     */
    @Test
    void executeCodeByConfigAndProxy() {
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(configType);
        // 创建一个代理类，将原本的实例传入，来实现功能的增强
        codeSandbox = new CodeSandboxProxy(codeSandbox);
//        String code = "int main() {}";
        String code = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        int a = Integer.parseInt(args[0]);\n" +
                "        int b = Integer.parseInt(args[1]);\n" +
                "        System.out.println(\"a+b的结果为：\" + (a + b));\n" +
                "    }\n" +
                "}";
        String language = QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4", "114 514");
        // 使用builder建造者模式，不用get，set方法，直接通过.的方式将属性传入构造器中
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        // 判断对象是否为空
//        Assertions.assertNotNull(executeCodeResponse);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String type = sc.next();
            CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
            String code = "int main() {}";
            String language = QuestionSubmitLanguageEnum.JAVA.getValue();
            List<String> inputList = Arrays.asList("1 2", "3 4");
            // 使用builder建造者模式，不用get，set方法，直接通过.的方式将属性传入构造器中
            ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                    .code(code)
                    .language(language)
                    .inputList(inputList)
                    .build();
            ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);

        }
    }

}