package com.zzc.www.util;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class FreemarkerUtil {

    private static final String BASE_PACKAGE = "src/main/java/";

    private static final String USER_DIR = getRootPath();

    private static final String PACKAGE_SERVICE = "service";

    private static final String PACKAGE_IMPL = "impl";

    private static class ConfigurationHolder{

        private static Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
        static {
            configuration.setClassForTemplateLoading(FreemarkerUtil.class,"/templates");
            configuration.setDefaultEncoding("UTF-8");
        }
    }


    public static Configuration getConfiguration(){
        return ConfigurationHolder.configuration;
    }


    public static void generateServiceInterfaceFile(String model) throws Exception{
        String packageName =  resolveModel2Package(model);
        String importPackageName =  resolveModel2ImportPackageName(model);
        String modelName = resolveModel2Name(model);

        /* interface */
        String servicePackagePath = USER_DIR+BASE_PACKAGE+packageName+PACKAGE_SERVICE;
        System.out.println(servicePackagePath);
        File servicePackage = new File(servicePackagePath);
        if(!servicePackage.exists()){
            servicePackage.mkdirs();
        }
        final String serviceTemplate = "service.ftl";
        File serviceFile = new File(servicePackagePath+"/"+modelName+"Service.java");
        Map<String,Object> dataMap = new HashMap<String, Object>(16);
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



    private static void generateFileByTemplate(final String templateName,File file,Map<String,Object> dataMap) throws Exception{
        Template template = FreemarkerUtil.getTemplate(templateName);
        FileOutputStream fos = new FileOutputStream(file);
        Writer out = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"),10240);
        template.process(dataMap,out);
    }
    public static Template getTemplate(String templateName) throws IOException {
        try {
            return getConfiguration().getTemplate(templateName);
        } catch (IOException e) {
            throw e;
        }
    }


    private static String getRootPath(){
        String currentPath = FreemarkerUtil.class.getResource("").getPath();
        return currentPath.substring(0,currentPath.indexOf("/target/classes"))+"/";
    }

    private static String resolveModel2Package(String model){
        return (resolveModel2ImportPackageName(model)).replace(".","/")+"/";
    }
    private static String resolveModel2ImportPackageName(String model){
        return model.substring(0,model.indexOf(".model"));
    }
    private static String resolveModel2Name(String model){
        return (model.substring(model.lastIndexOf(".")+1));
    }

}
