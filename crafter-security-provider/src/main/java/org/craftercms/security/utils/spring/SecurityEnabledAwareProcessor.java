package org.craftercms.security.utils.spring;

import org.craftercms.security.utils.SecurityEnabledAware;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * {@link BeanPostProcessor} implementation that passes the {@code securityEnabled} property to beans that
 * implement the {@link SecurityEnabledAware} interface.
 *
 * @author Alfonso Vásquez
 */
public class SecurityEnabledAwareProcessor implements BeanPostProcessor {

    private boolean securityEnabled;

    public SecurityEnabledAwareProcessor(boolean securityEnabled) {
        this.securityEnabled = securityEnabled;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof SecurityEnabledAware) {
            ((SecurityEnabledAware) bean).setSecurityEnabled(securityEnabled);
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
