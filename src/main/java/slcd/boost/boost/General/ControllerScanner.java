package slcd.boost.boost.General;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

@Component
public class ControllerScanner {

    private final ApplicationContext applicationContext;

    public ControllerScanner(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void printControllers() {
        Map<String, Object> controllerBeans = applicationContext.getBeansWithAnnotation(RestController.class);
        controllerBeans.forEach((beanName, bean) -> {
            Class<?> beanClass = bean.getClass();
            System.out.println(Arrays.toString(bean.getClass().getDeclaredMethods()));
            System.out.println("Controller: " + beanName + " (" + beanClass.getName() + ")");
        });
    }
}