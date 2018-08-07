package com.zzc.www.util;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;


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


    public static void generateServiceInterfaceFile(String servicePackagePath,String importPackageName,String modelName) throws Exception{

        /* interface */
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

    private static String resolvePath2Package(String packageName){
        return packageName.substring(0,packageName.lastIndexOf("/"));
    }
    private static String resolveModel2ImportPackageName(String model){
        return model.substring(0,model.indexOf(".model"));
    }

    private static String resolvePath(String packageName){
        return packageName.replace(".","/");
    }
    private static String resolveModel2Name(String model){
        return (model.substring(model.lastIndexOf(".")+1));
    }

    public static List generate(String rootPath, String packageName, boolean recursive){
        String basePackage = rootPath+"/src/main/java/"+resolvePath(packageName)+"/";
        Map<String,String> generatePathMap = new HashMap<>(8);
        generatePathMap.put("servicePath",rootPath+"/src/main/java/"+resolvePath2Package(resolvePath(packageName))+"/service");
        return getClassInFile(Paths.get(basePackage),resolveModel2ImportPackageName(packageName),generatePathMap,recursive);
    }

    public static List<Class<?>> getClassInFile(Path path, String packageName,Map<String,String> generatePathMap, boolean recursive) {
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }
        List<Class<?>> classList = new ArrayList<>();
        if (Files.isDirectory(path)) {
            if (!recursive) {
                return Collections.emptyList();
            }
            try {
                //获取目录下的所有文件
                Stream<Path> stream = Files.list(path);
                Iterator<Path> iterator = stream.iterator();
                while (iterator.hasNext()) {
                    classList.addAll(getClassInFile(iterator.next(), packageName,generatePathMap, recursive));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                //由于传入的文件可能是相对路径, 这里要拿到文件的实际路径, 如果不存在则报IOException
                path = path.toRealPath();
                String pathStr = path.toString();
                String modelName = pathStr.substring(pathStr.lastIndexOf("\\")+1,pathStr.lastIndexOf("."));
                generateServiceInterfaceFile(generatePathMap.get("servicePath"),packageName,modelName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return classList;
    }

}
