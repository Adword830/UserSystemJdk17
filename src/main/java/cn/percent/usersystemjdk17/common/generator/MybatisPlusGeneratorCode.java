package cn.percent.usersystemjdk17.common.generator;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author pengju.zhang
 * @date 2023-09-20 10:02
 */
//public class MybatisPlusGeneratorCode {
//
//    public static void main(String[] args) {
//        // 代码生成器
//        AutoGenerator mpg = new AutoGenerator();
//        // 全局配置
//        GlobalConfig gc = new GlobalConfig();
//        String projectPath = System.getProperty("user.dir");
//        // 输出目录是在（注意是子模块的话就要写项目名）
//        gc.setOutputDir(projectPath + "/mybatis-plus-generator/src/main/java");
//        // 代码生成时的author是谁
//        gc.setAuthor("pengju.zhang");
//        // 代码生成后打开目录
//        gc.setOpen(false);
//        // 实体属性 Swagger2 注解
//        gc.setSwagger2(true);
//        // 设置ServiceName默认是会在Service接口前加上I
//        gc.setServiceName("%sService");
//        // 设置主键生成策略，默认会使用雪花算法
//        gc.setIdType(IdType.AUTO);
//        // 设置EntityName(例如表名是)
//        gc.setEntityName("%sEntity");
//        mpg.setGlobalConfig(gc);
//
//        // 数据源配置
//        DataSourceConfig dsc = new DataSourceConfig();
//        // 数据源的url
//        dsc.setUrl("你自己的数据库url");
//        // 数据源Driver
//        dsc.setDriverName("你自己的数据库驱动");
//        // 用户名
//        dsc.setUsername("用户名");
//        // 密码
//        dsc.setPassword("密码");
//        mpg.setDataSource(dsc);
//
//        // 包配置
//        PackageConfig pc = new PackageConfig();
//        String moduleName = scanner("模块名");
//        if(StrUtil.isEmpty(moduleName)){
//            // 控制台输入模块名
//            pc.setModuleName(moduleName);
//        }
//        // 指定在是在那个包下边生成
//        pc.setParent("cn.percent.modules");
//        mpg.setPackageInfo(pc);
//
//        // 自定义配置
//        InjectionConfig cfg = new InjectionConfig() {
//            @Override
//            public void initMap() {
//                // to do nothing
//            }
//        };
//
//        // 模板引擎是 freemarker
//        String templatePath = "/templates/mapper.xml.ftl";
//        // 自定义输出配置
//        List<FileOutConfig> focList = new ArrayList<>();
//        // 自定义配置会被优先输出
//        focList.add(new FileOutConfig(templatePath) {
//            @Override
//            public String outputFile(TableInfo tableInfo) {
//                // 生成相关的mapper文件
//                String entityName = tableInfo.getEntityName();
//                entityName=entityName.replace("Entity","");
//                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
//                return projectPath + "/mybatis-plus-generator/src/main/resources/mapper/" + pc.getModuleName()
//                        + "/" + entityName + "Mapper" + StringPool.DOT_XML;
//            }
//        });
//        cfg.setFileOutConfigList(focList);
//        mpg.setCfg(cfg);
//
//        // 配置模板
//        TemplateConfig templateConfig = new TemplateConfig();
//        templateConfig.setXml(null);
//        mpg.setTemplate(templateConfig);
//
//        // 策略配置
//        StrategyConfig strategy = new StrategyConfig();
//        // 数据库表映射到实体的命名策略
//        strategy.setNaming(NamingStrategy.underline_to_camel);
//        // 数据库表字段映射到实体的命名策略
//        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
//        // 继承父类实体名
//        // strategy.setSuperEntityClass("你自己的父类实体,没有就不用设置!");
//        // 是否使用lombok模型（默认 false）
//        strategy.setEntityLombokModel(true);
//        // 是否使用RestController模型
//        strategy.setRestControllerStyle(true);
//        // 公共父类
//        // strategy.setSuperControllerClass("你自己的父类控制器,没有就不用设置!");
//        // 写于父类中的公共字段
//        strategy.setSuperEntityColumns("id");
//        // 使用那些表创建
//        strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
//        mpg.setStrategy(strategy);
//        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
//        mpg.execute();
//    }
//
//    /**
//     * <p>
//     * 读取控制台内容
//     * </p>
//     */
//    public static String scanner(String tip) {
//        Scanner scanner = new Scanner(System.in);
//        StringBuilder help = new StringBuilder();
//        help.append("请输入" + tip + "：");
//        System.out.println(help.toString());
//        if (scanner.hasNext()) {
//            String ipt = scanner.next();
//            if (StringUtils.isNotBlank(ipt)) {
//                return ipt;
//            }
//        }
//        throw new MybatisPlusException("请输入正确的" + tip + "！");
//    }
//
//}
