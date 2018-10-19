package cn.mybatisboost.spring.boot.autoconfigure;

import cn.mybatisboost.metric.MetricInterceptor;
import javassist.ClassPool;
import javassist.CtClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class NosqlConfiguration implements ImportBeanDefinitionRegistrar {

    private static Logger logger = LoggerFactory.getLogger(MetricInterceptor.class);
    private static boolean mapUnderscoreToCamelCase = EnvironmentHolder.getEnvironment()
            .getProperty("mybatis.configuration.map-underscore-to-camel-case", boolean.class, false);

    static {
        try {
            CtClass ctClass = ClassPool.getDefault().get("org.mybatis.spring.mapper.ClassPathMapperScanner");
            ctClass.getDeclaredMethod("checkCandidate")
                    .insertAfter("if ($_) cn.mybatisboost.nosql.MapperInstrument.modify($2.getBeanClassName(), " +
                            mapUnderscoreToCamelCase + ");");
            ctClass.toClass(NosqlConfiguration.class.getClassLoader(), NosqlConfiguration.class.getProtectionDomain());
        } catch (Exception e) {
            logger.error("Exception happened when configuring mybatis-boost-nosql", e);
        }
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // Noop
    }
}
