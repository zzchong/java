import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * idea 插件 http://www.jetbrains.org/intellij/sdk/docs/basics/types_of_plugins.html
 * 自动生成service serviceImpl文件
 * @author wb-zzc422008
 * 
 */
public class AutoCreateAction extends AnAction {


    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        System.out.println("start auto-create");
        try {
            generateServiceInterfaceFile("d:/code/auto_create/","com.zzc.www.model.User");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("end auto-create");
    }

    public  Configuration getConfiguration(){
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
        configuration.setClassForTemplateLoading(AutoCreateAction.class,"/templates");
        configuration.setDefaultEncoding("UTF-8");
        return configuration;
    }

    private   void generateServiceInterfaceFile(String  rootPath,String model) throws Exception{
        final String basePackage = "src/main/java/";

        final String service = "service";
        String packageName =  resolveModel2Package(model);
        String importPackageName =  resolveModel2ImportPackageName(model);
        String modelName = resolveModel2Name(model);

        /* interface */
        String servicePackagePath = rootPath+basePackage+packageName+service;
        System.out.println(servicePackagePath);
        File servicePackage = new File(servicePackagePath);
        if(!servicePackage.exists()){
            servicePackage.mkdirs();
        }
        final String serviceTemplate = "service.ftl";
        File serviceFile = new File(servicePackagePath+"/"+modelName+"Service.java");
        Map<String,Object> dataMap = new HashMap<>(16);
        dataMap.put("package_name",importPackageName);
        dataMap.put("service_annotation",modelName);
        dataMap.put("service_name",modelName);
        dataMap.put("date",new Date());
        dataMap.put("author",System.getProperty("user.name"));
        generateFileByTemplate(serviceTemplate,serviceFile,dataMap);

        /* impl */
        String serviceImplPackagePath = servicePackagePath+"/impl";
        System.out.println(serviceImplPackagePath);
        File serviceImplPackage = new File(serviceImplPackagePath);
        if(!serviceImplPackage.exists()){
            serviceImplPackage.mkdirs();
        }

        final String serviceImplTemplate = "serviceImpl.ftl";
        File serviceImplFile = new File(serviceImplPackagePath+"/"+modelName+"ServiceImpl.java");
        generateFileByTemplate(serviceImplTemplate,serviceImplFile,dataMap);
    }


    private  void generateFileByTemplate(final String templateName,File file,Map<String,Object> dataMap) throws Exception{
        Template template = getTemplate(templateName);
        FileOutputStream fos = new FileOutputStream(file);
        Writer out = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"),10240);
        template.process(dataMap,out);
    }
    private   Template getTemplate(String templateName) throws IOException {
        try {
            return getConfiguration().getTemplate(templateName);
        } catch (IOException e) {
            throw e;
        }
    }



    private  String resolveModel2Package(String model){
        return (resolveModel2ImportPackageName(model)).replace(".","/")+"/";
    }
    private  String resolveModel2ImportPackageName(String model){
        return model.substring(0,model.indexOf(".model"));
    }
    private  String resolveModel2Name(String model){
        return (model.substring(model.lastIndexOf(".")+1));
    }

}
